package com.example.agendaencarta2004v3.core.navigation

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.agendaencarta2004v3.actividades.ui.ActividadesScreen
import com.example.agendaencarta2004v3.agenda.ui.AgendaScreen
import com.example.agendaencarta2004v3.biblioteca.ui.BibliotecaScreen
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
            composable(Screen.Agenda.route) { AgendaScreen() }
            composable(Screen.Biblioteca.route) { BibliotecaScreen() }
            composable(Screen.Actividades.route) { ActividadesScreen() }
            composable(Screen.Resumen.route) { ResumenScreen() }
        }
    }
}

