package com.example.pomodorotechnique.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.pomodorotechnique.TimerViewModel
import com.example.pomodorotechnique.models.Converters
import com.example.pomodorotechnique.models.TimerState

@Entity(tableName = "tasks_history_table")
@TypeConverters(Converters::class)
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
    var focusedTime : String = ""

    )


               
