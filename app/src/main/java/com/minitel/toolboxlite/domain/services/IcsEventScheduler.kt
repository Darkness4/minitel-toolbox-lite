package com.minitel.toolboxlite.domain.services

import com.minitel.toolboxlite.domain.entities.calendar.IcsEvent

interface IcsEventScheduler {
    fun schedule(icsEvent: IcsEvent, earlyMinutes: Long = 5)
}
