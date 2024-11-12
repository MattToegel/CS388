package com.ethereallab.fb_todo.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Todo")
data class Todo(
    @PrimaryKey(autoGenerate = true) val localId: Int = 0,
    var firestoreId: String? = null,
    val content: String,
    val isDone: Boolean = false,
    val completedDate: Long? = null,
    val userId: String // author
)
