package com.ethereallab.fb_todo.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ethereallab.fb_todo.models.Todo
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {

    @Insert
    suspend fun insert(todo: Todo): Long

    @Insert
    suspend fun insertAll(todos: List<Todo>)

    @Update
    suspend fun update(todo: Todo)

    // Return pending todos as a Flow, so it will emit updates automatically
    @Query("SELECT * FROM Todo WHERE isDone = 0 AND userId = :userId")
    fun getPendingTodos(userId: String): Flow<List<Todo>>

    // Return completed todos as a Flow, ordered by completion date
    @Query("SELECT * FROM Todo WHERE isDone = 1 AND userId = :userId ORDER BY completedDate DESC")
    fun getCompletedTodos(userId: String): Flow<List<Todo>>
}
