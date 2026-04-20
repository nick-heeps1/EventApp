package com.example.eventapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// database class - only one instance needed
@Database(entities = [Event::class], version = 1, exportSchema = false)
abstract class EventDatabase : RoomDatabase() {

    abstract fun eventDao(): EventDao

    companion object {
        private var instance: EventDatabase? = null

        fun getDatabase(context: Context): EventDatabase {
            // create database if it doesnt exist yet
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    EventDatabase::class.java,
                    "event_database"
                ).build()
            }
            return instance!!
        }
    }
}