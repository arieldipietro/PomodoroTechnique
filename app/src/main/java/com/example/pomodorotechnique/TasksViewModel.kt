package com.example.pomodorotechnique

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pomodorotechnique.models.Task
import java.util.*

class TasksViewModel : ViewModel() {

    //lateinit var currentTask : Task

    val tasksList = mutableListOf<Task>()

    private val _tasksListData = MutableLiveData(tasksList)
    val tasksListData : LiveData<MutableList<Task>>
        get() = _tasksListData

    private val _currentTasksList = mutableListOf<Task>()
    val currentTasksList = _currentTasksList

    fun addCurrentTask(task : Task){
        _currentTasksList.add(0, task)
    }

    init{
        //addCurrentTask(Task("","", 0, 0L, 0L))
    }

    fun addTaskToHistory(task : Task){
        tasksList.add(0, task)
        _tasksListData.value = tasksList
    }

    fun createNewTask(name: String): Task{
        var newTask = Task("","",0,0L,0L)
        newTask.name = name
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        newTask.dateCreated = "$day/$month/$year"

        return newTask

        //TODO: Create the focus time*/
    }

}



