package com.pointpadel.app.ui.screens.marcador

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pointpadel.app.data.local.Partido
import com.pointpadel.app.data.repository.MatchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MarcadorViewModel @Inject constructor(
    private val matchRepository: MatchRepository
) : ViewModel() {

    var state by mutableStateOf(MarcadorState())
        private set

    fun iniciarPartido(
        jugadorA: String,
        jugadorB: String,
        jugadorQueSacaPrimero: String
    ) {
        state = state.copy(
            jugadorA_nombre = jugadorA,
            jugadorB_nombre = jugadorB,
            partidoIniciado = true,
            tiempoInicio = System.currentTimeMillis(),
            jugadorQueSaca = jugadorQueSacaPrimero,
            historialAcciones = emptyList(),
            puedeDeshacer = false
        )
    }

    private fun guardarAccion(accion: AccionPartido) {
        val nuevasAcciones = state.historialAcciones + accion
        state = state.copy(
            historialAcciones = nuevasAcciones.takeLast(10), // Límite de 10 acciones
            puedeDeshacer = true
        )
    }

    fun deshacerUltimaAccion() {
        if (!state.puedeDeshacer || state.partidoTerminado) return

        val ultimaAccion = state.historialAcciones.lastOrNull()
        if (ultimaAccion != null) {
            // Restaurar el estado anterior
            val estadoAnterior = when (ultimaAccion) {
                is AccionPartido.PuntoAnotado -> ultimaAccion.estadoAnterior
                is AccionPartido.JuegoGanado -> ultimaAccion.estadoAnterior
                is AccionPartido.SetGanado -> ultimaAccion.estadoAnterior
                is AccionPartido.TieBreakIniciado -> ultimaAccion.estadoAnterior
                is AccionPartido.TieBreakTerminado -> ultimaAccion.estadoAnterior
            }

            // Actualizar el historial quitando la última acción
            val nuevasAcciones = state.historialAcciones.dropLast(1)

            state = estadoAnterior.copy(
                historialAcciones = nuevasAcciones,
                puedeDeshacer = nuevasAcciones.isNotEmpty()
            )
        }
    }

    fun sumarPuntoA() {
        if (state.partidoTerminado) return

        // Guardar estado actual antes de hacer cambios
        val estadoAnterior = state.copy()

        if (state.enTieBreak) {
            sumarPuntoTieBreak("A")
        } else {
            val nuevoState = state.copy(puntosA = state.puntosA + 1)
            verificarJuego(nuevoState, jugadorQueAnoto = "A")
        }

        // Guardar la acción después del cambio
        guardarAccion(AccionPartido.PuntoAnotado("A", estadoAnterior))
    }

    fun sumarPuntoB() {
        if (state.partidoTerminado) return

        // Guardar estado actual antes de hacer cambios
        val estadoAnterior = state.copy()

        if (state.enTieBreak) {
            sumarPuntoTieBreak("B")
        } else {
            val nuevoState = state.copy(puntosB = state.puntosB + 1)
            verificarJuego(nuevoState, jugadorQueAnoto = "B")
        }

        // Guardar la acción después del cambio
        guardarAccion(AccionPartido.PuntoAnotado("B", estadoAnterior))
    }

    private fun verificarJuego(nuevoState: MarcadorState, jugadorQueAnoto: String) {
        when {
            // PUNTO DE ORO: 40-40, el siguiente punto gana el juego
            state.puntosA == 3 && state.puntosB == 3 -> {
                ganarJuego(jugadorQueAnoto)
            }
            // Ganar juego normal: llegar a 4+ puntos con ventaja de 2+
            nuevoState.puntosA >= 4 && nuevoState.puntosA - nuevoState.puntosB >= 2 -> {
                ganarJuego("A")
            }
            nuevoState.puntosB >= 4 && nuevoState.puntosB - nuevoState.puntosA >= 2 -> {
                ganarJuego("B")
            }
            else -> {
                state = nuevoState
            }
        }
    }

    private fun ganarJuego(jugador: String) {
        // Guardar estado antes del cambio
        val estadoAnterior = state.copy()

        // Actualizar el marcador de juegos según el set actual
        val nuevoState = when (state.setActual) {
            1 -> {
                if (jugador == "A") {
                    state.copy(puntosA = 0, puntosB = 0, set1_A = state.set1_A + 1)
                } else {
                    state.copy(puntosA = 0, puntosB = 0, set1_B = state.set1_B + 1)
                }
            }
            2 -> {
                if (jugador == "A") {
                    state.copy(puntosA = 0, puntosB = 0, set2_A = state.set2_A + 1)
                } else {
                    state.copy(puntosA = 0, puntosB = 0, set2_B = state.set2_B + 1)
                }
            }
            3 -> {
                if (jugador == "A") {
                    state.copy(puntosA = 0, puntosB = 0, set3_A = state.set3_A + 1)
                } else {
                    state.copy(puntosA = 0, puntosB = 0, set3_B = state.set3_B + 1)
                }
            }
            else -> state
        }

        // Cambiar el saque al otro jugador
        val nuevoSacador = if (state.jugadorQueSaca == "A") "B" else "A"
        state = nuevoState.copy(jugadorQueSaca = nuevoSacador)

        // Guardar la acción
        guardarAccion(AccionPartido.JuegoGanado(jugador, estadoAnterior))

        verificarSet()
    }

    private fun verificarSet() {
        val (juegosA, juegosB) = state.getJuegosDelSetActual()

        when {
            // Verificar si se debe iniciar tie-break (6-6)
            juegosA == 6 && juegosB == 6 -> {
                iniciarTieBreak()
            }
            // Verificar si alguien ganó el set (6+ juegos con ventaja de 2+)
            juegosA >= 6 && juegosA - juegosB >= 2 -> {
                ganarSet("A")
            }
            juegosB >= 6 && juegosB - juegosA >= 2 -> {
                ganarSet("B")
            }
            // Si está 5-5 o menos, continuar jugando
        }
    }

    private fun iniciarTieBreak() {
        // Guardar estado antes del cambio
        val estadoAnterior = state.copy()

        state = state.copy(
            enTieBreak = true,
            puntosTieBreakA = 0,
            puntosTieBreakB = 0,
            contadorSaquesTieBreak = 0,
            puntosA = 0,
            puntosB = 0
        )

        // Guardar la acción
        guardarAccion(AccionPartido.TieBreakIniciado(estadoAnterior))
    }

    private fun sumarPuntoTieBreak(jugador: String) {
        val nuevoState = if (jugador == "A") {
            state.copy(puntosTieBreakA = state.puntosTieBreakA + 1)
        } else {
            state.copy(puntosTieBreakB = state.puntosTieBreakB + 1)
        }

        // Actualizar contador de saques y rotación
        val nuevoContador = state.contadorSaquesTieBreak + 1
        val nuevoSacador = calcularSacadorTieBreak(nuevoContador)

        state = nuevoState.copy(
            contadorSaquesTieBreak = nuevoContador,
            jugadorQueSaca = nuevoSacador
        )

        verificarTieBreak()
    }

    private fun calcularSacadorTieBreak(contador: Int): String {
        // Lógica de saque en tie-break:
        // Punto 1: A saca (1 punto)
        // Puntos 2-3: B saca (2 puntos)
        // Puntos 4-5: A saca (2 puntos)
        // Puntos 6-7: B saca (2 puntos)... y así sucesivamente

        return when {
            contador == 1 -> "A" // Primer punto
            contador % 4 == 2 || contador % 4 == 3 -> if ((contador - 1) / 4 % 2 == 0) "B" else "A"
            contador % 4 == 0 || contador % 4 == 1 -> if ((contador - 1) / 4 % 2 == 0) "A" else "B"
            else -> "A"
        }
    }

    private fun verificarTieBreak() {
        val puntosA = state.puntosTieBreakA
        val puntosB = state.puntosTieBreakB

        when {
            // Ganar tie-break: 7+ puntos con ventaja de 2+
            puntosA >= 7 && puntosA - puntosB >= 2 -> {
                ganarTieBreak("A")
            }
            puntosB >= 7 && puntosB - puntosA >= 2 -> {
                ganarTieBreak("B")
            }
            // Continuar jugando si no se cumple la condición
        }
    }

    private fun ganarTieBreak(jugador: String) {
        // Guardar estado antes del cambio
        val estadoAnterior = state.copy()

        // El ganador del tie-break gana el set 7-6
        val nuevoState = when (state.setActual) {
            1 -> {
                if (jugador == "A") {
                    state.copy(set1_A = 7, set1_B = 6)
                } else {
                    state.copy(set1_A = 6, set1_B = 7)
                }
            }
            2 -> {
                if (jugador == "A") {
                    state.copy(set2_A = 7, set2_B = 6)
                } else {
                    state.copy(set2_A = 6, set2_B = 7)
                }
            }
            3 -> {
                if (jugador == "A") {
                    state.copy(set3_A = 7, set3_B = 6)
                } else {
                    state.copy(set3_A = 6, set3_B = 7)
                }
            }
            else -> state
        }

        state = nuevoState.copy(
            enTieBreak = false,
            puntosTieBreakA = 0,
            puntosTieBreakB = 0,
            contadorSaquesTieBreak = 0,
            puntosA = 0,
            puntosB = 0
        )

        // Guardar la acción
        guardarAccion(AccionPartido.TieBreakTerminado(jugador, estadoAnterior))

        ganarSet(jugador)
    }

    private fun ganarSet(jugador: String) {
        // Guardar estado antes del cambio
        val estadoAnterior = state.copy()

        // Las propiedades setsGanadosA y setsGanadosB ya reflejan el estado actualizado
        // porque se calculan en base a los sets ya guardados en el estado
        val setsGanadosA = state.setsGanadosA
        val setsGanadosB = state.setsGanadosB

        // En pádel siempre se gana con 2 sets
        val setsParaGanar = 2

        // Verificar si alguien ganó el partido
        if (setsGanadosA >= setsParaGanar) {
            terminarPartido("A")
        } else if (setsGanadosB >= setsParaGanar) {
            terminarPartido("B")
        } else {
            // Continuar con el siguiente set
            val nuevoSacador = if (state.jugadorQueSaca == "A") "B" else "A"
            state = state.copy(
                setActual = state.setActual + 1,
                jugadorQueSaca = nuevoSacador,
                puntosA = 0,
                puntosB = 0
            )
        }

        // Guardar la acción
        guardarAccion(AccionPartido.SetGanado(jugador, estadoAnterior))
    }

    private fun terminarPartido(ganador: String) {
        val tiempoFin = System.currentTimeMillis()
        val duracion = if (state.tiempoInicio != null) {
            ((tiempoFin - state.tiempoInicio!!) / 60000).toInt()
        } else 0

        state = state.copy(
            partidoTerminado = true,
            ganadorPartido = ganador,
            duracionMinutos = duracion
        )
    }

    fun guardarPartido() {
        if (!state.partidoTerminado || state.ganadorPartido.isEmpty()) return

        val partido = Partido(
            jugadorA_nombre = state.jugadorA_nombre,
            jugadorB_nombre = state.jugadorB_nombre,
            set1_A = state.set1_A,
            set1_B = state.set1_B,
            set2_A = state.set2_A,
            set2_B = state.set2_B,
            set3_A = state.set3_A,
            set3_B = state.set3_B,
            fecha = System.currentTimeMillis()
        )

        viewModelScope.launch {
            matchRepository.insertarPartido(partido)
        }
    }

    fun reiniciarPartido() {
        state = MarcadorState()
    }
}
