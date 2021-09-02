package com.minitel.toolboxlite.data.repositories

import androidx.datastore.core.DataStore
import com.minitel.toolboxlite.data.datastore.Credentials
import com.minitel.toolboxlite.data.datastore.LoginSettings
import com.minitel.toolboxlite.domain.repositories.LoginSettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginSettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<LoginSettings>,
) : LoginSettingsRepository {
    override suspend fun update(
        rememberMe: Boolean,
        username: String,
        password: String
    ) {
        if (rememberMe) {
            dataStore.updateData {
                LoginSettings.newBuilder()
                    .setRememberMe(true)
                    .setCredentials(
                        Credentials.newBuilder()
                            .setUsername(username)
                            .setPassword(password)
                    )
                    .build()
            }
        } else {
            dataStore.updateData {
                LoginSettings.newBuilder()
                    .setRememberMe(false)
                    .build()
            }
        }
    }

    override fun watch(): Flow<LoginSettings> = dataStore.data
}
