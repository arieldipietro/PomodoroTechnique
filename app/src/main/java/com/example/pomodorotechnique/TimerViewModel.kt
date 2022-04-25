package com.example.pomodorotechnique

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.*
import com.example.pomodorotechnique.models.Task
import com.example.pomodorotechnique.models.TimerState
import com.example.pomodorotechnique.models.TimerState.*

class TimerViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {


    object Timers {
        const val INITIAL_FOCUS_TIME = 25*60L
        const val SHORT_REST_TIME = 5*60L
        const val LONG_REST_TIME = 20*60L
    }

    private lateinit var timer : CountDownTimer
    private var timerLengthSeconds = 0L
    private var lastSavedTimer = 0L

    private val _timerState = MutableLiveData<TimerState>()
    val timerState : LiveData<TimerState>
        get() = _timerState

    // creates a LiveData that monitors this key in the saved state
    private val _secondsRemaining = savedStateHandle.getLiveData<Long>(SECONDS_REMAINING)
    val secondsRemaining : LiveData<Long> get() = _secondsRemaining

    // exposes a setter that updates the state - this will propagate to the LiveData
    fun setSecondsRemaining(secondsRemainingSetter: Long) {
        savedStateHandle[SECONDS_REMAINING] = secondsRemainingSetter
    }

    // creates a LiveData that monitors this key in the saved state
    private val _cyclesCount2 = savedStateHandle.getLiveData<Int>(CYCLES_COUNT, 0)
    val cyclesCount2 : LiveData<Int> get() = _cyclesCount2

    // exposes a setter that updates the state - this will propagate to the LiveData
    fun setCyclesCount(CyclesCountSetter: Int) {
        savedStateHandle[CYCLES_COUNT] = CyclesCountSetter
    }


    init{
            _timerState.value = NotStarted
        Log.i("MainActivity", "seconds remainin from viewModel: ${ _secondsRemaining.value }")
        Log.i("MainActivity", "cycles count from viewModel: ${ _cyclesCount2.value }")
    }


    fun startFocusTimer(task: Task){
        timerLengthSeconds =
        if(timerState.value == RestCompleted || timerState.value == NotStarted) {
            Timers.INITIAL_FOCUS_TIME
        }
        else{
            lastSavedTimer+1
            /*there's a small bug in the app: when the timer is on Pause, we authomatically miss a second
            when we restart the timer. Since we cannot add half a second because our variables are of type long,
            I added one second when we restart the timer, to give an effect that we are not loosing that second*/

        //TODO: Manage the else case, add different scenarios: focus runningpaused, restpaused
            }

        _timerState.value = OnFocusRunning

        Log.i("MainActivity", "timerLengthseconds: ${timerLengthSeconds}")
        Log.i("MainActivity", "secondsRemaining: ${secondsRemaining.value}")

        timer = object : CountDownTimer(timerLengthSeconds * 1000, 1000){
            override fun onFinish(){
                _timerState.value = TimerState.Completed
                startRestTimer(task)
            }

            override fun onTick(millisUntilFinished: Long) {
                val secondsRemainingInCountdown = millisUntilFinished / 1000

                lastSavedTimer = secondsRemainingInCountdown
                setSecondsRemaining(secondsRemainingInCountdown)
            }
        }.start()
    }

    fun startRestTimer(task: Task){
        Log.i("MainActivity", "timerState: ${timerState.value}")
        Log.i("MainActivity", "cycle: ${cyclesCount2.value}")

        timerLengthSeconds =
            if(timerState.value == OnRestPaused) {
                lastSavedTimer+1
                /*there's a small bug in the app: when the timer is on Pause, we authomatically miss a second
                when we restart the timer. Since we cannot add half a second because our variables are of type long,
                I added one second when we restart the timer, to give an effect that we are not loosing that second*/
            }
            else{
                if(isMultipleOf4(cyclesCount2.value!!)){
                    Timers.SHORT_REST_TIME
                    }
                else{
                    Timers.LONG_REST_TIME
                }
                //TODO: Manage the else case, add different scenarios: focus runningpaused, restpaused
            }

        _timerState.value = OnRestRunning


        timer = object : CountDownTimer(timerLengthSeconds * 1000, 1000){

            override fun onFinish() {
                setCyclesCount(cyclesCount2.value!! + 1)
                task.cyclesCompleted = cyclesCount2.value!!
                Log.i("MainActivity", "cycle: ${cyclesCount2.value}")

                _timerState.value = RestCompleted
                startFocusTimer(task)
            }

            override fun onTick(millisUntilFinished: Long) {
                val secondsRemainingInCountdown = millisUntilFinished / 1000

                lastSavedTimer = secondsRemainingInCountdown
                setSecondsRemaining(secondsRemainingInCountdown)

            }

        }.start()
    }

    fun onPause(){
        if(timerState.value == OnFocusRunning){
            _timerState.value = OnFocusPaused
        }
        if(timerState.value == OnRestRunning){
            _timerState.value = OnRestPaused
        }
        timer.cancel()
        Log.i("MainActivity", "last saved timer: $lastSavedTimer")
        Log.i("MainActivity", "timerState: ${timerState.value}")
    }

    fun onReset(){
        _timerState.value = NotStarted
        setCyclesCount(0)
        _secondsRemaining.value = 0
        if(::timer.isInitialized){
            timer.cancel()
        }
    }

    fun updateCurrentTaskState(task: Task){
        task.cyclesCompleted = cyclesCount2.value!!.toInt()
        task.focusedTime = cyclesCount2.value!!.toLong() * 25L
        task.restTime = cyclesCount2.value!!.toLong() * 10L
    }

    fun isMultipleOf4(cycles: Int): Boolean {
        if(cycles.toFloat() % 4 == 0F) {
            return true
        }
        else{
            return false
        }
    }

    companion object {
        val TIMER_STATE = "timerState"
        val SECONDS_REMAINING = "secondsRemaining"
        val CYCLES_COUNT = "CyclesCount"
    }




    }




