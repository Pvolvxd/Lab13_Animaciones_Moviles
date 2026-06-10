package com.karla.lab13_animaciones
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karla.lab13_animaciones.ui.theme.Lab13_AnimacionesTheme

enum class UiState { Cargando, Contenido, Error }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Lab13_AnimacionesTheme {
                Ejercicio4AnimatedContent()
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


@Composable
fun Ejercicio4AnimatedContent() {
    // Estado que cambia entre Cargando, Contenido y Error
    var uiState by remember { mutableStateOf(UiState.Cargando) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = { uiState = UiState.Cargando }) {
                Text("Cargando")
            }
            Button(onClick = { uiState = UiState.Contenido }) {
                Text("Contenido")
            }
            Button(onClick = { uiState = UiState.Error }) {
                Text("Error")
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        AnimatedContent(
            targetState = uiState,
            transitionSpec = {
                fadeIn(animationSpec = tween(700)) togetherWith fadeOut(animationSpec = tween(700))
            },
            label = "Cambio de Estado UI"
        ) { targetState ->
            when (targetState) {
                UiState.Cargando -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Cargando datos...", fontSize = 20.sp)
                    }
                }
                UiState.Contenido -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "¡Éxito!",
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Los datos se han cargado correctamente.", fontSize = 16.sp)
                    }
                }
                UiState.Error -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Error",
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFC62828)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "No se pudo conectar al servidor.", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}