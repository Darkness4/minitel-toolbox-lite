package com.minitel.toolboxlite.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minitel.toolboxlite.core.state.State
import com.minitel.toolboxlite.core.state.tryOrCatch
import com.minitel.toolboxlite.domain.services.EmseAuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val emseAuthService: EmseAuthService) : ViewModel() {
    val username = MutableStateFlow("")
    val password = MutableStateFlow("")

    private val _login = MutableStateFlow<State<String>?>(null)
    val login: StateFlow<State<String>?>
        get() = _login

    fun doLogin() = viewModelScope.launch {
        _login.value = tryOrCatch { emseAuthService.loginForIcs(username.value, password.value) }
    }

    fun loginFinished() {
        _login.value = null
    }
}
