package com.dsapps2018.dota2guessthesound.presentation.ui.screens.herocatalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsapps2018.dota2guessthesound.data.herocatalog.CatalogHero
import com.dsapps2018.dota2guessthesound.data.herocatalog.HeroCatalog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HeroCatalogViewModel @Inject constructor(
    private val heroCatalog: HeroCatalog,
) : ViewModel() {

    private val _heroes = MutableStateFlow<List<CatalogHero>>(emptyList())
    val heroes: StateFlow<List<CatalogHero>> = _heroes.asStateFlow()

    init {
        viewModelScope.launch {
            _heroes.value = heroCatalog.heroes()
        }
    }
}
