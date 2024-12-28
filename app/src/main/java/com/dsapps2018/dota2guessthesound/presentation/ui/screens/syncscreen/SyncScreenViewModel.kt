package com.dsapps2018.dota2guessthesound.presentation.ui.screens.syncscreen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsapps2018.dota2guessthesound.BuildConfig
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.repository.SyncRepository
import com.dsapps2018.dota2guessthesound.data.repository.UserDataRepository
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SyncScreenViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val syncRepository: SyncRepository,
    private val userDataRepository: UserDataRepository,
    private val firebaseCrashlytics: FirebaseCrashlytics
) : ViewModel() {

    private val _progressStatus = MutableSharedFlow<ProgressUpdateEvent>(10)
    val progressStatus: SharedFlow<ProgressUpdateEvent> = _progressStatus.asSharedFlow()

    private val _triviaData = MutableStateFlow("")
    val triviaData = _triviaData.asStateFlow()

    private var syncIndex = 0

    companion object {

        private val triviaList = listOf<String>(
            "If you explore the Dota 2 map in certain patches, you might spot hidden Easter eggs, like a frog on a lily pad—a nod to the mysterious Dota developer, IceFrog.",
            "Pudge, one of the most popular heroes, inspired a custom game called \"Pudge Wars\" in Warcraft III, where players used his \"Hook\" ability to pull each other. This game mode has its own loyal following even today!",
            " At The International tournaments, Valve creates a real-life version of the Secret Shop where fans can buy exclusive Dota 2 merchandise not available elsewhere.",
            "In 2013, one team pulled off the impossible by using five heroes to kill Roshan at the very beginning of a pro match—securing a legendary “First Blood” against Roshan himself.",
            " In a 2013 tournament, a team famously used a glitch called the “Fountain Hook” with Pudge and Chen, leading to a controversial but unforgettable victory.",
            "Players can unlock or purchase custom weather effects, like snow or rain, that subtly alter the atmosphere in their games. It doesn’t change gameplay but adds an extra level of personalization!",
            " In 2018, OpenAI’s bot team defeated pro players at Dota 2, marking one of the first times AI could beat human professionals in a complex, team-based game.",
            "Dota 2 holds a Guinness World Record for the largest single prize pool in eSports, with The International’s prize pool topping \$40 million in 2020.",
            "The longest pro Dota 2 match lasted over three hours, testing players’ endurance as they fought through multiple attempts to end the game in a record-breaking stalemate."
        )

        private val syncList = listOf<String>(
            "Checking game data",
            "Downloading config files",
            "Syncing caster type",
            "Syncing casters",
            "Syncing game mode",
            "Syncing changelog",
            "Syncing user data",
            "Syncing sounds",
            "Sync finished"
        )
    }

    val coroutineExceptionHandler: CoroutineExceptionHandler =
        CoroutineExceptionHandler { coroutineContext, throwable ->
            viewModelScope.launch {
                firebaseCrashlytics.recordException(throwable)
                _progressStatus.emit(
                    ProgressUpdateEvent.ProgressError(
                        throwable.message
                            ?: context.getString(
                                R.string.sync_unknown_error,
                                syncList[syncIndex - 1]
                            )
                    )
                )
            }
        }

    init {
        startTriviaRotation()
        startSync()
    }

    private fun startTriviaRotation() {
        viewModelScope.launch{
            val shuffledList = triviaList.shuffled()
            var index = 0
            _triviaData.update { shuffledList[index] }
            while (true){
                delay(10000L)
                index = (index + 1) % shuffledList.size
                _triviaData.update { shuffledList[index] }
            }
        }
    }

    private fun startSync() {
        viewModelScope.launch(coroutineExceptionHandler) {

            sendNextEvent()
            delay(1000L)

            sendNextEvent()
            val configDto = syncRepository.syncRemoteConfig()
            if (configDto.forcedVersion > BuildConfig.VERSION_CODE) {
                _progressStatus.emit(ProgressUpdateEvent.ProgressUpdateRequired)
                return@launch
            }

            syncRepository.insertInitialUserData()

            sendNextEvent()
            syncRepository.syncCasterType()

            sendNextEvent()
            syncRepository.syncCaster()

            sendNextEvent()
            syncRepository.syncGameMode()

            sendNextEvent()
            syncRepository.syncChangelog()

            sendNextEvent()
            userDataRepository.syncUserData()

            syncRepository.syncSound().onEach { progressUpdate ->
                val prog = ProgressUpdateEvent.ProgressUpdate(
                    (syncIndex + progressUpdate.first).toFloat(),
                    syncList.size.toFloat(),
                    context.getString(R.string.downloading_msg, progressUpdate.second)
                )

                _progressStatus.emit(prog)
            }.onCompletion {
                _progressStatus.emit(
                    ProgressUpdateEvent.ProgressUpdate(
                        syncList.size.toFloat(),
                        syncList.size.toFloat(),
                        syncList.last()
                    )
                )
                _progressStatus.emit(ProgressUpdateEvent.SyncFinished)
            }.collect()
        }
    }

    private suspend fun sendNextEvent() {
        _progressStatus.emit(
            ProgressUpdateEvent.ProgressUpdate(
                (syncIndex + 1).toFloat(),
                syncList.size.toFloat(),
                syncList[syncIndex]
            )
        )
        syncIndex++
    }

    fun restartSync() {
        syncIndex = 0
        startSync()
    }
}