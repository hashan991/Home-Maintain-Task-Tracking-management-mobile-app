package com.example.homemaintenancetracker

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homemaintenancetracker.data.Task
import com.example.homemaintenancetracker.ui.TaskAdapter
import com.example.homemaintenancetracker.utils.NotificationHelper
import com.example.homemaintenancetracker.utils.TaskNotificationReceiver
import com.example.homemaintenancetracker.viewmodel.TaskViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*








class MainActivity : AppCompatActivity() {
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var alarmManager: AlarmManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the notification channel
        val notificationHelper = NotificationHelper(this)
        notificationHelper.createNotificationChannel()

        // Initialize AlarmManager
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Find the button and set an OnClickListener
        val openTimerButton = findViewById<Button>(R.id.buttonOpenTimer)
        openTimerButton.setOnClickListener {
            // Create an intent to start TimerStopwatchActivity
            val intent = Intent(this, TimerStopwatchActivity::class.java)
            startActivity(intent)
        }


        // Set up RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        taskAdapter = TaskAdapter(
            onEditClicked = { task ->
                showTaskDialog(task) // Handle item click for editing
            },
            onDeleteClicked = { task ->
                taskViewModel.delete(task) // Handle item click for deleting
            }
        )
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = taskAdapter
        }

        // Initialize ViewModel
        taskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)

        // Observe LiveData from ViewModel
        taskViewModel.allTasks.observe(this) { tasks ->
            taskAdapter.setTasks(tasks)
        }

        // Set up FAB to add a new task
        val fabAddTask = findViewById<FloatingActionButton>(R.id.fabAddTask)
        fabAddTask.setOnClickListener {
            showTaskDialog() // Show dialog for adding a new task
        }

        // Add swipe-to-delete functionality
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                // Do nothing on move
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Get the swiped task
                val position = viewHolder.adapterPosition
                val task = taskAdapter.getTaskAtPosition(position)

                // Delete the task
                taskViewModel.delete(task)
            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    // Function to show dialog to add/edit a task
    private fun showTaskDialog(task: Task? = null) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null)
        val editTextTaskName = dialogView.findViewById<EditText>(R.id.editTextTaskName)
        val editTextDueDate = dialogView.findViewById<EditText>(R.id.editTextDueDate) // Due date input
        val editTextDueTime = dialogView.findViewById<EditText>(R.id.editTextDueTime) // Due time input
        val editTextPriority = dialogView.findViewById<EditText>(R.id.editTextPriority) // Priority input

        val calendar = Calendar.getInstance() // To store the selected date and time

        if (task != null) {
            // Editing an existing task
            editTextTaskName.setText(task.taskName)
            editTextDueDate.setText(task.dueDate) // Assuming dueDate is a String
            editTextDueTime.setText(task.dueTime) // Assuming dueTime is a String
            editTextPriority.setText(task.priority.toString())
        }

        // Set up DatePicker for due date
        editTextDueDate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                // Set the date in the calendar
                calendar.set(Calendar.YEAR, selectedYear)
                calendar.set(Calendar.MONTH, selectedMonth)
                calendar.set(Calendar.DAY_OF_MONTH, selectedDay)

                // Update the EditText with the selected date
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                editTextDueDate.setText(dateFormat.format(calendar.time))
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            datePickerDialog.show()
        }

        // Set up TimePicker for due time
        editTextDueTime.setOnClickListener {
            val timePickerDialog = TimePickerDialog(this, { _, hourOfDay, minute ->
                // Set the time in the calendar
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)

                // Update the EditText with the selected time
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                editTextDueTime.setText(timeFormat.format(calendar.time))
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false)
            timePickerDialog.show()
        }

        AlertDialog.Builder(this)
            .setTitle(if (task == null) "Add New Task" else "Edit Task")
            .setView(dialogView)
            .setPositiveButton(if (task == null) "Add" else "Update") { _, _ ->
                val taskName = editTextTaskName.text.toString()
                val dueDate = editTextDueDate.text.toString()
                val dueTime = editTextDueTime.text.toString()
                val priority = editTextPriority.text.toString().toIntOrNull() ?: 0 // Default to 0 if invalid

                if (taskName.isNotEmpty()) {
                    if (task == null) {
                        // Add new task
                        val newTask = Task(taskName = taskName, dueDate = dueDate, dueTime = dueTime, priority = priority)
                        taskViewModel.insert(newTask)
                        scheduleNotification(newTask) // Schedule notification for new task
                    } else {
                        // Update existing task
                        val updatedTask = task.copy(taskName = taskName, dueDate = dueDate, dueTime = dueTime, priority = priority)
                        taskViewModel.update(updatedTask)
                        scheduleNotification(updatedTask) // Schedule notification for updated task
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    // Function to schedule notifications for due tasks
    private fun scheduleNotification(task: Task) {
        try {
            // Create an Intent to trigger the broadcast receiver
            val intent = Intent(this, TaskNotificationReceiver::class.java).apply {
                putExtra("taskName", task.taskName)
            }

            // Create a PendingIntent
            val pendingIntent = PendingIntent.getBroadcast(
                this,
                task.id, // unique request code for each task
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Convert the dueDate and dueTime strings to a time in milliseconds
            val calendar = Calendar.getInstance().apply {
                val dateTime = "${task.dueDate} ${task.dueTime}"
                time = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).parse(dateTime)
            }

            // Schedule the alarm
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
            // Handle the exception, log it or show an error message to the user
        }
    }
}
