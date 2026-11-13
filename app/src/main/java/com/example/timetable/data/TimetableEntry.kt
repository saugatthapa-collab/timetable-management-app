package com.example.timetable

import com.example.timetable.data.TimetableEntry
import com.example.timetable.ui.TimetableViewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.util.Locale

val weekDays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

@Composable
fun TimetableScreen(viewModel: TimetableViewModel) {
    val entries by viewModel.timetable.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }
    var editingEntry by remember { mutableStateOf<TimetableEntry?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editingEntry = null
                showDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add class")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Weekly timetable",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            weekDays.forEachIndexed { index, dayName ->
                val dayEntries = entries.filter { it.dayOfWeek == index }
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = dayName,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "${dayEntries.size} classes",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (dayEntries.isEmpty()) {
                                Text(
                                    text = "No classes scheduled.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                dayEntries.sortedBy { it.startHour * 60 + it.startMinute }.forEach { item ->
                                    val color = Color(android.graphics.Color.parseColor(item.colorHex))
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.2f)),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(12.dp),
                                            verticalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = item.subject,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Row {
                                                    IconButton(onClick = {
                                                        editingEntry = item
                                                        showDialog = true
                                                    }) {
                                                        Icon(Icons.Default.Edit, contentDescription = "Edit class")
                                                    }
                                                    IconButton(onClick = { viewModel.delete(item) }) {
                                                        Icon(Icons.Default.Delete, contentDescription = "Delete class")
                                                    }
                                                }
                                            }

                                            Text(text = "Room: ${item.room}")
                                            Text(
                                                text = "Time: ${formatTime(item.startHour, item.startMinute)} - ${formatTime(item.startHour, item.startMinute + item.durationMinutes)}"
                                            )
                                            Text(text = "Duration: ${item.durationMinutes} min")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        ScheduleFormDialog(
            entry = editingEntry,
            onDismiss = { showDialog = false },
            onSave = { schedule ->
                viewModel.addOrUpdate(schedule)
                showDialog = false
                editingEntry = null
            }
        )
    }
}

private fun formatTime(hour: Int, rawMinute: Int): String {
    val minutes = rawMinute % 60
    val totalHours = hour + (rawMinute / 60)
    val normalizedHour = totalHours % 24
    val formattedHour = normalizedHour.toString().padStart(2, '0')
    val formattedMinute = minutes.toString().padStart(2, '0')
    return "$formattedHour:$formattedMinute"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleFormDialog(
    entry: TimetableEntry?,
    onDismiss: () -> Unit,
    onSave: (TimetableEntry) -> Unit
) {
    var subject by remember { mutableStateOf(entry?.subject ?: "") }
    var room by remember { mutableStateOf(entry?.room ?: "") }
    var dayOfWeek by remember { mutableStateOf(entry?.dayOfWeek ?: 0) }
    var hour by remember { mutableStateOf(entry?.startHour ?: 9) }
    var minute by remember { mutableStateOf(entry?.startMinute ?: 0) }
    var duration by remember { mutableStateOf((entry?.durationMinutes ?: 60).toString()) }
    var showTimePicker by remember { mutableStateOf(false) }

    val timeState = rememberTimePickerState(initialHour = hour, initialMinute = minute, is24Hour = true)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (entry == null) "Add class" else "Edit class",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Subject") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = room,
                    onValueChange = { room = it },
                    label = { Text("Room") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Day")
                    Button(onClick = {
                        dayOfWeek = (dayOfWeek + 1) % weekDays.size
                    }) {
                        Text(weekDays[dayOfWeek])
                    }
                }

                Button(
                    onClick = { showTimePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Start time: ${formatTime(hour, minute)}")
                }

                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it.filter { c -> c.isDigit() } },
                    label = { Text("Duration (minutes)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            val parsedDuration = duration.toIntOrNull() ?: 60
                            val schedule = TimetableEntry(
                                id = entry?.id ?: 0,
                                subject = subject.trim(),
                                room = room.trim(),
                                dayOfWeek = dayOfWeek,
                                startHour = timeState.hour,
                                startMinute = timeState.minute,
                                durationMinutes = parsedDuration,
                                colorHex = entry?.colorHex ?: listOf(
                                    "#FF8A80",
                                    "#FFD180",
                                    "#81D4FA",
                                    "#A5D6A7",
                                    "#CE93D8",
                                    "#FFAB91"
                                )[dayOfWeek % 6]
                            )
                            onSave(schedule)
                        },
                        enabled = subject.isNotBlank() && room.isNotBlank()
                    ) {
                        Text(if (entry == null) "Save" else "Update")
                    }
                }
            }
        }
    }

    if (showTimePicker) {
        Dialog(onDismissRequest = { showTimePicker = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Select time",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    TimePicker(
                        state = timeState,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Button(
                        onClick = {
                            hour = timeState.hour
                            minute = timeState.minute
                            showTimePicker = false
                        }
                    ) {
                        Text("OK")
                    }
                }
            }
        }
    }
}
