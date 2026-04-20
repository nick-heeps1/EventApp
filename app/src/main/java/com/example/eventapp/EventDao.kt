package com.example.eventapp.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface EventDao {

    // get all events sorted by date
    @Query("SELECT * FROM events ORDER BY dateTimeMillis ASC")
    fun getAllEvents(): LiveData<List<Event>>

    @Insert
    suspend fun insertEvent(event: Event)

    @Update
    suspend fun updateEvent(event: Event)

    @Delete
    suspend fun deleteEvent(event: Event)

    // used when editing an event
    @Query("SELECT * FROM events WHERE id = :eventId")
    suspend fun getEventById(eventId: Int): Event?
}