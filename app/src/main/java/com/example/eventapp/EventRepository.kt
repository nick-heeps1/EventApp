package com.example.eventapp.data

import androidx.lifecycle.LiveData

class EventRepository(private val dao: EventDao) {

    // list of all events from the database
    val allEvents: LiveData<List<Event>> = dao.getAllEvents()

    suspend fun insert(event: Event) {
        dao.insertEvent(event)
    }

    suspend fun update(event: Event) {
        dao.updateEvent(event)
    }

    suspend fun delete(event: Event) {
        dao.deleteEvent(event)
    }

    suspend fun getById(id: Int): Event? {
        return dao.getEventById(id)
    }
}