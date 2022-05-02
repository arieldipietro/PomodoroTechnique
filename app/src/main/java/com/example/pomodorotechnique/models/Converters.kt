package com.example.pomodorotechnique.models

//import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.example.pomodorotechnique.database.Task2

//@ProvidedTypeConverter
class Converters {
    @TypeConverter
    fun fromIntToTask(value: Long?): Task2? {
        return value?.let { Task2(it) }
    }

    @TypeConverter
    fun fromTaskToInt(task: Task2?): Long? {
        return task?.toString()!!.toLong()
    }
}