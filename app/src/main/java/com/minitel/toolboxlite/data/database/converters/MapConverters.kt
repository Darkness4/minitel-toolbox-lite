package com.minitel.toolboxlite.data.database.converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/** Converters for `Map` to be able to store `Map` in `Room`. */
@ProvidedTypeConverter
class MapConverters private constructor(private val json: Json) {
    @ExperimentalSerializationApi
    @TypeConverter
    fun fromString(value: String): Map<String, String?> {
        return json.decodeFromString(value)
    }

    @ExperimentalSerializationApi
    @TypeConverter
    fun fromMap(map: Map<String, String?>): String {
        return json.encodeToString(map)
    }

    companion object {
        fun create(json: Json): MapConverters {
            return MapConverters(json)
        }
    }
}
