package com.minitel.toolboxlite.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minitel.toolboxlite.domain.repositories.CalendarSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val calendarSettingsRepository: CalendarSettingsRepository) : ViewModel() {
    val earlyMinutes = MutableStateFlow(5L)

    init {
        viewModelScope.launch(Dispatchers.Default) {
            earlyMinutes.collect { value ->
                calendarSettingsRepository.update(value)
            }
        }
    }
}
