package com.minitel.toolboxlite.data.datastore

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.protobuf.InvalidProtocolBufferException
import okhttp3.Cookie
import java.io.InputStream
import java.io.OutputStream

object PortailEmseCookieSerializer : Serializer<PortailEmseCookie> {
    override val defaultValue: PortailEmseCookie = PortailEmseCookie.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): PortailEmseCookie {
        try {
            return PortailEmseCookie.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(
        t: PortailEmseCookie,
        output: OutputStream
    ) = t.writeTo(output)
}

val Context.portailEmseCookieDataStore: DataStore<PortailEmseCookie> by dataStore(
    fileName = "cookies.pb",
    serializer = PortailEmseCookieSerializer
)

suspend fun DataStore<PortailEmseCookie>.storeEmseCookie(cookie: Cookie) {
    updateData { cookie.toPortailEmseCookie() }
}

fun Cookie.toPortailEmseCookie(): PortailEmseCookie = PortailEmseCookie.newBuilder()
    .setName(name)
    .setValue(value)
    .setPath(path)
    .setExpires(expiresAt)
    .setSecure(secure)
    .build()

fun PortailEmseCookie.toCookie(): Cookie {
    val builder = Cookie.Builder()
        .name(name)
        .value(value)
        .path(path)
        .expiresAt(expires)

    if (secure) builder.secure()

    return builder.build()
}
