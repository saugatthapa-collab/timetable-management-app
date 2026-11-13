package com.example.timetable.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "timetable_entries")
data class TimetableEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val subject: String,
    val room: String,
    val dayOfWeek: Int,
    val startHour: Int,
    val startMinute: Int,
    val durationMinutes: Int,
    val colorHex: String = "#FF8A80"
)
