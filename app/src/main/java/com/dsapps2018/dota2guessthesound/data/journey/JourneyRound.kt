package com.dsapps2018.dota2guessthesound.data.journey

import android.content.Context
import android.content.res.Resources
import android.util.Log
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.affix.AffixEngine
import com.dsapps2018.dota2guessthesound.data.affix.AffixGameState
import com.dsapps2018.dota2guessthesound.data.api.response.JourneyDto
import com.dsapps2018.dota2guessthesound.data.api.response.JourneyLevelDto
import com.dsapps2018.dota2guessthesound.data.dao.AffixDao
import com.dsapps2018.dota2guessthesound.data.dao.CasterDao
import com.dsapps2018.dota2guessthesound.data.dao.SoundDao
import com.dsapps2018.dota2guessthesound.data.db.entity.AffixEntity
import com.dsapps2018.dota2guessthesound.data.model.AffixModel
import com.dsapps2018.dota2guessthesound.data.model.JourneyGameModel
import com.dsapps2018.dota2guessthesound.data.model.JourneyLevelModel
import com.dsapps2018.dota2guessthesound.data.model.SoundModel
import com.dsapps2018.dota2guessthesound.data.util.Constants
import com.dsapps2018.dota2guessthesound.data.util.SoundPlayback
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Deep Data-layer module for a Journey Round (and shared level-list load).
 * See docs/adr/0003-journey-round.md.
 */
class JourneyRound @Inject constructor(
    @ApplicationContext private val context: Context,
    private val resources: Resources,
    private val postgrest: Postgrest,
    private val soundDao: SoundDao,
    private val casterDao: CasterDao,
    private val affixDao: AffixDao,
    private val soundPlayback: SoundPlayback,
    private val firebaseCrashlytics: FirebaseCrashlytics,
) {

    private val _levelsState =
        MutableStateFlow<JourneyLevelsState>(JourneyLevelsState.Loading)
    val levelsState: StateFlow<JourneyLevelsState> = _levelsState.asStateFlow()

    private val _roundState =
        MutableStateFlow<JourneyRoundState>(JourneyRoundState.Idle)
    val roundState: StateFlow<JourneyRoundState> = _roundState.asStateFlow()

    private val _events = MutableSharedFlow<JourneyRoundEvent>()
    val events: SharedFlow<JourneyRoundEvent> = _events.asSharedFlow()

    private var affixEngine: AffixEngine? = null
    private var soundPlayCount = 0
    private var maxSoundPlays: Int? = null
    private var isSuddenDeath = false
    private var additionalLifeUsed = false

    private var timerJob: Job? = null
    private var timerDurationMs: Long = 0L
    private var timerRemainingWhenPaused: Long = 0L
    private var isTimerPaused: Boolean = false
    private var roundScope: CoroutineScope? = null

    suspend fun loadLevels() {
        _levelsState.value = JourneyLevelsState.Loading
        try {
            val affixList = affixDao.getAllAffixes()
            val levels = fetchLevelsData().map { level ->
                JourneyLevelModel(
                    id = level.id,
                    level = level.level,
                    affixes = level.affixes.map { affixId ->
                        toAffixModel(affixList.find { it.id == affixId }!!)
                    }
                )
            }
            _levelsState.value = JourneyLevelsState.Success(levels)
        } catch (t: Throwable) {
            firebaseCrashlytics.recordException(t)
            _levelsState.value = JourneyLevelsState.Error(
                context.getString(R.string.level_fetch_error)
            )
        }
    }

    suspend fun startRound(levelNum: Int, scope: CoroutineScope) {
        roundScope = scope
        clearTimer()
        additionalLifeUsed = false
        soundPlayCount = 0
        maxSoundPlays = null
        isSuddenDeath = false
        _roundState.value = JourneyRoundState.Loading

        try {
            val levelData = fetchLevelData(levelNum)
            val affixList = affixDao.getAllAffixes()
            val currentAffixes = levelData.affixes.map { affixId ->
                toAffixModel(affixList.find { it.id == affixId }!!)
            }

            val engine = AffixEngine(currentAffixes)
            affixEngine = engine

            val affixUIState = engine.applyUIModifications()
            val affixGameState = engine.applyGameplayModifications(AffixGameState())
            val hearts = affixGameState.modifiedHeartCount ?: DEFAULT_HEARTS
            isSuddenDeath = affixGameState.isSuddenDeath

            engine.getSoundLimitations()?.let { limitations ->
                maxSoundPlays = limitations.maxPlays
            }

            val heroIds = levelData.radiantHeroes + levelData.direHeroes
            val journeySounds = soundDao.getJourneySounds(heroIds)
            val (correctSounds, incorrectSounds) = journeySounds.partition { it.isCorrectSound }
            val randomSounds =
                incorrectSounds.shuffled().take(levelData.maxSounds - correctSounds.size)
            val soundList = (correctSounds + randomSounds).shuffled()
            val radiantHeroImages =
                casterDao.getCasterNames(levelData.radiantHeroes).map { name ->
                    resources.getIdentifier(
                        "hero_${name.lowercase().replace("'s", "s").replace("-", "")}",
                        "drawable",
                        context.packageName
                    )
                }
            val direHeroImages =
                casterDao.getCasterNames(levelData.direHeroes).map { name ->
                    resources.getIdentifier(
                        name.replace("'s", "s").replace("-", ""),
                        "drawable",
                        context.packageName
                    )
                }

            val selectedMarks = soundList.associate { it.soundModel.id to false }
            val game = JourneyGameModel(
                levelNum,
                radiantHeroImages,
                direHeroImages,
                soundList,
                correctSounds.size
            )

            _roundState.value = JourneyRoundState.Ready(
                level = levelNum,
                game = game,
                hearts = hearts,
                affixUI = affixUIState,
                timer = null,
                selectedMarks = selectedMarks,
                remainingPlays = remainingPlaysOrNull(),
                showContinueDialog = false,
            )

            engine.getTimerConfiguration()?.let { timerConfig ->
                initializeTimer(timerConfig.durationMs, scope)
            }
        } catch (t: Throwable) {
            firebaseCrashlytics.recordException(t)
            _roundState.value = JourneyRoundState.Error(
                context.getString(R.string.level_fetch_error)
            )
        }
    }

    fun toggleMark(soundId: Int) {
        updateReady { ready ->
            val current = ready.selectedMarks[soundId] ?: return@updateReady ready
            ready.copy(selectedMarks = ready.selectedMarks + (soundId to !current))
        }
    }

    fun playSound(sound: SoundModel) {
        maxSoundPlays?.let { maxPlays ->
            if (soundPlayCount >= maxPlays) return
        }
        soundPlayCount++
        soundPlayback.play(sound)
        updateReady { ready ->
            ready.copy(remainingPlays = remainingPlaysOrNull())
        }
    }

    fun submit() {
        val ready = _roundState.value as? JourneyRoundState.Ready ?: return
        val engine = affixEngine ?: return
        val scope = roundScope ?: return

        scope.launch {
            val correctSounds = ready.game.soundList
                .filter { it.isCorrectSound }
                .map { it.soundModel.id }
                .toSet()
            val allSoundIds = ready.game.soundList.map { it.soundModel.id }.toSet()
            val selectedSounds =
                ready.selectedMarks.filter { it.value }.keys.toSet()

            val validationResult = engine.modifyAnswerValidation(
                selectedSounds,
                correctSounds,
                allSoundIds
            )

            if (validationResult.isCorrect) {
                _events.emit(JourneyRoundEvent.Correct)
            } else {
                val newHearts = ready.hearts - 1
                if (newHearts <= 0) {
                    if (isSuddenDeath) {
                        _events.emit(JourneyRoundEvent.GameOver)
                    } else if (!additionalLifeUsed) {
                        pauseTimer()
                        updateReady { it.copy(hearts = newHearts, showContinueDialog = true) }
                    } else {
                        _events.emit(JourneyRoundEvent.GameOver)
                    }
                } else {
                    updateReady { it.copy(hearts = newHearts) }
                }
            }
        }
    }

    fun dismissContinueDialog() {
        updateReady { it.copy(showContinueDialog = false) }
    }

    fun grantExtraLife() {
        additionalLifeUsed = true
        updateReady { it.copy(hearts = it.hearts + 1, showContinueDialog = false) }
    }

    fun resumeAfterAd() {
        resumeTimer()
    }

    fun clear() {
        soundPlayback.stop()
        clearTimer()
        roundScope = null
    }

    private fun remainingPlaysOrNull(): Int? =
        maxSoundPlays?.let { max -> max - soundPlayCount }

    private fun updateReady(transform: (JourneyRoundState.Ready) -> JourneyRoundState.Ready) {
        _roundState.update { state ->
            if (state is JourneyRoundState.Ready) transform(state) else state
        }
    }

    private fun toAffixModel(affix: AffixEntity): AffixModel =
        AffixModel(
            id = affix.id,
            affix = affix.affix,
            description = affix.description,
            iconResourceId = resources.getIdentifier(
                "affix_${affix.affix.lowercase().replace(" ", "_")}",
                "drawable",
                context.packageName
            ),
            data = affix.data
        )

    private suspend fun fetchLevelData(levelNum: Int): JourneyDto =
        postgrest
            .from(Constants.TABLE_JOURNEY)
            .select(
                columns = Columns.list(
                    "id",
                    "level",
                    "radiant_heroes",
                    "dire_heroes",
                    "max_sounds",
                    "affixes"
                )
            ) {
                filter {
                    eq("level", levelNum)
                }
            }.decodeSingle()

    private suspend fun fetchLevelsData(): List<JourneyLevelDto> =
        postgrest.from(Constants.TABLE_JOURNEY)
            .select(
                columns = Columns.list(
                    "id",
                    "level",
                    "affixes",
                )
            ) {
                order("level", Order.ASCENDING)
            }.decodeList()

    private fun initializeTimer(durationMs: Long, scope: CoroutineScope) {
        Log.d(TAG, "initializeTimer durationMs=$durationMs")
        timerDurationMs = durationMs
        isTimerPaused = false
        timerRemainingWhenPaused = 0L
        startTimerCoroutine(scope)
    }

    private fun startTimerCoroutine(scope: CoroutineScope) {
        timerJob?.cancel()
        timerJob = scope.launch {
            Log.d(TAG, "startTimerCoroutine isTimerPaused=$isTimerPaused")

            val startTime = if (isTimerPaused) {
                isTimerPaused = false
                System.currentTimeMillis() - (timerDurationMs - timerRemainingWhenPaused)
            } else {
                System.currentTimeMillis()
            }

            val endTime =
                startTime + (if (isTimerPaused) timerRemainingWhenPaused else timerDurationMs)

            while (System.currentTimeMillis() < endTime && !isTimerPaused) {
                val remaining = endTime - System.currentTimeMillis()
                updateReady { ready ->
                    ready.copy(
                        timer = TimerState(
                            remainingMs = remaining,
                            totalMs = timerDurationMs,
                            isWarning = remaining < timerDurationMs / 4,
                            isPaused = false
                        )
                    )
                }
                delay(100)
            }

            if (!isTimerPaused) {
                _events.emit(JourneyRoundEvent.GameOver)
            }
        }
    }

    private fun pauseTimer() {
        if (timerJob?.isActive == true && !isTimerPaused) {
            isTimerPaused = true
            val current = (_roundState.value as? JourneyRoundState.Ready)?.timer
            timerRemainingWhenPaused = current?.remainingMs ?: 0L
            updateReady { ready ->
                ready.copy(timer = ready.timer?.copy(isPaused = true))
            }
            timerJob?.cancel()
        }
    }

    private fun resumeTimer() {
        val scope = roundScope ?: return
        if (isTimerPaused && timerRemainingWhenPaused > 0) {
            startTimerCoroutine(scope)
        }
    }

    private fun clearTimer() {
        timerJob?.cancel()
        timerJob = null
        isTimerPaused = false
        timerRemainingWhenPaused = 0L
        timerDurationMs = 0L
    }

    companion object {
        private const val TAG = "JourneyRound"
        private const val DEFAULT_HEARTS = 2
    }
}
