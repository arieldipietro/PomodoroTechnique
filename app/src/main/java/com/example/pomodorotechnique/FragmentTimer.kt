package com.example.pomodorotechnique

import android.app.Activity
import android.opengl.Visibility
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
import kotlin.concurrent.timer


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

        if(::currentTask.isInitialized) {
            binding.buttonPlay.setVisibility(View.VISIBLE)
            binding.buttonPause.setVisibility(View.VISIBLE)
            binding.imageButton3.setVisibility(View.VISIBLE)
        }
        else{
            binding.buttonPlay.setVisibility(View.GONE)
            binding.buttonPause.setVisibility(View.GONE)
            binding.imageButton3.setVisibility(View.GONE)
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
        var cycleString: String = (timerViewModel.cyclesCount+1).toString()

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

                if(::currentTask.isInitialized) {
                    timerViewModel.updateCurrentTaskState(currentTask)
                    tasksViewModel.addTaskToHistory(currentTask)

                    var newTask = tasksViewModel.createNewTask(inputTaskName.text.toString())
                    newTask.cyclesCompleted = 0
                    newTask.focusedTime = 0L
                    newTask.restTime = 0L

                    currentTask = newTask
                    //esto para pasar el task al otro fragment. dsps cuando ingrese uno nuevo tengo que limpiar la lista
                    tasksViewModel.addCurrentTask(currentTask)

                    timerViewModel.onReset()

                    Log.i("MainActivity", "ListData ${tasksViewModel.tasksListData.value}")
                }
                else{
                    var newTask = tasksViewModel.createNewTask(inputTaskName.text.toString())
                    newTask.cyclesCompleted = 0
                    newTask.focusedTime = 0L
                    newTask.restTime = 0L

                    currentTask = newTask
                    //esto para pasar el task al otro fragment. dsps cuando ingrese uno nuevo tengo que limpiar la lista
                    tasksViewModel.addCurrentTask(currentTask)

                    timerViewModel.onReset()

                    Log.i("MainActivity", "current task view model ${tasksViewModel.currentTasksList}")
                }
                binding.buttonPlay.setVisibility(View.VISIBLE)
                binding.buttonPause.setVisibility(View.VISIBLE)
                binding.imageButton3.setVisibility(View.VISIBLE)
            }
            .setNegativeButton(R.string.cancel){dialog, switch ->
                //TODO: Implement cancel button
            }
            .show()
    }

}
