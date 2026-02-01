package com.example.movil_simulaexameneva2_1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.movil_simulaexameneva2_1.ui.theme.MovilsimulaExamenEva21Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MovilsimulaExamenEva21Theme {
                // 这里直接调用我们写好的主App界面
                GestFutApp()
            }
        }
    }
}

@Composable
fun GestFutApp() {
    val navController = rememberNavController()
    val items = listOf(Screen.Calendar, Screen.Classification)

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.route == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Calendar.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Calendar.route) {
                CalendarScreen(navController)
            }
            composable(Screen.Classification.route) {
                ClassificationScreen(navController)
            }
            composable(Screen.TeamDetail.route) { backStackEntry ->
                val teamId = backStackEntry.arguments?.getString("teamId")
                TeamDetailScreen(teamId, navController)
            }
        }
    }
}