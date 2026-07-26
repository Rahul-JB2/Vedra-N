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
import com.example.services.Flashcard
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

    fun refreshData() {
        studyTasks.clear()
        studyTasks.addAll(dbService.getAllStudyTasks())

        flashcards.clear()
        flashcards.addAll(dbService.getAllFlashcards())
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
                val tabs = listOf("Planner", "Flashcards", "Image & PDF Q&A")
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

                            if (selectedImageUri != null) {
                                CustomButton(
                                    text = "Solve with AI",
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
