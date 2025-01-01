package com.example.homemaintenancetracker.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.homemaintenancetracker.R
import com.example.homemaintenancetracker.data.Task
import java.text.SimpleDateFormat
import java.util.Locale

class TaskAdapter(
    private val onEditClicked: (Task) -> Unit,
    private val onDeleteClicked: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private var taskList = listOf<Task>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentTask = taskList[position]
        holder.bind(currentTask)
    }

    override fun getItemCount() = taskList.size

    fun setTasks(tasks: List<Task>) {
        taskList = tasks
        notifyDataSetChanged()
    }

    fun getTaskAtPosition(position: Int): Task {
        return taskList[position]
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taskNameTextView: TextView = itemView.findViewById(R.id.taskName)
        private val dueDateTextView: TextView = itemView.findViewById(R.id.dueDate) // TextView for due date
        private val priorityTextView: TextView = itemView.findViewById(R.id.priority) // TextView for priority
        private val editIcon: ImageView = itemView.findViewById(R.id.editIcon)
        private val deleteIcon: ImageView = itemView.findViewById(R.id.deleteIcon)

        fun bind(task: Task) {
            taskNameTextView.text = task.taskName

            // Format and display the due date and time
            dueDateTextView.text = formatDueDateAndTime(task.dueDate, task.dueTime)

            // Display priority as a user-friendly string
            priorityTextView.text = when (task.priority) {
                1 -> "Hegh"
                2 -> "Medium"
                else -> "Low"
            }

            editIcon.setOnClickListener {
                onEditClicked(task) // Pass the clicked task to the edit listener
            }
            deleteIcon.setOnClickListener {
                onDeleteClicked(task) // Pass the clicked task to the delete listener
            }
        }

        // Helper function to format the due date and time
        private fun formatDueDateAndTime(dueDate: String?, dueTime: String?): String {
            return if (!dueDate.isNullOrEmpty() && !dueTime.isNullOrEmpty()) {
                try {
                    // Parse and format the date and time
                    val originalFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    val targetFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                    val dateTime = "$dueDate $dueTime"
                    val date = originalFormat.parse(dateTime)
                    targetFormat.format(date)
                } catch (e: Exception) {
                    "Invalid date/time"
                }
            } else if (!dueDate.isNullOrEmpty()) {
                try {
                    // If only date is available
                    val originalFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val targetFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    val date = originalFormat.parse(dueDate)
                    targetFormat.format(date)
                } catch (e: Exception) {
                    "Invalid date"
                }
            } else {
                "No due date"
            }
        }
    }
}
