package com.example.studenttaskmanager

import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditTaskActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_task)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val dbHelper = DbHelper(this, null)

        val titleEditText: EditText = findViewById(R.id.editTaskTitle)
        val descriptionEditText: EditText = findViewById(R.id.editTaskDescription)
        val datePicker: DatePicker = findViewById(R.id.editTaskDatePicker)
        val timePicker: TimePicker = findViewById(R.id.editTaskTimePicker)
        val saveButton: Button = findViewById(R.id.saveTaskButton)
        val deleteButton: Button = findViewById(R.id.deleteTaskButton)

        // Get task details from intent
        val taskId = intent.getIntExtra("task_id", 0)
        val taskTitle = intent.getStringExtra("task_title") ?: ""
        val taskDescription = intent.getStringExtra("task_description") ?: ""
        val taskDeadline = intent.getStringExtra("task_deadline") ?: ""

        // Pre-fill fields with existing task data
        titleEditText.setText(taskTitle)
        descriptionEditText.setText(taskDescription)
        val (year, month, day, hour, minute) = parseDeadline(taskDeadline)
        datePicker.updateDate(year, month, day)
        timePicker.hour = hour
        timePicker.minute = minute

        // Save Changes Button Click
        saveButton.setOnClickListener {
            val updatedTitle = titleEditText.text.toString()
            val updatedDescription = descriptionEditText.text.toString()
            val updatedDeadline = "${datePicker.year}-${datePicker.month + 1}-${datePicker.dayOfMonth} ${timePicker.hour}:${timePicker.minute}"
            val rowsUpdated = dbHelper.updateTask(Task(taskId, updatedTitle, updatedDescription, updatedDeadline))
            if (rowsUpdated > 0) {
                Toast.makeText(this, "Task updated successfully", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to update task", Toast.LENGTH_SHORT).show()
            }
            finish()
        }

        deleteButton.setOnClickListener {
            val rowsUpdated = dbHelper.deleteTask(taskId)
            if (rowsUpdated > 0) {
                Toast.makeText(this, "Task deleted successfully", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to delete task", Toast.LENGTH_SHORT).show()
            }
            finish()
        }
    }

    private fun parseDeadline(deadline: String): List<Int> {
        // Parse deadline into components (year, month, day, hour, minute)
        val dateTime = deadline.split(" ")
        val date = dateTime[0].split("-")
        val time = dateTime[1].split(":")
        return listOf(date[0].toInt(), date[1].toInt() - 1, date[2].toInt(), time[0].toInt(), time[1].toInt())
    }
}