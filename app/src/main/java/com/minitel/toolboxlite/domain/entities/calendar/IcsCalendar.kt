package com.minitel.toolboxlite.domain.entities.calendar

import android.os.Parcelable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.parcelize.Parcelize

private enum class ICalSection {
    None, VEVENT, VTIMEZONE, STANDARD, DAYLIGHT
}

@Parcelize
data class IcsCalendar(
    val version: String,
    val prodId: String,
    val calscale: String,
    val timezone: IcsTimezone,
    val events: List<IcsEvent>
) : Parcelable {
    private constructor(builder: Builder) : this(
        version = builder.version,
        prodId = builder.prodId,
        calscale = builder.calscale,
        timezone = builder.timezone.build(),
        events = builder.events
    )

    companion object {
        inline fun build(block: Builder.() -> Unit) = Builder().apply(block).build()
    }

    class Builder {
        var version: String = ""
        var prodId: String = ""
        var calscale: String = ""
        val timezone = IcsTimezone.Builder()
        val events: MutableList<IcsEvent> = mutableListOf()

        fun build() = IcsCalendar(this)

        suspend fun fromLineFlow(lines: Flow<String>) = apply {
            var mode: ICalSection = ICalSection.None

            var icsEventBuilder: IcsEvent.Builder? = null

            lines.collect { data ->
                val line = data.trim().split(':')

                // State switcher
                if (line.size == 2) {
                    val (key, value) = line
                    when {
                        key == "BEGIN" -> {
                            if (value == "VEVENT") {
                                icsEventBuilder = IcsEvent.Builder()
                            }
                            try {
                                mode = ICalSection.valueOf(value)
                            } catch (e: IllegalArgumentException) {
                            }
                            return@collect
                        }
                        key == "END" && value == "VEVENT" -> {
                            icsEventBuilder?.build()?.let(events::add)
                            mode = ICalSection.None
                            return@collect
                        }
                        key == "END" && value == "VTIMEZONE" -> {
                            mode = ICalSection.None
                            return@collect
                        }
                        key == "END" && value == "STANDARD" -> {
                            mode = ICalSection.VTIMEZONE
                            return@collect
                        }
                        key == "END" && value == "DAYLIGHT" -> {
                            mode = ICalSection.VTIMEZONE
                            return@collect
                        }
                    }
                }

                // Parser
                if (line.size >= 2) {
                    val (key, value) = line
                    when (mode) {
                        ICalSection.VEVENT -> icsEventBuilder?.set(key, value)
                        ICalSection.VTIMEZONE -> timezone[key] = value
                        ICalSection.STANDARD -> timezone.standard[key] = value
                        ICalSection.DAYLIGHT -> timezone.daylight[key] = value
                        ICalSection.None -> this[key] = value
                    }
                }
            }
        }

        operator fun set(key: String, value: String) {
            when (key) {
                "VERSION" -> version = value
                "PROID" -> prodId = value
                "CALSCALE" -> calscale = value
            }
        }
    }
}
