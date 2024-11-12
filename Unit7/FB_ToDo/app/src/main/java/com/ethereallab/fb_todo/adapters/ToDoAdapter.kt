// TodoAdapter.kt
package com.ethereallab.fb_todo.adapters

import android.view.LayoutInflater

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ethereallab.fb_todo.databinding.ItemTodoBinding
import com.ethereallab.fb_todo.models.Todo

class TodoAdapter(
    private val todos: List<Todo>,
    private val onTodoClick: (Todo) -> Unit // Click listener as a lambda
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    inner class TodoViewHolder(val binding: ItemTodoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(todo: Todo) {
            binding.contentTextView.text = todo.content
            binding.root.setOnClickListener { onTodoClick(todo) } // Set click listener
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val binding = ItemTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TodoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind(todos[position])
    }

    override fun getItemCount(): Int = todos.size
}
