package com.minitel.toolboxlite.domain.services

import com.minitel.toolboxlite.core.errors.CannotFetchIcs
import com.minitel.toolboxlite.core.errors.FailedLogin

interface EmseAuthService {
    @Throws(FailedLogin::class)
    suspend fun login(username: String, password: String, service: String): String

    @Throws(FailedLogin::class)
    suspend fun loginForIcs(username: String, password: String): String

    @Throws(CannotFetchIcs::class)
    suspend fun findIcs(): String
}
