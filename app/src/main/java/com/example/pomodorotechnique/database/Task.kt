package com.example.pomodorotechnique.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pomodorotechnique.models.TimerState

@Entity(tableName = "tasks_history_table")
data class Task(

    @PrimaryKey(autoGenerate = true)
    var taskId: Long = 0L,

    @ColumnInfo(name = "name")
    var name: String = "",

    @ColumnInfo(name = "date_created")
    var dateCreated: String = "",

    @ColumnInfo(name = "seconds_remaining")
    var secondsRemaining : Long = 0L,

    @ColumnInfo(name = "cycles_count")
    var cyclesCount : Int = 0,

    @ColumnInfo(name = "timer_state")
    var timerState : TimerState = TimerState.NotStarted,

    @ColumnInfo(name = "focused_time")
    var focusedTime : String = "",

    @ColumnInfo(name = "timer_length")
    var timerLength : Long = 0L,

    @ColumnInfo(name = "short_break_length")
    var shortBreakLength : Long = 0L,

    @ColumnInfo(name = "long_break_length")
    var longBreakLength : Long = 0L

    )


               
