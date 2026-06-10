package com.karla.lab13_animaciones.ui.game

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

// Estados del juego general
enum class GameStatus { MENU, PLAYING, GAME_OVER }
// Tipos de personas que cruzan
enum class PersonType { CAMINANTE, CICLISTA, ABUELITA, CARTERO }

data class Target(
    val type: PersonType,
    val speed: Long, // Milisegundos en cruzar la pantalla
    val points: Int,
    val isBad: Boolean // Si muerdes a la abuelita, pierdes vida
)

@Composable
fun GameScreen() {
    var gameStatus by remember { mutableStateOf(GameStatus.MENU) }
    var score by remember { mutableIntStateOf(0) }
    var lives by remember { mutableIntStateOf(3) }
    var combo by remember { mutableIntStateOf(0) }

    // Fondo dinámico según el combo
    val bgColor by animateColorAsState(
        targetValue = when {
            combo >= 5 -> Color(0xFFFFCDD2) // Rojo claro - ¡Racha!
            combo >= 3 -> Color(0xFFFFE0B2) // Naranja claro
            else -> Color(0xFFE8F5E9) // Verde pasto normal
        },
        animationSpec = tween(1000), label = "Fondo Dinámico"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        // Transición entre Menú, Juego y Game Over
        AnimatedContent(
            targetState = gameStatus,
            transitionSpec = {
                fadeIn(tween(700)) togetherWith fadeOut(tween(700))
            },
            label = "Pantallas Juego"
        ) { status ->
            when (status) {
                GameStatus.MENU -> MenuScreen(onStart = {
                    score = 0
                    lives = 3
                    combo = 0
                    gameStatus = GameStatus.PLAYING
                })
                GameStatus.PLAYING -> PlayingScreen(
                    score = score,
                    lives = lives,
                    combo = combo,
                    onScoreUpdate = { points, isBad ->
                        if (isBad) {
                            lives--
                            combo = 0
                            if (lives <= 0) gameStatus = GameStatus.GAME_OVER
                        } else {
                            combo++
                            score += points * (1 + combo / 3) // Multiplicador por combo
                        }
                    },
                    onMiss = {
                        combo = 0 // Si se escapan, se rompe el combo
                    }
                )
                GameStatus.GAME_OVER -> GameOverScreen(score = score, onRestart = {
                    gameStatus = GameStatus.MENU
                })
            }
        }
    }
}

@Composable
fun MenuScreen(onStart: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("🐶", fontSize = 80.sp)
        Text("Misión Mordidas", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4E342E))
        Spacer(modifier = Modifier.height(16.dp))
        Text("¡Muerde a todos menos a la abuelita!", fontSize = 18.sp, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onStart,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4E342E))
        ) {
            Text("¡A Morder!", fontSize = 24.sp)
        }
    }
}

@Composable
fun GameOverScreen(score: Int, onRestart: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("🩹", fontSize = 80.sp)
        Text("Juego Terminado", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Color.Red)
        Spacer(modifier = Modifier.height(16.dp))
        AnimatedContent(
            targetState = score,
            transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
            label = "Score Final"
        ) { targetScore ->
            Text("Puntaje Final: $targetScore", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onRestart, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4E342E))) {
            Text("Volver a Jugar", fontSize = 20.sp)
        }
    }
}

@Composable
fun PlayingScreen(
    score: Int,
    lives: Int,
    combo: Int,
    onScoreUpdate: (Int, Boolean) -> Unit,
    onMiss: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    // Estado del objetivo actual
    var currentTarget by remember { mutableStateOf<Target?>(null) }
    var isBiting by remember { mutableStateOf(false) }
    var hasBeenBitten by remember { mutableStateOf(false) }

    // Posición de la persona (Empieza a la derecha, se mueve a la izquierda)
    var personX by remember { mutableStateOf(screenWidth) }
    val animatedPersonX by animateDpAsState(
        targetValue = personX,
        animationSpec = tween(durationMillis = currentTarget?.speed?.toInt() ?: 3000),
        label = "Movimiento Persona"
    )

    // Animaciones del perrito
    val dogSize by animateDpAsState(
        targetValue = if (isBiting) 90.dp else 70.dp,
        animationSpec = spring(stiffness = Spring.StiffnessHigh), label = "Tamaño Perrito"
    )
    val dogColor by animateColorAsState(
        targetValue = when {
            hasBeenBitten && currentTarget?.isBad == true -> Color.Gray // Triste
            isBiting -> Color.Red // Emocionado
            else -> Color(0xFF8D6E63) // Normal
        },
        animationSpec = tween(300), label = "Color Perrito"
    )
    val dogOffsetY by animateDpAsState(
        targetValue = if (isBiting) (-30.dp) else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMedium), label = "Salto Perrito"
    )

    // Generador de objetivos (Game Loop)
    LaunchedEffect(key1 = Unit) {
        while (true) {
            if (currentTarget == null) {
                delay(1500L) // Pausa entre objetivos
                val rand = (0..100).random()
                currentTarget = when {
                    rand < 40 -> Target(PersonType.CAMINANTE, 3000, 10, false)
                    rand < 70 -> Target(PersonType.CICLISTA, 1500, 25, false) // Más rápido
                    rand < 85 -> Target(PersonType.ABUELITA, 4000, 0, true) // ¡Prohibido!
                    else -> Target(PersonType.CARTERO, 2500, 50, false) // ¡Premio!
                }
                hasBeenBitten = false
                personX = (-100).dp // Mueve a la izquierda
            }
            delay(100)
        }
    }

    // Detectar cuando la persona salió de la pantalla
    LaunchedEffect(animatedPersonX) {
        if (currentTarget != null && animatedPersonX <= (-80).dp && !hasBeenBitten) {
            onMiss() // Rompe el combo
            currentTarget = null
            personX = screenWidth // Resetea posición
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // --- HUD Superior ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedContent(targetState = score, label = "Puntaje") { targetScore ->
                Text("🦷 $targetScore", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            }

            // Texto de Combo (AnimatedVisibility)
            AnimatedVisibility(
                visible = combo >= 3,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                Text("🔥 COMBO x${1 + combo / 3}", fontSize = 20.sp, color = Color.Red, fontWeight = FontWeight.Bold)
            }

            // Vidas (Corazones)
            Row {
                repeat(3) { index ->
                    AnimatedVisibility(
                        visible = index < lives,
                        enter = fadeIn(),
                        exit = fadeOut(tween(300))
                    ) {
                        Text("❤️", fontSize = 28.sp)
                    }
                }
            }
        }

        // --- Área de Juego ---
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.CenterStart
        ) {
            // Objetivo moviéndose
            if (currentTarget != null) {
                Box(
                    modifier = Modifier
                        .offset(x = animatedPersonX)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null // Quitamos el ripple para que parezca un juego
                        ) {
                            if (!hasBeenBitten) {
                                hasBeenBitten = true
                                isBiting = true
                                onScoreUpdate(currentTarget!!.points, currentTarget!!.isBad)

                                // Animación de mordida y reinicio
                                Thread.sleep(500)
                                isBiting = false
                                currentTarget = null
                                personX = screenWidth
                            }
                        }
                ) {
                    val emoji = when (currentTarget!!.type) {
                        PersonType.CAMINANTE -> "🚶‍♂️"
                        PersonType.CICLISTA -> "🚴‍♀️"
                        PersonType.ABUELITA -> "👵"
                        PersonType.CARTERO -> "📦"
                    }
                    Text(emoji, fontSize = 50.sp)
                }
            }

            // Perrito (A la izquierda)
            Box(
                modifier = Modifier
                    .offset(y = dogOffsetY)
                    .size(dogSize)
                    .background(dogColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("🐶", fontSize = 40.sp)
            }
        }

        // Barra indicadora de velocidad
        if (currentTarget != null) {
            LinearProgressIndicator(
                progress = { 1f - (animatedPersonX.value / screenWidth.value).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                color = if (currentTarget!!.isBad) Color.Red else Color.Cyan
            )
        }

        Spacer(modifier = Modifier.height(60.dp)) // Espacio para el botón si lo quisieras poner abajo
    }
}