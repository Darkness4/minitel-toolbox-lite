package com.minitel.toolboxlite.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.minitel.toolboxlite.databinding.ItemMonthBinding
import org.threeten.bp.Month

class MonthListAdapter :
    ListAdapter<MonthListAdapter.MonthData, MonthListAdapter.ViewHolder>(Comparator) {
    object Comparator : DiffUtil.ItemCallback<MonthData>() {
        override fun areItemsTheSame(oldItem: MonthData, newItem: MonthData) =
            oldItem.month == newItem.month && oldItem.year == oldItem.year

        override fun areContentsTheSame(oldItem: MonthData, newItem: MonthData) = oldItem == newItem
    }

    data class MonthData(val month: Month, val year: Int, val days: List<DayListAdapter.DayData>) {
        class Builder {
            var month: Month? = null
            var year: Int? = null
            var days = mutableListOf<DayListAdapter.DayData>()

            fun build() = MonthData(month!!, year!!, days)
        }
    }

    class ViewHolder(private val binding: ItemMonthBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(monthData: MonthData) {
            binding.monthData = monthData
            binding.executePendingBindings()
        }

        companion object {
            fun create(parent: ViewGroup): ViewHolder {
                val binding = ItemMonthBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                binding.recyclerViewDays.adapter = DayListAdapter()
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
