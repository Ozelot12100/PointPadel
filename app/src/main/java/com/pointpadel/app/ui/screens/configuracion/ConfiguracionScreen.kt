package com.pointpadel.app.ui.screens.configuracion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pointpadel.app.ui.theme.PadelAccentOrange
import com.pointpadel.app.ui.theme.PadelPrimaryGreen
import com.pointpadel.app.ui.theme.PadelSecondaryBlue

@Composable
fun ConfiguracionScreen(
    onIniciarPartido: (String, String, String) -> Unit,
    onNavigateToHistorial: () -> Unit,
    viewModel: ConfiguracionViewModel = hiltViewModel()
) {
    val state = viewModel.state

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
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "⚡ Nuevo Partido",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = PadelPrimaryGreen,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                // Campos de entrada
                OutlinedTextField(
                    value = state.jugadorA_nombre,
                    onValueChange = { viewModel.actualizarJugadorA(it) },
                    label = { Text("🎾 Equipo A", fontWeight = FontWeight.Medium) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PadelPrimaryGreen,
                        focusedLabelColor = PadelPrimaryGreen
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = state.jugadorB_nombre,
                    onValueChange = { viewModel.actualizarJugadorB(it) },
                    label = { Text("🏆 Equipo B", fontWeight = FontWeight.Medium) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PadelPrimaryGreen,
                        focusedLabelColor = PadelPrimaryGreen
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                // Selección de quien saca primero
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = PadelSecondaryBlue.copy(alpha = 0.1f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "🏓 ¿Quién saca primero?",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = PadelSecondaryBlue,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Opción Jugador A
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .selectable(
                                        selected = state.jugadorQueSacaPrimero == "A",
                                        onClick = { viewModel.seleccionarSacadorInicial("A") }
                                    ),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (state.jugadorQueSacaPrimero == "A")
                                        PadelPrimaryGreen.copy(alpha = 0.2f)
                                    else
                                        MaterialTheme.colorScheme.surface
                                ),
                                border = if (state.jugadorQueSacaPrimero == "A")
                                    CardDefaults.outlinedCardBorder().copy(width = 2.dp)
                                else null
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    RadioButton(
                                        selected = state.jugadorQueSacaPrimero == "A",
                                        onClick = { viewModel.seleccionarSacadorInicial("A") },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = PadelPrimaryGreen
                                        )
                                    )
                                    Text(
                                        text = if (state.jugadorA_nombre.isNotBlank())
                                            state.jugadorA_nombre
                                        else "Equipo A",
                                        fontWeight = FontWeight.Medium,
                                        textAlign = TextAlign.Center,
                                        color = if (state.jugadorQueSacaPrimero == "A")
                                            PadelPrimaryGreen
                                        else
                                            MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            // Opción Jugador B
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .selectable(
                                        selected = state.jugadorQueSacaPrimero == "B",
                                        onClick = { viewModel.seleccionarSacadorInicial("B") }
                                    ),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (state.jugadorQueSacaPrimero == "B")
                                        PadelPrimaryGreen.copy(alpha = 0.2f)
                                    else
                                        MaterialTheme.colorScheme.surface
                                ),
                                border = if (state.jugadorQueSacaPrimero == "B")
                                    CardDefaults.outlinedCardBorder().copy(width = 2.dp)
                                else null
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    RadioButton(
                                        selected = state.jugadorQueSacaPrimero == "B",
                                        onClick = { viewModel.seleccionarSacadorInicial("B") },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = PadelPrimaryGreen
                                        )
                                    )
                                    Text(
                                        text = if (state.jugadorB_nombre.isNotBlank())
                                            state.jugadorB_nombre
                                        else "Equipo B",
                                        fontWeight = FontWeight.Medium,
                                        textAlign = TextAlign.Center,
                                        color = if (state.jugadorQueSacaPrimero == "B")
                                            PadelPrimaryGreen
                                        else
                                            MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                // Información del formato
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = PadelAccentOrange.copy(alpha = 0.1f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "📋 FORMATO DEL PARTIDO",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = PadelAccentOrange,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "🥇 Gana quien alcance 2 sets",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "⚔️ Máximo 3 sets por partido (2-0 o 2-1)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                // Botón de inicio
                Button(
                    onClick = {
                        onIniciarPartido(
                            state.jugadorA_nombre,
                            state.jugadorB_nombre,
                            state.jugadorQueSacaPrimero
                        )
                    },
                    enabled = state.puedeIniciarPartido,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PadelPrimaryGreen,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                ) {
                    Text(
                        "🚀 INICIAR PARTIDO",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
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
                    containerColor = androidx.compose.ui.graphics.Color.Transparent
                )
            ) {
                Text(
                    "📊 Ver Historial",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = androidx.compose.ui.graphics.Color.White
                )
            }
        }
    }
}
