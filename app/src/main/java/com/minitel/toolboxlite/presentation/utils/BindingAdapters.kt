package com.minitel.toolboxlite.presentation.utils

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.minitel.toolboxlite.R
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

@BindingAdapter("dtstart", "dtend")
fun formatDateTimeInterval(
    view: TextView,
    dtstart: LocalDateTime,
    dtend: LocalDateTime,
) {
    view.text = view.context.getString(R.string.event_time_format).format(
        dtstart.format(DateTimeFormatter.ofPattern("HH:mm")),
        dtend.format(DateTimeFormatter.ofPattern("HH:mm"))
    )
}
