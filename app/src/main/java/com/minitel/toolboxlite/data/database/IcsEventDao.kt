package com.minitel.toolboxlite.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.minitel.toolboxlite.data.models.IcsEventModel
import kotlinx.coroutines.flow.Flow

/** Cache for storing the `IcsEventModel`. */
@Dao
interface IcsEventDao {
    /** Observe the whole cache */
    @Query("SELECT * FROM ics_events ORDER BY datetime(dtstart), datetime(dtend)")
    fun watch(): Flow<List<IcsEventModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg items: IcsEventModel)

    @Query("DELETE FROM ics_events")
    suspend fun clear()
}
