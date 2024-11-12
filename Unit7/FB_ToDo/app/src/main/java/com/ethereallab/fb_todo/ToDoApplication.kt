package com.ethereallab.fb_todo


import android.app.Application
import com.ethereallab.fb_todo.database.AppDatabase

class ToDoApplication : Application() {
    val db by lazy { AppDatabase.getInstance(this) }
}
