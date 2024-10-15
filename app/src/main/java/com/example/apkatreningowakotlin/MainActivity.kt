package com.example.apkatreningowakotlin

import android.content.Intent
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

class MainActivity : AppCompatActivity() {

    private lateinit var editTextName: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var database: WorkoutDatabase
    private lateinit var adapter: WorkoutAdapter
    private var currentWorkoutId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextName = findViewById(R.id.edit_text_name)
        editTextDescription = findViewById(R.id.edit_text_description)
        recyclerView = findViewById(R.id.recycler_view)
        val buttonAddWorkout: Button = findViewById(R.id.button_add_workout)

        database = WorkoutDatabase.getInstance(this) ?: throw IllegalStateException("Database not initialized")

        adapter = WorkoutAdapter()
        adapter.setOnItemClickListener(object : WorkoutAdapter.OnItemClickListener {
            override fun onPlayClick(workout: Workout?) {
                currentWorkoutId = workout!!.id
                val intent = Intent(this@MainActivity, StartWorkoutActivity::class.java)
                intent.putExtra("workoutId", currentWorkoutId)
                startActivity(intent)
            }

            override fun onEditClick(workout: Workout?) {
                currentWorkoutId = workout!!.id
                val intent = Intent(this@MainActivity, EditWorkoutActivity::class.java)
                intent.putExtra(EditWorkoutActivity.EXTRA_WORKOUT_ID, currentWorkoutId)
                startActivity(intent)
            }

            override fun onDeleteClick(workout: Workout?) {
                if (workout != null) {
                    deleteWorkout(workout)
                }
            }
        })

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        buttonAddWorkout.setOnClickListener { addWorkout() }

        val itemTouchHelper = ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition
                adapter.onItemMove(fromPosition, toPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val workoutToDelete = adapter.getWorkout(position)
                deleteWorkout(workoutToDelete!!)
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    viewHolder?.itemView?.setBackgroundResource(R.drawable.selected_border)
                }
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                viewHolder.itemView.setBackgroundResource(R.drawable.normal_border)
            }
        })

        itemTouchHelper.attachToRecyclerView(recyclerView)
        loadWorkouts()
    }

    private fun addWorkout() {
        val name = editTextName.text.toString().trim()
        val description = editTextDescription.text.toString().trim()

        if (name.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please enter a name and description", Toast.LENGTH_SHORT).show()
            return
        }

        val workout = Workout(name, description)
        CoroutineScope(Dispatchers.IO).launch {
            val workoutId = database.workoutDao().insert(workout)
            withContext(Dispatchers.Main) {
                currentWorkoutId = workoutId
                clearFields()
                loadWorkouts()
            }
        }
    }

    private fun loadWorkouts() {
        CoroutineScope(Dispatchers.IO).launch {
            val workouts = database.workoutDao().allWorkouts
            withContext(Dispatchers.Main) {
                adapter.setWorkouts(workouts)
            }
        }
    }

    private fun clearFields() {
        editTextName.setText("")
        editTextDescription.setText("")
        currentWorkoutId = -1
    }

    private fun deleteWorkout(workout: Workout) {
        CoroutineScope(Dispatchers.IO).launch {
            database.workoutDao().delete(workout.id)
            withContext(Dispatchers.Main) {
                Toast.makeText(applicationContext, "Workout deleted", Toast.LENGTH_SHORT).show()
                loadWorkouts()
            }
        }
    }
}
