package com.example.pomodorotechnique


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.pomodorotechnique.databinding.FragmentTimerBinding
import com.example.pomodorotechnique.models.Task
import com.example.pomodorotechnique.models.TimerState
import com.example.pomodorotechnique.utils.cancelNotifications
import com.example.pomodorotechnique.utils.sendNotification
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton


class FragmentTimer : Fragment() {

    private lateinit var binding : FragmentTimerBinding
    private val timerViewModel by viewModels<TimerViewModel>()
    private lateinit var tasksViewModel : TasksViewModel
    private lateinit var task : Task
    private lateinit var currentTask : Task

    //variables for notifications
    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action


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

        timerViewModel.secondsRemaining.observe(viewLifecycleOwner,{
            updateCountdownUI()
            if(::currentTask.isInitialized) {
                timerViewModel.updateCurrentTaskState(currentTask)
            }
        })

        timerViewModel.timerState.observe(viewLifecycleOwner,{
                updateUIText()
            //updateAnimation()
        })

        val fab: FloatingActionButton = binding.fab
        fab.setOnClickListener { view ->
            showAlertDialog()
        }

        //notifications
        createChanel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name)
        )

        timerViewModel.timerState.observe(viewLifecycleOwner, {
            if(timerViewModel.timerState.value == TimerState.Completed) {
                Log.i("MainActivity", "Should send Notification")
                onTimerCompleted("Timer Done, take a Rest!")
            }
            if(timerViewModel.timerState.value == TimerState.RestCompleted) {
                Log.i("MainActivity", "Should send Notification")
                onTimerCompleted("Timer Done, back to work!")
            }
        })

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
    }


    private fun updateCountdownUI(){
        var secondsRemaining = timerViewModel.secondsRemaining.value!!
        val minutesUntilFinished = secondsRemaining / 60
        val secondsInMinutesUntilFinished = secondsRemaining - minutesUntilFinished * 60

        val secondsInString = secondsInMinutesUntilFinished.toString()

            if(timerViewModel.secondsRemaining == null){
                binding.textViewCountdown.text = ""
            }
            else{
                binding.textViewCountdown.text = "$minutesUntilFinished:${
                if (secondsInString.length == 2) secondsInString
                else "0" + secondsInString}"
            }
        Log.i("MainActivity", "Seconds remaining liveData {${timerViewModel.secondsRemaining.value}}")
        }

    private fun updateUIText() {
        val cycleString = (timerViewModel.cyclesCount2.value!! + 1).toString()

        var stateString: String =
            when (timerViewModel.timerState.value) {
                TimerState.OnFocusRunning -> "Stay Focused!"
                TimerState.OnRestRunning -> "Take a rest!"
                TimerState.OnFocusPaused -> "Stay Focused! (Paused)"
                TimerState.OnRestPaused -> "Take a rest! (Paused)"
                else -> "Get started by creating a new task!"
            }

        if(timerViewModel.timerState.value == TimerState.Completed ||
            timerViewModel.timerState.value == TimerState.NotStarted) {
            binding.textViewState.text = "$stateString"
        }
        else {
            binding.textViewState.text = "Cycle: $cycleString - $stateString"
        }
    }

    private fun updateAnimation(){
        Log.i("MainActivity", "Update animation called")
        when (timerViewModel.timerState.value) {
            TimerState.OnFocusRunning -> {
                binding.animations.animateProgress(timerViewModel.secondsRemaining.value!!)
            }
            TimerState.OnRestRunning -> {
               binding.animations.animateProgress(timerViewModel.secondsRemaining.value!!)
            }
            TimerState.OnFocusPaused -> {
               binding.animations.pauseAnimation()
            }
            TimerState.OnRestPaused -> {
                binding.animations.pauseAnimation()
            }
            else -> {
                binding.animations.cancelAnimation()
            }
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
                    //TODO: esto para pasar el task al otro fragment. dsps cuando ingrese uno nuevo tengo que limpiar la lista
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

    private fun onTimerCompleted(message: String) {
        //initialize an instance of Notification Manager
        val notificationManager = ContextCompat.getSystemService(
            requireContext(),
            NotificationManager::class.java
        ) as NotificationManager

        //cancel previous notifications
        notificationManager.cancelNotifications()
        //send new notification
        notificationManager.sendNotification(message, requireContext())
    }

    private fun createChanel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId, channelName, NotificationManager.IMPORTANCE_LOW
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.app_name)

            val notificationManager = requireActivity().getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }

    }


}
