package com.example.services

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import java.io.File
import java.util.Locale

data class BatteryStatus(
    val percentage: Int,
    val statusText: String,
    val isCharging: Boolean
)

data class WeatherInfo(
    val temperature: String = "28°C",
    val condition: String = "Partly Cloudy ⛅",
    val location: String = "Patna, Bihar",
    val humidity: String = "64%",
    val windSpeed: String = "12 km/h"
)

data class StorageDetails(
    val cacheSizeMB: Double,
    val freeSpaceGB: Double,
    val totalSpaceGB: Double
)

object StorageWeatherService {

    fun getBatteryStatus(context: Context): BatteryStatus {
        return try {
            val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val batteryStatus: Intent? = context.registerReceiver(null, filter)

            val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: 85
            val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: 100
            val pct = (level * 100 / scale.toFloat()).toInt()

            val status = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
            val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL

            val statusText = if (isCharging) "Charging ⚡" else "Discharging"
            BatteryStatus(pct, statusText, isCharging)
        } catch (e: Exception) {
            BatteryStatus(85, "Discharging", false)
        }
    }

    fun getWeatherInfo(): WeatherInfo {
        // Structured to easily swap in OpenWeather or Weather API fetch
        return WeatherInfo(
            temperature = "28°C",
            condition = "Partly Cloudy ⛅",
            location = "Patna, Bihar",
            humidity = "64%",
            windSpeed = "12 km/h"
        )
    }

    fun getStorageDetails(context: Context): StorageDetails {
        val cacheSize = getFolderSize(context.cacheDir)
        val cacheSizeMB = cacheSize / (1024.0 * 1024.0)

        val filesDir = context.filesDir
        val freeBytes = filesDir.freeSpace
        val totalBytes = filesDir.totalSpace

        val freeGB = freeBytes / (1024.0 * 1024.0 * 1024.0)
        val totalGB = totalBytes / (1024.0 * 1024.0 * 1024.0)

        return StorageDetails(
            cacheSizeMB = String.format(Locale.US, "%.2f", cacheSizeMB).toDouble(),
            freeSpaceGB = String.format(Locale.US, "%.1f", freeGB).toDouble(),
            totalSpaceGB = String.format(Locale.US, "%.1f", totalGB).toDouble()
        )
    }

    fun clearAppCache(context: Context): String {
        return try {
            val initialSize = getFolderSize(context.cacheDir)
            deleteDirContent(context.cacheDir)
            val clearedMB = String.format(Locale.US, "%.2f", initialSize / (1024.0 * 1024.0))
            "App Cache Cleared! Freed $clearedMB MB storage 🧹"
        } catch (e: Exception) {
            "Error clearing cache: ${e.localizedMessage}"
        }
    }

    private fun getFolderSize(file: File?): Long {
        if (file == null || !file.exists()) return 0L
        if (file.isFile) return file.length()
        var size = 0L
        file.listFiles()?.forEach {
            size += getFolderSize(it)
        }
        return size
    }

    private fun deleteDirContent(dir: File?): Boolean {
        if (dir == null || !dir.exists() || !dir.isDirectory) return false
        dir.listFiles()?.forEach { child ->
            if (child.isDirectory) deleteDirContent(child)
            child.delete()
        }
        return true
    }
}
