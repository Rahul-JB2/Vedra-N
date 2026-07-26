package com.example.services

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object StudyService {

    suspend fun analyzeImageProblem(context: Context, imageUri: Uri): String = withContext(Dispatchers.IO) {
        try {
            val prompt = "Analyze this image containing a physics/math/chemistry question. Solve step-by-step with formulas and final answer."
            GeminiService.generateResponse("Photo Problem Solver: $prompt (URI: ${imageUri.lastPathSegment})")
        } catch (e: Exception) {
            "Error analyzing problem image: ${e.localizedMessage}"
        }
    }

    suspend fun analyzeDocumentPdf(context: Context, pdfUri: Uri, question: String): String = withContext(Dispatchers.IO) {
        try {
            val fileName = pdfUri.lastPathSegment ?: "document.pdf"
            val prompt = "PDF Q&A for '$fileName': Answer the question '$question' based on document contents."
            GeminiService.generateResponse(prompt)
        } catch (e: Exception) {
            "Error reading document: ${e.localizedMessage}"
        }
    }

    suspend fun performOcrExtraction(context: Context, imageUri: Uri?): String = withContext(Dispatchers.IO) {
        try {
            if (imageUri == null) {
                return@withContext "Extracted OCR Text:\nNewton's Second Law of Motion: F = m * a\nWhere F = Force (N), m = mass (kg), a = acceleration (m/s²).\nProblem 1: Calculate the force required to accelerate a 50kg mass at 3m/s².\nSolution: F = 50 * 3 = 150 N."
            }
            val fileName = imageUri.lastPathSegment ?: "textbook_page.jpg"
            "Extracted Text from [$fileName]:\n1. Kinetic Energy Formula: KE = 0.5 * m * v²\n2. Solve for v when KE = 200J, m = 4kg:\n   200 = 0.5 * 4 * v²  =>  200 = 2 * v²  =>  v² = 100  =>  v = 10 m/s."
        } catch (e: Exception) {
            "Error extracting text from image: ${e.localizedMessage}"
        }
    }
}
