package com.example.timetable.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TimetableDao {
    @Query("SELECT * FROM timetable_entries ORDER BY dayOfWeek, startHour, startMinute")
    fun getAll(): Flow<List<TimetableEntry>>

    @Upsert
    suspend fun upsert(entry: TimetableEntry)

    @Delete
    suspend fun delete(entry: TimetableEntry)
}
