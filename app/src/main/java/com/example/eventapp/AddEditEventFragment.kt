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

class AddEditEventFragment : Fragment() {

    private var binding: FragmentAddEditEventBinding? = null
    private val viewModel: EventViewModel by activityViewModels()
    private val args: AddEditEventFragmentArgs by navArgs()

    // calendar to keep track of the selected date
    private var myCalendar: Calendar = Calendar.getInstance()
    private var dateSelected = false
    private var currentEvent: Event? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddEditEventBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // set up the category dropdown
        val categories = arrayOf("Work", "Social", "Travel", "Health", "Personal", "Other")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding!!.spinnerCategory.adapter = spinnerAdapter

        // check if we are editing or adding
        if (args.eventId != -1) {
            binding!!.tvFormTitle.text = "Edit Event"

            // load the existing event data
            lifecycleScope.launch {
                currentEvent = viewModel.getEventById(args.eventId)

                if (currentEvent != null) {
                    binding!!.etTitle.setText(currentEvent!!.title)
                    binding!!.etLocation.setText(currentEvent!!.location)

                    // set the correct category in the spinner
                    val catList = categories.toList()
                    val index = catList.indexOf(currentEvent!!.category)
                    if (index >= 0) {
                        binding!!.spinnerCategory.setSelection(index)
                    }

                    // load the saved date
                    myCalendar.timeInMillis = currentEvent!!.dateTimeMillis
                    dateSelected = true
                    showDateTime()
                }
            }
        } else {
            binding!!.tvFormTitle.text = "New Event"
        }

        binding!!.btnPickDate.setOnClickListener {
            pickDate()
        }

        binding!!.btnPickTime.setOnClickListener {
            pickTime()
        }

        binding!!.btnSave.setOnClickListener {
            saveEvent()
        }

        binding!!.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun pickDate() {
        val year = myCalendar.get(Calendar.YEAR)
        val month = myCalendar.get(Calendar.MONTH)
        val day = myCalendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(requireContext(), { _, y, m, d ->
            myCalendar.set(Calendar.YEAR, y)
            myCalendar.set(Calendar.MONTH, m)
            myCalendar.set(Calendar.DAY_OF_MONTH, d)
            dateSelected = true
            showDateTime()
        }, year, month, day)

        // dont allow past dates for new events
        if (currentEvent == null) {
            datePicker.datePicker.minDate = System.currentTimeMillis()
        }

        datePicker.show()
    }

    private fun pickTime() {
        val hour = myCalendar.get(Calendar.HOUR_OF_DAY)
        val minute = myCalendar.get(Calendar.MINUTE)

        val timePicker = TimePickerDialog(requireContext(), { _, h, min ->
            myCalendar.set(Calendar.HOUR_OF_DAY, h)
            myCalendar.set(Calendar.MINUTE, min)
            showDateTime()
        }, hour, minute, false)

        timePicker.show()
    }

    private fun showDateTime() {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        binding!!.tvSelectedDateTime.text = sdf.format(myCalendar.time)
    }

    private fun saveEvent() {
        val title = binding!!.etTitle.text.toString().trim()
        val location = binding!!.etLocation.text.toString().trim()
        val category = binding!!.spinnerCategory.selectedItem.toString()

        // check title isnt empty
        if (title.isEmpty()) {
            binding!!.tilTitle.error = "Please enter a title"
            Toast.makeText(requireContext(), "Title is required", Toast.LENGTH_SHORT).show()
            return
        }

        binding!!.tilTitle.error = null

        // check date was picked
        if (!dateSelected) {
            Toast.makeText(requireContext(), "Please pick a date and time", Toast.LENGTH_SHORT).show()
            return
        }

        val millis = myCalendar.timeInMillis

        // make sure date isnt in the past for new events
        if (currentEvent == null && millis < System.currentTimeMillis()) {
            Snackbar.make(binding!!.root, "Date cannot be in the past!", Snackbar.LENGTH_LONG).show()
            return
        }

        if (currentEvent != null) {
            // update existing event
            currentEvent!!.title = title
            currentEvent!!.category = category
            currentEvent!!.location = location
            currentEvent!!.dateTimeMillis = millis
            viewModel.updateEvent(currentEvent!!)
            Toast.makeText(requireContext(), "Event updated!", Toast.LENGTH_SHORT).show()
        } else {
            // create new event
            val newEvent = Event(
                title = title,
                category = category,
                location = location,
                dateTimeMillis = millis
            )
            viewModel.addEvent(newEvent)
            Toast.makeText(requireContext(), "Event saved!", Toast.LENGTH_SHORT).show()
        }

        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}