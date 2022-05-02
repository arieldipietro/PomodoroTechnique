package com.example.pomodorotechnique.database

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * Defines methods for using the SleepNight class with Room.
 */
@Dao
interface TasksDatabaseDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(task: Task)

    /**
     * When updating a row with a value already set in a column,
     * replaces the old value with the new one.
     *
     * @param task new value to write
     */
    @Update
    suspend fun update(task: Task)

    @Query("SELECT COUNT(*) FROM tasks_history_table")
    fun getCount(): Int

    /**
     * Selects and returns the row that matches the supplied Id, which is our key.
     */
    @Query("SELECT * from tasks_history_table WHERE  taskId = :key ")
    suspend fun get(key: Long): Task?

    /**
     * Deletes all values from the table.
     *
     * This does not delete the table, only its contents.
     */
    @Query("DELETE FROM tasks_history_table")
    suspend fun clear()

    @Query("DELETE FROM tasks_history_table WHERE  taskId = :key")
    suspend fun deleteTask(key: Long)

    /**
     * Selects and returns all rows in the table,
     *
     * sorted by Id in descending order.
     */
    @Query("SELECT * FROM tasks_history_table ORDER BY date_created DESC")
    fun getAllTasks(): LiveData<List<Task>>

    /**
     * Selects and returns the latest task.
     */
    @Query("SELECT * FROM tasks_history_table ORDER BY taskId DESC LIMIT 1")
    fun getCurrentTask(): Task?

}

