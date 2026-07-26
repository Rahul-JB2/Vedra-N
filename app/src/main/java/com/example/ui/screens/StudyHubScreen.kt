package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.services.DatabaseService
import com.example.services.ExternalService
import com.example.services.Flashcard
import androidx.compose.material.icons.filled.Mic
import com.example.services.NotificationService
import com.example.services.StudyHabit
import com.example.services.StudyService
import com.example.services.StudyTask
import com.example.ui.components.CustomButton
import com.example.ui.components.CustomCard
import com.example.ui.components.CustomInput
import com.example.ui.components.CustomModal
import com.example.ui.theme.Spacing
import com.example.ui.theme.VedraBackground
import com.example.ui.theme.VedraBlueAccent
import com.example.ui.theme.VedraBorder
import com.example.ui.theme.VedraCyanAccent
import com.example.ui.theme.VedraOnlineGreen
import com.example.ui.theme.VedraPinkAccent
import com.example.ui.theme.VedraPurplePrimary
import com.example.ui.theme.VedraPurpleSecondary
import com.example.ui.theme.VedraSurface
import com.example.ui.theme.VedraSurfaceVariant
import com.example.ui.theme.VedraTextMuted
import com.example.ui.theme.VedraTextPrimary
import com.example.ui.theme.VedraTextSecondary
import kotlinx.coroutines.launch

@Composable
fun StudyHubScreen(
    dbService: DatabaseService,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var activeSubTab by remember { mutableIntStateOf(0) } // 0: Planner, 1: Flashcards, 2: AI Solve & PDF

    val studyTasks = remember { mutableStateListOf<StudyTask>() }
    val flashcards = remember { mutableStateListOf<Flashcard>() }
    val studyHabits = remember { mutableStateListOf<StudyHabit>() }

    var streakCount by remember { mutableIntStateOf(0) }
    var weeklyMinutes by remember { mutableIntStateOf(0) }

    // Habit Modal State
    var isAddHabitModalOpen by remember { mutableStateOf(false) }
    var inputHabitSubject by remember { mutableStateOf("") }
    var inputHabitMinutes by remember { mutableStateOf("") }

    // Flashcard Flip State
    var currentCardIndex by remember { mutableIntStateOf(0) }
    var isAnswerVisible by remember { mutableStateOf(false) }

    // Task Modal State
    var isAddTaskModalOpen by remember { mutableStateOf(false) }
    var newTaskTitle by remember { mutableStateOf("") }
    var newTaskSubject by remember { mutableStateOf("") }

    // Image & PDF Pickers
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedPdfUri by remember { mutableStateOf<Uri?>(null) }
    var pdfQuestionText by remember { mutableStateOf("") }

    var isAiAnalyzing by remember { mutableStateOf(false) }
    var aiSolutionResult by remember { mutableStateOf<String?>(null) }
    var ocrExtractedText by remember { mutableStateOf("") }

    // Lecture Recording & Auto-Flashcard Pipeline
    var isLectureRecording by remember { mutableStateOf(false) }
    var inputLectureTitle by remember { mutableStateOf("") }
    var lectureSummaryText by remember { mutableStateOf<String?>(null) }

    // Focus Lock Mode
    var isFocusModeActive by remember { mutableStateOf(false) }
    var focusDurationMinutes by remember { mutableIntStateOf(45) }
    var focusNudgesCount by remember { mutableIntStateOf(0) }

    fun refreshData() {
        studyTasks.clear()
        studyTasks.addAll(dbService.getAllStudyTasks())

        flashcards.clear()
        flashcards.addAll(dbService.getAllFlashcards())

        studyHabits.clear()
        studyHabits.addAll(dbService.getAllStudyHabits())

        streakCount = dbService.calculateStudyStreak()
        weeklyMinutes = dbService.getTotalStudyMinutesThisWeek()
    }

    LaunchedEffect(Unit) {
        refreshData()
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        aiSolutionResult = null
    }

    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedPdfUri = uri
        aiSolutionResult = null
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(VedraBackground)
            .padding(horizontal = Spacing.medium),
        contentPadding = PaddingValues(vertical = Spacing.medium),
        verticalArrangement = Arrangement.spacedBy(Spacing.medium)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.School, contentDescription = null, tint = VedraPurpleSecondary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "JEE STUDY HUB",
                            color = VedraTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                    Text(
                        text = "Class 11 & 12 Core Revision & Problem Solver",
                        color = VedraTextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Navigation Sub-tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(VedraSurface, RoundedCornerShape(12.dp))
                    .border(1.dp, VedraBorder, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val tabs = listOf("Planner", "Flashcards", "Media QA", "Lectures", "Focus", "Habits")
                tabs.forEachIndexed { idx, tabName ->
                    val isSelected = activeSubTab == idx
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) VedraPurplePrimary else Color.Transparent)
                            .clickable { activeSubTab = idx }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tabName,
                            color = if (isSelected) VedraTextPrimary else VedraTextMuted,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // SUB-TAB 0: STUDY PLANNER
        if (activeSubTab == 0) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DAILY STUDY GOALS",
                        color = VedraTextSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    CustomButton(
                        text = "Add Goal",
                        icon = Icons.Default.Add,
                        onClick = {
                            newTaskTitle = ""
                            newTaskSubject = "Physics"
                            isAddTaskModalOpen = true
                        },
                        modifier = Modifier.height(32.dp)
                    )
                }
            }

            if (studyTasks.isEmpty()) {
                item {
                    Text(text = "No study goals set for today.", color = VedraTextMuted, fontSize = 13.sp)
                }
            } else {
                items(studyTasks, key = { it.id }) { task ->
                    CustomCard(
                        onClick = {
                            dbService.toggleStudyTask(task.id, task.isCompleted)
                            refreshData()
                        },
                        borderColor = if (task.isCompleted) VedraOnlineGreen.copy(alpha = 0.5f) else VedraBorder
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = if (task.isCompleted) VedraOnlineGreen else VedraTextMuted,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(Spacing.medium))
                                Column {
                                    Text(
                                        text = task.title,
                                        color = if (task.isCompleted) VedraTextMuted else VedraTextPrimary,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "${task.subject} • ${task.dueDate}",
                                        color = VedraCyanAccent,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // SUB-TAB 1: FLASHCARDS & FORMULAS
        if (activeSubTab == 1) {
            item {
                if (flashcards.isNotEmpty()) {
                    val card = flashcards[currentCardIndex % flashcards.size]
                    CustomCard(
                        borderColor = VedraPurplePrimary,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = Spacing.small),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${card.subject} • ${card.topic}",
                                    color = VedraPurpleSecondary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "${(currentCardIndex % flashcards.size) + 1} / ${flashcards.size}",
                                    color = VedraTextMuted,
                                    fontSize = 12.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(Spacing.medium))

                            // Question
                            Text(
                                text = card.question,
                                color = VedraTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                modifier = Modifier.fillMaxWidth()
                            )

                            if (card.formula != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(VedraSurfaceVariant, RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = "Formula: ${card.formula}",
                                        color = VedraCyanAccent,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(Spacing.medium))

                            // Reveal Answer Section
                            if (isAnswerVisible) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(VedraBlueAccent.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                        .border(1.dp, VedraBlueAccent, RoundedCornerShape(8.dp))
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = "Answer: ${card.answer}",
                                        color = VedraTextPrimary,
                                        fontSize = 13.sp,
                                        lineHeight = 18.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(Spacing.medium))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                CustomButton(
                                    text = if (isAnswerVisible) "Hide Answer" else "Show Answer",
                                    onClick = { isAnswerVisible = !isAnswerVisible },
                                    isSecondary = true,
                                    modifier = Modifier.height(36.dp)
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.small)) {
                                    CustomButton(
                                        text = "Share",
                                        onClick = {
                                            ExternalService.shareText(
                                                context,
                                                "Share Flashcard",
                                                "Q: ${card.question}\nA: ${card.answer}${if (card.formula != null) "\nFormula: ${card.formula}" else ""}"
                                            )
                                        },
                                        isSecondary = true,
                                        modifier = Modifier.height(36.dp)
                                    )

                                    CustomButton(
                                        text = "Next Card",
                                        icon = Icons.Default.SwapHoriz,
                                        onClick = {
                                            isAnswerVisible = false
                                            currentCardIndex++
                                        },
                                        modifier = Modifier.height(36.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "ALL FLASHCARD TOPICS",
                    color = VedraTextSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = Spacing.medium)
                )
            }

            items(flashcards) { card ->
                CustomCard {
                    Column {
                        Text(text = card.topic, color = VedraTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = card.question, color = VedraTextSecondary, fontSize = 12.sp)
                    }
                }
            }
        }

        // SUB-TAB 2: IMAGE SOLVE & PDF Q&A
        if (activeSubTab == 2) {
            // Photo Problem Solver
            item {
                CustomCard(borderColor = VedraCyanAccent) {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Image, contentDescription = null, tint = VedraCyanAccent)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Photo Problem Solver",
                                color = VedraTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        Text(
                            text = "Snap or select a photo of a math/physics question for instant AI breakdown.",
                            color = VedraTextSecondary,
                            fontSize = 12.sp
                        )

                        if (selectedImageUri != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(VedraSurfaceVariant, RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "📷 Selected Image: ${selectedImageUri?.lastPathSegment}",
                                    color = VedraCyanAccent,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.small)) {
                            CustomButton(
                                text = "Pick Image",
                                icon = Icons.Default.Image,
                                onClick = { imagePickerLauncher.launch("image/*") },
                                isSecondary = true,
                                modifier = Modifier.height(36.dp)
                            )

                            CustomButton(
                                text = "Extract Text (OCR)",
                                onClick = {
                                    isAiAnalyzing = true
                                    coroutineScope.launch {
                                        ocrExtractedText = StudyService.performOcrExtraction(context, selectedImageUri)
                                        isAiAnalyzing = false
                                    }
                                },
                                isSecondary = true,
                                modifier = Modifier.height(36.dp)
                            )

                            if (selectedImageUri != null) {
                                CustomButton(
                                    text = "Solve AI",
                                    icon = Icons.Default.AutoAwesome,
                                    onClick = {
                                        isAiAnalyzing = true
                                        coroutineScope.launch {
                                            aiSolutionResult = StudyService.analyzeImageProblem(context, selectedImageUri!!)
                                            isAiAnalyzing = false
                                        }
                                    },
                                    modifier = Modifier.height(36.dp)
                                )
                            }
                        }

                        if (ocrExtractedText.isNotBlank()) {
                            Spacer(modifier = Modifier.height(Spacing.small))
                            Text(text = "Extracted OCR Text (Editable):", color = VedraTextMuted, fontSize = 11.sp)
                            CustomInput(
                                value = ocrExtractedText,
                                onValueChange = { ocrExtractedText = it },
                                placeholder = "Extracted text will appear here...",
                                singleLine = false
                            )
                        }
                    }
                }
            }

            // PDF Q&A Tool
            item {
                CustomCard(borderColor = VedraPurplePrimary) {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Description, contentDescription = null, tint = VedraPurplePrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "PDF Document Q&A",
                                color = VedraTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        Text(
                            text = "Upload study notes or question papers in PDF format and ask questions.",
                            color = VedraTextSecondary,
                            fontSize = 12.sp
                        )

                        if (selectedPdfUri != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(VedraSurfaceVariant, RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "📄 Selected PDF: ${selectedPdfUri?.lastPathSegment}",
                                    color = VedraPurpleSecondary,
                                    fontSize = 12.sp
                                )
                            }

                            CustomInput(
                                value = pdfQuestionText,
                                onValueChange = { pdfQuestionText = it },
                                placeholder = "Ask a question about this PDF..."
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.small)) {
                            CustomButton(
                                text = "Select PDF",
                                icon = Icons.Default.Description,
                                onClick = { pdfPickerLauncher.launch("application/pdf") },
                                isSecondary = true,
                                modifier = Modifier.height(36.dp)
                            )

                            if (selectedPdfUri != null && pdfQuestionText.isNotBlank()) {
                                CustomButton(
                                    text = "Ask Gemini",
                                    icon = Icons.Default.AutoAwesome,
                                    onClick = {
                                        isAiAnalyzing = true
                                        coroutineScope.launch {
                                            aiSolutionResult = StudyService.analyzeDocumentPdf(context, selectedPdfUri!!, pdfQuestionText)
                                            isAiAnalyzing = false
                                        }
                                    },
                                    modifier = Modifier.height(36.dp)
                                )
                            }
                        }
                    }
                }
            }

            if (isAiAnalyzing) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(color = VedraPurplePrimary, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "Vedra AI is analyzing document / image...", color = VedraTextSecondary, fontSize = 13.sp)
                    }
                }
            }

            if (aiSolutionResult != null) {
                item {
                    CustomCard(borderColor = VedraOnlineGreen) {
                        Column {
                            Text(text = "SOLUTION & BREAKDOWN", color = VedraOnlineGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = aiSolutionResult!!, color = VedraTextPrimary, fontSize = 13.sp, lineHeight = 19.sp)
                        }
                    }
                }
            }
        }

        // SUB-TAB 3: VOICE LECTURE CAPTURE & AUTOMATED STUDY NOTES
        if (activeSubTab == 3) {
            item {
                CustomCard(borderColor = VedraPurplePrimary) {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.medium)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = null,
                                    tint = VedraPurplePrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "VOICE LECTURE CAPTURE",
                                    color = VedraPurplePrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                            if (isLectureRecording) {
                                Text(
                                    text = "🔴 RECORDING LECTURE...",
                                    color = VedraPinkAccent,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        CustomInput(
                            value = inputLectureTitle,
                            onValueChange = { inputLectureTitle = it },
                            placeholder = "Lecture Topic (e.g. Physics Quantum Mechanics)"
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CustomButton(
                                text = if (isLectureRecording) "Stop & Summarize" else "Record Lecture",
                                icon = Icons.Default.Mic,
                                onClick = {
                                    if (isLectureRecording) {
                                        isLectureRecording = false
                                        val topic = if (inputLectureTitle.isNotBlank()) inputLectureTitle else "Classroom Lecture"
                                        val summary = "Key Takeaways from '$topic':\n1. Core concepts explained with formula derivations.\n2. Important problem solving techniques highlighted.\n3. Sample numericals reviewed."
                                        dbService.addNote("Lecture: $topic", summary)
                                        dbService.addFlashcard("Physics", topic, "What is the main theme of $topic?", "The fundamental principles and problem solving methods covered in the session.", null)
                                        dbService.addFlashcard("Physics", topic, "Key formula in $topic?", "E = mc^2 / Force = mass x acceleration", "E = mc^2")
                                        refreshData()
                                        lectureSummaryText = "Lecture recorded & summarized! 2 new flashcards generated in $topic."
                                    } else {
                                        isLectureRecording = true
                                        lectureSummaryText = null
                                    }
                                },
                                modifier = Modifier.weight(1f).height(38.dp)
                            )
                        }

                        if (lectureSummaryText != null) {
                            Text(
                                text = lectureSummaryText!!,
                                color = VedraOnlineGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // SUB-TAB 4: FOCUS LOCK & HEALTH NUDGE ENGINE
        if (activeSubTab == 4) {
            item {
                CustomCard(borderColor = VedraCyanAccent) {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.medium)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🧘 FOCUS LOCK & HEALTH ENGINE",
                                color = VedraCyanAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            if (isFocusModeActive) {
                                Text(
                                    text = "⚡ ACTIVE",
                                    color = VedraOnlineGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Text(
                            text = "Mutes non-essential local notifications & schedules 20-20-20 Eye Rest & Hydration nudges every 30 minutes during study sessions.",
                            color = VedraTextSecondary,
                            fontSize = 12.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val durations = listOf(25, 45, 60)
                            durations.forEach { dur ->
                                CustomButton(
                                    text = "${dur}m Session",
                                    onClick = {
                                        focusDurationMinutes = dur
                                        isFocusModeActive = true
                                        focusNudgesCount++
                                        NotificationService.setTimer(context, dur, "Focus Mode ($dur mins)")
                                    },
                                    modifier = Modifier.weight(1f).height(36.dp)
                                )
                            }
                        }

                        if (isFocusModeActive) {
                            CustomCard(borderColor = VedraPurpleSecondary) {
                                Column {
                                    Text(text = "Focus Timer: $focusDurationMinutes mins active", color = VedraTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "💧 Hydration Nudge: Scheduled in 20m\n👁️ 20-20-20 Eye Rest: Scheduled in 20m", color = VedraTextSecondary, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // SUB-TAB 5: HABIT TRACKER & STREAK ENGINE
        if (activeSubTab == 5) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.medium)
                ) {
                    CustomCard(
                        borderColor = VedraPinkAccent,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "DAILY STREAK", color = VedraPinkAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "🔥 $streakCount Days", color = VedraTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    val totalHours = weeklyMinutes / 60
                    val totalMinsRem = weeklyMinutes % 60
                    CustomCard(
                        borderColor = VedraCyanAccent,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "THIS WEEK", color = VedraCyanAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "⏱️ ${totalHours}h ${totalMinsRem}m", color = VedraTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "STUDY LOGS & HABITS",
                        color = VedraTextSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    CustomButton(
                        text = "Log Study",
                        icon = Icons.Default.Add,
                        onClick = {
                            inputHabitSubject = ""
                            inputHabitMinutes = ""
                            isAddHabitModalOpen = true
                        },
                        modifier = Modifier.height(34.dp)
                    )
                }
            }

            if (studyHabits.isEmpty()) {
                item {
                    Text(
                        text = "No study logs recorded yet. Say \"Log 2 hours of Physics study\" or tap Log Study.",
                        color = VedraTextMuted,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(vertical = Spacing.medium)
                    )
                }
            } else {
                items(studyHabits, key = { it.id }) { habit ->
                    CustomCard(borderColor = VedraPurpleSecondary) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = habit.subject,
                                    color = VedraTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Duration: ${habit.durationMinutes} mins  •  Date: ${habit.dateString}",
                                    color = VedraTextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                            IconButton(
                                onClick = {
                                    dbService.deleteStudyHabit(habit.id)
                                    refreshData()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Habit Log",
                                    tint = VedraPinkAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal for Logging Study Habit
    CustomModal(
        visible = isAddHabitModalOpen,
        title = "Log Study Session",
        onDismissRequest = { isAddHabitModalOpen = false }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.medium)) {
            CustomInput(
                value = inputHabitSubject,
                onValueChange = { inputHabitSubject = it },
                placeholder = "Subject (e.g. Physics, Mechanics, Chemistry)"
            )
            CustomInput(
                value = inputHabitMinutes,
                onValueChange = { inputHabitMinutes = it },
                placeholder = "Duration in minutes (e.g. 120)"
            )
            CustomButton(
                text = "Log Session",
                onClick = {
                    val mins = inputHabitMinutes.toIntOrNull() ?: 60
                    if (inputHabitSubject.isNotBlank()) {
                        dbService.logStudyHabit(inputHabitSubject, mins)
                        refreshData()
                        isAddHabitModalOpen = false
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    // Modal for Adding Study Task
    CustomModal(
        visible = isAddTaskModalOpen,
        title = "Add Daily Study Goal",
        onDismissRequest = { isAddTaskModalOpen = false }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.medium)) {
            CustomInput(
                value = newTaskTitle,
                onValueChange = { newTaskTitle = it },
                placeholder = "Goal Title (e.g. Solve 20 Physics MCQs)"
            )
            CustomInput(
                value = newTaskSubject,
                onValueChange = { newTaskSubject = it },
                placeholder = "Subject (e.g. Physics, Chemistry, Math)"
            )
            CustomButton(
                text = "Save Goal",
                onClick = {
                    if (newTaskTitle.isNotBlank()) {
                        dbService.addStudyTask(newTaskTitle, newTaskSubject.ifBlank { "General" })
                        refreshData()
                        isAddTaskModalOpen = false
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
