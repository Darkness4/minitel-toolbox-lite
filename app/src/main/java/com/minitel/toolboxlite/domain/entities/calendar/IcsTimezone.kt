package com.minitel.toolboxlite.domain.entities.calendar

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.TimeZone

@Parcelize
data class IcsTimezone(
    val tzid: TimeZone,
    val daylight: IcsTimezoneDescription,
    val standard: IcsTimezoneDescription,
) : Parcelable
