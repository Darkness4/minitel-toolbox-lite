package com.minitel.toolboxlite.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.ConfigurationCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.minitel.toolboxlite.databinding.ItemDayBinding
import com.minitel.toolboxlite.domain.entities.calendar.IcsEvent
import org.threeten.bp.LocalDate
import org.threeten.bp.format.TextStyle

class DayListAdapter : ListAdapter<DayListAdapter.DayData, DayListAdapter.ViewHolder>(Comparator) {
    object Comparator : DiffUtil.ItemCallback<DayData>() {
        override fun areItemsTheSame(oldItem: DayData, newItem: DayData) =
            oldItem.dtstart == newItem.dtstart

        override fun areContentsTheSame(oldItem: DayData, newItem: DayData) = oldItem == newItem
    }

    data class DayData(val dtstart: LocalDate, val events: List<IcsEvent>) {
        class Builder {
            var date: LocalDate? = null
            var events = mutableListOf<IcsEvent>()

            fun build() = DayData(date!!, events)
        }
    }

    class ViewHolder(private val binding: ItemDayBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(dayData: DayData) {
            binding.dayData = dayData
            val locale = ConfigurationCompat.getLocales(itemView.resources.configuration)[0]
            binding.dayOfWeekFormatted =
                dayData.dtstart.dayOfWeek.getDisplayName(TextStyle.SHORT, locale)
            binding.executePendingBindings()
        }

        companion object {
            fun create(parent: ViewGroup): ViewHolder {
                val binding = ItemDayBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                binding.recyclerViewEvents.adapter = EventListAdapter()
                return ViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder.create(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        item?.let { holder.bind(it) }
    }
}
