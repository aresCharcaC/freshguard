package com.example.freshguard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.freshguard.ui.theme.FreshguardTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.freshguard.ui.screen.ConfigurationScreen
import com.example.freshguard.ui.screen.FoodMonitorScreen
import com.example.freshguard.viewmodel.FoodMonitorViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FreshguardTheme {
                // Creamos un NavController para manejar la navegación
                val navController = rememberNavController()
                // Creamos una instancia compartida del ViewModel
                val viewModel: FoodMonitorViewModel = viewModel()

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(navController = navController, startDestination = "monitor") {
                        composable("monitor") {
                            FoodMonitorScreen(viewModel = viewModel, navController = navController)
                        }
                        composable("config") {
                            ConfigurationScreen(viewModel = viewModel, navController = navController)
                        }
                    }
                }
            }
        }
    }
}