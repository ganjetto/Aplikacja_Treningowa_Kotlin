package com.example.apkatreningowakotlin

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

class ExerciseViewHolder(itemView: View) : ViewHolder(itemView) {
    var textViewExerciseName: TextView = itemView.findViewById(R.id.text_view_exercise_name)
    var textViewDuration: TextView = itemView.findViewById(R.id.text_view_exercise_duration)
    var textViewSetsReps: TextView = itemView.findViewById(R.id.text_view_exercise_sets_reps)
}

