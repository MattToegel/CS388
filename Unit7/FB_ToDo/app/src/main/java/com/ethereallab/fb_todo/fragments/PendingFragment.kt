package com.ethereallab.fb_todo.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ethereallab.fb_todo.adapters.TodoAdapter
import com.ethereallab.fb_todo.databinding.FragmentPendingBinding
import com.ethereallab.fb_todo.models.Todo
import com.ethereallab.fb_todo.repository.ToDoRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class PendingFragment : Fragment() {

    private var _binding: FragmentPendingBinding? = null
    private val binding get() = _binding!!
    private lateinit var repository: ToDoRepository
    private val pendingTodos = mutableListOf<Todo>()
    private lateinit var todoAdapter: TodoAdapter
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPendingBinding.inflate(inflater, container, false)
        repository = ToDoRepository(requireContext())

        // Setup RecyclerView with click listener
        todoAdapter = TodoAdapter(pendingTodos) { todo ->
            showConfirmationDialog(todo)
        }
        binding.recyclerView.apply {
            adapter = todoAdapter
            layoutManager = LinearLayoutManager(context)
        }

        // Add new TODO item
        binding.addTodoButton.setOnClickListener {
            val newContent = binding.todoInputEditText.text.toString().trim()
            if (newContent.isNotEmpty()) {
                val currentUserId = auth.currentUser?.uid
                if (currentUserId != null) {
                    val newTodo = Todo(content = newContent, userId = currentUserId)
                    lifecycleScope.launch {
                        repository.addTodo(newTodo)
                    }
                    binding.todoInputEditText.text.clear()
                } else {
                    Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Please enter a TODO item", Toast.LENGTH_SHORT).show()
            }
        }

        observePendingTodos()

        return binding.root
    }

    private fun observePendingTodos() {
        val currentUserId = auth.currentUser?.uid
        if (currentUserId != null) {
            // Collect the Flow from Room to observe changes in pending todos
            lifecycleScope.launch {
                repository.getPendingTodos().collect { todos ->
                    if (_binding != null) { // Check if binding is still valid
                        pendingTodos.clear()
                        pendingTodos.addAll(todos)
                        todoAdapter.notifyDataSetChanged()

                        // Show a message if there are no pending items
                        binding.emptyTextView.visibility = if (todos.isEmpty()) View.VISIBLE else View.GONE
                    }
                }
            }
        } else {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showConfirmationDialog(todo: Todo) {
        AlertDialog.Builder(requireContext())
            .setTitle("Mark as Completed")
            .setMessage("Are you sure you want to mark this TODO as completed?")
            .setPositiveButton("Yes") { _, _ -> updateTodoStatus(todo, true) }
            .setNegativeButton("No", null)
            .show()
    }

    private fun updateTodoStatus(todo: Todo, isDone: Boolean) {
        lifecycleScope.launch {
            val updatedTodo = todo.copy(isDone = isDone, completedDate = if (isDone) System.currentTimeMillis() else null)
            repository.updateTodoStatus(updatedTodo)
            Toast.makeText(context, "Todo updated successfully", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
