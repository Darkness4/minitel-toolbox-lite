package com.minitel.toolboxlite.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minitel.toolboxlite.domain.entities.calendar.IcsEvent
import com.minitel.toolboxlite.domain.repositories.IcsEventRepository
import com.minitel.toolboxlite.presentation.adapters.MonthListAdapter
import com.minitel.toolboxlite.presentation.utils.toMonthDataList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.threeten.bp.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    icsEventRepository: IcsEventRepository,
) : ViewModel() {
    val events: StateFlow<List<IcsEvent>> = icsEventRepository.watchEvents().map { list ->
        val now = LocalDateTime.now()
        list.filter { it.dtstart < now && it.dtend < now }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val months: StateFlow<List<MonthListAdapter.MonthData>> = events.map { it.toMonthDataList() }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}
