package com.example.eventapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.eventapp.data.Event
import com.example.eventapp.databinding.ItemEventBinding
import java.text.SimpleDateFormat
import java.util.*

class EventAdapter(
    private var eventList: List<Event>,
    private val onEditClick: (Event) -> Unit,
    private val onDeleteClick: (Event) -> Unit
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    fun updateEvents(newList: List<Event>) {
        eventList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = eventList[position]
        holder.bind(event)
    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    inner class EventViewHolder(private val binding: ItemEventBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(event: Event) {
            val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            val dateString = sdf.format(Date(event.dateTimeMillis))

            binding.tvTitle.text = event.title
            binding.tvCategory.text = event.category
            binding.tvDateTime.text = dateString

            // show "No location" if empty
            if (event.location == "") {
                binding.tvLocation.text = "No location"
            } else {
                binding.tvLocation.text = event.location
            }

            binding.btnEdit.setOnClickListener {
                onEditClick(event)
            }

            binding.btnDelete.setOnClickListener {
                onDeleteClick(event)
            }
        }
    }
}