package com.karla.lab13_animaciones.ui.game

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

enum class GameStatus { MENU, PLAYING, GAME_OVER }
enum class PersonType { CAMINANTE, CICLISTA, ABUELITA, CARTERO, GATO }

class ActiveTarget(
    val id: Long,
    val type: PersonType,
    val points: Int,
    val isBad: Boolean,
    val angle: Double,
    initialRadius: Float,
    val speed: Float,
    initialLives: Int = 1
) {
    var currentRadius by mutableStateOf(initialRadius)
    var isBitten by mutableStateOf(false)
    var opacity by mutableStateOf(1f)
    var livesLeft by mutableStateOf(initialLives)
}

@Composable
fun GameScreen() {
    var gameStatus by remember { mutableStateOf(GameStatus.MENU) }
    var score by remember { mutableIntStateOf(0) }
    var lives by remember { mutableIntStateOf(3) }
    var combo by remember { mutableIntStateOf(0) }

    val targetBgColors = when {
        combo >= 5 -> listOf(Color(0xFFD32F2F), Color(0xFFFF9800))
        combo >= 3 -> listOf(Color(0xFFFFB74D), Color(0xFFFF5722))
        else -> listOf(Color(0xFF81C784), Color(0xFFC8E6C9))
    }

    val colorAnimate1 by animateColorAsState(targetValue = targetBgColors[0], animationSpec = tween(800), label = "")
    val colorAnimate2 by animateColorAsState(targetValue = targetBgColors[1], animationSpec = tween(800), label = "")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(colorAnimate1, colorAnimate2))),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = gameStatus,
            transitionSpec = {
                (fadeIn(tween(500)) + scaleIn(tween(500, easing = FastOutSlowInEasing))) togetherWith
                        (fadeOut(tween(500)) + scaleOut(tween(500)))
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
                            SoundManager.playGuilt()
                            if (lives <= 0) {
                                SoundManager.playGameOver()
                                gameStatus = GameStatus.GAME_OVER
                            }
                        } else {
                            combo++
                            score += points * (1 + combo / 3)
                            if (combo % 3 == 0) SoundManager.playCombo() else SoundManager.playBite()
                        }
                    },
                    onMiss = { combo = 0 }
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
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val bounceOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text("🐶", fontSize = 85.sp, textAlign = TextAlign.Center, modifier = Modifier.offset(y = bounceOffset.dp))
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "MISIÓN\nMORDIDAS",
            fontSize = 38.sp,
            lineHeight = 40.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF3E2723),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "¡EDICIÓN MODO INFIERNO!",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFD32F2F),
            letterSpacing = 1.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.85f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "🚨 REGLAS 🚨",
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF3E2723),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🚴‍♀️ ", fontSize = 18.sp)
                    Text("¡Requiere 2 mordidas! 🪖 (+25 pts)", fontSize = 14.sp, color = Color.Black)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🐱 ", fontSize = 18.sp)
                    Text("Si lo muerdes te aturde (😵) por 1.2s.", fontSize = 14.sp, color = Color(0xFF00796B), fontWeight = FontWeight.Bold)
                }
                Row(verticalAlignment = Alignment.Top) {
                    Text("📈 ", fontSize = 18.sp)
                    Text("¡Más rápido cada 100 puntos!", fontSize = 14.sp, color = Color(0xFFE65100))
                }
                Row(verticalAlignment = Alignment.Top) {
                    Text("👵 ", fontSize = 18.sp)
                    Text("No la toques. Si la muerdes pierdes vida.", fontSize = 14.sp, color = Color(0xFFB71C1C), fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
        Button(
            onClick = onStart,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3E2723)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth(0.8f).height(58.dp)
        ) {
            Text("¡Aceptar el Reto!", fontSize = 20.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun GameOverScreen(score: Int, onRestart: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("😵💥🐕", fontSize = 70.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "¡TE DERROTARON!",
            fontSize = 39.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFFB71C1C),
            textAlign = TextAlign.Center,
            lineHeight = 46.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "La abuelita ganó esta vez...",
            fontSize = 18.sp,
            color = Color.Black.copy(alpha = 0.6f),
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(0.85f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.12f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 28.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "PUNTAJE FINAL",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    color = Color.Black.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$score",
                    fontSize = 68.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = onRestart,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(0.8f).height(60.dp)
        ) {
            Text("Volver a Intentar", fontSize = 20.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        }

        Spacer(modifier = Modifier.height(20.dp))
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
    val activeTargets = remember { mutableStateListOf<ActiveTarget>() }
    var isBiting by remember { mutableStateOf(false) }
    var isGuilty by remember { mutableStateOf(false) }
    var isStunned by remember { mutableStateOf(false) }
    var targetIdCounter by remember { mutableStateOf(0L) }

    val globalSpeedMultiplier = 1f + (score / 150f)

    val dogSize by animateDpAsState(
        targetValue = if (isStunned) 85.dp else if (isBiting) 115.dp else 90.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium), label = ""
    )

    val dogColor by animateColorAsState(
        targetValue = when {
            isStunned -> Color(0xFF9E9E9E)
            isGuilty -> Color(0xFF78909C)
            isBiting -> Color(0xFFFF1744)
            combo >= 5 -> Color(0xFFE65100)
            else -> Color(0xFFD7CCC8)
        }, label = ""
    )

    LaunchedEffect(Unit) {
        while (lives > 0) {
            val spawnDelay = (1100L - (score * 2.5).coerceAtMost(650.0).toLong()).coerceAtLeast(400L)
            delay(spawnDelay)

            val rand = (0..100).random()
            val angle = Math.random() * 2 * Math.PI

            val newTarget = when {
                rand < 25 -> ActiveTarget(targetIdCounter++, PersonType.CAMINANTE, 10, false, angle, 600f, 3.5f)
                rand < 50 -> ActiveTarget(targetIdCounter++, PersonType.CICLISTA, 25, false, angle, 600f, 6.5f, initialLives = 2)
                rand < 70 -> ActiveTarget(targetIdCounter++, PersonType.ABUELITA, 0, true, angle, 600f, 2.2f)
                rand < 88 -> ActiveTarget(targetIdCounter++, PersonType.CARTERO, 50, false, angle, 600f, 4.5f)
                else -> ActiveTarget(targetIdCounter++, PersonType.GATO, 0, false, angle, 600f, 5.0f)
            }
            activeTargets.add(newTarget)
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            withFrameMillis { _ ->
                val iterator = activeTargets.iterator()
                while (iterator.hasNext()) {
                    val target = iterator.next()

                    if (target.type == PersonType.CARTERO && target.currentRadius < 350f) {
                        target.opacity = (target.opacity - 0.025f).coerceAtLeast(0f)
                    }

                    target.currentRadius -= target.speed * globalSpeedMultiplier

                    if (target.currentRadius <= 15f) {
                        if (!target.isBitten && !target.isBad && target.type != PersonType.GATO) {
                            onMiss()
                        }
                        iterator.remove()
                    }
                }
            }
        }
    }

    if (isBiting) {
        LaunchedEffect(isBiting) {
            delay(180L)
            isBiting = false
            isGuilty = false
        }
    }

    if (isStunned) {
        LaunchedEffect(isStunned) {
            delay(1200L)
            isStunned = false
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val centerX = maxWidth / 2
        val centerY = maxHeight / 2

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 36.dp, start = 16.dp, end = 16.dp).align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("PUNTAJE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black.copy(0.6f))
                Text("🦷 $score", fontSize = 32.sp, fontWeight = FontWeight.Black)
            }

            AnimatedVisibility(visible = isStunned) {
                Box(modifier = Modifier.background(Color.Black, RoundedCornerShape(12.dp)).padding(horizontal = 10.dp, vertical = 4.dp)) {
                    Text("😵 ¡CONGELADO!", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text("VIDAS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black.copy(0.6f))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    repeat(3) { index ->
                        AnimatedVisibility(visible = index < lives, enter = fadeIn() + scaleIn(), exit = fadeOut() + scaleOut()) {
                            Text("❤️", fontSize = 24.sp)
                        }
                    }
                }
            }
        }

        activeTargets.forEach { target ->
            val emoji = when (target.type) {
                PersonType.CAMINANTE -> "🚶‍♂️"
                PersonType.CICLISTA -> if (target.livesLeft > 1) "🚴‍♀️" else "🏃‍♀️"
                PersonType.ABUELITA -> "👵"
                PersonType.CARTERO -> "📦"
                PersonType.GATO -> "🐱"
            }

            Box(
                modifier = Modifier
                    .offset {
                        val radiusPx = target.currentRadius
                        val x = centerX.toPx() + (radiusPx * cos(target.angle).toFloat()) - 35.dp.toPx()
                        val y = centerY.toPx() + (radiusPx * sin(target.angle).toFloat()) - 35.dp.toPx()
                        IntOffset(x.roundToInt(), y.roundToInt())
                    }
                    .graphicsLayer {
                        val alphaVal = if (target.type == PersonType.CARTERO) target.opacity else 1f
                        alpha = alphaVal
                        scaleX = alphaVal
                        scaleY = alphaVal
                    }
                    .size(70.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        if (!isStunned && !isBiting && (target.type != PersonType.CARTERO || target.opacity > 0.1f)) {

                            if (target.type == PersonType.GATO) {
                                isStunned = true
                                activeTargets.remove(target)
                                return@clickable
                            }

                            if (target.type == PersonType.CICLISTA && target.livesLeft > 1) {
                                target.livesLeft--
                                isBiting = true
                                SoundManager.playBite()
                                return@clickable
                            }

                            isBiting = true
                            if (target.isBad) isGuilty = true

                            onScoreUpdate(target.points, target.isBad)
                            activeTargets.remove(target)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(contentAlignment = Alignment.TopEnd) {
                    Text(emoji, fontSize = if (target.type == PersonType.CICLISTA) 52.sp else 46.sp)
                    if (target.type == PersonType.CICLISTA && target.livesLeft > 1) {
                        Text("🪖", fontSize = 18.sp, modifier = Modifier.offset(x = 6.dp, y = (-6).dp))
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(dogSize)
                .clip(CircleShape)
                .background(dogColor)
                .border(4.dp, Color.White.copy(alpha = 0.6f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            val dogFace = when {
                isStunned -> "😵"
                isGuilty -> "😰"
                isBiting -> "🤬"
                combo >= 5 -> "😈"
                else -> "🐶"
            }
            Text(dogFace, fontSize = if (isBiting) 58.sp else 48.sp)
        }
    }
}