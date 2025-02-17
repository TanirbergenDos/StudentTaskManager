package com.example.studenttaskmanager

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DbHelper(val context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, "app", factory, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val query = "CREATE TABLE tasks (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, description TEXT, due_date_time TEXT)"
        db!!.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS tasks")
        onCreate(db)
    }

    fun addTask(task: Task) {
        val values = ContentValues()
        values.put("title", task.title)
        values.put("description", task.desc)
        values.put("due_date_time", task.dueDateTime)

        val db = this.writableDatabase
        db.insert("tasks", null, values)

        db.close()
    }

    fun deleteTask(taskId: Int): Int {
        val db = this.writableDatabase
        return db.delete("tasks", "id = ?", arrayOf(taskId.toString()))    }

    fun getAllTasks(): List<Task> {
        val tasks = mutableListOf<Task>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM tasks", null)

        if (cursor.moveToFirst()) {
            do {
                val task = Task(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    title = cursor.getString(cursor.getColumnIndexOrThrow("title")),
                    desc = cursor.getString(cursor.getColumnIndexOrThrow("description")) ?: "",
                    dueDateTime = cursor.getString(cursor.getColumnIndexOrThrow("due_date_time"))
                )
                tasks.add(task)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return tasks
    }

    fun getTasksSortedBy(sortType: Int): List<Task> {
        val tasks = mutableListOf<Task>()
        val db = readableDatabase
        val orderBy = when (sortType) {
            0 -> "id DESC"  // Newest to Oldest
            1 -> "id ASC"   // Oldest to Newest
            2 -> "due_date_time ASC" // Closest to Deadline
            3 -> "due_date_time DESC" // Furthest to Deadline
            else -> "id DESC" // Default to newest
        }

        val cursor = db.rawQuery("SELECT * FROM tasks ORDER BY $orderBy", null)

        if (cursor.moveToFirst()) {
            do {
                val task = Task(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    title = cursor.getString(cursor.getColumnIndexOrThrow("title")),
                    desc = cursor.getString(cursor.getColumnIndexOrThrow("description")) ?: "",
                    dueDateTime = cursor.getString(cursor.getColumnIndexOrThrow("due_date_time"))
                )
                tasks.add(task)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return tasks
    }


    fun updateTask(task: Task): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("title", task.title)
            put("description", task.desc)
            put("due_date_time", task.dueDateTime)
        }
        return db.update("tasks", values, "id = ?", arrayOf(task.id.toString()))
    }

}