package com.minitel.toolboxlite.data.repositories

import androidx.datastore.core.DataStore
import com.minitel.toolboxlite.data.datastore.CalendarSettings
import com.minitel.toolboxlite.domain.repositories.CalendarSettingsRepository
import javax.inject.Inject

class CalendarSettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<CalendarSettings>,
) : CalendarSettingsRepository {
    override suspend fun update(earlyMinutes: Long) {
        dataStore.updateData {
            CalendarSettings.newBuilder()
                .setEarlyMinutes(earlyMinutes)
                .build()
        }
    }
}
