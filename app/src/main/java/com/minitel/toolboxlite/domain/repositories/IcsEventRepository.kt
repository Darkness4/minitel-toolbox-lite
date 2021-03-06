package com.minitel.toolboxlite.domain.repositories

import com.minitel.toolboxlite.domain.entities.calendar.IcsEvent
import kotlinx.coroutines.flow.Flow

interface IcsEventRepository {
    fun watchEventsAfterNow(): Flow<List<IcsEvent>>
}
