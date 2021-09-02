package com.minitel.toolboxlite.domain.repositories

import com.minitel.toolboxlite.data.datastore.LoginSettings
import kotlinx.coroutines.flow.Flow

interface LoginSettingsRepository {
    suspend fun update(
        rememberMe: Boolean,
        username: String = "",
        password: String = ""
    )

    fun watch(): Flow<LoginSettings>
}
