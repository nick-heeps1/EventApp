package com.example.eventapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.eventapp.R
import com.example.eventapp.databinding.FragmentEventListBinding
import com.example.eventapp.viewmodel.EventViewModel
import com.google.android.material.snackbar.Snackbar

class EventListFragment : Fragment() {

    private var binding: FragmentEventListBinding? = null
    private val viewModel: EventViewModel by activityViewModels()
    private lateinit var adapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEventListBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // set up the adapter with empty list first
        adapter = EventAdapter(
            emptyList(),
            onEditClick = { event ->
                // navigate to edit screen and pass the event id
                val action = EventListFragmentDirections
                    .actionEventListFragmentToAddEditEventFragment(event.id)
                findNavController().navigate(action)
            },
            onDeleteClick = { event ->
                viewModel.deleteEvent(event)
                Snackbar.make(binding!!.root, "Event deleted", Snackbar.LENGTH_SHORT).show()
            }
        )

        binding!!.recyclerView.adapter = adapter

        // observe events and update the list
        viewModel.allEvents.observe(viewLifecycleOwner) { events ->
            adapter.updateEvents(events)

            // show empty message if no events
            if (events.isEmpty()) {
                binding!!.tvEmpty.visibility = View.VISIBLE
            } else {
                binding!!.tvEmpty.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}