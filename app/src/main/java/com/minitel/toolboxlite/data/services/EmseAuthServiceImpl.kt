package com.minitel.toolboxlite.data.services

import android.content.Context
import com.minitel.toolboxlite.data.datastore.portailEmseCookieDataStore
import com.minitel.toolboxlite.data.datastore.toCookie
import com.minitel.toolboxlite.domain.services.EmseAuthService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.Cookie
import javax.inject.Inject

class EmseAuthServiceImpl @Inject constructor(@ApplicationContext private val context: Context) :
    EmseAuthService {
    /** Login CAS Emse Portal and store the cookie. */
    override suspend fun login(username: String, password: String) {
        TODO("Not yet implemented")
    }

    /** Fetch the cookie for Emse Portal from cache */
    override fun watchCookie(): Flow<Cookie> = context.portailEmseCookieDataStore.data.map {
        it.toCookie()
    }
}
