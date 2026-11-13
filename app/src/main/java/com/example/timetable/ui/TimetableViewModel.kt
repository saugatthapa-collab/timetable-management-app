package com.example.timetable.data

import kotlinx.coroutines.flow.Flow

class TimetableRepository(private val dao: TimetableDao) {
    fun getAll(): Flow<List<TimetableEntry>> = dao.getAll()

    suspend fun upsert(entry: TimetableEntry) {
        dao.upsert(entry)
    }

    suspend fun delete(entry: TimetableEntry) {
        dao.delete(entry)
    }
}
