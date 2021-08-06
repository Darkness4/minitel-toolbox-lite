package com.minitel.toolboxlite.domain.entities.calendar

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.threeten.bp.LocalDateTime

@Parcelize
data class IcsTimezoneDescription(
    val dtstart: LocalDateTime,
    val tzOffsetTo: String,
    val tzOffsetFrom: String,
    val rRule: String,
    val tzName: String
) : Parcelable
