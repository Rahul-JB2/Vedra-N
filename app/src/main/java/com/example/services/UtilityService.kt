package com.example.services

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.hardware.camera2.CameraManager
import java.util.Locale
import kotlin.math.pow
import org.json.JSONArray

data class UtilityResult(
    val isHandled: Boolean,
    val responseMessage: String,
    val actionType: String = "GENERAL",
    val eventData: CalendarEventItem? = null
)

object UtilityService {

    private var isTorchOn = false

    fun toggleFlashlight(context: Context, turnOn: Boolean? = null): String {
        return try {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val cameraId = cameraManager.cameraIdList.firstOrNull { id ->
                cameraManager.getCameraCharacteristics(id)
                    .get(android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            } ?: return "Flashlight hardware is not available on this device."

            val targetState = turnOn ?: !isTorchOn
            cameraManager.setTorchMode(cameraId, targetState)
            isTorchOn = targetState
            if (targetState) "Flashlight turned ON 🔦" else "Flashlight turned OFF 🔦"
        } catch (e: Exception) {
            "Flashlight control error: ${e.localizedMessage}"
        }
    }

    fun writeToClipboard(context: Context, text: String): String {
        return try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("VEDRA Clip", text)
            clipboard.setPrimaryClip(clip)
            "Copied to clipboard: \"$text\" 📋"
        } catch (e: Exception) {
            "Failed to copy to clipboard: ${e.localizedMessage}"
        }
    }

    fun readFromClipboard(context: Context): String {
        return try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            if (clipboard.hasPrimaryClip() && (clipboard.primaryClip?.itemCount ?: 0) > 0) {
                val item = clipboard.primaryClip?.getItemAt(0)
                val text = item?.text?.toString()
                if (!text.isNullOrEmpty()) {
                    "Clipboard contents: \"$text\" 📋"
                } else {
                    "Clipboard is empty."
                }
            } else {
                "Clipboard is empty."
            }
        } catch (e: Exception) {
            "Failed to read clipboard: ${e.localizedMessage}"
        }
    }

    fun evaluateMathExpression(input: String): String? {
        val clean = input.replace("calculate", "", ignoreCase = true)
            .replace("math", "", ignoreCase = true)
            .replace("what is", "", ignoreCase = true)
            .replace("=", "")
            .trim()

        if (clean.isEmpty()) return null

        val regex = Regex("""^(-?\d+(?:\.\d+)?)\s*([\+\-\*\/\^%])\s*(-?\d+(?:\.\d+)?)$""")
        val match = regex.find(clean) ?: return null

        val num1 = match.groupValues[1].toDoubleOrNull() ?: return null
        val op = match.groupValues[2]
        val num2 = match.groupValues[3].toDoubleOrNull() ?: return null

        val result = when (op) {
            "+" -> num1 + num2
            "-" -> num1 - num2
            "*" -> num1 * num2
            "/" -> if (num2 != 0.0) num1 / num2 else Double.NaN
            "%" -> num1 % num2
            "^" -> num1.pow(num2)
            else -> Double.NaN
        }

        return if (result.isNaN()) {
            "Math error: Division by zero."
        } else {
            val formatted = if (result % 1.0 == 0.0) result.toLong().toString() else String.format(Locale.US, "%.2f", result)
            "Result: $clean = $formatted 🧮"
        }
    }

    fun performUnitConversion(input: String): String? {
        val lower = input.lowercase(Locale.US).trim()
        if (!lower.contains(" to ")) return null

        val parts = lower.split(" to ")
        if (parts.size != 2) return null

        val leftTokens = parts[0].trim().split(" ")
        if (leftTokens.size < 2) return null

        val valString = leftTokens[0]
        val fromUnit = leftTokens.subList(1, leftTokens.size).joinToString(" ")
        val toUnit = parts[1].trim()

        val value = valString.toDoubleOrNull() ?: return null

        return when {
            (fromUnit == "kg" || fromUnit == "kgs" || fromUnit == "kilogram") && (toUnit == "lbs" || toUnit == "lb" || toUnit == "pounds") -> {
                val converted = value * 2.20462
                String.format(Locale.US, "%.2f kg = %.2f lbs ⚖️", value, converted)
            }
            (fromUnit == "lbs" || fromUnit == "lb" || fromUnit == "pounds") && (toUnit == "kg" || toUnit == "kgs" || toUnit == "kilogram") -> {
                val converted = value / 2.20462
                String.format(Locale.US, "%.2f lbs = %.2f kg ⚖️", value, converted)
            }
            (fromUnit == "km" || fromUnit == "kilometer" || fromUnit == "kms") && (toUnit == "miles" || toUnit == "mile" || toUnit == "mi") -> {
                val converted = value * 0.621371
                String.format(Locale.US, "%.2f km = %.2f miles 📏", value, converted)
            }
            (fromUnit == "miles" || fromUnit == "mile" || fromUnit == "mi") && (toUnit == "km" || toUnit == "kilometer" || toUnit == "kms") -> {
                val converted = value / 0.621371
                String.format(Locale.US, "%.2f miles = %.2f km 📏", value, converted)
            }
            (fromUnit == "c" || fromUnit == "celsius") && (toUnit == "f" || toUnit == "fahrenheit") -> {
                val converted = (value * 9 / 5) + 32
                String.format(Locale.US, "%.1f °C = %.1f °F 🌡️", value, converted)
            }
            (fromUnit == "f" || fromUnit == "fahrenheit") && (toUnit == "c" || toUnit == "celsius") -> {
                val converted = (value - 32) * 5 / 9
                String.format(Locale.US, "%.1f °F = %.1f °C 🌡️", value, converted)
            }
            else -> null
        }
    }

    fun parseAndExecuteLocalCommand(context: Context, dbService: DatabaseService, text: String): UtilityResult {
        val lower = text.trim().lowercase(Locale.US)

        // Routine Execution Check
        val routineJson = dbService.getRoutineForTrigger(lower)
        if (routineJson != null) {
            return executeRoutineChain(context, dbService, lower, routineJson)
        }

        // Flashlight commands
        if (lower.contains("turn on flashlight") || lower == "flashlight on" || lower == "flashlight") {
            val msg = toggleFlashlight(context, true)
            return UtilityResult(true, msg, "FLASHLIGHT")
        }
        if (lower.contains("turn off flashlight") || lower == "flashlight off") {
            val msg = toggleFlashlight(context, false)
            return UtilityResult(true, msg, "FLASHLIGHT")
        }

        // Battery / Weather / Storage Commands
        if (lower.contains("battery") || lower == "read battery") {
            val b = StorageWeatherService.getBatteryStatus(context)
            val msg = "Battery Level: ${b.percentage}% (${b.statusText})"
            return UtilityResult(true, msg, "BATTERY")
        }
        if (lower.contains("weather") || lower == "read weather") {
            val w = StorageWeatherService.getWeatherInfo()
            val msg = "Weather in ${w.location}: ${w.temperature}, ${w.condition}. Humidity: ${w.humidity}."
            return UtilityResult(true, msg, "WEATHER")
        }
        if (lower.contains("clear cache") || lower.contains("clear storage")) {
            val msg = StorageWeatherService.clearAppCache(context)
            return UtilityResult(true, msg, "STORAGE")
        }

        // Call Command: "Call [Name]"
        if (lower.startsWith("call ")) {
            val nameOrAlias = text.substring(5).trim()
            if (nameOrAlias.isNotEmpty()) {
                // 1. Resolve Alias from DB
                val resolvedTarget = dbService.resolveAlias(nameOrAlias) ?: nameOrAlias
                // 2. Query Contact or dial directly
                val contactInfo = ContactsService.findContactByName(context, resolvedTarget)
                val dialTarget = contactInfo?.phoneNumber ?: resolvedTarget
                val msg = ContactsService.makeCall(context, dialTarget)
                return UtilityResult(true, msg, "CALL")
            }
        }

        // Text / SMS Command: "Text [Name] [Message]"
        if (lower.startsWith("text ") || lower.startsWith("send sms ")) {
            val raw = text.replace("send sms ", "", ignoreCase = true).replace("text ", "", ignoreCase = true).trim()
            val spaceIdx = raw.indexOf(' ')
            if (spaceIdx > 0) {
                val targetName = raw.substring(0, spaceIdx).trim()
                val smsMsg = raw.substring(spaceIdx + 1).trim()

                val resolvedTarget = dbService.resolveAlias(targetName) ?: targetName
                val contactInfo = ContactsService.findContactByName(context, resolvedTarget)
                val finalNum = contactInfo?.phoneNumber ?: resolvedTarget

                val msg = ContactsService.sendSMS(context, finalNum, smsMsg)
                return UtilityResult(true, msg, "SMS")
            }
        }

        // Calendar Reminder: "Remind me to [Task] at [Time]"
        if (lower.startsWith("remind me to ") || lower.startsWith("add reminder ")) {
            val clean = text.replace("remind me to ", "", ignoreCase = true)
                .replace("add reminder ", "", ignoreCase = true)
                .trim()

            var taskPart = clean
            var timePart = "Today at 5:00 PM"

            if (clean.contains(" at ")) {
                val parts = clean.split(" at ")
                taskPart = parts[0]
                timePart = parts[1]
            }

            val event = CalendarService.createReminderEvent(context, taskPart, timePart)
            return UtilityResult(
                isHandled = true,
                responseMessage = "Reminder created: \"${event.title}\" at ${event.timeStr}",
                actionType = "CALENDAR_CARD",
                eventData = event
            )
        }

        // Clipboard commands
        if (lower.startsWith("copy to clipboard") || lower.startsWith("copy ")) {
            val textToCopy = text.replace("copy to clipboard", "", ignoreCase = true)
                .replace("copy", "", ignoreCase = true)
                .trim()
            val msg = if (textToCopy.isNotEmpty()) {
                writeToClipboard(context, textToCopy)
            } else {
                "Please specify what to copy."
            }
            return UtilityResult(true, msg, "CLIPBOARD")
        }
        if (lower == "read clipboard" || lower == "paste from clipboard" || lower == "clipboard") {
            val msg = readFromClipboard(context)
            return UtilityResult(true, msg, "CLIPBOARD")
        }

        // Math calculations
        if (lower.startsWith("calculate") || lower.startsWith("math") || lower.matches(Regex(""".*\d+\s*[\+\-\*\/\^]\s*\d+.*"""))) {
            val result = evaluateMathExpression(text)
            if (result != null) {
                return UtilityResult(true, result, "MATH")
            }
        }

        // Unit conversions
        if (lower.startsWith("convert") || lower.contains(" to ")) {
            val cleanQuery = text.replace("convert", "", ignoreCase = true).trim()
            val result = performUnitConversion(cleanQuery)
            if (result != null) {
                return UtilityResult(true, result, "CONVERSION")
            }
        }

        // App Launch commands (e.g. "open whatsapp", "launch chrome")
        if (lower.startsWith("open ") || lower.startsWith("launch ") || lower.startsWith("start ")) {
            val appWord = text.replace("open ", "", ignoreCase = true)
                .replace("launch ", "", ignoreCase = true)
                .replace("start ", "", ignoreCase = true)
                .trim()
            if (appWord.isNotEmpty()) {
                val launchMsg = AppLauncher.launchAppByCustomWord(context, dbService, appWord)
                return UtilityResult(true, launchMsg, "APP_LAUNCH")
            }
        }

        return UtilityResult(false, "")
    }

    private fun executeRoutineChain(
        context: Context,
        dbService: DatabaseService,
        triggerName: String,
        jsonArrayStr: String
    ): UtilityResult {
        return try {
            val array = JSONArray(jsonArrayStr)
            val reports = mutableListOf<String>()

            for (i in 0 until array.length()) {
                val subCmd = array.getString(i)
                val subResult = parseAndExecuteLocalCommand(context, dbService, subCmd)
                if (subResult.isHandled) {
                    reports.add("⚡ ${subCmd}: ${subResult.responseMessage}")
                } else {
                    reports.add("⚡ ${subCmd}: Executed action")
                }
            }

            val fullReport = "Executed Custom Routine \"${triggerName.uppercase()}\":\n\n" + reports.joinToString("\n")
            UtilityResult(true, fullReport, "ROUTINE_CHAIN")
        } catch (e: Exception) {
            UtilityResult(true, "Error running routine: ${e.localizedMessage}", "ROUTINE_ERROR")
        }
    }
}
