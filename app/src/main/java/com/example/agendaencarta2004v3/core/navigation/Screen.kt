package com.example.agendaencarta2004v3.core.navigation

sealed class Screen(val route: String, val title: String) {
    object Inicio : Screen("inicio", "Inicio")
    object Agenda : Screen("agenda", "Agenda")
    object Biblioteca : Screen("biblioteca", "Biblioteca")
    object Actividades : Screen("actividades", "Actividades")
    object Resumen : Screen("resumen", "Resumen")
}