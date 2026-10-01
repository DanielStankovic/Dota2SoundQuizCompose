package com.dsapps2018.dota2guessthesound.presentation.ui.screens.herocatalog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.dsapps2018.dota2guessthesound.data.herocatalog.HeroCatalog
import com.dsapps2018.dota2guessthesound.data.model.SoundModel
import com.dsapps2018.dota2guessthesound.presentation.navigation.HeroCatalogSoundsDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HeroCatalogSoundsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val heroCatalog: HeroCatalog,
) : ViewModel() {

    private val destination = savedStateHandle.toRoute<HeroCatalogSoundsDestination>()

    val heroName: String = destination.heroName

    private val _sounds = MutableStateFlow<List<SoundModel>>(emptyList())
    val sounds: StateFlow<List<SoundModel>> = _sounds.asStateFlow()

    init {
        viewModelScope.launch {
            _sounds.value = heroCatalog.sounds(destination.heroId)
        }
    }

    fun play(sound: SoundModel) = heroCatalog.play(sound)

    fun stop() = heroCatalog.stop()

    override fun onCleared() {
        super.onCleared()
        heroCatalog.stop()
    }
}
