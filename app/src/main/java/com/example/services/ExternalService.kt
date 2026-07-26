package com.example.services

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.WindowManager

object ExternalService {

    fun openNavigation(context: Context, destination: String): String {
        return try {
            val encoded = Uri.encode(destination)
            val uri = Uri.parse("geo:0,0?q=$encoded")
            val mapIntent = Intent(Intent.ACTION_VIEW, uri).apply {
                setPackage("com.google.android.apps.maps")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            if (mapIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(mapIntent)
            } else {
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=$encoded")).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(webIntent)
            }
            "Opening navigation to '$destination'..."
        } catch (e: Exception) {
            "Unable to launch maps for '$destination'."
        }
    }

    fun sendEmail(context: Context, recipient: String, subject: String, body: String): String {
        return try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            "Opening email composer for '$recipient'..."
        } catch (e: Exception) {
            "No email app found on device."
        }
    }

    fun searchWeb(context: Context, query: String): String {
        return try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=${Uri.encode(query)}")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            "Searching web for '$query'..."
        } catch (e: Exception) {
            "Unable to perform web search."
        }
    }

    fun openYouTube(context: Context, searchQuery: String): String {
        return try {
            val encoded = Uri.encode(searchQuery)
            val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$encoded")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            if (appIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(appIntent)
            } else {
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=$encoded")).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(webIntent)
            }
            "Searching YouTube for '$searchQuery'..."
        } catch (e: Exception) {
            "Unable to open YouTube."
        }
    }

    fun shareText(context: Context, title: String, text: String): String {
        return try {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, text)
                type = "text/plain"
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val shareIntent = Intent.createChooser(sendIntent, title).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(shareIntent)
            "Sharing note/text..."
        } catch (e: Exception) {
            "Unable to launch share dialog."
        }
    }

    fun setScreenBrightness(activity: Activity?, percentage: Int): String {
        if (activity == null) return "Activity context required for brightness."
        return try {
            val clamped = percentage.coerceIn(5, 100)
            val layoutParams = activity.window.attributes
            layoutParams.screenBrightness = clamped / 100f
            activity.window.attributes = layoutParams
            "Brightness set to $clamped%."
        } catch (e: Exception) {
            "Unable to change screen brightness."
        }
    }
}
