package com.minitel.toolboxlite.presentation.utils

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.minitel.toolboxlite.R
import com.minitel.toolboxlite.domain.entities.calendar.IcsEvent
import com.minitel.toolboxlite.presentation.adapters.DayListAdapter
import com.minitel.toolboxlite.presentation.adapters.EventListAdapter
import com.minitel.toolboxlite.presentation.adapters.MonthListAdapter
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

@BindingAdapter("months")
fun bindMonthList(
    view: RecyclerView,
    months: List<MonthListAdapter.MonthData>?,
) {
    val adapter = view.adapter as MonthListAdapter
    months?.let {
        adapter.submitList(months.sortedWith(compareBy({ it.year }, { it.month })))
    }
}

@BindingAdapter("showWhenMonthsIsEmpty")
fun showWhenMonthsIsEmpty(
    view: View,
    months: List<MonthListAdapter.MonthData>?,
) {
    view.visibility = if (months.isNullOrEmpty()) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

@BindingAdapter("days")
fun bindDayList(
    view: RecyclerView,
    days: List<DayListAdapter.DayData>?,
) {
    val adapter = view.adapter as DayListAdapter
    days?.let {
        adapter.submitList(days.sortedBy { it.dtstart })
    }
}

@BindingAdapter("events")
fun bindEventList(
    view: RecyclerView,
    events: List<IcsEvent>?,
) {
    val adapter = view.adapter as EventListAdapter
    events?.let {
        adapter.submitList(events.sortedWith(compareBy({ it.dtstart }, { it.dtend })))
    }
}
