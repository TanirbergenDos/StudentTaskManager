package com.example.studenttaskmanager

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.app.AlarmManager
import android.app.PendingIntent
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val settingsButton = findViewById<ImageButton>(R.id.settings_icon)
        val addTaskButton = findViewById<ImageButton>(R.id.add_task_button)
        val taskRecyclerView = findViewById<RecyclerView>(R.id.task_list_recycler_view)

        // Navigate to Settings Page
        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        // Navigate to Create Task Page
        addTaskButton.setOnClickListener {
            val intent = Intent(this, CreateTask::class.java)
            startActivity(intent)
        }

        // Fetch tasks from the database
        val db = DbHelper(this, null)
        val sharedPreferences = getSharedPreferences("app_settings", MODE_PRIVATE)
        val sortType = sharedPreferences.getInt("Sorting", 0)
        val tasks = db.getTasksSortedBy(sortType)

        // Set up RecyclerView
        val taskAdapter = TaskAdapter(this, tasks)
        taskRecyclerView.layoutManager = LinearLayoutManager(this)
        taskRecyclerView.adapter = taskAdapter

        createNotificationChannel()
    }

    override fun onResume() {
        super.onResume()
        refreshTaskList()
    }

    private fun refreshTaskList() {
        val db = DbHelper(this, null)
        val sharedPreferences = getSharedPreferences("app_settings", MODE_PRIVATE)
        val sortType = sharedPreferences.getInt("Sorting", 0)
        val tasks = db.getTasksSortedBy(sortType)
        val taskAdapter = TaskAdapter(this, tasks)
        val taskRecyclerView = findViewById<RecyclerView>(R.id.task_list_recycler_view)
        taskRecyclerView.adapter = taskAdapter
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel()
    {
        val name = "Notif Channel"
        val desc = "A Description of the Channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = desc
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}






