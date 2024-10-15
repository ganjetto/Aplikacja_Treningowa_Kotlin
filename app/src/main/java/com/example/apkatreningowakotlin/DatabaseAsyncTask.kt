package com.example.apkatreningowakotlin

import android.os.AsyncTask

class DatabaseAsyncTask(private val task: Runnable) : AsyncTask<Unit, Unit, Unit>() {
    override fun doInBackground(vararg params: Unit?): Unit? {
        task.run()
        return null
    }
}
