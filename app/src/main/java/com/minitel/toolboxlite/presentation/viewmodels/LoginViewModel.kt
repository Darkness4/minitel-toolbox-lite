package com.minitel.toolboxlite.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minitel.toolboxlite.core.state.State
import com.minitel.toolboxlite.core.state.tryOrCatch
import com.minitel.toolboxlite.domain.entities.calendar.IcsEvent
import com.minitel.toolboxlite.domain.repositories.CalendarSettingsRepository
import com.minitel.toolboxlite.domain.repositories.IcsEventRepository
import com.minitel.toolboxlite.domain.repositories.IcsReferenceRepository
import com.minitel.toolboxlite.domain.repositories.LoginSettingsRepository
import com.minitel.toolboxlite.domain.services.EmseAuthService
import com.minitel.toolboxlite.domain.services.IcsDownloader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val emseAuthService: EmseAuthService,
    private val icsDownloader: IcsDownloader,
    private val loginSettingsRepository: LoginSettingsRepository,
    private val icsReferenceRepository: IcsReferenceRepository,
    calendarSettingsRepository: CalendarSettingsRepository,
    icsEventRepository: IcsEventRepository
) : ViewModel() {
    val username = MutableStateFlow("")
    val password = MutableStateFlow("")
    val rememberMe = MutableStateFlow(false)

    val isEmseLoggedIn =
        emseAuthService.isSignedIn().stateIn(viewModelScope, SharingStarted.Lazily, false)
    val isIcsSaved = MutableStateFlow(false)
    val icsUrl = MutableStateFlow("Not found")

    val calendarSettings = calendarSettingsRepository
        .watch()
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    init {
        viewModelScope.launch(Dispatchers.Default) {
            icsReferenceRepository.watch().collect {
                if (it.path.isNotBlank()) {
                    isIcsSaved.value = true
                    icsUrl.value = it.path
                    doDownload(it.path)
                }
            }
        }

        viewModelScope.launch(Dispatchers.Default) {
            loginSettingsRepository.watch().collect {
                rememberMe.value = it.rememberMe
                if (it.rememberMe) {
                    username.value = it.credentials.username
                    password.value = it.credentials.password
                }
            }
        }
    }

    fun updateLoginSettings() = viewModelScope.launch(Dispatchers.Default) {
        loginSettingsRepository.update(
            rememberMe.value,
            username.value,
            password.value
        )
    }

    fun updateIcsReference() = viewModelScope.launch(Dispatchers.Default) {
        val path = emseAuthService.findIcs()
        icsReferenceRepository.update(username.value, path)
    }

    private val _login = MutableStateFlow<State<String>?>(null)
    val login: StateFlow<State<String>?>
        get() = _login

    fun doLogin() = viewModelScope.launch {
        _login.value = State.Loading()
        _login.value = tryOrCatch { emseAuthService.loginForIcs(username.value, password.value) }
    }

    fun loginFinished() {
        _login.value = null
    }

    val events: StateFlow<List<IcsEvent>> =
        icsEventRepository.watchEventsAfterNow()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _download = MutableStateFlow<State<Unit>?>(null)
    val download: StateFlow<State<Unit>?>
        get() = _download

    private fun doDownload(path: String) = viewModelScope.launch {
        _download.value = tryOrCatch {
            icsDownloader.download(path)
        }
    }

    fun downloadFinished() {
        _download.value = null
    }
}
