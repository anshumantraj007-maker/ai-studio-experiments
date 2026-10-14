package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class CaptionStylePreset(
    val displayName: String,
    val textColor: Color,
    val highlightColor: Color,
    val backgroundColor: Color,
    val isUppercase: Boolean
) {
    HORMOZI_BOLD(
        displayName = "Hormozi Bold",
        textColor = Color(0xFFFFFFFF),
        highlightColor = Color(0xFFFACC15), // Punchy Yellow
        backgroundColor = Color(0xE6000000), // Solid Black Backdrop
        isUppercase = true
    ),
    MR_BEAST_POP(
        displayName = "MrBeast Pop",
        textColor = Color(0xFFFFFFFF),
        highlightColor = Color(0xFF22C55E), // Vibrant Green
        backgroundColor = Color(0xCC0F172A),
        isUppercase = true
    ),
    NEON_CYBER(
        displayName = "Neon Cyber",
        textColor = Color(0xFFE2E8F0),
        highlightColor = Color(0xFF06B6D4), // Cyan
        backgroundColor = Color(0xCC1E1B4B), // Deep Purple
        isUppercase = false
    ),
    CLEAN_MINIMAL(
        displayName = "Clean Minimal",
        textColor = Color(0xFFF8FAFC),
        highlightColor = Color(0xFF8B5CF6), // Violet
        backgroundColor = Color(0x99000000),
        isUppercase = false
    )
}
