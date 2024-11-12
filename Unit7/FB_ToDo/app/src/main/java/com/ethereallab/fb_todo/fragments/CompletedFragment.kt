package com.ethereallab.fb_todo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ethereallab.fb_todo.adapters.TodoAdapter
import com.ethereallab.fb_todo.databinding.FragmentCompletedBinding
import com.ethereallab.fb_todo.models.Todo
import com.ethereallab.fb_todo.repository.ToDoRepository
import kotlinx.coroutines.launch
import androidx.appcompat.app.AlertDialog

class CompletedFragment : Fragment() {

    private var _binding: FragmentCompletedBinding? = null
    private val binding get() = _binding!!
    private lateinit var repository: ToDoRepository
    private val completedTodos = mutableListOf<Todo>()
    private lateinit var todoAdapter: TodoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompletedBinding.inflate(inflater, container, false)
        repository = ToDoRepository(requireContext())

        // Setup RecyclerView with an item click listener to un-complete the todo
        todoAdapter = TodoAdapter(completedTodos) { todo -> showMarkNotDoneDiagog(todo) }
        binding.recyclerView.apply {
            adapter = todoAdapter
            layoutManager = LinearLayoutManager(context)
        }

        observeCompletedTodos()

        return binding.root
    }

    private fun observeCompletedTodos() {
        lifecycleScope.launch {
            repository.getCompletedTodos().collect { todos ->
                if (_binding != null) { // Check if binding is still valid
                    completedTodos.clear()
                    completedTodos.addAll(todos)
                    todoAdapter.notifyDataSetChanged()

                    // Show emptyTextView if the list is empty
                    binding.emptyTextView.visibility = if (todos.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }

    // Show confirmation dialog for un-completing a todo
    private fun showMarkNotDoneDiagog(todo: Todo) {
        AlertDialog.Builder(requireContext())
            .setTitle("Mark as Pending")
            .setMessage("Do you want to mark this TODO as pending?")
            .setPositiveButton("Yes") { _, _ ->
                markToDoNotDone(todo)
            }
            .setNegativeButton("No", null)
            .show()
    }
    private fun markToDoNotDone(todo: Todo) {
        val updatedTodo = todo.copy(isDone = false, completedDate = null) // Mark as pending
        lifecycleScope.launch {
            try {
                repository.updateTodoStatus(updatedTodo)
                Toast.makeText(requireContext(), "TODO marked as pending", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Failed to mark TODO as pending", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
