package com.dsapps2018.dota2guessthesound.presentation.ui.screens.invoker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsapps2018.dota2guessthesound.data.invoker.InvokerEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvokerExplanationViewModel @Inject constructor(
    private val invokerEntry: InvokerEntry,
) : ViewModel() {

    fun enter(onEntered: () -> Unit) {
        viewModelScope.launch {
            if (invokerEntry.enter()) {
                onEntered()
            }
        }
    }
}
