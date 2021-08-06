package com.minitel.toolboxlite.domain.services

import kotlinx.coroutines.flow.Flow
import okhttp3.Cookie

interface EmseAuthService {
    suspend fun login(username: String, password: String)
    fun watchCookie(): Flow<Cookie>
}
