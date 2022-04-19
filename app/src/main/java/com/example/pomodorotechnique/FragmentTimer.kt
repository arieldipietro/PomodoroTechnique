package com.example.pomodorotechnique

import android.app.Activity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import com.example.pomodorotechnique.models.TimerState.*
import com.example.pomodorotechnique.databinding.FragmentTimerBinding
import com.example.pomodorotechnique.models.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.util.*


class FragmentTimer : Fragment() {

    private lateinit var binding : FragmentTimerBinding
    //private var task = Task("","",0,0L,0L)
    private val timerViewModel = TimerViewModel()
    private val tasksViewModel = TasksViewModel()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_timer, container, false)

        binding.buttonPlay.setOnClickListener{ v ->

            if(timerViewModel.timerState.value == OnRestPaused){
                timerViewModel.startRestTimer()
            }
            else {
                timerViewModel.startFocusTimer()
                updateCountdownUI()
            }
        }
        binding.buttonPause.setOnClickListener{ v ->
            timerViewModel.onPause()
            updateCountdownUI()
        }

        timerViewModel.secondsRemaining.observe(viewLifecycleOwner,{
            updateCountdownUI()
        })
        timerViewModel.timerState.observe(viewLifecycleOwner,{
            if(timerViewModel.timerState.value !== Completed) {
                updateUIText()
            }
        })

        binding.imageButton3.setOnClickListener{ v ->
            showAlertDialog()
        }

        return binding.root
    }


    private fun updateCountdownUI(){
        val secondsInString = timerViewModel.secondsInMinutesUntilFinished.toString()
        val minutesUntilFinished = timerViewModel.minutesUntilFinished

        binding.textViewCountdown.text = "$minutesUntilFinished:${
            if (secondsInString.length == 2) secondsInString
            else "0" + secondsInString}"
    }

    private fun updateUIText() {
        var cycleString: String = when (timerViewModel.cyclesCount) {
            1 -> "1"
            2 -> "2"
            3 -> "3"
            else -> "4"
        }
        var stateString: String =
            when (timerViewModel.timerState.value) {
                OnFocusRunning -> "Stay Focused!"
                OnRestRunning -> "Take a rest!"
                OnFocusPaused -> "Stay Focused! (Paused)"
                else -> "Take a rest! (Paused)"
            }

        binding.textViewState.text = "Cycle: $cycleString - $stateString"
    }
    
    /*fun createNewTask(name:String) : Task{
        task.name = name
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        task.dateCreated = "Date created: $day/$month/$year"

        //Alternative method for getting the date
        *//*val current = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.now()
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
        val formatted = current.format(formatter)*//*

        task.cyclesCompleted = timerViewModel.cyclesCount
        task.focusedTime = timerViewModel.cyclesCount *25L
        task.restTime = 10L
        //TODO: Create the focus time
        return task
    }*/

    fun showAlertDialog(){
        val inputTaskName = EditText(context)
        inputTaskName.setInputType(InputType.TYPE_CLASS_TEXT)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.new_task)
            .setView(inputTaskName)
            .setPositiveButton(R.string.ok){dialog, switch ->
                var newTask = tasksViewModel.createNewTask(inputTaskName.text.toString())
                newTask.cyclesCompleted = timerViewModel.cyclesCount
                newTask.focusedTime = timerViewModel.cyclesCount *25L
                newTask.restTime = 10L
                Snackbar.make(binding.root, "name: ${newTask.name}, ${newTask.dateCreated}", Snackbar.LENGTH_SHORT).show()
                Snackbar.make(binding.root, tasksViewModel.tasksList.size.toString(), Snackbar.LENGTH_SHORT).show()
                Log.i("MainActivity", "ListData ${tasksViewModel.tasksListData.value}")
            }
            .setNegativeButton(R.string.cancel){dialog, switch ->
                //TODO: Implement cancel button
            }
            .show()
    }





}

