package com.pointpadel.app.ui.screens.marcador

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pointpadel.app.ui.theme.PadelAccentOrange
import com.pointpadel.app.ui.theme.PadelPrimaryGreen
import com.pointpadel.app.ui.theme.PadelSecondaryBlue

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

        // Información del estado actual
        if (!state.partidoTerminado) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when {
                        state.esPuntoDeOro -> PadelAccentOrange.copy(alpha = 0.1f)
                        state.enTieBreak -> PadelSecondaryBlue.copy(alpha = 0.1f)
                        else -> MaterialTheme.colorScheme.primaryContainer
                    }
                )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = state.marcadorTexto,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            state.esPuntoDeOro -> PadelAccentOrange
                            state.enTieBreak -> PadelSecondaryBlue
                            else -> PadelPrimaryGreen
                        },
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = state.infoSaque,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Marcador de puntos principal - más grande y visual
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Jugador A Score
                ScoreDisplay(
                    score = state.puntosTextoA,
                    isSpecialState = state.esPuntoDeOro || state.enTieBreak
                )

                // Separador central mejorado
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (state.esPuntoDeOro) {
                        Text(
                            text = "🔥",
                            fontSize = 24.sp
                        )
                        Text(
                            text = "PUNTO",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PadelAccentOrange
                        )
                        Text(
                            text = "DE ORO",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PadelAccentOrange
                        )
                    } else if (state.enTieBreak) {
                        Text(
                            text = "⚡",
                            fontSize = 24.sp
                        )
                        Text(
                            text = "TIE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PadelSecondaryBlue
                        )
                        Text(
                            text = "BREAK",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PadelSecondaryBlue
                        )
                    } else {
                        Text(
                            text = ":",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Jugador B Score
                ScoreDisplay(
                    score = state.puntosTextoB,
                    isSpecialState = state.esPuntoDeOro || state.enTieBreak
                )
            }
        }

        if (!state.partidoTerminado) {
            // Botones para sumar puntos
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Botón Jugador A
                Button(
                    onClick = onPuntoA,
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PadelPrimaryGreen
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "+1 PUNTO",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            state.jugadorA_nombre,
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    }
                }

                // Botón Jugador B
                Button(
                    onClick = onPuntoB,
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PadelPrimaryGreen
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "+1 PUNTO",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            state.jugadorB_nombre,
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    }
                }
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
private fun ScoreDisplay(
    score: String,
    isSpecialState: Boolean
) {
    Card(
        modifier = Modifier.size(140.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSpecialState) {
                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
            } else {
                MaterialTheme.colorScheme.primaryContainer
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = score,
                fontSize = 48.sp,
                fontWeight = FontWeight.Black,
                color = if (isSpecialState) PadelAccentOrange else PadelPrimaryGreen
            )
        }
    }
}
