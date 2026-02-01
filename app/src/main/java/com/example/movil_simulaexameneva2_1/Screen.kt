package com.example.movil_simulaexameneva2_1

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Calendar : Screen("calendar", "Calendario", Icons.Default.DateRange)
    object Classification : Screen("classification", "Clasificación",
        Icons.AutoMirrored.Filled.List
    )
    object TeamDetail : Screen("team_detail/{teamId}", "Detalle Equipo",
        Icons.AutoMirrored.Filled.List
    )
}