package com.example.studenttaskmanager

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {

    private lateinit var reminderTimeEditText: EditText
    private lateinit var saveSettingsButton: Button
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sortingSpinner: Spinner


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        reminderTimeEditText = findViewById(R.id.reminderTimeEditText)
        saveSettingsButton = findViewById(R.id.saveSettingsButton)
        sortingSpinner = findViewById(R.id.sortingSpinner)

        // Получаем доступ к SharedPreferences
        sharedPreferences = getSharedPreferences("app_settings", MODE_PRIVATE)

        // Загружаем сохранённое значение, если оно есть
        val savedTime = sharedPreferences.getInt("reminder_time", 15) // Значение по умолчанию 15 минут
        reminderTimeEditText.setText(savedTime.toString())


        val sortingOptions = arrayOf("Newest to Oldest", "Oldest to Newest", "Closest to Deadline", "Furthest to Deadline")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, sortingOptions)
        sortingSpinner.adapter = adapter

        // Устанавливаем сохранённый вариант сортировки
        val savedSorting = sharedPreferences.getInt("Sorting", 0) // По умолчанию 0 (Newest to Oldest)
        sortingSpinner.setSelection(savedSorting)

        // Сохранение настроек
        saveSettingsButton.setOnClickListener {
            val reminderTimeText = reminderTimeEditText.text.toString()

            if (reminderTimeText.isNotEmpty()) {
                val reminderTime = reminderTimeText.toInt()
                val selectedSorting = sortingSpinner.selectedItemPosition

                val editor = sharedPreferences.edit()
                editor.putInt("reminder_time", reminderTime)
                editor.putInt("Sorting", selectedSorting)
                editor.apply()

                Toast.makeText(this, "Settings saved!", Toast.LENGTH_SHORT).show()
                finish() // Закрыть экран настроек
            } else {
                Toast.makeText(this, "Enter a valid value.", Toast.LENGTH_SHORT).show()
            }
        }

    }
}





