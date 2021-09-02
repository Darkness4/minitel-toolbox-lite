package com.minitel.toolboxlite.data.datastore

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.protobuf.InvalidProtocolBufferException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream

@Suppress("BlockingMethodInNonBlockingContext")
object LoginSettingsSerializer : Serializer<LoginSettings> {
    override val defaultValue: LoginSettings = LoginSettings.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): LoginSettings {
        return try {
            withContext(Dispatchers.IO) {
                LoginSettings.parseFrom(input)
            }
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: LoginSettings, output: OutputStream) {
        withContext(Dispatchers.IO) {
            t.writeTo(output)
        }
    }
}

val Context.loginSettingsDataStore: DataStore<LoginSettings> by dataStore(
    fileName = "loginsettings.pb",
    serializer = LoginSettingsSerializer
)
