package com.example.services

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object StudyService {

    suspend fun analyzeImageProblem(context: Context, imageUri: Uri): String = withContext(Dispatchers.IO) {
        try {
            // Processing state scaffolding: query Gemini with image context
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
}
