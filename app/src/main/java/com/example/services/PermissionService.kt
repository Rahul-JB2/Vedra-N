package com.example.services

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

data class PermissionStatus(
    val permissionName: String,
    val isGranted: Boolean,
    val requiredForFeature: String
)

object PermissionService {

    val REQUIRED_PERMISSIONS = listOf(
        Manifest.permission.RECORD_AUDIO to "Voice Commands & Ved AI",
        Manifest.permission.CAMERA to "QR Code & Document Scanner",
        Manifest.permission.READ_CONTACTS to "Contact Aliases & Calls",
        Manifest.permission.SEND_SMS to "SMS Messaging Actions",
        Manifest.permission.READ_CALENDAR to "Calendar Event Scheduling",
        Manifest.permission.CALL_PHONE to "Direct Voice Calling"
    )

    fun checkAllPermissions(context: Context): List<PermissionStatus> {
        val list = mutableListOf<PermissionStatus>()
        for ((perm, feature) in REQUIRED_PERMISSIONS) {
            val isGranted = ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED
            list.add(PermissionStatus(perm, isGranted, feature))
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val isNotifGranted = ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            list.add(PermissionStatus(Manifest.permission.POST_NOTIFICATIONS, isNotifGranted, "Push Notifications & Alarms"))
        }

        return list
    }

    fun isPermissionGranted(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }
}
