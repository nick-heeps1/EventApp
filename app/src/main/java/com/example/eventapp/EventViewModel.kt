package com.example.eventapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.eventapp.data.Event
import com.example.eventapp.data.EventDatabase
import com.example.eventapp.data.EventRepository
import kotlinx.coroutines.launch

class EventViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: EventRepository
    val allEvents: LiveData<List<Event>>

    init {
        // set up the database and repository
        val db = EventDatabase.getDatabase(application)
        val dao = db.eventDao()
        repository = EventRepository(dao)
        allEvents = repository.allEvents
    }

    fun addEvent(event: Event) {
        viewModelScope.launch {
            repository.insert(event)
        }
    }

    fun updateEvent(event: Event) {
        viewModelScope.launch {
            repository.update(event)
        }
    }

    fun deleteEvent(event: Event) {
        viewModelScope.launch {
            repository.delete(event)
        }
    }

    // need suspend here because we have to wait for the result
    suspend fun getEventById(id: Int): Event? {
        return repository.getById(id)
    }
}