package com.example.apkatreningowakotlin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.Collections

class ExerciseAdapter(private var exercises: MutableList<Exercise>) :
    RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    private var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    fun setExercises(exercises: List<Exercise?>?) {
        this.exercises.clear()
        exercises?.let { this.exercises.addAll(it.filterNotNull()) }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercise, parent, false)
        return ExerciseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val currentExercise = exercises[position]
        holder.textViewExerciseName.text = currentExercise.name

        if (currentExercise.duration != 0) {
            holder.textViewDuration.visibility = View.VISIBLE
            holder.textViewDuration.text = "Duration: ${currentExercise.duration} seconds"
        } else {
            holder.textViewDuration.visibility = View.GONE
        }

        val sets = if (currentExercise.sets != 0) "Sets: ${currentExercise.sets}" else ""
        val reps = if (currentExercise.repetitions != 0) "Reps: ${currentExercise.repetitions}" else ""

        if (sets.isEmpty() && reps.isEmpty()) {
            holder.textViewSetsReps.visibility = View.GONE
        } else {
            holder.textViewSetsReps.visibility = View.VISIBLE
            holder.textViewSetsReps.text = "$sets ${if (sets.isNotEmpty() && reps.isNotEmpty()) ", " else ""} $reps"
        }

        holder.itemView.setOnLongClickListener {
            listener?.onStartDrag(holder)
            true
        }
    }

    override fun getItemCount(): Int {
        return exercises.size
    }

    fun onItemMove(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(exercises, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(exercises, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewExerciseName = itemView.findViewById<TextView>(R.id.text_view_exercise_name)
        val textViewDuration = itemView.findViewById<TextView>(R.id.text_view_exercise_duration)
        val textViewSetsReps = itemView.findViewById<TextView>(R.id.text_view_exercise_sets_reps)
    }
}
