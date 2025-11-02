package com.pointpadel.app.ui.screens.marcador

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pointpadel.app.ui.theme.PadelAccentOrange
import com.pointpadel.app.ui.theme.PadelPrimaryGreen
import com.pointpadel.app.ui.theme.PadelSecondaryBlue
import kotlinx.coroutines.delay

@Composable
fun MarcadorScreen(
    jugadorA: String,
    jugadorB: String,
    jugadorQueSacaPrimero: String,
    onNavigateToHistorial: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: MarcadorViewModel = hiltViewModel()
) {
    val state = viewModel.state

    // Inicializar el partido cuando se entra a la pantalla
    LaunchedEffect(jugadorA, jugadorB, jugadorQueSacaPrimero) {
        if (!state.partidoIniciado) {
            viewModel.iniciarPartido(jugadorA, jugadorB, jugadorQueSacaPrimero)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MarcadorPartido(
            state = state,
            onPuntoA = { viewModel.sumarPuntoA() },
            onPuntoB = { viewModel.sumarPuntoB() },
            onDeshacerAccion = { viewModel.deshacerUltimaAccion() },
            onGuardarPartido = {
                viewModel.guardarPartido()
                onNavigateBack()
            },
            onReiniciarPartido = {
                viewModel.reiniciarPartido()
                onNavigateBack()
            },
            onNavigateToHistorial = onNavigateToHistorial
        )
    }
}

@Composable
private fun MarcadorPartido(
    state: MarcadorState,
    onPuntoA: () -> Unit,
    onPuntoB: () -> Unit,
    onDeshacerAccion: () -> Unit,
    onGuardarPartido: () -> Unit,
    onReiniciarPartido: () -> Unit,
    onNavigateToHistorial: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Card para nombres de jugadores
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = PadelPrimaryGreen.copy(alpha = 0.1f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "🎾 ${state.jugadorA_nombre}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = PadelPrimaryGreen
                )
                Text(
                    text = "VS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${state.jugadorB_nombre} 🏆",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = PadelPrimaryGreen
                )
            }
        }

        // Marcador de sets mejorado
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "📊 MARCADOR DE SETS",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = PadelSecondaryBlue,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SetDisplay("Set 1", state.set1_A, state.set1_B)
                    if (state.setActual >= 2) {
                        SetDisplay("Set 2", state.set2_A, state.set2_B)
                    }
                    if (state.setActual >= 3) {
                        SetDisplay("Set 3", state.set3_A, state.set3_B)
                    }
                }
            }
        }

        // Marcador de puntos principal - unificado con información de estado
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            colors = CardDefaults.cardColors(
                containerColor = when {
                    state.esPuntoDeOro -> PadelAccentOrange.copy(alpha = 0.15f)
                    state.enTieBreak -> PadelSecondaryBlue.copy(alpha = 0.15f)
                    else -> MaterialTheme.colorScheme.surface
                }
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Título del estado actual (solo si no es partido terminado)
                if (!state.partidoTerminado) {
                    Text(
                        text = when {
                            state.esPuntoDeOro -> "🔥 Punto de Oro"
                            state.enTieBreak -> "⚡ Tie Break"
                            else -> state.marcadorTexto
                        },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            state.esPuntoDeOro -> PadelAccentOrange
                            state.enTieBreak -> PadelSecondaryBlue
                            else -> PadelPrimaryGreen
                        },
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // Marcador de puntos
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Jugador A Score
                    ScoreDisplay(
                        score = state.puntosTextoA,
                        isSpecialState = state.esPuntoDeOro || state.enTieBreak,
                        isPuntoDeOro = state.esPuntoDeOro,
                        playerIndex = 0
                    )

                    // Separador central
                    Text(
                        text = ":",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            state.esPuntoDeOro -> PadelAccentOrange
                            state.enTieBreak -> PadelSecondaryBlue
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    )

                    // Jugador B Score
                    ScoreDisplay(
                        score = state.puntosTextoB,
                        isSpecialState = state.esPuntoDeOro || state.enTieBreak,
                        isPuntoDeOro = state.esPuntoDeOro,
                        playerIndex = 1
                    )
                }

                // Información del saque (solo si no es partido terminado)
                if (!state.partidoTerminado) {
                    Text(
                        text = state.infoSaque,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        if (!state.partidoTerminado) {
            // Botones para sumar puntos con animaciones
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Botón Jugador A con animación
                AnimatedPuntoButton(
                    onClick = onPuntoA,
                    modifier = Modifier.weight(1f),
                    text = "+1 PUNTO",
                    playerName = state.jugadorA_nombre,
                    backgroundColor = PadelPrimaryGreen,
                    isPuntoDeOro = state.esPuntoDeOro
                )

                // Botón Jugador B con animación
                AnimatedPuntoButton(
                    onClick = onPuntoB,
                    modifier = Modifier.weight(1f),
                    text = "+1 PUNTO",
                    playerName = state.jugadorB_nombre,
                    backgroundColor = PadelPrimaryGreen,
                    isPuntoDeOro = state.esPuntoDeOro
                )
            }

            // Botón de deshacer
            if (state.puedeDeshacer) {
                Button(
                    onClick = onDeshacerAccion,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PadelAccentOrange
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "⏪ DESHACER ÚLTIMA ACCIÓN",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            // Partido terminado
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = PadelPrimaryGreen.copy(alpha = 0.1f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "🏆",
                        fontSize = 48.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "¡PARTIDO TERMINADO!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = PadelPrimaryGreen,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "🥇 GANADOR",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PadelAccentOrange,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    Text(
                        text = if (state.ganadorPartido == "A") state.jugadorA_nombre else state.jugadorB_nombre,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PadelPrimaryGreen,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "📊 Sets",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${state.setsGanadosA} - ${state.setsGanadosB}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "⏱️ Duración",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${state.duracionMinutos} min",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = onGuardarPartido,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PadelPrimaryGreen
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                "💾 Guardar",
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = onReiniciarPartido,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PadelSecondaryBlue
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                "🔄 Nuevo",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón de historial
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = PadelSecondaryBlue
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Button(
                onClick = onNavigateToHistorial,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                )
            ) {
                Text(
                    "📊 Ver Historial",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

// Componentes auxiliares
@Composable
private fun SetDisplay(setName: String, scoreA: Int, scoreB: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = setName,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "$scoreA - $scoreB",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = PadelPrimaryGreen
        )
    }
}

@Composable
private fun AnimatedPuntoButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String,
    playerName: String,
    backgroundColor: Color,
    isPuntoDeOro: Boolean = false
) {
    var isPressed by remember { mutableStateOf(false) }

    // Animación de escala al presionar
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(
            durationMillis = 100,
            easing = FastOutSlowInEasing
        ),
        label = "button_press"
    )

    // Animación de elevación
    val elevation by animateDpAsState(
        targetValue = if (isPressed) 2.dp else if (isPuntoDeOro) 12.dp else 6.dp,
        animationSpec = tween(
            durationMillis = 100,
            easing = FastOutSlowInEasing
        ),
        label = "button_elevation"
    )

    Button(
        onClick = {
            onClick()
        },
        modifier = modifier
            .height(if (isPuntoDeOro) 70.dp else 60.dp)
            .scale(scale),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = elevation)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text,
                fontSize = if (isPuntoDeOro) 18.sp else 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            if (playerName.isNotEmpty()) {
                Text(
                    playerName,
                    fontSize = 12.sp,
                    maxLines = 1,
                    color = Color.White
                )
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun ScoreDisplay(
    score: String,
    isSpecialState: Boolean,
    isPuntoDeOro: Boolean = false,
    playerIndex: Int = 0 // 0 para jugador A, 1 para jugador B
) {
    // Estado para controlar las animaciones
    var isAnimating by remember { mutableStateOf(false) }
    var lastScore by remember { mutableStateOf(score) }

    // Detectar cambio de score para activar animación
    LaunchedEffect(score) {
        if (score != lastScore && lastScore.isNotEmpty()) {
            isAnimating = true
            delay(300) // Duración de la animación
            isAnimating = false
        }
        lastScore = score
    }

    // Animación de pulsación
    val scale by animateFloatAsState(
        targetValue = if (isAnimating) 1.2f else 1f,
        animationSpec = tween(
            durationMillis = 150,
            easing = FastOutSlowInEasing
        ),
        label = "score_pulse"
    )

    // Animación de brillo/color
    val brightness by animateFloatAsState(
        targetValue = if (isAnimating) 1.3f else 1f,
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        ),
        label = "score_brightness"
    )

    // Pequeña rotación para efectos especiales
    val rotation by animateFloatAsState(
        targetValue = if (isAnimating && isPuntoDeOro)
            if (playerIndex == 0) -2f else 2f
        else 0f,
        animationSpec = tween(
            durationMillis = 200,
            easing = FastOutSlowInEasing
        ),
        label = "score_rotation"
    )

    Card(
        modifier = Modifier
            .size(if (isPuntoDeOro) 150.dp else 140.dp)
            .scale(scale)
            .graphicsLayer {
                rotationZ = rotation
                scaleX = brightness
                scaleY = brightness
            },
        colors = CardDefaults.cardColors(
            containerColor = when {
                isPuntoDeOro -> PadelAccentOrange.copy(alpha = if (isAnimating) 0.3f else 0.15f)
                isSpecialState -> if (isAnimating)
                    PadelAccentOrange.copy(alpha = 0.4f)
                else
                    MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
                else -> if (isAnimating)
                    PadelPrimaryGreen.copy(alpha = 0.3f)
                else
                    MaterialTheme.colorScheme.primaryContainer
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isAnimating) 16.dp else if (isPuntoDeOro) 12.dp else 8.dp
        ),
        shape = RoundedCornerShape(if (isPuntoDeOro) 20.dp else 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animación del texto del score
            AnimatedContent(
                targetState = score,
                transitionSpec = {
                    (scaleIn(
                        animationSpec = tween(150),
                        initialScale = 0.8f
                    ) + fadeIn(animationSpec = tween(150))) togetherWith
                    (scaleOut(
                        animationSpec = tween(150),
                        targetScale = 1.2f
                    ) + fadeOut(animationSpec = tween(150)))
                },
                label = "score_change"
            ) { targetScore ->
                Text(
                    text = targetScore,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black,
                    color = when {
                        isPuntoDeOro -> PadelAccentOrange
                        isSpecialState -> if (isAnimating) Color.White else PadelAccentOrange
                        else -> if (isAnimating) Color.White else PadelPrimaryGreen
                    },
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
