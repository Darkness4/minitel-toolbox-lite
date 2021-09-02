package com.minitel.toolboxlite.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minitel.toolboxlite.domain.repositories.CalendarSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val calendarSettingsRepository: CalendarSettingsRepository) : ViewModel() {
    val calendarSettings = calendarSettingsRepository.watch()
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val earlyMinutes = MutableStateFlow(0.0f)

    init {
        viewModelScope.launch(Dispatchers.Default) {
            calendarSettings.collect {
                it?.let {
                    earlyMinutes.value = it.earlyMinutes.toFloat()
                }
            }
        }
        viewModelScope.launch(Dispatchers.Default) {
            earlyMinutes.collect {
                if (calendarSettings.value != null) {
                    calendarSettingsRepository.update(it.toLong())
                }
            }
        }
    }

    private val _openLicences = MutableStateFlow<Unit?>(null)
    val openLicences: StateFlow<Unit?>
        get() = _openLicences

    fun doOpenLicences() {
        _openLicences.value = Unit
    }

    fun openLicencesDone() {
        _openLicences.value = null
    }
}
