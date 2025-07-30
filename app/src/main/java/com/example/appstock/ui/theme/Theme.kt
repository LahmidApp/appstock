package com.example.appstock.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
// This imports the CLASS androidx.compose.material3.Typography
// It does NOT import your specific instance if it's just named 'Typography'
// and in the same package (though it should still work due to Kotlin's scoping rules).

// If your Type.kt is in the same package, this 'com.example.appstock.ui.theme.Typography'
// will refer to the 'val Typography' object you defined in Type.kt.
// If Type.kt were in a different package, you'd need an import like:
// import com.example.yourproject.actualpackagename.Typography as AppTypography

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun AppStockTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        // Explicitly refer to your Typography object from the same package.
        // This 'Typography' should now unambiguously point to the 'val Typography'
        // defined in your Type.kt (or equivalent) within the 'com.example.appstock.ui.theme' package.
        typography = com.example.appstock.ui.theme.Typography, // More explicit
        // OR, if 'Type.kt' and 'Theme.kt' are in the same package 'com.example.appstock.ui.theme',
        // just 'Typography' should work, assuming there are no other conflicting 'Typography' symbols.
        // typography = Typography, // This should also work if Type.kt is set up correctly
        content = content
    )
}
