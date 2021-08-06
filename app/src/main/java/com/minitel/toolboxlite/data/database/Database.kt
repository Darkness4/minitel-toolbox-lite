package com.minitel.toolboxlite.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.minitel.toolboxlite.data.database.converters.LocalDateTimeConverters
import com.minitel.toolboxlite.data.models.IcsEventModel

@Database(entities = [IcsEventModel::class], version = 1)
@TypeConverters(LocalDateTimeConverters::class)
abstract class Database : RoomDatabase() {
    abstract fun icsEventDao(): IcsEventDao
}
