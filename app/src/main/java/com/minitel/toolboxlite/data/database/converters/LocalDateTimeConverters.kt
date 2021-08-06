package com.minitel.toolboxlite.data.database.converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import org.threeten.bp.LocalDateTime

/** Converters for `LocalDateTime` to `String` to be able to store `LocalDateTime` in `Room`. */
@ProvidedTypeConverter
class LocalDateTimeConverters {
    @TypeConverter
    fun fromString(value: String): LocalDateTime {
        return LocalDateTime.parse(value)
    }

    @TypeConverter
    fun toString(value: LocalDateTime): String {
        return value.toString()
    }
}
