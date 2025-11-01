package com.pointpadel.app.ui.screens.historial

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pointpadel.app.data.local.Partido
import com.pointpadel.app.ui.theme.PadelAccentOrange
import com.pointpadel.app.ui.theme.PadelPrimaryGreen
import com.pointpadel.app.ui.theme.PadelSecondaryBlue
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(
    onNavigateBack: () -> Unit,
    viewModel: HistorialViewModel = hiltViewModel()
) {
    val partidos by viewModel.partidos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showDeleteAllDialog by remember { mutableStateOf(false) }

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
    ) {
        // TopAppBar mejorada
        TopAppBar(
            title = {
                Text(
                    "📊 Historial",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = PadelPrimaryGreen
                    )
                }
            },
            actions = {
                if (partidos.isNotEmpty()) {
                    IconButton(onClick = { showDeleteAllDialog = true }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar todo",
                            tint = PadelAccentOrange
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = PadelPrimaryGreen
            )
        )

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = PadelPrimaryGreen,
                        strokeWidth = 4.dp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Cargando historial...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else if (partidos.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = PadelPrimaryGreen.copy(alpha = 0.05f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text(
                            text = "🏓",
                            fontSize = 64.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Text(
                            text = "Sin partidos aún",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = PadelPrimaryGreen,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "¡Juega tu primer partido y aparecerá aquí!",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        } else {
            // Estadísticas rápidas
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = PadelSecondaryBlue.copy(alpha = 0.1f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem("🏆", "Partidos", partidos.size.toString())
                    StatItem("📈", "Últimos 7 días", partidos.count {
                        System.currentTimeMillis() - it.fecha < 7 * 24 * 60 * 60 * 1000
                    }.toString())
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(partidos) { partido ->
                    PartidoCard(
                        partido = partido,
                        onEliminar = { viewModel.eliminarPartido(partido) }
                    )
                }

                // Espaciado al final
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    if (showDeleteAllDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAllDialog = false },
            title = {
                Text(
                    "🗑️ Eliminar historial",
                    fontWeight = FontWeight.Bold,
                    color = PadelAccentOrange
                )
            },
            text = {
                Text(
                    "¿Estás seguro de que quieres eliminar todos los partidos? Esta acción no se puede deshacer.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.eliminarTodosLosPartidos()
                        showDeleteAllDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PadelAccentOrange
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Eliminar todo", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteAllDialog = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text("Cancelar", fontWeight = FontWeight.Medium)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun PartidoCard(
    partido: Partido,
    onEliminar: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Calcular ganador
    val setsGanadosA = listOf(
        if (partido.set1_A > partido.set1_B && partido.set1_A >= 6 && partido.set1_A - partido.set1_B >= 2) 1 else 0,
        if (partido.set2_A > partido.set2_B && partido.set2_A >= 6 && partido.set2_A - partido.set2_B >= 2) 1 else 0,
        if (partido.set3_A > partido.set3_B && partido.set3_A >= 6 && partido.set3_A - partido.set3_B >= 2) 1 else 0
    ).sum()

    val setsGanadosB = listOf(
        if (partido.set1_B > partido.set1_A && partido.set1_B >= 6 && partido.set1_B - partido.set1_A >= 2) 1 else 0,
        if (partido.set2_B > partido.set2_A && partido.set2_B >= 6 && partido.set2_B - partido.set2_A >= 2) 1 else 0,
        if (partido.set3_B > partido.set3_A && partido.set3_B >= 6 && partido.set3_B - partido.set3_A >= 2) 1 else 0
    ).sum()

    val ganadorEsA = setsGanadosA > setsGanadosB
    val nombreGanador = if (ganadorEsA) partido.jugadorA_nombre else partido.jugadorB_nombre

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header con fecha y botón eliminar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📅",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(partido.fecha)),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = PadelSecondaryBlue
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(partido.fecha)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = PadelAccentOrange,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Marcador principal
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = PadelPrimaryGreen.copy(alpha = 0.05f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Jugador A
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (ganadorEsA) {
                                Text(
                                    text = "🏆",
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                            }
                            Text(
                                text = partido.jugadorA_nombre,
                                fontSize = 16.sp,
                                fontWeight = if (ganadorEsA) FontWeight.Bold else FontWeight.Medium,
                                color = if (ganadorEsA) PadelPrimaryGreen else MaterialTheme.colorScheme.onSurface,
                                maxLines = 1
                            )
                        }
                    }

                    // Sets en el centro
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (partido.set1_A > 0 || partido.set1_B > 0) {
                            SetScoreDisplay(partido.set1_A, partido.set1_B)
                        }
                        if (partido.set2_A > 0 || partido.set2_B > 0) {
                            SetScoreDisplay(partido.set2_A, partido.set2_B)
                        }
                        if (partido.set3_A > 0 || partido.set3_B > 0) {
                            SetScoreDisplay(partido.set3_A, partido.set3_B)
                        }
                    }

                    // Jugador B
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.End
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = partido.jugadorB_nombre,
                                fontSize = 16.sp,
                                fontWeight = if (!ganadorEsA) FontWeight.Bold else FontWeight.Medium,
                                color = if (!ganadorEsA) PadelPrimaryGreen else MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                textAlign = TextAlign.End
                            )
                            if (!ganadorEsA) {
                                Text(
                                    text = "🏆",
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer con resultado final
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🥇 Ganador: $nombreGanador",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PadelPrimaryGreen
                )

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = PadelSecondaryBlue.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Sets: $setsGanadosA - $setsGanadosB",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = PadelSecondaryBlue,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    "🗑️ Eliminar partido",
                    fontWeight = FontWeight.Bold,
                    color = PadelAccentOrange
                )
            },
            text = {
                Text(
                    "¿Estás seguro de que quieres eliminar este partido?",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onEliminar()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PadelAccentOrange
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Eliminar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text("Cancelar", fontWeight = FontWeight.Medium)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

// Componentes auxiliares
@Composable
private fun StatItem(icon: String, label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = PadelSecondaryBlue
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SetScoreDisplay(scoreA: Int, scoreB: Int) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = PadelSecondaryBlue.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = "$scoreA-$scoreB",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = PadelSecondaryBlue,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
