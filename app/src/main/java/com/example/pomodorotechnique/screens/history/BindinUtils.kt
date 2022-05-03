package com.example.pomodorotechnique.screens.history

import android.app.Application
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.ViewModelProvider
import com.example.pomodorotechnique.TimerViewModel
import com.example.pomodorotechnique.database.Task
import com.example.pomodorotechnique.database.TasksDatabase
import com.example.pomodorotechnique.tasks.ViewModelFactory

@BindingAdapter("nameAdapter")
fun TextView.setName(item: Task?) {
    item?.let {
        text = item.name
    }
}

@BindingAdapter("dateCreatedFormated")
fun TextView.setDateCreatedString(item: Task?) {
    item?.let {
        text = "Date Created: ${item.dateCreated}"
    }
}

@BindingAdapter("cyclesCompletedFormated")
fun TextView.setCyclesComplete(item: Task?) {
    item?.let {
        text = "Cycles Completed: ${item.cyclesCount}"
    }
}

@BindingAdapter("focusedTimeFormated")
fun TextView.setFocusedTime(item: Task?) {
    item?.let {
        text = "Total Focused Time: "
    }
}