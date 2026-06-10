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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import kotlinx.coroutines.delay

enum class GameState { ESPERANDO, APUNTANDO, MORDIDA_EXITOSA, FALLO }
enum class TargetType { CAMINANTE, CICLISTA }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Lab13_AnimacionesTheme {
                EjercicioFinalMisionMordidas()
            }
        }
    }
}

@Composable
fun EjercicioFinalMisionMordidas() {
    var score by remember { mutableIntStateOf(0) }
    var gameState by remember { mutableStateOf(GameState.ESPERANDO) }
    var targetType by remember { mutableStateOf(TargetType.CAMINANTE) }


    // Color del perrito (Marrón = Normal, Rojo = Mordiendo, Gris = Falló)
    val dogColor by animateColorAsState(
        targetValue = when (gameState) {
            GameState.MORDIDA_EXITOSA -> Color.Red
            GameState.FALLO -> Color.Gray
            else -> Color(0xFF8D6E63) // Marrón
        },
        animationSpec = tween(500),
        label = "Color Perrito"
    )

    // 2. Movimiento del perrito (Salta hacia la derecha y arriba)
    val dogOffsetX by animateDpAsState(
        targetValue = if (gameState == GameState.MORDIDA_EXITOSA || gameState == GameState.FALLO) 100.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "Salto X"
    )
    val dogOffsetY by animateDpAsState(
        targetValue = if (gameState == GameState.MORDIDA_EXITOSA) (-50.dp) else if (gameState == GameState.FALLO) (-20.dp) else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "Salto Y"
    )

    LaunchedEffect(gameState) {
        if (gameState == GameState.MORDIDA_EXITOSA || gameState == GameState.FALLO) {
            delay(1500) // Espera 1.5 segundos para ver la animación
            gameState = GameState.ESPERANDO
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(text = "🐾 Misión Mordidas 🐾", fontSize = 28.sp, fontWeight = FontWeight.Bold)

        AnimatedContent(
            targetState = score,
            transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
            label = "Puntaje"
        ) { targetScore ->
            Text(text = "Mordidas: $targetScore", fontSize = 24.sp, color = Color.Magenta)
        }

        AnimatedContent(
            targetState = gameState,
            transitionSpec = { fadeIn(tween(600)) togetherWith fadeOut(tween(600)) },
            label = "Estado Juego"
        ) { state ->
            Text(
                text = when (state) {
                    GameState.ESPERANDO -> "Buscando a quién morder..."
                    GameState.APUNTANDO -> "¡OBJETIVO! ¡Rápido!"
                    GameState.MORDIDA_EXITOSA -> "¡ÑAM! ¡Mordida perfecta!"
                    GameState.FALLO -> "¡Se escapó! Era muy rápido"
                },
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = when (state) {
                    GameState.MORDIDA_EXITOSA -> Color.Red
                    GameState.FALLO -> Color.Gray
                    else -> Color.Black
                }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color(0xFFE8F5E9), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.CenterStart
        ) {
            if (gameState == GameState.APUNTANDO) {
                AnimatedContent(
                    targetState = targetType,
                    transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
                    label = "Objetivo",
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) { target ->
                    Text(
                        text = if (target == TargetType.CAMINANTE) "🚶‍♂️" else "🚴‍♀️",
                        fontSize = 50.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .offset(x = dogOffsetX, y = dogOffsetY)
                    .size(80.dp)
                    .background(dogColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🐶", fontSize = 40.sp)
            }
        }

        AnimatedVisibility(
            visible = gameState == GameState.APUNTANDO,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Button(
                onClick = {
                    if (targetType == TargetType.CAMINANTE) {
                        gameState = GameState.MORDIDA_EXITOSA
                        score++
                    } else {
                        gameState = GameState.FALLO
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text(text = "🦷 ¡MORDER! 🦷", fontSize = 24.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                targetType = if ((0..10).random() > 6) TargetType.CICLISTA else TargetType.CAMINANTE
                gameState = GameState.APUNTANDO
            },
            enabled = gameState == GameState.ESPERANDO
        ) {
            Text(text = "👀 Buscar Objetivo", fontSize = 20.sp)
        }
    }
}