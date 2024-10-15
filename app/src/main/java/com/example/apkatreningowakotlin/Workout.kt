package com.example.apkatreningowakotlin

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_table")
data class Workout(
    val name: String,
    val description: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
        private set

    var exercises: List<Exercise>? = null

    fun setId(tmpId: Int) {
        this.id = tmpId.toLong()
    }
}
