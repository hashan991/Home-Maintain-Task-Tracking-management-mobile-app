package com.example.homemaintenancetracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_table")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val taskName: String,
    val isCompleted: Boolean = false,
    val dueDate: String? = null, // Field for due date
    val dueTime: String? = null, // Field for due time (New)
    val priority: Int = 0 // Field for priority (e.g., 0 = Low, 1 = Medium, 2 = High)
)
