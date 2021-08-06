package com.minitel.toolboxlite.domain.entities.calendar

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class IcsCalendar(
    val version: String,
    val prodId: String,
    val calscale: String,
    val timezone: IcsTimezone,
    val events: List<IcsEvent>
) : Parcelable
