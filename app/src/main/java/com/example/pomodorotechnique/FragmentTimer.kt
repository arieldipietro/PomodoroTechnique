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
import androidx.lifecycle.ViewModelProvider
import com.example.pomodorotechnique.models.TimerState.*
import com.example.pomodorotechnique.databinding.FragmentTimerBinding
import com.example.pomodorotechnique.models.Task
import com.example.pomodorotechnique.models.TimerState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import java.util.*


class FragmentTimer : Fragment() {

    private lateinit var binding : FragmentTimerBinding
    private val timerViewModel = TimerViewModel()
    private lateinit var tasksViewModel : TasksViewModel
    private lateinit var task : Task
    private lateinit var currentTask : Task
    private lateinit var oldTask : Task

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_timer,
            container,
            false
        )

        //reference to the viewModel
        tasksViewModel = ViewModelProvider(requireActivity()).get(TasksViewModel::class.java)

        task = Task("","",0,0L,0L)
        binding.task = task

        //TODO: ELIMINAR VISIBILIDAD DE LOS BOTONES
        if(tasksViewModel.tasksList.size == 0){
            currentTask = Task("","",0,0L,0L)
        }
        else{
            currentTask = tasksViewModel.getCurrentTask()
        }

        binding.buttonPlay.setOnClickListener{ v ->

            if(timerViewModel.timerState.value == TimerState.OnRestPaused){
                timerViewModel.startRestTimer(currentTask)
            }
            else {
                timerViewModel.startFocusTimer(currentTask)
                updateCountdownUI()
            }
        }

        binding.buttonPause.setOnClickListener{ v ->
            timerViewModel.onPause()
            updateCountdownUI()
        }

        timerViewModel.secondsRemaining.observe(viewLifecycleOwner,{
            updateCountdownUI()
            timerViewModel.updateCurrentTaskState(currentTask)
        })
        timerViewModel.timerState.observe(viewLifecycleOwner,{
            updateUIText()
        })

        val fab: FloatingActionButton = binding.fab
        fab.setOnClickListener { view ->
            showAlertDialog()
        }

        return binding.root
    }

    private fun updateCountdownUI(){
        val secondsInString = timerViewModel.secondsInMinutesUntilFinished.toString()
        val minutesUntilFinished = timerViewModel.minutesUntilFinished

            if(timerViewModel.timerState.value == TimerState.Completed){
                binding.textViewCountdown.text = ""
            }
            else{
                binding.textViewCountdown.text = "$minutesUntilFinished:${
                if (secondsInString.length == 2) secondsInString
                else "0" + secondsInString}"
            }
        }

    private fun updateUIText() {
        var cycleString: String = when (timerViewModel.cyclesCount) {
            0 -> "1"
            1 -> "2"
            2 -> "3"
            3 -> "4"
            else -> "5"
        }
        var stateString: String =
            when (timerViewModel.timerState.value) {
                TimerState.Completed -> "Press play to start a new cycle!"
                TimerState.OnFocusRunning -> "Stay Focused!"
                TimerState.OnRestRunning -> "Take a rest!"
                TimerState.OnFocusPaused -> "Stay Focused! (Paused)"
                else -> "Take a rest! (Paused)"
            }

        if(timerViewModel.timerState.value == TimerState.Completed){
            binding.textViewState.text = "$stateString"
        }
        else {
            binding.textViewState.text = "Cycle: $cycleString - $stateString"
        }
    }

    fun showAlertDialog(){
        val inputTaskName = EditText(context)
        inputTaskName.setInputType(InputType.TYPE_CLASS_TEXT)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.new_task)
            .setView(inputTaskName)
            .setPositiveButton(R.string.ok){dialog, switch ->
                var newTask = tasksViewModel.createNewTask(inputTaskName.text.toString())
                newTask.cyclesCompleted = 0
                newTask.focusedTime = 0L
                newTask.restTime = 0L
                timerViewModel.updateCurrentTaskState(currentTask)
                tasksViewModel.addNewTask(newTask)
                //oldTask = tasksViewModel.tasksList[1]
                timerViewModel.onReset()
                currentTask = tasksViewModel.getCurrentTask()
                Log.i("MainActivity", "ListData ${tasksViewModel.tasksListData.value}")
            }
            .setNegativeButton(R.string.cancel){dialog, switch ->
                //TODO: Implement cancel button
            }
            .show()
    }

}
