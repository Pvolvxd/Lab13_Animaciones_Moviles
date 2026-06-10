package com.karla.lab13_animaciones

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.karla.lab13_animaciones.ui.theme.Lab13_AnimacionesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Lab13_AnimacionesTheme {
                Ejercicio3AnimacionTamanioPosicion()
            }
        }
    }
}

@Composable
fun Ejercicio1AnimatedVisibility() {
    var isVisible by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.fillMaxSize(),
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
fun Ejercicio2CambioColor() {
    var isBlue by remember { mutableStateOf(true) }

    val animatedColor by animateColorAsState(
        targetValue = if (isBlue) Color.Blue else Color.Green,
        animationSpec = tween(durationMillis = 1000),
        label = "Animación de Color"
    )

    Column(
        modifier = Modifier.fillMaxSize(),
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

@Composable
fun Ejercicio3AnimacionTamanioPosicion() {
    var isMovedAndExpanded by remember { mutableStateOf(false) }

    val targetSize by animateDpAsState(
        targetValue = if (isMovedAndExpanded) 150.dp else 80.dp,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "Tamaño"
    )

    val targetOffsetX by animateDpAsState(
        targetValue = if (isMovedAndExpanded) 50.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "Offset X"
    )

    val targetOffsetY by animateDpAsState(
        targetValue = if (isMovedAndExpanded) 30.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "Offset Y"
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = { isMovedAndExpanded = !isMovedAndExpanded }) {
            Text(text = "Mover y Cambiar Tamaño")
        }

        Spacer(modifier = Modifier.height(40.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Offset -> Size")
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .offset(x = targetOffsetX, y = targetOffsetY)
                        .size(targetSize)
                        .background(Color.Red)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Size -> Offset")
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .size(targetSize)
                        .offset(x = targetOffsetX, y = targetOffsetY)
                        .background(Color.Magenta)
                )
            }
        }
    }
}