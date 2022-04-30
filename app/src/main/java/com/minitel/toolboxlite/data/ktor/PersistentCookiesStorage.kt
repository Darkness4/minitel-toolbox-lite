package com.minitel.toolboxlite.data.ktor

import com.minitel.toolboxlite.data.database.CookieDao
import com.minitel.toolboxlite.data.models.CookieModel
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.http.Cookie
import io.ktor.http.Url
import io.ktor.util.date.GMTDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PersistentCookiesStorage @Inject constructor(private val cookieDao: CookieDao) :
    CookiesStorage {

    override suspend fun addCookie(requestUrl: Url, cookie: Cookie) {
        with(cookie) {
            if (name.isBlank()) return
        }

        val cookies = cookieDao.get()
            .map { it.toKtor() }
            .filter { !(it.name == cookie.name && it.matches(requestUrl)) }
            .map { CookieModel.fromKtor(it) }
        cookieDao.clear()
        cookieDao.insert(cookies)
        cookieDao.insert(CookieModel.fromKtor(cookie.fillDefaults(requestUrl)))
    }

    override fun close() = Unit

    override suspend fun get(requestUrl: Url): List<Cookie> {
        val date = GMTDate()
        cookieDao.cleanup(date.timestamp)

        return cookieDao.get().map { it.toKtor() }.filter { it.matches(requestUrl) }
    }

    fun getFlow(requestUrl: Url): Flow<List<Cookie>> {
        return cookieDao.getFlow().map { list ->
            val date = GMTDate()
            list.map { it.toKtor() }.filter {
                it.matches(requestUrl) && (it.expires == null || it.expires!! > date)
            }
        }
    }

    suspend fun clear(requestUrl: Url) {
        val cookies = cookieDao.get()
            .map { it.toKtor() }
            .filter { !(it.matches(requestUrl)) }
            .map { CookieModel.fromKtor(it) }
        cookieDao.clear()
        cookieDao.insert(cookies)
    }
}
