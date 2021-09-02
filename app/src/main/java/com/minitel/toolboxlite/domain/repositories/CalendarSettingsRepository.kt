package com.minitel.toolboxlite.domain.repositories

import com.minitel.toolboxlite.data.datastore.CalendarSettings
import kotlinx.coroutines.flow.Flow

interface CalendarSettingsRepository {
    suspend fun update(earlyMinutes: Long = 5)
    fun watch(): Flow<CalendarSettings>
}
