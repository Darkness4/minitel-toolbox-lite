package com.minitel.toolboxlite.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.minitel.toolboxlite.data.models.CookieModel
import kotlinx.coroutines.flow.Flow

/** Cache for storing the `CookieModel`. */
@Dao
interface CookieDao {
    /** Observe the whole cache */
    @Query("SELECT * FROM cookies")
    suspend fun get(): List<CookieModel>

    @Query("SELECT * FROM cookies")
    fun getFlow(): Flow<List<CookieModel>>

    @Query("DELETE FROM cookies WHERE expires <> NULL AND expires < :timestamp")
    suspend fun cleanup(timestamp: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg items: CookieModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(items: List<CookieModel>)

    @Query("DELETE FROM cookies")
    suspend fun clear()
}
