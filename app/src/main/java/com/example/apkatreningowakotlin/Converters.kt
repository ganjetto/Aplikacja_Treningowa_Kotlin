package com.example.apkatreningowakotlin

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.util.Log

object Converters {
    private val gson = Gson()

    @JvmStatic
    @TypeConverter
    fun fromString(value: String?): List<Exercise> {
        return if (value.isNullOrEmpty()) {
            emptyList()
        } else {
            try {
                val listType = object : TypeToken<List<Exercise>>() {}.type
                gson.fromJson<List<Exercise>>(value, listType) ?: emptyList()
            } catch (e: Exception) {
                Log.e("Converters", "Error converting from JSON string", e)
                emptyList()
            }
        }
    }

    @JvmStatic
    @TypeConverter
    fun fromList(list: List<Exercise>?): String {
        return gson.toJson(list)
    }
}
