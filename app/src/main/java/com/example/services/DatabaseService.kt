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

data class UserMemory(
    val id: Long = 0,
    val memoryKey: String,
    val memoryValue: String
)

data class ContactAlias(
    val id: Long = 0,
    val aliasName: String,
    val targetContactOrNumber: String
)

data class CustomRoutine(
    val id: Long = 0,
    val triggerPhrase: String,
    val actionChainJson: String
)

data class StudyTask(
    val id: Long = 0,
    val title: String,
    val subject: String,
    val isCompleted: Boolean = false,
    val dueDate: String = "Today"
)

data class Flashcard(
    val id: Long = 0,
    val subject: String,
    val topic: String,
    val question: String,
    val answer: String,
    val formula: String? = null
)

class DatabaseService(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "vedra_memory.db"
        private const val DATABASE_VERSION = 2

        const val TABLE_MAPPINGS = "app_mappings"
        const val TABLE_MEMORY = "user_memory"
        const val TABLE_ALIASES = "aliases"
        const val TABLE_ROUTINES = "routines"
        const val TABLE_STUDY_TASKS = "study_tasks"
        const val TABLE_FLASHCARDS = "flashcards"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE $TABLE_MAPPINGS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                custom_word TEXT NOT NULL UNIQUE,
                app_identifier TEXT NOT NULL
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE $TABLE_MEMORY (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                memory_key TEXT NOT NULL UNIQUE,
                memory_value TEXT NOT NULL
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE $TABLE_ALIASES (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                alias_name TEXT NOT NULL UNIQUE,
                target_contact TEXT NOT NULL
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE $TABLE_ROUTINES (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                trigger_phrase TEXT NOT NULL UNIQUE,
                action_chain_json TEXT NOT NULL
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE $TABLE_STUDY_TASKS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                subject TEXT NOT NULL,
                is_completed INTEGER DEFAULT 0,
                due_date TEXT NOT NULL
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE $TABLE_FLASHCARDS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                subject TEXT NOT NULL,
                topic TEXT NOT NULL,
                question TEXT NOT NULL,
                answer TEXT NOT NULL,
                formula TEXT
            )
        """.trimIndent())

        seedDefaultMappings(db)
        seedDefaultMemories(db)
        seedDefaultAliases(db)
        seedDefaultRoutines(db)
        seedDefaultStudyTasks(db)
        seedDefaultFlashcards(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MAPPINGS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MEMORY")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ALIASES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ROUTINES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_STUDY_TASKS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_FLASHCARDS")
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
                put("custom_word", mapping.customWord.lowercase())
                put("app_identifier", mapping.appIdentifier)
            }
            db.insertWithOnConflict(TABLE_MAPPINGS, null, values, SQLiteDatabase.CONFLICT_IGNORE)
        }
    }

    private fun seedDefaultMemories(db: SQLiteDatabase) {
        val memories = listOf(
            UserMemory(memoryKey = "School Board", memoryValue = "BSEB Class 12 Science"),
            UserMemory(memoryKey = "Target Exam", memoryValue = "JEE Main & Advanced 2026"),
            UserMemory(memoryKey = "Favorite Subject", memoryValue = "Physics (Rotational Dynamics)")
        )
        for (m in memories) {
            val values = ContentValues().apply {
                put("memory_key", m.memoryKey)
                put("memory_value", m.memoryValue)
            }
            db.insertWithOnConflict(TABLE_MEMORY, null, values, SQLiteDatabase.CONFLICT_IGNORE)
        }
    }

    private fun seedDefaultAliases(db: SQLiteDatabase) {
        val aliases = listOf(
            ContactAlias(aliasName = "mom", targetContactOrNumber = "Mom (+91 98765 43210)"),
            ContactAlias(aliasName = "dad", targetContactOrNumber = "Dad (+91 98765 43211)"),
            ContactAlias(aliasName = "friend", targetContactOrNumber = "Rahul Classmate")
        )
        for (a in aliases) {
            val values = ContentValues().apply {
                put("alias_name", a.aliasName.lowercase())
                put("target_contact", a.targetContactOrNumber)
            }
            db.insertWithOnConflict(TABLE_ALIASES, null, values, SQLiteDatabase.CONFLICT_IGNORE)
        }
    }

    private fun seedDefaultRoutines(db: SQLiteDatabase) {
        val routines = listOf(
            CustomRoutine(
                triggerPhrase = "good morning",
                actionChainJson = """["Read Weather", "Read Battery", "Open WhatsApp"]"""
            ),
            CustomRoutine(
                triggerPhrase = "study mode",
                actionChainJson = """["Turn Off Flashlight", "Open Calculator", "Check Study Goals"]"""
            )
        )
        for (r in routines) {
            val values = ContentValues().apply {
                put("trigger_phrase", r.triggerPhrase.lowercase())
                put("action_chain_json", r.actionChainJson)
            }
            db.insertWithOnConflict(TABLE_ROUTINES, null, values, SQLiteDatabase.CONFLICT_IGNORE)
        }
    }

    private fun seedDefaultStudyTasks(db: SQLiteDatabase) {
        val tasks = listOf(
            StudyTask(title = "Revise Newton's Laws & Friction", subject = "Physics", isCompleted = false, dueDate = "Today"),
            StudyTask(title = "Solve 15 Rotational Dynamics MCQs", subject = "Physics", isCompleted = true, dueDate = "Today"),
            StudyTask(title = "Thermodynamics Carnot Engine derivation", subject = "Physics/Chemistry", isCompleted = false, dueDate = "Tomorrow")
        )
        for (t in tasks) {
            val values = ContentValues().apply {
                put("title", t.title)
                put("subject", t.subject)
                put("is_completed", if (t.isCompleted) 1 else 0)
                put("due_date", t.dueDate)
            }
            db.insertWithOnConflict(TABLE_STUDY_TASKS, null, values, SQLiteDatabase.CONFLICT_IGNORE)
        }
    }

    private fun seedDefaultFlashcards(db: SQLiteDatabase) {
        val cards = listOf(
            Flashcard(
                subject = "Physics",
                topic = "Rotational Dynamics",
                question = "What is Moment of Inertia of a solid cylinder/disk about its central axis?",
                answer = "Moment of Inertia I = 1/2 * M * R²",
                formula = "I = 0.5 M R²"
            ),
            Flashcard(
                subject = "Physics",
                topic = "Newton's Laws of Motion",
                question = "State Impulse-Momentum Theorem.",
                answer = "Impulse J delivered by a net force equals the total change in momentum Δp.",
                formula = "J = ∫ F dt = Δp = m(v - u)"
            ),
            Flashcard(
                subject = "Physics",
                topic = "Thermodynamics",
                question = "What is the efficiency of a Carnot Engine?",
                answer = "Efficiency η = 1 - (T_cold / T_hot), where temperatures are in Kelvin.",
                formula = "η = 1 - (T₂ / T₁)"
            ),
            Flashcard(
                subject = "Physics",
                topic = "Mechanics",
                question = "What is escape velocity from Earth's surface?",
                answer = "Escape velocity v_e = √(2 g R) ≈ 11.2 km/s.",
                formula = "v_e = √(2 G M / R)"
            ),
            Flashcard(
                subject = "Mathematics",
                topic = "Calculus",
                question = "What is derivative of e^(a*x) * sin(b*x)?",
                answer = "d/dx [e^(ax) sin(bx)] = e^(ax) * [a sin(bx) + b cos(bx)].",
                formula = "y' = e^{ax} (a \\sin bx + b \\cos bx)"
            )
        )
        for (c in cards) {
            val values = ContentValues().apply {
                put("subject", c.subject)
                put("topic", c.topic)
                put("question", c.question)
                put("answer", c.answer)
                put("formula", c.formula)
            }
            db.insertWithOnConflict(TABLE_FLASHCARDS, null, values, SQLiteDatabase.CONFLICT_IGNORE)
        }
    }

    // --- MAPPINGS ---
    fun getAllMappings(): List<AppMapping> {
        val list = mutableListOf<AppMapping>()
        val db = readableDatabase
        val cursor = db.query(TABLE_MAPPINGS, null, null, null, null, null, "custom_word ASC")
        cursor.use {
            val idIndex = it.getColumnIndexOrThrow("id")
            val wordIndex = it.getColumnIndexOrThrow("custom_word")
            val appIndex = it.getColumnIndexOrThrow("app_identifier")
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
            put("custom_word", customWord.lowercase().trim())
            put("app_identifier", appIdentifier.trim())
        }
        val result = db.insertWithOnConflict(TABLE_MAPPINGS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        return result != -1L
    }

    fun deleteMapping(id: Long): Boolean {
        val db = writableDatabase
        return db.delete(TABLE_MAPPINGS, "id = ?", arrayOf(id.toString())) > 0
    }

    fun getAppIdentifierForWord(word: String): String? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_MAPPINGS,
            arrayOf("app_identifier"),
            "custom_word = ?",
            arrayOf(word.lowercase().trim()),
            null, null, null
        )
        cursor.use {
            if (it.moveToFirst()) {
                return it.getString(it.getColumnIndexOrThrow("app_identifier"))
            }
        }
        return null
    }

    // --- USER MEMORY ---
    fun getAllMemories(): List<UserMemory> {
        val list = mutableListOf<UserMemory>()
        val db = readableDatabase
        val cursor = db.query(TABLE_MEMORY, null, null, null, null, null, "memory_key ASC")
        cursor.use {
            val idIdx = it.getColumnIndexOrThrow("id")
            val keyIdx = it.getColumnIndexOrThrow("memory_key")
            val valIdx = it.getColumnIndexOrThrow("memory_value")
            while (it.moveToNext()) {
                list.add(UserMemory(it.getLong(idIdx), it.getString(keyIdx), it.getString(valIdx)))
            }
        }
        return list
    }

    fun addOrUpdateMemory(key: String, value: String): Boolean {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("memory_key", key.trim())
            put("memory_value", value.trim())
        }
        val res = db.insertWithOnConflict(TABLE_MEMORY, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
        return res != -1L
    }

    fun deleteMemory(id: Long): Boolean {
        return writableDatabase.delete(TABLE_MEMORY, "id = ?", arrayOf(id.toString())) > 0
    }

    // --- ALIASES ---
    fun getAllAliases(): List<ContactAlias> {
        val list = mutableListOf<ContactAlias>()
        val cursor = readableDatabase.query(TABLE_ALIASES, null, null, null, null, null, "alias_name ASC")
        cursor.use {
            val idIdx = it.getColumnIndexOrThrow("id")
            val aliasIdx = it.getColumnIndexOrThrow("alias_name")
            val targetIdx = it.getColumnIndexOrThrow("target_contact")
            while (it.moveToNext()) {
                list.add(ContactAlias(it.getLong(idIdx), it.getString(aliasIdx), it.getString(targetIdx)))
            }
        }
        return list
    }

    fun addOrUpdateAlias(alias: String, target: String): Boolean {
        val cv = ContentValues().apply {
            put("alias_name", alias.lowercase().trim())
            put("target_contact", target.trim())
        }
        return writableDatabase.insertWithOnConflict(TABLE_ALIASES, null, cv, SQLiteDatabase.CONFLICT_REPLACE) != -1L
    }

    fun deleteAlias(id: Long): Boolean {
        return writableDatabase.delete(TABLE_ALIASES, "id = ?", arrayOf(id.toString())) > 0
    }

    fun resolveAlias(aliasName: String): String? {
        val cursor = readableDatabase.query(
            TABLE_ALIASES,
            arrayOf("target_contact"),
            "alias_name = ?",
            arrayOf(aliasName.lowercase().trim()),
            null, null, null
        )
        cursor.use {
            if (it.moveToFirst()) {
                return it.getString(it.getColumnIndexOrThrow("target_contact"))
            }
        }
        return null
    }

    // --- ROUTINES ---
    fun getAllRoutines(): List<CustomRoutine> {
        val list = mutableListOf<CustomRoutine>()
        val cursor = readableDatabase.query(TABLE_ROUTINES, null, null, null, null, null, "trigger_phrase ASC")
        cursor.use {
            val idIdx = it.getColumnIndexOrThrow("id")
            val trigIdx = it.getColumnIndexOrThrow("trigger_phrase")
            val actIdx = it.getColumnIndexOrThrow("action_chain_json")
            while (it.moveToNext()) {
                list.add(CustomRoutine(it.getLong(idIdx), it.getString(trigIdx), it.getString(actIdx)))
            }
        }
        return list
    }

    fun addOrUpdateRoutine(trigger: String, actionChainJson: String): Boolean {
        val cv = ContentValues().apply {
            put("trigger_phrase", trigger.lowercase().trim())
            put("action_chain_json", actionChainJson.trim())
        }
        return writableDatabase.insertWithOnConflict(TABLE_ROUTINES, null, cv, SQLiteDatabase.CONFLICT_REPLACE) != -1L
    }

    fun deleteRoutine(id: Long): Boolean {
        return writableDatabase.delete(TABLE_ROUTINES, "id = ?", arrayOf(id.toString())) > 0
    }

    fun getRoutineForTrigger(trigger: String): String? {
        val cursor = readableDatabase.query(
            TABLE_ROUTINES,
            arrayOf("action_chain_json"),
            "trigger_phrase = ?",
            arrayOf(trigger.lowercase().trim()),
            null, null, null
        )
        cursor.use {
            if (it.moveToFirst()) {
                return it.getString(it.getColumnIndexOrThrow("action_chain_json"))
            }
        }
        return null
    }

    // --- STUDY TASKS ---
    fun getAllStudyTasks(): List<StudyTask> {
        val list = mutableListOf<StudyTask>()
        val cursor = readableDatabase.query(TABLE_STUDY_TASKS, null, null, null, null, null, "id DESC")
        cursor.use {
            val idIdx = it.getColumnIndexOrThrow("id")
            val titleIdx = it.getColumnIndexOrThrow("title")
            val subjIdx = it.getColumnIndexOrThrow("subject")
            val compIdx = it.getColumnIndexOrThrow("is_completed")
            val dateIdx = it.getColumnIndexOrThrow("due_date")
            while (it.moveToNext()) {
                list.add(
                    StudyTask(
                        id = it.getLong(idIdx),
                        title = it.getString(titleIdx),
                        subject = it.getString(subjIdx),
                        isCompleted = it.getInt(compIdx) == 1,
                        dueDate = it.getString(dateIdx)
                    )
                )
            }
        }
        return list
    }

    fun addStudyTask(title: String, subject: String, dueDate: String = "Today"): Boolean {
        val cv = ContentValues().apply {
            put("title", title)
            put("subject", subject)
            put("is_completed", 0)
            put("due_date", dueDate)
        }
        return writableDatabase.insert(TABLE_STUDY_TASKS, null, cv) != -1L
    }

    fun toggleStudyTask(id: Long, currentStatus: Boolean): Boolean {
        val cv = ContentValues().apply {
            put("is_completed", if (!currentStatus) 1 else 0)
        }
        return writableDatabase.update(TABLE_STUDY_TASKS, cv, "id = ?", arrayOf(id.toString())) > 0
    }

    fun deleteStudyTask(id: Long): Boolean {
        return writableDatabase.delete(TABLE_STUDY_TASKS, "id = ?", arrayOf(id.toString())) > 0
    }

    // --- FLASHCARDS ---
    fun getAllFlashcards(): List<Flashcard> {
        val list = mutableListOf<Flashcard>()
        val cursor = readableDatabase.query(TABLE_FLASHCARDS, null, null, null, null, null, "id ASC")
        cursor.use {
            val idIdx = it.getColumnIndexOrThrow("id")
            val subjIdx = it.getColumnIndexOrThrow("subject")
            val topIdx = it.getColumnIndexOrThrow("topic")
            val qIdx = it.getColumnIndexOrThrow("question")
            val aIdx = it.getColumnIndexOrThrow("answer")
            val fIdx = it.getColumnIndexOrThrow("formula")
            while (it.moveToNext()) {
                list.add(
                    Flashcard(
                        id = it.getLong(idIdx),
                        subject = it.getString(subjIdx),
                        topic = it.getString(topIdx),
                        question = it.getString(qIdx),
                        answer = it.getString(aIdx),
                        formula = if (it.isNull(fIdx)) null else it.getString(fIdx)
                    )
                )
            }
        }
        return list
    }

    fun addFlashcard(subject: String, topic: String, question: String, answer: String, formula: String?): Boolean {
        val cv = ContentValues().apply {
            put("subject", subject)
            put("topic", topic)
            put("question", question)
            put("answer", answer)
            put("formula", formula)
        }
        return writableDatabase.insert(TABLE_FLASHCARDS, null, cv) != -1L
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
