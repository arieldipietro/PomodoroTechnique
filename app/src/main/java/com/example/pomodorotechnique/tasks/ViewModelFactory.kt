package com.example.pomodorotechnique.tasks

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pomodorotechnique.TimerViewModel
import com.example.pomodorotechnique.database.TasksDatabaseDao

/**
 * This is pretty much boiler plate code for a ViewModel Factory.
 *
 * Provides the TasksDatabaseDao and context to the ViewModel.
 */
class ViewModelFactory(
    private val dataSource: TasksDatabaseDao,
    private val application: Application) : ViewModelProvider.Factory {


    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimerViewModel::class.java)) {
            return TimerViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
