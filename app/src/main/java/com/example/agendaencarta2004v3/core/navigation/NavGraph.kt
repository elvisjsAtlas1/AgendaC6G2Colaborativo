package com.example.agendaencarta2004v3.core.navigation

import android.app.Application
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.agendaencarta2004v3.OnBoarding.OnboardingScreen
import com.example.agendaencarta2004v3.actividades.ui.ActividadesScreen
import com.example.agendaencarta2004v3.actividades.viewmodel.ActividadViewModel
import com.example.agendaencarta2004v3.actividades.viewmodel.ActividadViewModelFactory
import com.example.agendaencarta2004v3.agenda.ui.AgendaScreen
import com.example.agendaencarta2004v3.agenda.viewmodel.EventoViewModel
import com.example.agendaencarta2004v3.agenda.viewmodel.EventoViewModelFactory
import com.example.agendaencarta2004v3.biblioteca.repository.CursoRepository
import com.example.agendaencarta2004v3.biblioteca.repository.MaterialRepository
import com.example.agendaencarta2004v3.biblioteca.repository.SemanaRepository
import com.example.agendaencarta2004v3.biblioteca.ui.BibliotecaScreen
import com.example.agendaencarta2004v3.biblioteca.viewmodel.BibliotecaViewModel
import com.example.agendaencarta2004v3.biblioteca.viewmodel.BibliotecaViewModelFactory
import com.example.agendaencarta2004v3.core.database.AppDatabase
import com.example.agendaencarta2004v3.core.ui.MainScaffold
import com.example.agendaencarta2004v3.inicio.ui.InicioScreen
import com.example.agendaencarta2004v3.resumen.ui.ResumenScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    val context = LocalContext.current
    var showOnBoarding by remember { mutableStateOf(true) }

    // ⚡ Leer preferencia guardada
    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        showOnBoarding = !prefs.getBoolean("onboarding_done", false)
    }

    MainScaffold(navController, drawerState) {
        // 🎨 Fondo global para TODA la app
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF023E73), // Azul oscuro arriba
                            Color(0xFF03658C), // Azul intermedio
                            Color(0xFF04ADBF)  // Azul claro abajo
                        )
                    )
                )
        ) {
            NavHost(
                navController,
                startDestination = if (showOnBoarding) "onboarding" else Screen.Inicio.route
            ) {
                // 👉 Ruta OnBoarding
                composable(Screen.Onboarding.route) {
                    val context = LocalContext.current
                    OnboardingScreen(
                        navController = navController,
                        onFinish = {
                            // Guardar que ya lo vio
                            val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
                            prefs.edit().putBoolean("onboarding_done", true).apply()

                            // Ir a Inicio y quitar onboarding del backstack
                            navController.navigate(Screen.Inicio.route) {
                                popUpTo(Screen.Onboarding.route) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Screen.Inicio.route) { InicioScreen() }

                composable(Screen.Agenda.route) {
                    val context = LocalContext.current
                    val application = context.applicationContext as Application

                    val agendaFactory = remember { EventoViewModelFactory(application) }
                    val bibliotecaFactory = remember { BibliotecaViewModelFactory(application) }

                    val agendaViewModel: EventoViewModel = viewModel(factory = agendaFactory)
                    val bibliotecaViewModel: BibliotecaViewModel = viewModel(factory = bibliotecaFactory)

                    AgendaScreen(
                        agendaViewModel = agendaViewModel,
                        bibliotecaViewModel = bibliotecaViewModel
                    )
                }

                composable(Screen.Biblioteca.route) {
                    val context = LocalContext.current
                    val application = context.applicationContext as Application

                    val bibliotecaFactory = remember { BibliotecaViewModelFactory(application) }
                    val bibliotecaViewModel: BibliotecaViewModel = viewModel(factory = bibliotecaFactory)

                    BibliotecaScreen(bibliotecaViewModel = bibliotecaViewModel)
                }

                composable(Screen.Actividades.route) {
                    val context = LocalContext.current
                    val application = context.applicationContext as Application

                    val actividadFactory = remember { ActividadViewModelFactory(application) }
                    val bibliotecaFactory = remember { BibliotecaViewModelFactory(application) }

                    val actividadViewModel: ActividadViewModel = viewModel(factory = actividadFactory)
                    val bibliotecaViewModel: BibliotecaViewModel = viewModel(factory = bibliotecaFactory)

                    ActividadesScreen(
                        actividadViewModel = actividadViewModel,
                        bibliotecaViewModel = bibliotecaViewModel
                    )
                }

                composable(Screen.Resumen.route) { ResumenScreen() }
            }
        }
    }
}
