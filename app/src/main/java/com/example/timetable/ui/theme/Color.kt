package com.example.timetable.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timetable.data.TimetableEntry
import com.example.timetable.data.TimetableRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TimetableViewModel(
    private val repository: TimetableRepository
) : ViewModel() {

    val timetable: StateFlow<List<TimetableEntry>> = repository.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addOrUpdate(entry: TimetableEntry) {
        viewModelScope.launch {
            repository.upsert(entry)
        }
    }

    fun delete(entry: TimetableEntry) {
        viewModelScope.launch {
            repository.delete(entry)
        }
    }
}
