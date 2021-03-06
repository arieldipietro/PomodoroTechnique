package com.example.pomodorotechnique

import android.app.Application
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pomodorotechnique.database.Task
import com.example.pomodorotechnique.database.TasksDatabaseDao
import com.example.pomodorotechnique.models.TimerState
import com.example.pomodorotechnique.models.TimerState.*
import kotlinx.coroutines.*
import java.util.*


class TimerViewModel(
    val database: TasksDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

 /*   object Timers {
        const val INITIAL_FOCUS_TIME = 25 * 60L
        const val SHORT_REST_TIME = 5 * 60L
        const val LONG_REST_TIME = 20 * 60L
    }
*/
    private lateinit var timerFocus: CountDownTimer
    private lateinit var timerRest: CountDownTimer

    private var timerLengthSeconds = 0L
    private var lastSavedTimer = 0L

/*    private var animationGreen = AnimationGreen
    private var animationRed = AnimationRed*/


    private val _currentTask = MutableLiveData<Task>()
    val currentTask: LiveData<Task>
        get() = _currentTask

    private val _allTasks: LiveData<List<Task>> = database.getAllTasks()
    val allTasks = _allTasks

    private val _timerState = MutableLiveData<TimerState>()
    val timerState: LiveData<TimerState>
        get() = _timerState

    private val _previousTimerState = MutableLiveData<TimerState>()
    val previousTimerState: LiveData<TimerState>
        get() = _previousTimerState

    private val _secondsRemaining = MutableLiveData<Long>()
    val secondsRemaining: LiveData<Long> get() = _secondsRemaining

    private val _cyclesCount = MutableLiveData<Int>()
    val cyclesCount: LiveData<Int> get() = _cyclesCount

    private fun getCurrentTaskFromDatabase() {
        Log.i("Mainctivity", "getCurrentTaskFromDatabase")
        GlobalScope.launch { // creates worker thread
            //Since we need to update UI from the coroutine, we should execute it on the main context
            withContext(Dispatchers.Default) {
                Log.i("MainActivity", "getCurrenttask called: ")

                val currentTaskFromDatabase = database.getCurrentTask()!!
                _currentTask.postValue(currentTaskFromDatabase)
                _selectedTaskId.postValue(currentTaskFromDatabase.taskId)
                updateLiveData()
            }
        }
    }

    fun getSelectedTaskFromDatabase(taskId: Long) {
        //If the user is clicking on the current runnning task, don't do anything
        if (taskId !== currentTask.value!!.taskId) {

            Log.i("Mainctivity", "getCurrentTaskFromDatabase")
            GlobalScope.launch { // creates worker thread
                //Since we need to update UI from the coroutine, we should execute it on the main context
                withContext(Dispatchers.Default) {
                    Log.i("MainActivity", "getCurrenttask called: ")

                    val selectedTaskFromDatabase = database.get(taskId)!!
                    _currentTask.postValue(selectedTaskFromDatabase)
                    _selectedTaskId.postValue(taskId)
                    updateLiveData()
                    onResume(selectedTaskFromDatabase.taskId)
                    Log.i("MainActivity", "timerState: ${timerState.value}")
                    Log.i("MainActivity", "cycles Count: ${cyclesCount.value}")
                }
            }
            //onResume(selectedTaskId.value!!)
        }
    }

    private val _selectedTaskId = MutableLiveData<Long>()
    val selectedTaskId: LiveData<Long> get() = _selectedTaskId

    fun setSelectedTaskId(taskId: Long) {
        _selectedTaskId.value = taskId
        Log.i("MainActivity", "selectedTaskId called, changed ID: ${selectedTaskId.value}")
    }

    init {
        //init called
        //dummy data to start UI
        _selectedTaskId.value = 0L
    }

    fun instantiateUI() {
        //creating a dummy task to instantiate the UI
        _currentTask.value = Task(0L, "FakeTask", "", 0L, 0, NotStarted, "")
        _cyclesCount.value = 0
        _secondsRemaining.value = 0L
        _timerState.value = NotStarted
    }

    private fun updateLiveData() {
        viewModelScope.launch { // creates worker thread
            _cyclesCount.value = currentTask.value!!.cyclesCount
            _secondsRemaining.value = 0L
            _timerState.value = currentTask.value!!.timerState
            Log.i("MainActivity", "cycles count : ${cyclesCount.value}")
            Log.i("MainActivity", "timer state : ${timerState.value}")
        }
    }


    //calling this function on every tick of the clock, so no need to call it again when the UI changes
    fun updateCurrentTaskPropertiesInDatabase() {
        if (selectedTaskId.value !== 0L) {
            CoroutineScope(Dispatchers.IO).launch {
                val currentTaskDatabase = database.get(selectedTaskId.value!!)
                currentTaskDatabase?.timerState = timerState.value!!
                currentTaskDatabase?.cyclesCount = cyclesCount.value!!
                currentTaskDatabase?.secondsRemaining = secondsRemaining.value!!
                currentTaskDatabase?.focusedTime = updateFocusedTimeUI()
                database.update(currentTaskDatabase!!)
            }
        }
    }

    fun startFocusTimer() {
        Log.i("MainActivity", "timerState in startFocus: ${timerState.value}")
        Log.i("MainActivity", "cycles Count in startFocus: ${cyclesCount.value}")
        when (timerState.value) {
            NotStarted -> {
                timerLengthSeconds = currentTask.value!!.timerLength
                _previousTimerState.value = NotStarted
            }
            RestCompleted -> {
                timerLengthSeconds = currentTask.value!!.timerLength
                _previousTimerState.value = RestCompleted
            }
            else -> {
                if (::timerFocus.isInitialized) {
                    timerLengthSeconds = secondsRemaining.value!!
                }
                _previousTimerState.value = OnFocusPaused
            }
        }
        //setting seconds Remaining to start the animation
        _secondsRemaining.value = timerLengthSeconds
        _timerState.value = OnFocusRunning

        Log.i(
            "Main Activity",
            "current state: ${timerState.value}, previous state: ${previousTimerState.value}"
        )

        timerFocus = object : CountDownTimer(timerLengthSeconds * 1000, 1000) {
            override fun onFinish() {
                _timerState.value = Completed
                _cyclesCount.value = cyclesCount.value!! + 1
                updateCurrentTaskPropertiesInDatabase()
                timerFocus.cancel()
                startRestTimer()
            }

            override fun onTick(millisUntilFinished: Long) {
                val secondsRemainingInCountdown = millisUntilFinished / 1000
                lastSavedTimer = secondsRemainingInCountdown
                _secondsRemaining.value = lastSavedTimer
                updateCurrentTaskPropertiesInDatabase()
            }
        }.start()
    }

    fun startRestTimer() {
        if (timerState.value == OnRestPaused) {
            timerLengthSeconds = lastSavedTimer
            _previousTimerState.value = OnRestPaused

/*there's a small bug in the app: when the timer is on Pause, we authomatically miss a second
                when we restart the timer.*/

        } else {
            timerLengthSeconds = if (isMultipleOf4(cyclesCount.value!!)) {
                currentTask.value!!.longBreakLength
            } else {
                currentTask.value!!.shortBreakLength
            }
            _previousTimerState.value = OnFocusRunning
            //TODO: Manage the else case, add different scenarios: focus runningpaused, restpaused
        }

        //setting seconds Remaining to start the animation
        _secondsRemaining.value = timerLengthSeconds

        _timerState.value = OnRestRunning

        timerRest = object : CountDownTimer(timerLengthSeconds * 1000, 1000) {

            override fun onFinish() {
                _timerState.value = RestCompleted
                //_cyclesCount.value = cyclesCount.value!! + 1
                updateCurrentTaskPropertiesInDatabase()
                timerRest.cancel()
                startFocusTimer()
            }

            override fun onTick(millisUntilFinished: Long) {
                val secondsRemainingInCountdown = millisUntilFinished / 1000
                lastSavedTimer = secondsRemainingInCountdown
                _secondsRemaining.value = lastSavedTimer

                //NO PUEDO UNIR ESTOS DOS EN LA MISMA FUNCION?
                updateCurrentTaskPropertiesInDatabase()

            }

        }.start()
    }

    fun cancelTimers() {
        //If the user is clicking on the current runnning task, don't do anything
        if (selectedTaskId.value !== currentTask.value!!.taskId) {
            if (::timerFocus.isInitialized) {
                timerFocus.cancel()
            }
            if (::timerRest.isInitialized) {
                timerRest.cancel()
            }
        }
    }

    fun onNextCycle() {
        when (timerState.value) {
            OnFocusRunning -> timerFocus.onFinish()
            OnFocusPaused -> timerFocus.onFinish()
            OnRestRunning -> timerRest.onFinish()
            OnRestPaused -> timerRest.onFinish()
            else -> {
            }
        }
    }

    fun onPause() {
        /*The original idea was to save properties into database in this function. This was adding an unnecesary overload
            since we've already saved the state in the last count of the clock. And then we are saving some
            code in onResume method, since a task will never be saved in Pause states*/
        if (timerState.value == OnFocusRunning) {
            _timerState.value = OnFocusPaused
            timerFocus.cancel()
            //updateCurrentTaskPropertiesInDatabase()
        }
        if (timerState.value == OnRestRunning) {
            _timerState.value = OnRestPaused
            timerRest.cancel()
            //updateCurrentTaskPropertiesInDatabase()
        }
    }

    private fun onResume(taskId: Long) {

        //CUANDO NO LO PAUSO, NO CAMBIA EL LIVE DATA
        viewModelScope.launch {
            val task = database.get(taskId)!!
            Log.i("MainActivity", "onResume function called")
            _secondsRemaining.postValue(0)
            _cyclesCount.postValue(task.cyclesCount)

            when (task.timerState) {
                OnFocusRunning -> {
                    _timerState.postValue(NotStarted)
                    _previousTimerState.postValue(NotStarted)
                    /*when the user STOPS the focus running, it means they haven't finished the focus time
                        so when coming back, it restarts the same cycle*/
                    if (cyclesCount.value !== 0) {
                        _cyclesCount.postValue(task.cyclesCount)
                    } else {
                        _cyclesCount.postValue(1)
                    }
                }
                OnRestRunning -> {
                    _cyclesCount.postValue(cyclesCount.value!!)
                    _timerState.postValue(RestCompleted)
                }
                //OnFocusPause and OnRestPause are not possible here, since data is not saved in onPause method
                else -> {
                    _timerState.postValue(NotStarted)
                }
            }
            Log.i("MainActivity", "timerState: ${timerState.value}")
            Log.i("MainActivity", "cycles Count: ${cyclesCount.value}")
        }
    }

    private fun isMultipleOf4(cycles: Int): Boolean {
        return cycles.toFloat() % 4 == 0F
    }

    fun createNewTask(name: String, timerLength: Int, shortBreakLength: Int, longBreakLength: Int) {
        cancelTimers()
        _timerState.value = NotStarted
        val newTask = Task()
        viewModelScope.launch {
            newTask.name = name

            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            newTask.dateCreated = "$day/$month/$year"

            newTask.timerLength = timerLength * 60L
            newTask.shortBreakLength = shortBreakLength * 60L
            newTask.longBreakLength = longBreakLength * 60L

            insert(newTask)
            Log.i("MainActivity", "after Insert New Task")
            //Log.i("Main Activity", "task created: ${newTask}")
            getCurrentTaskFromDatabase()
            Log.i("MainActivity", "after Initialize current Task")

        }
    }

    private suspend fun insert(task: Task) {
        database.insert(task)
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            database.deleteTask(taskId)
            instantiateUI()
        }
    }

    fun deleteAllTasks() {
        viewModelScope.launch {
            database.clear()
        }
        instantiateUI()
    }

    private fun updateFocusedTimeUI(): String {
        val secondsCompleted = cyclesCount.value!! * (currentTask.value!!.timerLength.toInt())

        val hours = secondsCompleted / 3600
        val minutes = (secondsCompleted % 3600) / 60
        //val seconds = secondsCompleted % 60;

        return "$hours hs $minutes mins"

    }
}




