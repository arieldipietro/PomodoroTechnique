package com.example.pomodorotechnique.screens


import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.media.AudioAttributes
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pomodorotechnique.R
import com.example.pomodorotechnique.TimerViewModel
import com.example.pomodorotechnique.database.TasksDatabase
import com.example.pomodorotechnique.database.TasksDatabaseDao
import com.example.pomodorotechnique.databinding.FragmentTimerBinding
import com.example.pomodorotechnique.models.TimerState
import com.example.pomodorotechnique.tasks.ViewModelFactory
import com.example.pomodorotechnique.utils.cancelNotifications
import com.example.pomodorotechnique.utils.sendNotification
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FragmentTimer : Fragment() {

    private lateinit var binding : FragmentTimerBinding

    //Instance of the  ViewModels
    private lateinit var timerViewModel: TimerViewModel

    //Instance of Database
    private lateinit var datasource : TasksDatabaseDao


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_timer,
            container,
            false
        )


        val application = requireNotNull(this.activity).application
        datasource = TasksDatabase.getInstance(application!!).tasksDatabaseDao

        val viewModelFactory = ViewModelFactory(datasource, application)

        timerViewModel = ViewModelProvider(requireActivity(), viewModelFactory)[TimerViewModel::class.java]

        timerViewModel.instantiateUI()

        //Observing if there's a task created already. In that case, show media buttons
        timerViewModel.currentTask.observe(viewLifecycleOwner) {
            Log.i("MainActivity", "current task oberver called ONCREATEVIEW")
            Log.i("MainActivity", "current task from observer ONCREATEVIEW :${timerViewModel.currentTask.value}")
            if (timerViewModel.currentTask == null || timerViewModel.currentTask.value!!.name == "FakeTask") {
                binding.buttonPlayPause.setVisibility(View.GONE)
                binding.buttonStop.setVisibility(View.GONE)
                binding.buttonNext.setVisibility(View.GONE)
            } else {
                binding.buttonPlayPause.setVisibility(View.VISIBLE)
                binding.buttonStop.setVisibility(View.VISIBLE)
                binding.buttonNext.setVisibility(View.VISIBLE)
            }
            updateUIText()
            updateCountdownUI()
            updateAnimation()
        }

        //Observing selected taskId, to update the UI
        timerViewModel.selectedTaskId.observe(viewLifecycleOwner) {
            Log.i("MainActivity", "selected task live data called")
            updateUIText()
            updateCountdownUI()
        }

        //Observing seconds remaining in timer, to update the UI clock
            timerViewModel.secondsRemaining.observe(viewLifecycleOwner) {
                if (timerViewModel.currentTask.value !== null) {
                    updateCountdownUI()
                }
            }

        //Observing TimerState, to update the text shown at the top
            timerViewModel.timerState.observe(viewLifecycleOwner) {
                when(timerViewModel.timerState.value){
                    TimerState.OnFocusRunning -> {
                        binding.buttonPlayPause.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24)
                    }
                    TimerState.OnRestRunning ->{
                        binding.buttonPlayPause.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24)
                    }
                    TimerState.OnFocusPaused ->{
                        binding.buttonPlayPause.setImageResource(R.drawable.ic_baseline_play_circle_outline_24)
                    }
                    TimerState.OnRestPaused ->{
                        binding.buttonPlayPause.setImageResource(R.drawable.ic_baseline_play_circle_outline_24)
                    }
                    else ->{
                        binding.buttonPlayPause.setImageResource(R.drawable.ic_baseline_play_circle_outline_24)
                    }
                }
                updateUIText()
                updateAnimation()
            }

        //Observing all tasks, to update the UI clock when the user deletes the last available task
        timerViewModel.allTasks.observe(viewLifecycleOwner) {
            if (timerViewModel.allTasks.value!!.isEmpty()) {
                timerViewModel.instantiateUI()
            }
        }


        val fab: FloatingActionButton = binding.fab
        fab.setOnClickListener { view ->
            showAlertDialog()
        }

        /*Notifications*/

        createChanel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name)
        )

        timerViewModel.timerState.observe(viewLifecycleOwner, {
            if(timerViewModel.timerState.value == TimerState.Completed) {
                //Log.i("MainActivity", "Should send Notification")
                onTimerCompleted("Timer Done, take a Rest!")
            }
            if(timerViewModel.timerState.value == TimerState.RestCompleted) {
                //Log.i("MainActivity", "Should send Notification")
                onTimerCompleted("Timer Done, back to work!")
            }
        })

        /*End of Notifications*/

        updateUIText()
        updateCountdownUI()
        updateAnimation()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonPlayPause.setOnClickListener{ v ->
            when(timerViewModel.timerState.value){
                TimerState.NotStarted -> {
                    timerViewModel.startFocusTimer()
                    //Log.i("MainActivity", "secondsRemaining for animation: ${timerViewModel.secondsRemaining.value}")
                }
                TimerState.OnFocusRunning -> {
                    timerViewModel.onPause()
                    updateCountdownUI()
                }
                TimerState.OnFocusPaused  -> {
                    timerViewModel.startFocusTimer()
                }
                TimerState.OnRestRunning -> {
                    timerViewModel.onPause()
                    updateCountdownUI()
                }
                TimerState.OnRestPaused  -> {
                    timerViewModel.startRestTimer()
                }
                else -> {
                    timerViewModel.startFocusTimer()
                }
            }
        }

        binding.buttonStop.setOnClickListener { v ->
            timerViewModel.instantiateUI()
            timerViewModel.cancelTimers()
        }

        binding.buttonNext.setOnClickListener{
            timerViewModel.onNextCycle()
        }

    }

    override fun onPause() {
        super.onPause()
        Log.i("MainActivity", "onPause called")
    }

    private fun updateCountdownUI() {
        val secondsRemaining = timerViewModel.secondsRemaining.value!!
        val minutesUntilFinished = secondsRemaining / 60
        val secondsInMinutesUntilFinished = secondsRemaining - minutesUntilFinished * 60

        val secondsInString = secondsInMinutesUntilFinished.toString()

        if(timerViewModel.secondsRemaining.value == null || timerViewModel.timerState.value == TimerState.NotStarted
            || timerViewModel.secondsRemaining.value == 0L){
            binding.textViewCountdown.text = ""
        }
        else binding.textViewCountdown.text = "$minutesUntilFinished:${
            if (secondsInString.length == 2) secondsInString
            else "0$secondsInString"
        }"
    }

    private fun updateUIText() {
        if (timerViewModel.currentTask == null ||
            timerViewModel.currentTask.value!!.name == "FakeTask"
        ) {
            binding.textViewState.text = getString(R.string.get_started)
            binding.taskTitle.setVisibility(View.GONE)
        } else {
            binding.taskTitle.setVisibility(View.VISIBLE)
            binding.taskTitle.text = "${timerViewModel.currentTask.value!!.name}"
            val stateString: String =
                when (timerViewModel.timerState.value) {
                    TimerState.OnFocusRunning -> "Stay Focused!"
                    TimerState.OnRestRunning -> "Take a rest!"
                    TimerState.OnFocusPaused -> "Stay Focused! (Paused)"
                    TimerState.OnRestPaused -> "Take a rest! (Paused)"
                    else -> "Press Play Button to start!"
                }

            if (timerViewModel.timerState.value == TimerState.Completed ||
                timerViewModel.timerState.value == TimerState.NotStarted
            ) {
                binding.textViewState.text = stateString
            } else {
                val cyclesString: String
                    when (timerViewModel.timerState.value) {
                        TimerState.OnRestRunning -> {
                            cyclesString = timerViewModel.cyclesCount.value!!.toString()
                            binding.textViewState.text = "Cycle: $cyclesString - $stateString"
                        }
                        TimerState.OnRestPaused -> {
                            cyclesString = timerViewModel.cyclesCount.value!!.toString()
                            binding.textViewState.text = "Cycle: $cyclesString - $stateString"
                        }
                        else -> {
                            cyclesString = (timerViewModel.cyclesCount.value!! + 1).toString()
                            binding.textViewState.text = "Cycle: $cyclesString - $stateString"
                        }
                    }
            }
        }
    }



    private fun updateAnimation(){
        when(timerViewModel.timerState.value){
            TimerState.OnFocusRunning -> {
                binding.animationGreen.setVisibility(View.GONE)
                binding.animationRed.setVisibility(View.VISIBLE)

                if(timerViewModel.previousTimerState.value == TimerState.NotStarted) {
                    binding.animationRed.animateProgress(timerViewModel.secondsRemaining.value!! * 1000L)
                }
                if(timerViewModel.previousTimerState.value == TimerState.RestCompleted) {
                    binding.animationRed.animateProgress(timerViewModel.secondsRemaining.value!! * 1000L)
                }
                if(timerViewModel.previousTimerState.value == TimerState.Completed) {
                    binding.animationRed.animateProgress(timerViewModel.secondsRemaining.value!! * 1000L)
                }
                else{
                    binding.animationRed.resumeAnimation()
                }
            }
            TimerState.OnFocusPaused  -> {
                binding.animationGreen.setVisibility(View.GONE)
                binding.animationRed.setVisibility(View.VISIBLE)
                binding.animationRed.pauseAnimation()
            }
            TimerState.OnRestRunning -> {
                binding.animationGreen.setVisibility(View.VISIBLE)
                binding.animationRed.setVisibility(View.GONE)
                if(timerViewModel.previousTimerState.value == TimerState.OnFocusRunning) {
                    binding.animationGreen.animateProgress(timerViewModel.secondsRemaining.value!! * 1000L)
                }
                else{
                    binding.animationGreen.resumeAnimation()
                }
            }
            TimerState.OnRestPaused  -> {
                binding.animationGreen.setVisibility(View.VISIBLE)
                binding.animationRed.setVisibility(View.GONE)
                binding.animationGreen.pauseAnimation()
            }
            else -> {
                binding.animationGreen.setVisibility(View.GONE)
                binding.animationRed.setVisibility(View.GONE)
            }
        }
    }

    fun showAlertDialog(){
        val inputTaskName = EditText(context)
        inputTaskName.setInputType(InputType.TYPE_CLASS_TEXT)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.new_task)
            .setMessage("Set here the new task name")
            .setView(inputTaskName)
            .setPositiveButton(R.string.ok){ dialog, switch ->

                timerViewModel.createNewTask(inputTaskName.text.toString())

            }
            .setNegativeButton(R.string.cancel){ dialog, switch ->
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

            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                .build()

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.app_name)
            notificationChannel.importance = NotificationManager.IMPORTANCE_DEFAULT
            notificationChannel.setSound(Settings.System.DEFAULT_RINGTONE_URI, audioAttributes)

            val notificationManager = requireActivity().getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }

    }


}
