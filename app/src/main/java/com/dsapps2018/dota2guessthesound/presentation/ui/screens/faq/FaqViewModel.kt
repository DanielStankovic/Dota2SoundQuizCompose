package com.dsapps2018.dota2guessthesound.presentation.ui.screens.faq

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsapps2018.dota2guessthesound.data.db.entity.FaqEntity
import com.dsapps2018.dota2guessthesound.data.repository.FaqRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FaqViewModel @Inject constructor(
    private val faqRepository: FaqRepository
) : ViewModel() {

    val faqList: StateFlow<List<FaqEntity>> = faqRepository.getAllFaqs().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

}