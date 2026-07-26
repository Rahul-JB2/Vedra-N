package com.example.services

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri

data class AppMapping(
    val id: Long = 0,
    val customWord: String,
    val appIdentifier: String
)

class DatabaseService(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "vedra_memory.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_MAPPINGS = "app_mappings"
        const val COLUMN_ID = "id"
        const val COLUMN_WORD = "custom_word"
        const val COLUMN_APP = "app_identifier"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_MAPPINGS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_WORD TEXT NOT NULL UNIQUE,
                $COLUMN_APP TEXT NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTable)

        // Seed default app mappings
        seedDefaultMappings(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MAPPINGS")
        onCreate(db)
    }

    private fun seedDefaultMappings(db: SQLiteDatabase) {
        val defaults = listOf(
            AppMapping(customWord = "whatsapp", appIdentifier = "com.whatsapp"),
            AppMapping(customWord = "youtube", appIdentifier = "com.google.android.youtube"),
            AppMapping(customWord = "chrome", appIdentifier = "com.android.chrome"),
            AppMapping(customWord = "camera", appIdentifier = "com.android.camera"),
            AppMapping(customWord = "calculator", appIdentifier = "com.google.android.calculator"),
            AppMapping(customWord = "notes", appIdentifier = "com.google.android.keep"),
            AppMapping(customWord = "spotify", appIdentifier = "com.spotify.music")
        )
        for (mapping in defaults) {
            val values = ContentValues().apply {
                put(COLUMN_WORD, mapping.customWord.lowercase())
                put(COLUMN_APP, mapping.appIdentifier)
            }
            db.insertWithOnConflict(TABLE_MAPPINGS, null, values, SQLiteDatabase.CONFLICT_IGNORE)
        }
    }

    fun getAllMappings(): List<AppMapping> {
        val list = mutableListOf<AppMapping>()
        val db = readableDatabase
        val cursor = db.query(TABLE_MAPPINGS, null, null, null, null, null, "$COLUMN_WORD ASC")
        cursor.use {
            val idIndex = it.getColumnIndexOrThrow(COLUMN_ID)
            val wordIndex = it.getColumnIndexOrThrow(COLUMN_WORD)
            val appIndex = it.getColumnIndexOrThrow(COLUMN_APP)
            while (it.moveToNext()) {
                list.add(
                    AppMapping(
                        id = it.getLong(idIndex),
                        customWord = it.getString(wordIndex),
                        appIdentifier = it.getString(appIndex)
                    )
                )
            }
        }
        return list
    }

    fun addOrUpdateMapping(customWord: String, appIdentifier: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_WORD, customWord.lowercase().trim())
            put(COLUMN_APP, appIdentifier.trim())
        }
        val result = db.insertWithOnConflict(TABLE_MAPPINGS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        return result != -1L
    }

    fun deleteMapping(id: Long): Boolean {
        val db = writableDatabase
        return db.delete(TABLE_MAPPINGS, "$COLUMN_ID = ?", arrayOf(id.toString())) > 0
    }

    fun getAppIdentifierForWord(word: String): String? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_MAPPINGS,
            arrayOf(COLUMN_APP),
            "$COLUMN_WORD = ?",
            arrayOf(word.lowercase().trim()),
            null,
            null,
            null
        )
        cursor.use {
            if (it.moveToFirst()) {
                return it.getString(it.getColumnIndexOrThrow(COLUMN_APP))
            }
        }
        return null
    }
}

object AppLauncher {

    fun launchAppByCustomWord(context: Context, dbService: DatabaseService, customWord: String): String {
        val cleanWord = customWord.lowercase().trim()
        val appIdentifier = dbService.getAppIdentifierForWord(cleanWord)

        if (appIdentifier != null) {
            val launched = tryLaunchPackage(context, appIdentifier)
            if (launched) {
                return "Launching '$cleanWord' ($appIdentifier)..."
            }
        }

        val directLaunched = tryLaunchPackage(context, cleanWord)
        if (directLaunched) {
            return "Launching app '$cleanWord'..."
        }

        // Web launch fallback
        return try {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=$cleanWord+app"))
            webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(webIntent)
            "Opening search for '$cleanWord' app..."
        } catch (e: Exception) {
            "Custom mapping '$cleanWord' not found or app is not installed."
        }
    }

    private fun tryLaunchPackage(context: Context, packageName: String): Boolean {
        return try {
            val pm = context.packageManager
            val intent = pm.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
}
