package com.example.eventapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var title: String = "",
    var category: String = "",
    var location: String = "",
    var dateTimeMillis: Long = 0
)