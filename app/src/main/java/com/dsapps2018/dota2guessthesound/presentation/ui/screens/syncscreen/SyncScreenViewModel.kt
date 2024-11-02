package com.dsapps2018.dota2guessthesound.presentation.ui.screens.syncscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsapps2018.dota2guessthesound.BuildConfig
import com.dsapps2018.dota2guessthesound.data.repository.SyncRepository
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SyncScreenViewModel @Inject constructor(
    private val syncRepository: SyncRepository,
    private val firebaseCrashlytics: FirebaseCrashlytics
) : ViewModel() {

    private val _progressStatus = MutableSharedFlow<ProgressUpdateEvent>(10)
    val progressStatus: SharedFlow<ProgressUpdateEvent> = _progressStatus.asSharedFlow()

    private var syncIndex = 0

    companion object {
        private val syncList = listOf<String>(
            "Checking game data",
            "Downloading config files",
            "Syncing caster type",
            "Syncing casters",
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
                            ?: "Unknown Error Message when syncing ${syncList[syncIndex - 1]}"
                    )
                )
            }
        }

    init {
        startSync()
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

            sendNextEvent()
            syncRepository.syncCasterType()

            sendNextEvent()
            syncRepository.syncCaster()

            syncRepository.syncSound().onEach { progressUpdate ->
                val prog = ProgressUpdateEvent.ProgressUpdate((syncIndex + progressUpdate.first).toFloat(), syncList.size.toFloat(), "Downloading: ${progressUpdate.second}")

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

    fun restartSync(){
        syncIndex = 0
        startSync()
    }
}