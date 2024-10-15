package com.example.apkatreningowakotlin

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Workout::class, Exercise::class], version = 3)
@TypeConverters(Converters::class)
abstract class WorkoutDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
    abstract fun exerciseDao(): ExerciseDao

    companion object {
        private var instance: WorkoutDatabase? = null

        @Synchronized
        fun getInstance(context: Context): WorkoutDatabase? {
            if (instance == null) {
                instance = databaseBuilder(
                    context.applicationContext,
                    WorkoutDatabase::class.java, "workout_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return instance
        }
    }
}
