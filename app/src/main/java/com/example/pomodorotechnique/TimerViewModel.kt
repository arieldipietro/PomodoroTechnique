package com.example.pomodorotechnique

import android.app.Application
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.*
import com.example.pomodorotechnique.database.Task2
import com.example.pomodorotechnique.database.TasksDatabaseDao
import com.example.pomodorotechnique.models.TimerState
import com.example.pomodorotechnique.models.TimerState.*
import kotlinx.coroutines.*
import java.util.*

class TimerViewModel(
    val database: TasksDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    object Timers {
        const val INITIAL_FOCUS_TIME = 3*1L
        const val SHORT_REST_TIME = 3*1L
        const val LONG_REST_TIME = 10*1L
    }

    private lateinit var timerFocus : CountDownTimer
    private lateinit var timerRest : CountDownTimer

    private var timerLengthSeconds = 0L
    private var lastSavedTimer = 0L

    private val _currentTask = MutableLiveData<Task2>()
    val currentTask : LiveData<Task2>
        get() = _currentTask

    val allTasks = database.getAllTasks()

    private val _timerState = MutableLiveData<TimerState>()
    val timerState : LiveData<TimerState>
        get() = _timerState

    private val _previousTimerState = MutableLiveData<TimerState>()
    val previousTimerState : LiveData<TimerState>
        get() = _previousTimerState

    private val _secondsRemaining = MutableLiveData<Long>()
    val secondsRemaining : LiveData<Long> get() = _secondsRemaining

    private val _cyclesCount = MutableLiveData<Int>()
    val cyclesCount : LiveData<Int> get() = _cyclesCount

    /*fun getCurrentTaskFromDatabase() {
        Log.i("Mainctivity", "getCurrentTaskFromDatabase")
        val job = GlobalScope.launch { // creates worker thread
            //Since we need to update UI from the coroutine, we should execute it on the main context
            withContext(Dispatchers.Default) {
                Log.i("MainActivity", "getCurrenttask called: ")

                val currentTaskFromDatabase = database.getCurrentTask()!!
                _currentTask.postValue(currentTaskFromDatabase)
                //updateLiveData()
            }
        }
        runBlocking {
            job.join() // wait until child coroutine completes
            updateLiveData()
        }
    }*/

    private fun initializeCurrentTask(){
        viewModelScope.launch {
            _currentTask.value = getCurrentTaskFromDatabase()!!
        }
    }
    suspend fun getCurrentTaskFromDatabase(): Task2? {
        var currentTask = database.getCurrentTask()
        return currentTask
        }

    fun initializeSelectedTask(){
        viewModelScope.launch {
            _currentTask.value = getSelectedTaskFromDatabase(selectedTaskId.value!!)!!
        }
    }
    suspend fun getSelectedTaskFromDatabase(taskId: Long): Task2? {
        var currentTask = database.get(selectedTaskId.value!!)!!
        return currentTask
    }
    private val _selectedTaskId = MutableLiveData<Long>()
    val selectedTaskId : LiveData<Long> get() = _selectedTaskId

    fun setSelectedTaskId(taskId: Long){
        _selectedTaskId.value = taskId
        Log.i("MainActivity", "selectedTaskId called, changed ID: ${selectedTaskId.value}")
    }




    /*fun getSelectedTaskFromDatabase(taskId: Long) {
        val currentTaskFromDatabase = Task2(0L, "FakeTask", "", 0L, 0, NotStarted, 0L)
        Log.i("Mainctivity", "getCurrentTaskFromDatabase")
        val job = GlobalScope.launch { // creates worker thread
            withContext(Dispatchers.Default) {
                Log.i("MainActivity", "getCurrenttask called: ")

                val currentTaskFromDatabase = database.get(taskId)!!
                _currentTask.postValue(currentTaskFromDatabase)
                //updateLiveData()
                Log.i("MainActivity", "current task: ${currentTask.value} ")


            }
        }
        runBlocking {
            job.join() // wait until child coroutine completes
            //updateLiveData()
            _cyclesCount.value = currentTaskFromDatabase.cyclesCount
            _secondsRemaining.value = currentTaskFromDatabase.secondsRemaining
            _timerState.value = currentTaskFromDatabase.timerState
        }
    }*/



    init {
        //init called
        updateLiveData()
    }

    fun instantiateUI(){
        //creating a dummy task to instantiate the UI
        _currentTask.value = Task2(0L, "FakeTask", "", 0L, 0, NotStarted, 0L)
        _cyclesCount.value = 0
        _secondsRemaining.value = 0L
        _timerState.value = NotStarted
    }

    fun updateLiveData() {
        val job = GlobalScope.async { // creates worker thread
            _cyclesCount.value = currentTask.value!!.cyclesCount
            _secondsRemaining.value = currentTask.value!!.secondsRemaining
            _timerState.value = currentTask.value!!.timerState
        }
/*
        Log.i("MainActivity", "updated live data in database: ${currentTask.value}")
        Log.i("MainActivity", "cycles count: ${cyclesCount.value}")
        Log.i("MainActivity", "seconds remaining: ${secondsRemaining.value}")
        Log.i("MainActivity", "timerState: ${timerState.value}")*/
    }


    //call this function before UI changes and when destroying viws
    fun updateCurrentTaskPropertiesInDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            if(database.getCurrentTask() !== null) {
                val currentTaskDatabase = database.getCurrentTask()
                currentTaskDatabase?.timerState = timerState.value!!
                currentTaskDatabase?.cyclesCount = cyclesCount.value!!
                currentTaskDatabase?.secondsRemaining = secondsRemaining.value!!
                database.update(currentTaskDatabase!!)
            }
        }
    }

    fun startFocusTimer() {
   /*     Log.i("MainActivity", "Start focus timer called")
        Log.i("MainActivity", "Starting focus timer from: ${timerState.value}")*/
        if (timerState.value == NotStarted) {
            timerLengthSeconds = Timers.INITIAL_FOCUS_TIME
            _cyclesCount.value = 0
            _previousTimerState.value = NotStarted
        }
        if (timerState.value == RestCompleted) {
            timerLengthSeconds = Timers.INITIAL_FOCUS_TIME
            _previousTimerState.value = RestCompleted
        } else {
            lastSavedTimer
            _previousTimerState.value = OnFocusPaused
        }
            //setting seconds Remaining to start the animation
            _secondsRemaining.value = timerLengthSeconds
            _timerState.value = OnFocusRunning

        //Log.i("Main Activity", "current state: ${timerState.value}, previous state: ${previousTimerState.value}")


            timerFocus = object : CountDownTimer(timerLengthSeconds * 1000, 1000) {
                override fun onFinish() {
                    _timerState.value = TimerState.Completed
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

/* Log.i("MainActivity", "timerState: ${timerState.value}")
        Log.i("MainActivity", "cycle: ${cyclesCount2.value}")*/


                if (timerState.value == OnRestPaused) {
                    timerLengthSeconds = lastSavedTimer
                    _previousTimerState.value = OnRestPaused

/*there's a small bug in the app: when the timer is on Pause, we authomatically miss a second
                when we restart the timer.*/

                } else {
                    if (isMultipleOf4(cyclesCount.value!!)) {
                        timerLengthSeconds = Timers.SHORT_REST_TIME
                    } else {
                        timerLengthSeconds = Timers.LONG_REST_TIME
                    }
                    _previousTimerState.value = OnFocusRunning
                    //TODO: Manage the else case, add different scenarios: focus runningpaused, restpaused
                }

            //setting seconds Remaining to start the animation
            _secondsRemaining.value = timerLengthSeconds

            _timerState.value = OnRestRunning
            //updateCurrentTaskTimerState()

            timerRest = object : CountDownTimer(timerLengthSeconds * 1000, 1000) {

                override fun onFinish() {
                    _timerState.value = RestCompleted
                    _cyclesCount.value = cyclesCount.value!! + 1
                    updateCurrentTaskPropertiesInDatabase()
                    timerRest.cancel()
                    startFocusTimer()
                }

                override fun onTick(millisUntilFinished: Long) {
                    val secondsRemainingInCountdown = millisUntilFinished / 1000
                    lastSavedTimer = secondsRemainingInCountdown
                    _secondsRemaining.value = lastSavedTimer
                    updateCurrentTaskPropertiesInDatabase()

                }

            }.start()
        }

        fun onPause() {
            if (timerState.value == OnFocusRunning) {
                _timerState.value = OnFocusPaused
                timerFocus.cancel()
                updateCurrentTaskPropertiesInDatabase()
            }
            if (timerState.value == OnRestRunning) {
                _timerState.value = OnRestPaused
                timerRest.cancel()
                updateCurrentTaskPropertiesInDatabase()
            }
            else{}
        }

        fun onReset() {
            _timerState.value = NotStarted
            //updateCurrentTaskTimerState()
            _cyclesCount.value = 0
            //updateCurrentTaskCyclesCount()
            _secondsRemaining.value = 0
            if (::timerFocus.isInitialized) {
                timerFocus.cancel()
            }
        }

        fun isMultipleOf4(cycles: Int): Boolean {
            if (cycles.toFloat() % 4 == 0F) {
                return true
            } else {
                return false
            }
        }

    fun createNewTask(name: String) {
        val newTask = Task2()
        viewModelScope.launch {
            newTask.name = name

            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            newTask.dateCreated = "$day/$month/$year"

            insert(newTask)
            Log.i("MainActivity", "after Insert New Task")
            //Log.i("Main Activity", "task created: ${newTask}")
            initializeCurrentTask()
            Log.i("MainActivity", "after Initialize current Task")

        }
    }

    suspend fun insert(task: Task2){
        database.insert(task)
    }
    fun deleteTask(taskId: Long){
        viewModelScope.launch {
            database.deleteTask(taskId)
        }
    }
}




