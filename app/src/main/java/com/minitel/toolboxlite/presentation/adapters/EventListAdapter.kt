package com.minitel.toolboxlite.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.minitel.toolboxlite.databinding.ItemEventBinding
import com.minitel.toolboxlite.domain.entities.calendar.IcsEvent

class EventListAdapter : ListAdapter<IcsEvent, EventListAdapter.ViewHolder>(Comparator) {
    object Comparator : DiffUtil.ItemCallback<IcsEvent>() {
        override fun areItemsTheSame(oldItem: IcsEvent, newItem: IcsEvent) =
            oldItem.uid == newItem.uid

        override fun areContentsTheSame(oldItem: IcsEvent, newItem: IcsEvent) = oldItem == newItem
    }

    class ViewHolder(private val binding: ItemEventBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(event: IcsEvent) {
            binding.event = event
            binding.executePendingBindings()
        }

        companion object {
            fun create(parent: ViewGroup) =
                ViewHolder(
                    ItemEventBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder.create(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        item?.let { holder.bind(it) }
    }
}
