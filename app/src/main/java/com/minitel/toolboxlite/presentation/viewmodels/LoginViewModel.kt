package com.minitel.toolboxlite.presentation.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class LoginViewModel : ViewModel() {
    val username = MutableStateFlow("")
    val password = MutableStateFlow("")
}
