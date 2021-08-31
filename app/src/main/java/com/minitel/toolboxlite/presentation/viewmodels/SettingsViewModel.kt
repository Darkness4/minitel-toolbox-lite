package com.minitel.toolboxlite.presentation.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class SettingsViewModel : ViewModel() {
    val earlyMinutes = MutableStateFlow(5L)
}
