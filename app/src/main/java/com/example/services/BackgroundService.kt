package com.example.services

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object BackgroundService {

    private var isStarted = false

    fun startBackgroundTasks(context: Context, dbService: DatabaseService) {
        if (isStarted) return
        isStarted = true

        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                try {
                    // Periodic cache optimization & study reminders
                    performDailyCacheCleanup(dbService)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                // Check every 6 hours
                delay(6 * 60 * 60 * 1000L)
            }
        }
    }

    private fun performDailyCacheCleanup(dbService: DatabaseService) {
        // Keeps user database optimized and ready
        val writableDb = dbService.writableDatabase
        writableDb.execSQL("VACUUM;")
    }
}
