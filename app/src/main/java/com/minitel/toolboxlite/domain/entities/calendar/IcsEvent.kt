package com.minitel.toolboxlite.domain.entities.calendar

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.threeten.bp.LocalDateTime

@Parcelize
data class IcsEvent(
    val dtend: LocalDateTime,
    val uid: String,
    val dtstamp: LocalDateTime,
    val location: String,
    val description: String,
    val summary: String,
    val dtstart: LocalDateTime,
) : Parcelable
