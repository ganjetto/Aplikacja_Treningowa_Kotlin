package com.example.apkatreningowakotlin

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ExerciseDao {
    @Insert
    fun insert(exercise: Exercise?)

    @Query("SELECT * FROM exercise_table WHERE workoutId = :workoutId")
    fun getExercisesForWorkout(workoutId: Long): List<Exercise?>?
}
