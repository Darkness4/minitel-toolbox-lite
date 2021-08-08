package com.minitel.toolboxlite.presentation.utils

import com.minitel.toolboxlite.domain.entities.calendar.IcsEvent
import com.minitel.toolboxlite.presentation.adapters.DayListAdapter
import com.minitel.toolboxlite.presentation.adapters.MonthListAdapter
import org.threeten.bp.LocalDate
import org.threeten.bp.Month

fun List<IcsEvent>.toDayDataList(): List<DayListAdapter.DayData> {
    val dayDatas = mutableMapOf<LocalDate, DayListAdapter.DayData.Builder>()

    map { event ->
        val date = event.dtstart.toLocalDate()
        if (dayDatas[date] == null) {
            dayDatas[date] = DayListAdapter.DayData.Builder().apply {
                this.date = date
            }
        }

        dayDatas[date]!!.events.add(event)
    }

    return dayDatas.values.map { it.build() }
}

fun List<DayListAdapter.DayData>.toMonthDataList(): List<MonthListAdapter.MonthData> {
    val monthDatas = mutableMapOf<Pair<Month, Int>, MonthListAdapter.MonthData.Builder>()

    map { dateData ->
        if (monthDatas[Pair(dateData.dtstart.month, dateData.dtstart.year)] == null) {
            monthDatas[Pair(dateData.dtstart.month, dateData.dtstart.year)] = MonthListAdapter.MonthData.Builder().apply {
                month = dateData.dtstart.month
                year = dateData.dtstart.year
            }
        }

        monthDatas[Pair(dateData.dtstart.month, dateData.dtstart.year)]!!.days.add(dateData)
    }

    return monthDatas.values.map { it.build() }
}

@JvmName("icsEventsToMonthDataList")
fun List<IcsEvent>.toMonthDataList(): List<MonthListAdapter.MonthData> {
    return this.toDayDataList().toMonthDataList()
}
