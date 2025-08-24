package com.example.agendaencarta2004v3.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.agendaencarta2004v3.core.navigation.Screen
import kotlinx.coroutines.launch

@Composable
fun DrawerMenu(navController: NavHostController, drawerState: DrawerState) {
    val scope = rememberCoroutineScope()

    val screens = listOf(
        Screen.Inicio,
        Screen.Agenda,
        Screen.Biblioteca,
        Screen.Actividades,
        Screen.Resumen
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE0E0E0)) // gris claro
            .padding(8.dp)
    ) {
        Text(
            text = "Menú Principal",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp)
        )

        screens.forEach { screen ->
            Button(
                onClick = {
                    scope.launch { drawerState.close() } // cerrar menú al seleccionar
                    navController.navigate(screen.route) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(Screen.Inicio.route) { saveState = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(screen.title)
            }
        }
    }
}

