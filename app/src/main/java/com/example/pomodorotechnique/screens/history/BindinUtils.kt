package com.example.pomodorotechnique.screens.history

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.pomodorotechnique.R
import com.example.pomodorotechnique.database.Task

@BindingAdapter("nameAdapter")
fun TextView.setName(item: Task?) {
    item?.let {
        text = item.name
    }
}

@BindingAdapter("dateCreatedFormated")
fun TextView.setDateCreatedString(item: Task?) {
    item?.let {
        text = resources.getString(R.string.date_created_string, item.dateCreated)
    }
}

@BindingAdapter("cyclesCompletedFormated")
fun TextView.setCyclesComplete(item: Task?) {
    item?.let {
        text = resources.getString(R.string.cycles_completed_string, item.cyclesCount)
    }
}

@BindingAdapter("focusedTimeFormated")
fun TextView.setFocusedTime(item: Task?) {
    item?.let {
        text = resources.getString(R.string.focused_time_string, item.focusedTime)
    }
}