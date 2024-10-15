package com.example.apkatreningowakotlin

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.Executors

class StartWorkoutActivity : AppCompatActivity() {
    private var textViewExerciseName: TextView? = null
    private var textViewExerciseDuration: TextView? = null
    private var textViewExerciseSetsReps: TextView? = null
    private var buttonNextExercise: Button? = null
    private var database: WorkoutDatabase? = null
    private var exercises: List<Exercise>? = null
    private var currentExerciseIndex = 0
    private var timer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_workout)

        textViewExerciseName = findViewById(R.id.text_view_exercise_name)
        textViewExerciseDuration = findViewById(R.id.text_view_exercise_duration)
        textViewExerciseSetsReps = findViewById(R.id.text_view_exercise_sets_reps)
        buttonNextExercise = findViewById(R.id.button_next_exercise)

        database = WorkoutDatabase.getInstance(this)
        val workoutId = intent.getLongExtra("workoutId", -1)

        loadExercises(workoutId)

        buttonNextExercise!!.setOnClickListener { v -> showNextExercise() }
    }

    private fun loadExercises(workoutId: Long) {
        Executors.newSingleThreadExecutor().execute {
            val exercisesList = database!!.exerciseDao().getExercisesForWorkout(workoutId)
            exercises = exercisesList?.filterNotNull()
            runOnUiThread { showNextExercise() }
        }
    }

    private fun showNextExercise() {
        if (currentExerciseIndex >= exercises!!.size) {
            finish()
            return
        }

        if (currentExerciseIndex == exercises!!.size - 1) {
            buttonNextExercise!!.text = "Finish workout"
        } else {
            buttonNextExercise!!.text = "Next exercise"
        }

        val exercise = exercises!![currentExerciseIndex]
        textViewExerciseName!!.text = exercise.name

        if (exercise.duration != 0) {
            textViewExerciseDuration!!.visibility = View.VISIBLE
            startTimer(exercise.duration)
        } else {
            textViewExerciseDuration!!.visibility = View.GONE
        }

        if (exercise.sets != 0 || exercise.repetitions != 0) {
            textViewExerciseSetsReps!!.visibility = View.VISIBLE
            textViewExerciseSetsReps!!.text =
                "Sets: ${exercise.sets}, Reps: ${exercise.repetitions}"
        } else {
            textViewExerciseSetsReps!!.visibility = View.GONE
        }

        currentExerciseIndex++
    }

    private fun startTimer(duration: Int) {
        timer?.cancel()

        timer = object : CountDownTimer((duration * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 60000
                val seconds = (millisUntilFinished % 60000) / 1000
                textViewExerciseDuration!!.text =
                    "Duration: $minutes:${String.format("%02d", seconds)}"
            }

            override fun onFinish() {
                textViewExerciseDuration!!.text = "Duration: 0:00"
                showNextExercise()
            }
        }.start()
    }
}
