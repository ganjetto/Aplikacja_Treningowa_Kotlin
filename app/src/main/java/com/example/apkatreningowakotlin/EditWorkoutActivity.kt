package com.example.apkatreningowakotlin

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditWorkoutActivity : AppCompatActivity() {

    private lateinit var editTextExerciseName: EditText
    private lateinit var editTextDuration: EditText
    private lateinit var editTextSets: EditText
    private lateinit var editTextRepetitions: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var database: WorkoutDatabase
    private lateinit var adapter: ExerciseAdapter
    private var currentWorkoutId: Long = -1
    private lateinit var itemTouchHelper: ItemTouchHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_workout)

        editTextExerciseName = findViewById(R.id.edit_text_exercise_name)
        editTextDuration = findViewById(R.id.edit_text_duration)
        editTextSets = findViewById(R.id.edit_text_sets)
        editTextRepetitions = findViewById(R.id.edit_text_repetitions)
        recyclerView = findViewById(R.id.exercises_recycler_view)
        val buttonAddExercise: Button = findViewById(R.id.button_add_exercise)

        adapter = ExerciseAdapter(mutableListOf())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        database = WorkoutDatabase.getInstance(this) ?: throw IllegalStateException("Database not initialized")

        currentWorkoutId = intent.getLongExtra(EXTRA_WORKOUT_ID, -1)
        loadExercises()

        buttonAddExercise.setOnClickListener { addExercise() }
        adapter.setOnItemClickListener(object : ExerciseAdapter.OnItemClickListener {
            override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
                itemTouchHelper.startDrag(viewHolder)
            }
        })

        val callback: ItemTouchHelper.Callback = object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                return makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                adapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                    viewHolder?.itemView?.setBackgroundColor(resources.getColor(android.R.color.darker_gray))
                }
                super.onSelectedChanged(viewHolder, actionState)
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                viewHolder.itemView.setBackgroundColor(0)
            }
        }

        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun loadExercises() {
        CoroutineScope(Dispatchers.IO).launch {
            val exercises = database.exerciseDao().getExercisesForWorkout(currentWorkoutId)
            withContext(Dispatchers.Main) {
                adapter.setExercises(exercises)
            }
        }
    }

    private fun addExercise() {
        if (currentWorkoutId == -1L) {
            Toast.makeText(this, "Error: No workout selected", Toast.LENGTH_SHORT).show()
            return
        }

        val exerciseName = editTextExerciseName.text.toString().trim()
        val duration = editTextDuration.text.toString().trim().toIntOrNull() ?: 0
        val sets = editTextSets.text.toString().trim().toIntOrNull() ?: 0
        val repetitions = editTextRepetitions.text.toString().trim().toIntOrNull() ?: 0

        val exercise = Exercise(currentWorkoutId, exerciseName, duration, sets, repetitions)

        CoroutineScope(Dispatchers.IO).launch {
            database.exerciseDao().insert(exercise)
            withContext(Dispatchers.Main) {
                loadExercises()
            }
        }

        editTextExerciseName.setText("")
        editTextDuration.setText("")
        editTextSets.setText("")
        editTextRepetitions.setText("")
    }

    companion object {
        const val EXTRA_WORKOUT_ID = "extra_workout_id"
    }
}
