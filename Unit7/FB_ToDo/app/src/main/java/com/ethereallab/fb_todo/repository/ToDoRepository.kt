package com.ethereallab.fb_todo.repository

import android.content.Context
import android.util.Log
import com.ethereallab.fb_todo.database.AppDatabase
import com.ethereallab.fb_todo.models.Todo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ToDoRepository(context: Context) {

    private val db: AppDatabase = AppDatabase.getInstance(context)
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val tag = "TodoRepository"

    // Fetch pending todos
    fun getPendingTodos(): Flow<List<Todo>> {
        val userId = auth.currentUser?.uid ?: throw IllegalStateException("User is not logged in")
        return db.todoDao().getPendingTodos(userId).onEach { todos ->
            Log.d(tag, "Syncing pending todos from Firestore for user: $userId")
            try {
                val collection = firestore.collection("todos")
                val snapshot = collection
                    .whereEqualTo("isDone", false)
                    .whereEqualTo("userId", userId)
                    .get().await()

                val fetchedTodos = snapshot.documents.map { document ->
                    document.toObject(Todo::class.java)!!.apply { firestoreId = document.id }
                }
                db.todoDao().insertAll(fetchedTodos)
                Log.d(tag, "Synced pending todos from Firestore to Room")
            } catch (e: Exception) {
                Log.e(tag, "Failed to sync pending todos from Firestore: ${e.message}", e)
            }
        }
    }

    // Fetch completed todos
    fun getCompletedTodos(): Flow<List<Todo>> {
        val userId = auth.currentUser?.uid ?: throw IllegalStateException("User is not logged in")
        return db.todoDao().getCompletedTodos(userId).onEach { todos ->
            Log.d(tag, "Syncing completed todos from Firestore for user: $userId")
            try {
                val collection = firestore.collection("todos")
                val snapshot = collection
                    .whereEqualTo("isDone", true)
                    .whereEqualTo("userId", userId)
                    .get().await()

                val fetchedTodos = snapshot.documents.map { document ->
                    document.toObject(Todo::class.java)!!.apply { firestoreId = document.id }
                }
                db.todoDao().insertAll(fetchedTodos)
                Log.d(tag, "Synced completed todos from Firestore to Room")
            } catch (e: Exception) {
                Log.e(tag, "Failed to sync completed todos from Firestore: ${e.message}", e)
            }
        }
    }

    // Add new todo
    suspend fun addTodo(todo: Todo) = withContext(Dispatchers.IO) {
        val userId = auth.currentUser?.uid ?: throw IllegalStateException("User is not logged in")
        val newTodo = todo.copy(userId = userId)
        val newId = db.todoDao().insert(newTodo)
        Log.d(tag, "Todo added to Room with local ID $newId")

        try {
            val document = firestore.collection("todos").add(newTodo).await()
            newTodo.firestoreId = document.id
            db.todoDao().update(newTodo)
            Log.d(tag, "Synced new todo to Firestore with Firestore ID ${newTodo.firestoreId}")
        } catch (e: Exception) {
            Log.e(tag, "Failed to sync new todo to Firestore: ${e.message}", e)
        }
    }
    suspend fun updateTodoStatus(todo: Todo) = withContext(Dispatchers.IO) {
        db.todoDao().update(todo) // Update in Room
        Log.d(tag, "Updated TODO status in Room with ID ${todo.localId}")

        // Sync the updated status with Firestore if possible
        try {
            val docId = todo.firestoreId
            if (docId != null) {
                firestore.collection("todos").document(docId).set(todo).await()
                Log.d(tag, "Synced TODO status to Firestore with ID $docId")
            } else {
                Log.w(tag, "Firestore ID is null for the TODO item")
            }
        } catch (e: Exception) {
            Log.e(tag, "Failed to sync TODO status to Firestore: ${e.message}", e)
        }
    }
}
