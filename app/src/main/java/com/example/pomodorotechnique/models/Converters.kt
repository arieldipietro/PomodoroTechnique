package com.example.pomodorotechnique.models

//import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.example.pomodorotechnique.database.Task

//@ProvidedTypeConverter
class Converters {
    @TypeConverter
    fun fromIntToTask(value: Long?): Task? {
        return value?.let { Task(it) }
    }

    @TypeConverter
    fun fromTaskToInt(task: Task?): Long? {
        return task?.toString()!!.toLong()
    }
}