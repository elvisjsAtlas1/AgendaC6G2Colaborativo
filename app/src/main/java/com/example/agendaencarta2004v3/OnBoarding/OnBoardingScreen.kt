package com.example.agendaencarta2004v3.OnBoarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.agendaencarta2004v3.core.navigation.Screen

@Composable
fun OnboardingScreen(
    navController: NavController,
    onFinish: () -> Unit
) {
    val pages = listOf(
        "Organiza tus actividades",
        "Administra tu biblioteca",
        "Revisa tu resumen"
    )

    var currentPage by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Contenido principal
        Text(
            text = pages[currentPage],
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.weight(1f).padding(top = 100.dp),
            textAlign = TextAlign.Center
        )

        // 🔹 Indicadores (puntitos)
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            pages.forEachIndexed { index, _ ->
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(
                            if (index == currentPage) Color.Blue else Color.Gray
                        )
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // 🔹 Botón inferior
        if (currentPage < pages.lastIndex) {
            Button(
                onClick = { currentPage++ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Siguiente")
            }
        } else {
            Button(
                onClick = {
                    onFinish() // Guarda flag en prefs/datastore
                    navController.navigate(Screen.Inicio.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Comenzar")
            }
        }
    }
}


