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

    private val _events = MutableSharedFlow<JourneyRoundEvent>(extraBufferCapacity = 8)
    val events: SharedFlow<JourneyRoundEvent> = _events.asSharedFlow()

    private var affixEngine: AffixEngine? = null
    private var soundPlayCount = 0
    private var maxSoundPlays: Int? = null
    private var extraLifeGateAllowed = true
    private var extraLifeGateUsed = false
    private var pendingContinueOffer: ExtraLifeContinueOffer? = null

    private var timerJob: Job? = null
    private var timerDurationMs: Long = 0L
    private var timerRemainingWhenPaused: Long = 0L
    private var isTimerPaused: Boolean = false
    private var endsRoundOnTimeout: Boolean = false
    private var soundquakeClearsMarks: Boolean = false
    private var soundquakeDrainsHeart: Boolean = false
    private var timerExtensionMs: Long = DEFAULT_TIMER_EXTENSION_SECONDS * 1000L
    /** Armed by time buyback; started only when gameplay surface is clear (ad dismissed). */
    private var pendingTimerStartMs: Long? = null
    /** True while a fullscreen ad (or similar) covers the game; blocks lifecycle resume. */
    private var timerBlockedByOverlay: Boolean = false
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
        extraLifeGateUsed = false
        pendingContinueOffer = null
        soundPlayCount = 0
        maxSoundPlays = null
        extraLifeGateAllowed = true
        endsRoundOnTimeout = false
        soundquakeClearsMarks = false
        soundquakeDrainsHeart = false
        timerExtensionMs = DEFAULT_TIMER_EXTENSION_SECONDS * 1000L
        pendingTimerStartMs = null
        timerBlockedByOverlay = false
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
            extraLifeGateAllowed = affixGameState.extraLifeGateAllowed
            soundquakeClearsMarks = affixGameState.soundquakeClearsMarks
            soundquakeDrainsHeart = affixGameState.soundquakeDrainsHeart

            // Echo Limit: Affix enables; budget = board size + level offset (default Medium +5).
            if (engine.getSoundLimitations() != null) {
                val offset =
                    (levelData.echoLimitOffset ?: DEFAULT_ECHO_LIMIT_OFFSET).coerceAtLeast(0)
                maxSoundPlays = levelData.maxSounds + offset
            }

            val heroIds = levelData.radiantHeroes + levelData.direHeroes
            val journeySounds = soundDao.getJourneySounds(heroIds)
            val (correctSounds, incorrectSounds) = journeySounds.partition { it.isCorrectSound }
            val randomSounds =
                incorrectSounds.shuffled().take(levelData.maxSounds - correctSounds.size)
            val soundList = (correctSounds + randomSounds).shuffled()
            val casterNameById =
                casterDao.getActiveCasters(heroIds).associate { it.id to it.name }
            val portraits = HeroPortraitPolicy.resolve(
                affixUI = affixUIState,
                radiantHeroIds = levelData.radiantHeroes,
                direHeroIds = levelData.direHeroes,
                maskedHeroIdsFromLevel = levelData.maskedHeroIds,
                blurredHeroIdsFromLevel = levelData.blurredHeroIds,
                hiddenHeroId = levelData.hiddenHeroId,
                casterNameById = casterNameById,
                resources = resources,
                packageName = context.packageName,
            )

            val selectedMarks = soundList.associate { it.soundModel.id to false }
            val game = JourneyGameModel(
                level = levelNum,
                radiantHeroPortraits = portraits.radiant,
                direHeroPortraits = portraits.dire,
                soundList = soundList,
                totalCorrectSounds = correctSounds.size
            )

            _roundState.value = JourneyRoundState.Ready(
                level = levelNum,
                game = game,
                hearts = hearts,
                hud = affixUIState.toHudChrome(),
                timer = null,
                selectedMarks = selectedMarks,
                remainingPlays = remainingPlaysOrNull(),
                showContinueDialog = false,
                continueOffer = null,
            )

            engine.getTimerConfiguration()?.let { timerConfig ->
                endsRoundOnTimeout = timerConfig.endsRoundOnTimeout
                val levelSeconds = levelData.timerSeconds?.takeIf { it > 0 }
                val durationMs = when {
                    levelSeconds != null -> levelSeconds * 1000L
                    else -> timerConfig.durationMs
                }
                timerExtensionMs =
                    (levelData.timerExtensionSeconds?.takeIf { it > 0 }
                        ?: DEFAULT_TIMER_EXTENSION_SECONDS) * 1000L
                initializeTimer(durationMs, scope)
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
                applyHeartLoss(restartSoundquakeTimerAfterGate = false)
            }
        }
    }

    fun dismissContinueDialog() {
        // Hide only — keep pendingContinueOffer so grantExtraLifeGate still knows Heart vs Time
        // after the UI dismisses ahead of the rewarded ad callback.
        updateReady { it.copy(showContinueDialog = false) }
    }

    /**
     * Applies Extra Life Gate reward only. Does **not** start/resume the timer — that waits until
     * [onGameplaySurfaceClear] (ad closed / surface visible again).
     */
    fun grantExtraLifeGate() {
        extraLifeGateUsed = true
        val offer = pendingContinueOffer
        pendingContinueOffer = null
        when (offer) {
            ExtraLifeContinueOffer.TimeExtension -> {
                pendingTimerStartMs = timerExtensionMs
                updateReady { it.copy(showContinueDialog = false, continueOffer = null) }
            }
            ExtraLifeContinueOffer.Heart, null -> {
                updateReady {
                    it.copy(
                        hearts = it.hearts + 1,
                        showContinueDialog = false,
                        continueOffer = null,
                    )
                }
            }
        }
    }

    /** Fullscreen ad / overlay covering the board — pause any running timer. */
    fun onGameplaySurfaceObscured() {
        timerBlockedByOverlay = true
        pauseTimer()
    }

    /**
     * Game screen is interactive again (ad dismissed, app resumed with no dialog).
     * Starts a pending time buyback or resumes a paused countdown.
     */
    fun onGameplaySurfaceClear() {
        timerBlockedByOverlay = false
        val ready = _roundState.value as? JourneyRoundState.Ready ?: return
        if (ready.showContinueDialog) return

        pendingTimerStartMs?.let { ms ->
            pendingTimerStartMs = null
            val scope = roundScope ?: return
            initializeTimer(ms, scope)
            return
        }
        resumeTimer()
    }

    /** App background / ON_PAUSE — pause without treating as overlay (resume may still be blocked). */
    fun onHostPaused() {
        pauseTimer()
    }

    /** App foreground / ON_RESUME — resume only if nothing covers the board. */
    fun onHostResumed() {
        if (timerBlockedByOverlay) return
        val ready = _roundState.value as? JourneyRoundState.Ready ?: return
        if (ready.showContinueDialog) return
        // Do not start pending time buyback here — that waits for ad dismiss via onGameplaySurfaceClear.
        resumeTimer()
    }

    fun clear() {
        soundPlayback.stop()
        clearTimer()
        pendingTimerStartMs = null
        timerBlockedByOverlay = false
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
                    "affixes",
                    "masked_hero_ids",
                    "blurred_hero_ids",
                    "hidden_hero_id",
                    "timer_seconds",
                    "timer_extension_seconds",
                    "echo_limit_offset",
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
                if (endsRoundOnTimeout) {
                    offerTimeExtensionOrGameOver()
                } else {
                    handleSoundquakeTimeout()
                }
            }
        }
    }


    /**
     * Soundquake interval elapsed: reshuffle board, optionally clear marks / drain a heart,
     * then restart the interval unless Extra Life Gate or Game Over intervenes.
     */
    private suspend fun handleSoundquakeTimeout() {
        val ready = _roundState.value as? JourneyRoundState.Ready ?: return
        val reshuffled = ready.game.soundList.shuffled()
        val marks = if (soundquakeClearsMarks) {
            reshuffled.associate { it.soundModel.id to false }
        } else {
            // Marks are keyed by sound id — keep selections across position shuffle.
            ready.selectedMarks
        }
        updateReady {
            it.copy(
                game = it.game.copy(soundList = reshuffled),
                selectedMarks = marks,
                timer = it.timer?.copy(remainingMs = 0L, isPaused = true),
            )
        }

        // Fire FX after board update so LazyGrid animateItem + shake run together.
        _events.emit(
            JourneyRoundEvent.Soundquake(
                strong = soundquakeClearsMarks,
                drainedHeart = soundquakeDrainsHeart,
            )
        )

        if (soundquakeDrainsHeart) {
            val survived = applyHeartLoss(restartSoundquakeTimerAfterGate = true)
            if (!survived) return
        }

        val scope = roundScope ?: return
        initializeTimer(timerDurationMs, scope)
    }

    /**
     * Drain one heart. Returns false when the round ended or Extra Life Gate was offered
     * (caller must not continue). Returns true when play continues with hearts remaining.
     *
     * @param restartSoundquakeTimerAfterGate when Gate is offered from a Soundquake quake,
     * arm a full interval restart after the rewarded ad (not a Race +time buyback).
     */
    private suspend fun applyHeartLoss(restartSoundquakeTimerAfterGate: Boolean): Boolean {
        val ready = _roundState.value as? JourneyRoundState.Ready ?: return false
        val newHearts = ready.hearts - 1
        if (newHearts <= 0) {
            // Sudden Death: Extra Life Gate disabled — first fail ends the round.
            // Fragile Spirit / default: offer Gate once if unused.
            if (!extraLifeGateAllowed) {
                _events.emit(JourneyRoundEvent.GameOver)
                return false
            }
            if (!extraLifeGateUsed) {
                pauseTimer()
                pendingContinueOffer = ExtraLifeContinueOffer.Heart
                if (restartSoundquakeTimerAfterGate) {
                    pendingTimerStartMs = timerDurationMs
                }
                updateReady {
                    it.copy(
                        hearts = newHearts,
                        showContinueDialog = true,
                        continueOffer = ExtraLifeContinueOffer.Heart,
                    )
                }
                return false
            }
            _events.emit(JourneyRoundEvent.GameOver)
            return false
        }
        updateReady { it.copy(hearts = newHearts) }
        return true
    }

    private suspend fun offerTimeExtensionOrGameOver() {
        if (!extraLifeGateAllowed || extraLifeGateUsed) {
            _events.emit(JourneyRoundEvent.GameOver)
            return
        }
        pendingContinueOffer = ExtraLifeContinueOffer.TimeExtension
        updateReady {
            it.copy(
                showContinueDialog = true,
                continueOffer = ExtraLifeContinueOffer.TimeExtension,
                timer = it.timer?.copy(remainingMs = 0L, isPaused = true),
            )
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
        pendingTimerStartMs = null
    }

    companion object {
        private const val TAG = "JourneyRound"
        private const val DEFAULT_HEARTS = 2
        private const val DEFAULT_TIMER_EXTENSION_SECONDS = 20
        /** Medium Echo Limit tier when Journey `echo_limit_offset` is unset. */
        private const val DEFAULT_ECHO_LIMIT_OFFSET = 5
    }
}
