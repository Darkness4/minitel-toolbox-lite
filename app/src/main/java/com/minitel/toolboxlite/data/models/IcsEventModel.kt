package com.minitel.toolboxlite.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.minitel.toolboxlite.domain.entities.calendar.IcsEvent
import org.threeten.bp.LocalDateTime

@Entity(tableName = "ics_events")
data class IcsEventModel(
    val dtend: LocalDateTime,
    @PrimaryKey val uid: String,
    val dtstamp: LocalDateTime,
    val location: String,
    val description: String,
    val summary: String,
    val dtstart: LocalDateTime,
) {
    companion object {
        @JvmStatic
        fun fromEntity(e: IcsEvent) = IcsEventModel(
            dtend = e.dtend,
            uid = e.uid,
            dtstamp = e.dtstamp,
            location = e.location,
            description = e.description,
            summary = e.summary,
            dtstart = e.dtstart,
        )
    }

    fun asEntity() = IcsEvent(
        dtend = dtend,
        uid = uid,
        dtstamp = dtstamp,
        location = location,
        description = description,
        summary = summary,
        dtstart = dtstart,
    )
}
