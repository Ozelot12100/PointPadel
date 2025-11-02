package com.pointpadel.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pointpadel.app.ui.screens.configuracion.ConfiguracionScreen
import com.pointpadel.app.ui.screens.historial.HistorialScreen
import com.pointpadel.app.ui.screens.marcador.MarcadorScreen

sealed class Screen(val route: String) {
    object Configuracion : Screen("configuracion")
    object Marcador : Screen("marcador/{jugadorA}/{jugadorB}/{sacadorInicial}")
    object Historial : Screen("historial")
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Configuracion.route
    ) {
        composable(Screen.Configuracion.route) {
            ConfiguracionScreen(
                onIniciarPartido = { jugadorA: String, jugadorB: String, sacadorInicial: String ->
                    val route = "marcador/$jugadorA/$jugadorB/$sacadorInicial"
                    navController.navigate(route)
                },
                onNavigateToHistorial = {
                    navController.navigate(Screen.Historial.route)
                }
            )
        }

        composable(Screen.Marcador.route) { backStackEntry ->
            val jugadorA = backStackEntry.arguments?.getString("jugadorA") ?: ""
            val jugadorB = backStackEntry.arguments?.getString("jugadorB") ?: ""
            val sacadorInicial = backStackEntry.arguments?.getString("sacadorInicial") ?: "A"

            MarcadorScreen(
                jugadorA = jugadorA,
                jugadorB = jugadorB,
                jugadorQueSacaPrimero = sacadorInicial,
                onNavigateToHistorial = {
                    navController.navigate(Screen.Historial.route)
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRevanche = { jugadorA: String, jugadorB: String, sacadorInicial: String ->
                    // Navegamos al mismo MarcadorScreen pero con los mismos parámetros
                    // Esto reinicia el marcador pero mantiene los equipos
                    val route = "marcador/$jugadorA/$jugadorB/$sacadorInicial"
                    navController.navigate(route) {
                        // Eliminamos la instancia actual del stack para evitar acumulación
                        popUpTo(Screen.Marcador.route) { inclusive = true }
                    }
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
