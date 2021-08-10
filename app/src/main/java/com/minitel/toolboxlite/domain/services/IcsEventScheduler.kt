package com.minitel.toolboxlite.domain.services

import android.content.Context
import com.minitel.toolboxlite.domain.entities.calendar.IcsEvent

interface IcsEventScheduler {
    fun schedule(context: Context, icsEvent: IcsEvent, earlyMinutes: Int = 5)
}
