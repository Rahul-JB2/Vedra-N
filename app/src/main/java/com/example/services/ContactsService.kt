package com.example.services

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract

object ContactsService {

    data class ContactInfo(
        val name: String,
        val phoneNumber: String
    )

    fun findContactByName(context: Context, nameQuery: String): ContactInfo? {
        val cleanQuery = nameQuery.trim()
        if (cleanQuery.isEmpty()) return null

        return try {
            val contentResolver = context.contentResolver
            val cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER
                ),
                "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} LIKE ?",
                arrayOf("%$cleanQuery%"),
                null
            )

            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIdx = it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                    val numIdx = it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    val name = it.getString(nameIdx)
                    val number = it.getString(numIdx)
                    return ContactInfo(name, number)
                }
            }
            null
        } catch (e: Exception) {
            null
        }
    }

    fun makeCall(context: Context, target: String): String {
        return try {
            val digits = target.filter { it.isDigit() || it == '+' }
            val uri = if (digits.isNotEmpty()) Uri.parse("tel:$digits") else Uri.parse("tel:$target")
            val intent = Intent(Intent.ACTION_DIAL, uri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            "Initiating call to $target..."
        } catch (e: Exception) {
            "Unable to open dialer: ${e.localizedMessage}"
        }
    }

    fun sendSMS(context: Context, target: String, messageText: String): String {
        return try {
            val digits = target.filter { it.isDigit() || it == '+' }
            val uri = Uri.parse("smsto:${if (digits.isNotEmpty()) digits else target}")
            val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
                putExtra("sms_body", messageText)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            "Opening SMS composer for $target..."
        } catch (e: Exception) {
            "Failed to open SMS composer: ${e.localizedMessage}"
        }
    }
}
