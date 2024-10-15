package com.example.apkatreningowakotlin

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface WorkoutDao {
    @Insert
    fun insert(workout: Workout?): Long

    @get:Query("SELECT * FROM workout_table")
    val allWorkouts: List<Workout?>?

    @Query("DELETE FROM workout_table WHERE id = :workoutId")
    fun delete(workoutId: Long)
}
