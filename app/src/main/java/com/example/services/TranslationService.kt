package com.example.services

import java.util.Locale

data class LanguageOption(
    val code: String,
    val displayName: String,
    val locale: Locale
)

object TranslationService {

    val SUPPORTED_LANGUAGES = listOf(
        LanguageOption("en", "English", Locale.ENGLISH),
        LanguageOption("hi", "Hindi", Locale("hi", "IN")),
        LanguageOption("es", "Spanish", Locale("es", "ES")),
        LanguageOption("fr", "French", Locale.FRENCH),
        LanguageOption("de", "German", Locale.GERMAN),
        LanguageOption("ja", "Japanese", Locale.JAPANESE)
    )

    private var currentTargetLanguage = SUPPORTED_LANGUAGES[0]

    fun setTargetLanguage(code: String) {
        val found = SUPPORTED_LANGUAGES.find { it.code.equals(code, ignoreCase = true) }
        if (found != null) {
            currentTargetLanguage = found
        }
    }

    fun getTargetLanguage(): LanguageOption = currentTargetLanguage

    suspend fun translateText(text: String, targetLangCode: String): String {
        val targetOption = SUPPORTED_LANGUAGES.find { it.code.equals(targetLangCode, ignoreCase = true) || it.displayName.equals(targetLangCode, ignoreCase = true) } ?: currentTargetLanguage
        
        // Fast local translation mapping for common assistant responses, or Gemini translation
        val prompt = "Translate the following text into ${targetOption.displayName} accurately. Only output the translated text:\n\"$text\""
        return GeminiService.generateResponse(prompt)
    }
}
