package com.minitel.toolboxlite.presentation.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class MainViewModel : ViewModel() {
    val showBottomBar = MutableStateFlow(false)
}
