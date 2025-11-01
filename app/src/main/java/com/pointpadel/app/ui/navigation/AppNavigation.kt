package com.pointpadel.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pointpadel.app.ui.screens.historial.HistorialScreen
import com.pointpadel.app.ui.screens.marcador.MarcadorScreen

sealed class Screen(val route: String) {
    object Marcador : Screen("marcador")
    object Historial : Screen("historial")
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Marcador.route
    ) {
        composable(Screen.Marcador.route) {
            MarcadorScreen(
                onNavigateToHistorial = {
                    navController.navigate(Screen.Historial.route)
                }
            )
        }

        composable(Screen.Historial.route) {
            HistorialScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
