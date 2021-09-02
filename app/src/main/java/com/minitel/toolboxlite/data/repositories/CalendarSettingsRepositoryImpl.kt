package com.minitel.toolboxlite.data.repositories

import androidx.datastore.core.DataStore
import com.minitel.toolboxlite.data.datastore.CalendarSettings
import com.minitel.toolboxlite.domain.repositories.CalendarSettingsRepository
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject

class CalendarSettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<CalendarSettings>,
) : CalendarSettingsRepository {
    override suspend fun update(earlyMinutes: Long) {
        val newValue = CalendarSettings.newBuilder()
            .setEarlyMinutes(earlyMinutes)
            .build()
        Timber.d("Updated CalendarSettings $newValue.")
        dataStore.updateData { newValue }
    }

    override fun watch(): Flow<CalendarSettings> = dataStore.data
}
