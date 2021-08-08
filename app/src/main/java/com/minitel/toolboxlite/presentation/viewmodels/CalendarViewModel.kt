package com.minitel.toolboxlite.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minitel.toolboxlite.core.state.State
import com.minitel.toolboxlite.core.state.tryOrCatch
import com.minitel.toolboxlite.domain.repositories.IcsEventRepository
import com.minitel.toolboxlite.domain.services.EmseAuthService
import com.minitel.toolboxlite.domain.services.IcsDownloader
import com.minitel.toolboxlite.presentation.utils.toMonthDataList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val emseAuthService: EmseAuthService,
    private val icsDownloader: IcsDownloader,
    icsEventRepository: IcsEventRepository
) : ViewModel() {
    val months = icsEventRepository.watchEvents().map {
        it.toMonthDataList()
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _download = MutableStateFlow<State<Unit>?>(null)
    val download: StateFlow<State<Unit>?>
        get() = _download

    init {
        doDownload()
    }

    private fun doDownload() = viewModelScope.launch(Dispatchers.Default) {
        _download.value = tryOrCatch {
            val path = emseAuthService.findIcs()
            icsDownloader.download(path)
        }
    }

    fun downloadFinished() {
        _download.value = null
    }
}
