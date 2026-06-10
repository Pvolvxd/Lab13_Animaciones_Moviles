package com.karla.lab13_animaciones

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.karla.lab13_animaciones.ui.theme.Lab13_AnimacionesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab13_AnimacionesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // EJERCICIO 1 (Comentado para que no llene la pantalla)
                    // Ejercicio1AnimatedVisibility(modifier = Modifier.padding(innerPadding))

                    // EJERCICIO 2 (Activo para probar el cambio de color)
                    Ejercicio2CambioColor(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun Ejercicio1AnimatedVisibility(modifier: Modifier = Modifier) {
    var isVisible by remember { mutableStateOf(true) }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = { isVisible = !isVisible }) {
            Text(text = if (isVisible) "Ocultar Cuadro" else "Mostrar Cuadro")
        }

        Spacer(modifier = Modifier.height(24.dp))

        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .background(Color.Blue)
            )
        }
    }
}

@Composable
fun Ejercicio2CambioColor(modifier: Modifier = Modifier) {
    var isBlue by remember { mutableStateOf(true) }

    /*
    val animatedColor by animateColorAsState(
        targetValue = if (isBlue) Color.Blue else Color.Green,
        animationSpec = tween(durationMillis = 1000),
        label = "Animación de Color"
    )
    */

    val animatedColor by animateColorAsState(
        targetValue = if (isBlue) Color.Blue else Color.Green,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "Animación de Color"
    )

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = { isBlue = !isBlue }) {
            Text(text = "Cambiar Color")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .size(150.dp)
                .background(animatedColor)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Ejercicio2Preview() {
    Lab13_AnimacionesTheme {
        Ejercicio2CambioColor()
    }
}