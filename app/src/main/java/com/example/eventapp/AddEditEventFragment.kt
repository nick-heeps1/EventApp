package com.example.eventapp.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.eventapp.data.Event
import com.example.eventapp.databinding.FragmentAddEditEventBinding
import com.example.eventapp.viewmodel.EventViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// Fragment handles both creating and editing events
class AddEditEventFragment : Fragment() {

    private var binding: FragmentAddEditEventBinding? = null
    private val viewModel: EventViewModel by activityViewModels()
    private val args: AddEditEventFragmentArgs by navArgs()

    // Tracks the user's chosen date and time
    private var myCalendar: Calendar = Calendar.getInstance()
    // Prevents saving if no date has been chosen
    private var dateSelected = false
    // Holds existing event data when editing, null when creating
    private var currentEvent: Event? = null

    // Inflates the layout and returns the root view
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddEditEventBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Populate the category spinner with predefined options
        val categories = arrayOf("Work", "Social", "Travel", "Health", "Personal", "Other")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding!!.spinnerCategory.adapter = spinnerAdapter

        // eventId of -1 means we're creating a new event
        if (args.eventId != -1) {
            binding!!.tvFormTitle.text = "Edit Event"

            // Fetch the existing event asynchronously from the database
            lifecycleScope.launch {
                currentEvent = viewModel.getEventById(args.eventId)

                if (currentEvent != null) {
                    // Pre-fill all fields with the existing event's data
                    binding!!.etTitle.setText(currentEvent!!.title)
                    binding!!.etLocation.setText(currentEvent!!.location)

                    // Find and select the matching category in the spinner
                    val catList = categories.toList()
                    val index = catList.indexOf(currentEvent!!.category)
                    if (index >= 0) {
                        binding!!.spinnerCategory.setSelection(index)
                    }

                    // Restore the calendar to the event's saved timestamp
                    myCalendar.timeInMillis = currentEvent!!.dateTimeMillis
                    dateSelected = true
                    showDateTime()
                }
            }
        } else {
            binding!!.tvFormTitle.text = "New Event"
        }

        // Wire up buttons to their respective picker and save actions
        binding!!.btnPickDate.setOnClickListener {
            pickDate()
        }

        binding!!.btnPickTime.setOnClickListener {
            pickTime()
        }

        binding!!.btnSave.setOnClickListener {
            saveEvent()
        }

        // Cancel discards changes and returns to the previous screen
        binding!!.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun pickDate() {
        // Start the picker at the currently selected calendar date
        val year = myCalendar.get(Calendar.YEAR)
        val month = myCalendar.get(Calendar.MONTH)
        val day = myCalendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(requireContext(), { _, y, m, d ->
            // Update calendar with the user's chosen date
            myCalendar.set(Calendar.YEAR, y)
            myCalendar.set(Calendar.MONTH, m)
            myCalendar.set(Calendar.DAY_OF_MONTH, d)
            dateSelected = true
            showDateTime()
        }, year, month, day)

        // Block past dates only when creating a brand new event
        if (currentEvent == null) {
            datePicker.datePicker.minDate = System.currentTimeMillis()
        }

        datePicker.show()
    }

    private fun pickTime() {
        // Start the picker at the currently selected calendar time
        val hour = myCalendar.get(Calendar.HOUR_OF_DAY)
        val minute = myCalendar.get(Calendar.MINUTE)

        val timePicker = TimePickerDialog(requireContext(), { _, h, min ->
            // Update calendar with the user's chosen time
            myCalendar.set(Calendar.HOUR_OF_DAY, h)
            myCalendar.set(Calendar.MINUTE, min)
            showDateTime()
        }, hour, minute, false)

        timePicker.show()
    }

    // Formats and displays the selected date and time on screen
    private fun showDateTime() {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        binding!!.tvSelectedDateTime.text = sdf.format(myCalendar.time)
    }

    private fun saveEvent() {
        val title = binding!!.etTitle.text.toString().trim()
        val location = binding!!.etLocation.text.toString().trim()
        val category = binding!!.spinnerCategory.selectedItem.toString()

        // Reject save if the title field is empty
        if (title.isEmpty()) {
            binding!!.tilTitle.error = "Please enter a title"
            Toast.makeText(requireContext(), "Title is required", Toast.LENGTH_SHORT).show()
            return
        }

        // Clear any previously shown title error
        binding!!.tilTitle.error = null

        // Reject save if the user never picked a date
        if (!dateSelected) {
            Toast.makeText(requireContext(), "Please pick a date and time", Toast.LENGTH_SHORT).show()
            return
        }

        val millis = myCalendar.timeInMillis

        // New events must not be scheduled in the past
        if (currentEvent == null && millis < System.currentTimeMillis()) {
            Snackbar.make(binding!!.root, "Date cannot be in the past!", Snackbar.LENGTH_LONG).show()
            return
        }

        if (currentEvent != null) {
            // Overwrite existing event fields and persist via ViewModel
            currentEvent!!.title = title
            currentEvent!!.category = category
            currentEvent!!.location = location
            currentEvent!!.dateTimeMillis = millis
            viewModel.updateEvent(currentEvent!!)
            Toast.makeText(requireContext(), "Event updated!", Toast.LENGTH_SHORT).show()
        } else {
            // Build a new Event object and save it
            val newEvent = Event(
                title = title,
                category = category,
                location = location,
                dateTimeMillis = millis
            )
            viewModel.addEvent(newEvent)
            Toast.makeText(requireContext(), "Event saved!", Toast.LENGTH_SHORT).show()
        }

        // Navigate back after successfully saving the event
        findNavController().popBackStack()
    }

    // Release binding reference to avoid memory leaks
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}