package com.example.pomodorotechnique.screens.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pomodorotechnique.R


import com.example.pomodorotechnique.database.Task2
import com.example.pomodorotechnique.databinding.ListItemTaskBinding

class TasksAdapter(val itemClickListener: ItemClickListener) : ListAdapter<Task2,
        TasksAdapter.ViewHolder>(TasksDiffCallback()), View.OnClickListener {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)

        holder.buttonNavigate.setOnClickListener {
            itemClickListener.navigateClick(item.taskId)
        }
        holder.buttonDelete.setOnClickListener {
            itemClickListener.deleteClick(item.taskId)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ListItemTaskBinding)
        : RecyclerView.ViewHolder(binding.root) {

        val buttonDelete = binding.deleteIcon
        val buttonNavigate = binding.taskListContainer

        fun bind(item: Task2) {
            binding.task = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemTaskBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id)   {
            R.id.deleteIcon ->{}
            R.id.task_list_container ->{}
        }
    }
}

/**
 * Callback for calculating the diff between two non-null items in a list.
 *
 * Used by ListAdapter to calculate the minumum number of changes between and old list and a new
 * list that's been passed to `submitList`.
 */
class TasksDiffCallback : DiffUtil.ItemCallback<Task2>() {
    override fun areItemsTheSame(oldItem: Task2, newItem: Task2): Boolean {
        return oldItem.taskId == newItem.taskId
    }

    override fun areContentsTheSame(oldItem: Task2, newItem: Task2): Boolean {
        return oldItem == newItem
    }
}
/*
class TasksListener(val clickListener: (taskId: Long) -> Unit) {
    fun onClick(task: Task2) = clickListener(task.taskId)
}
class NavigateTasksListener(val clickListener: (taskId: Long) -> Unit) {
    fun onClick(task: Task2) = clickListener(task.taskId)
}*/

interface ItemClickListener{
    fun navigateClick(taskId: Long)
    fun deleteClick(taskId: Long)
}