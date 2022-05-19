package com.example.pomodorotechnique.screens.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pomodorotechnique.R


import com.example.pomodorotechnique.database.Task
import com.example.pomodorotechnique.databinding.ListItemTaskBinding

class TasksAdapter(private val itemClickListener: ItemClickListener) : ListAdapter<Task,
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

        fun bind(item: Task) {
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

class TasksDiffCallback : DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem.taskId == newItem.taskId
    }

    override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem == newItem
    }
}

interface ItemClickListener{
    fun navigateClick(taskId: Long)
    fun deleteClick(taskId: Long)
}