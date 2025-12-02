package com.example.storygeneration

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.storygeneration.ui.screen.AssetsScreen
import com.example.storygeneration.ui.screen.BottomNavigationBarScreen
import com.example.storygeneration.ui.screen.CreateScreen
import com.example.storygeneration.ui.screen.PreviewScreen
import com.example.storygeneration.ui.screen.ShotDetailScreen
import com.example.storygeneration.ui.screen.StoryboardScreen
import com.example.storygeneration.viewmodel.StoryViewModel
import com.example.storygeneration.ui.theme.StoryGenerationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StoryGenerationTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") { BottomNavigationBarScreen(navController) }
                    composable("create") { 
                        val viewModel = StoryViewModel()
                        CreateScreen(navController, viewModel) 
                    }
                    composable("storyboard") { 
                        val viewModel = StoryViewModel()
                        StoryboardScreen(navController, viewModel) 
                    }
                    composable("shotDetail/{shotId}") { backStackEntry ->
                        ShotDetailScreen(
                            navController,
                            backStackEntry.arguments?.getString("shotId")
                        )
                    }
                    composable("assets") { AssetsScreen(navController) }
                    composable("preview") { PreviewScreen(navController) }
                }
            }
        }
    }
}