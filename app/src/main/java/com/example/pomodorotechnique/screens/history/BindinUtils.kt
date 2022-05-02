package com.example.pomodorotechnique.screens.history

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.pomodorotechnique.database.Task2

@BindingAdapter("nameAdapter")
fun TextView.setName(item: Task2?) {
    item?.let {
        text = item.name
    }
}

@BindingAdapter("dateCreatedFormated")
fun TextView.setDateCreatedString(item: Task2?) {
    item?.let {
        text = "Date Created: ${item.dateCreated}"
    }
}

@BindingAdapter("cyclesCompletedFormated")
fun TextView.setCyclesComplete(item: Task2?) {
    item?.let {
        text = "Cycles Completed: ${item.cyclesCount}"
    }
}