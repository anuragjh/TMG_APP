package com.example.material.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont.Provider
import androidx.compose.ui.text.font.FontFamily
import com.example.material.R

// 1. Create the font provider
private val fontProvider = Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)


// 2. Choose a font
private val robotoFont = GoogleFont("Roboto")

// 3. Create a FontFamily using weights
val RobotoFontFamily = FontFamily(
    Font(googleFont = robotoFont, fontProvider = fontProvider, weight = FontWeight.Normal),
    Font(googleFont = robotoFont, fontProvider = fontProvider, weight = FontWeight.Medium),
    Font(googleFont = robotoFont, fontProvider = fontProvider, weight = FontWeight.Bold)
)

// 4. Apply Roboto in Typography
val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp
    ),
    titleLarge = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    labelLarge = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    )
)
