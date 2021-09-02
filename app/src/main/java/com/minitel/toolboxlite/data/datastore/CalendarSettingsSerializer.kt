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
object CalendarSettingsSerializer : Serializer<CalendarSettings> {
    override val defaultValue: CalendarSettings =
        CalendarSettings.newBuilder()
            .setEarlyMinutes(5L)
            .build()

    override suspend fun readFrom(input: InputStream): CalendarSettings {
        return try {
            withContext(Dispatchers.IO) {
                CalendarSettings.parseFrom(input)
            }
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: CalendarSettings, output: OutputStream) {
        withContext(Dispatchers.IO) {
            t.writeTo(output)
        }
    }
}

val Context.calendarSettingsDataStore: DataStore<CalendarSettings> by dataStore(
    fileName = "calendarsettings.pb",
    serializer = CalendarSettingsSerializer
)
