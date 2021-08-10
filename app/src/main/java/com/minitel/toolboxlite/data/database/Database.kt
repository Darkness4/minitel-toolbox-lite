package com.minitel.toolboxlite.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.minitel.toolboxlite.data.database.converters.LocalDateTimeConverters
import com.minitel.toolboxlite.data.database.converters.MapConverters
import com.minitel.toolboxlite.data.models.CookieModel
import com.minitel.toolboxlite.data.models.IcsEventModel

@Database(entities = [IcsEventModel::class, CookieModel::class], version = 1)
@TypeConverters(LocalDateTimeConverters::class, MapConverters::class)
abstract class Database : RoomDatabase() {
    abstract fun icsEventDao(): IcsEventDao
    abstract fun cookieDao(): CookieDao
}
