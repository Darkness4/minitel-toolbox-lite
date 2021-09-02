package com.minitel.toolboxlite.domain.repositories

interface CalendarSettingsRepository {
    suspend fun update(earlyMinutes: Long = 5)
}
