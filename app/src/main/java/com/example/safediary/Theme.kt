package com.example.safediary

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val DiaryPrimary = Color(0xFF6D83B8)
private val DiarySecondary = Color(0xFFF3E9D2)
private val DiaryAccent = Color(0xFFE29587)
private val DiaryBackground = Color(0xFFF5F5F5)

private val DiaryTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Medium,
        fontSize = 56.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Medium,
        fontSize = 30.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
)

val DiaryTheme = lightColorScheme(
    primary = DiaryPrimary,
    secondary = DiarySecondary,
    background = DiaryBackground,
    onPrimary = Color.White,
    onSecondary = Color.Black
)

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(colorScheme = DiaryTheme, typography = DiaryTypography) {
        content()
    }
}