package com.minitel.toolboxlite.data.models

import androidx.room.Entity
import io.ktor.http.Cookie
import io.ktor.http.CookieEncoding
import io.ktor.util.date.GMTDate

@Entity(
    tableName = "cookies",
    primaryKeys = [
        "name", "value", "encoding", "maxAge", "secure", "httpOnly"
    ]
)
data class CookieModel(
    val name: String,
    val value: String,
    val encoding: String,
    val maxAge: Int = 0,
    val expires: Long? = null,
    val domain: String? = null,
    val path: String? = null,
    val secure: Boolean = false,
    val httpOnly: Boolean = false,
    val extensions: Map<String, String?> = emptyMap()
) {
    companion object {
        fun fromKtor(cookie: Cookie): CookieModel = CookieModel(
            name = cookie.name,
            value = cookie.value,
            encoding = cookie.encoding.toString(),
            maxAge = cookie.maxAge,
            expires = cookie.expires?.timestamp,
            domain = cookie.domain,
            path = cookie.path,
            secure = cookie.secure,
            httpOnly = cookie.httpOnly,
            extensions = cookie.extensions
        )
    }

    fun toKtor(): Cookie = Cookie(
        name = name,
        value = value,
        encoding = CookieEncoding.valueOf(encoding),
        maxAge = maxAge,
        expires = expires?.let { GMTDate(expires) },
        domain = domain,
        path = path,
        secure = secure,
        httpOnly = httpOnly,
        extensions = extensions
    )
}
