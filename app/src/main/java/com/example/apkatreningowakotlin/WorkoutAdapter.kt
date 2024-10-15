package com.example.apkatreningowakotlin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.apkatreningowakotlin.WorkoutAdapter.WorkoutViewHolder
import java.util.Collections

class WorkoutAdapter : RecyclerView.Adapter<WorkoutViewHolder>() {
    private var workouts: List<Workout?>? = ArrayList()
    private var listener: OnItemClickListener? = null
    private var selectedPosition = RecyclerView.NO_POSITION

    interface OnItemClickListener {
        fun onPlayClick(workout: Workout?)
        fun onEditClick(workout: Workout?)
        fun onDeleteClick(workout: Workout?)
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        this.listener = listener
    }

    fun getWorkout(position: Int): Workout? {
        return if (position < workouts!!.size) {
            workouts!![position]
        } else {
            null
        }
    }

    fun setWorkouts(workouts: List<Workout?>?) {
        this.workouts = workouts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.workout_item, parent, false)
        return WorkoutViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val currentWorkout = workouts!![position]
        holder.textViewName.text = currentWorkout?.name;
        holder.textViewDescription.text = currentWorkout?.description;

        if (selectedPosition == position) {
            holder.itemView.setBackgroundResource(R.drawable.selected_border)
            holder.buttonLayout.visibility = View.VISIBLE
        } else {
            holder.itemView.setBackgroundResource(R.drawable.normal_border)
            holder.buttonLayout.visibility = View.GONE
        }

        holder.itemView.setOnClickListener { v: View? ->
            val previousPosition = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
        }

        holder.buttonPlay.setOnClickListener { v: View? ->
            if (listener != null) {
                listener!!.onPlayClick(currentWorkout)
            }
        }

        holder.buttonEdit.setOnClickListener { v: View? ->
            if (listener != null) {
                listener!!.onEditClick(currentWorkout)
            }
        }

        holder.buttonDelete.setOnClickListener { v: View? ->
            if (listener != null) {
                listener!!.onDeleteClick(currentWorkout)
            }
        }
    }

    override fun getItemCount(): Int {
        return workouts!!.size
    }

    fun onItemMove(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(workouts, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(workouts, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    class WorkoutViewHolder(itemView: View) : ViewHolder(itemView) {
        val textViewName: TextView = itemView.findViewById(R.id.text_view_name)
        val textViewDescription: TextView = itemView.findViewById(R.id.text_view_description)
        val buttonLayout: LinearLayout = itemView.findViewById(R.id.button_layout)
        val buttonPlay: ImageButton = itemView.findViewById(R.id.button_play)
        val buttonEdit: ImageButton = itemView.findViewById(R.id.button_edit)
        val buttonDelete: ImageButton = itemView.findViewById(R.id.button_delete)
    }
}
