package com.example.appstock.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
//import com.example.appstock.data.DatabaseManager
import com.example.appstock.ui.navigation.LibraryNavigation
import com.example.appstock.ui.theme.appstockTheme
import kotlinx.coroutines.launch

/**
 * Main activity of the library application
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize database
        lifecycleScope.launch {
            try {
                //DatabaseManager.getInstance().initializeDatabase()
            } catch (e: Exception) {
                // Handle database initialization error
                e.printStackTrace()
            }
        }
        
        setContent {
            appstockTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    com.example.appstock.ui.components.ScreenWithFooter {
                        LibraryNavigation()
                    }
                }
            }
        }
    }
}

