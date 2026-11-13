package com.example.timetable

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.timetable.data.TimetableDatabase
import com.example.timetable.data.TimetableRepository
import com.example.timetable.ui.TimetableViewModel
import com.example.timetable.ui.theme.TimetableManagementAppTheme

class MainActivity : ComponentActivity() {
    private val viewModel: TimetableViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val database = TimetableDatabase.getDatabase(applicationContext)
                val repository = TimetableRepository(database.timetableDao())
                @Suppress("UNCHECKED_CAST")
                return TimetableViewModel(repository) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TimetableManagementAppTheme {
                TimetableScreen(viewModel = viewModel)
            }
        }
    }
}
