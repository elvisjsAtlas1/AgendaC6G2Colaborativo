package com.example.agendaencarta2004v3.core.navigation

import android.app.Application
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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

    MainScaffold(navController, drawerState) {
        NavHost(navController, startDestination = Screen.Inicio.route) {

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
