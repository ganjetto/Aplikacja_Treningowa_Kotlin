package com.example.apkatreningowakotlin

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "exercise_table",
    foreignKeys = [ForeignKey(
        entity = Workout::class,
        parentColumns = ["id"],
        childColumns = ["workoutId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Exercise(
    val workoutId: Long,
    var name: String,
    val duration: Int,
    val sets: Int,
    val repetitions: Int
) : Serializable {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
