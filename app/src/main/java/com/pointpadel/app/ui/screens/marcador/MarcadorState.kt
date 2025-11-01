package com.pointpadel.app.ui.screens.marcador

data class MarcadorState(
    val jugadorA_nombre: String = "",
    val jugadorB_nombre: String = "",

    // Puntos actuales en el juego (0=0, 1=15, 2=30, 3=40)
    val puntosA: Int = 0,
    val puntosB: Int = 0,

    // Juegos ganados por set
    val set1_A: Int = 0,
    val set1_B: Int = 0,

    val set2_A: Int = 0,
    val set2_B: Int = 0,

    val set3_A: Int = 0,
    val set3_B: Int = 0,

    // Control del partido
    val setActual: Int = 1, // 1, 2 o 3
    val partidoTerminado: Boolean = false,
    val ganadorPartido: String = "", // "A" o "B"
    val partidoIniciado: Boolean = false,
    val tiempoInicio: Long? = null,
    val duracionMinutos: Int = 0,

    // Control del saque
    val jugadorQueSaca: String = "A", // "A" o "B"

    // Tie-Break
    val enTieBreak: Boolean = false,
    val puntosTieBreakA: Int = 0,
    val puntosTieBreakB: Int = 0,
    val contadorSaquesTieBreak: Int = 0 // Para controlar rotación de saques en tie-break
) {
    // Sets ganados por cada jugador
    val setsGanadosA: Int
        get() {
            var sets = 0
            if (isSetGanado(set1_A, set1_B)) sets++
            if (isSetGanado(set2_A, set2_B)) sets++
            if (isSetGanado(set3_A, set3_B)) sets++
            return sets
        }

    val setsGanadosB: Int
        get() {
            var sets = 0
            if (isSetGanado(set1_B, set1_A)) sets++
            if (isSetGanado(set2_B, set2_A)) sets++
            if (isSetGanado(set3_B, set3_A)) sets++
            return sets
        }

    // Verificar si un set está ganado
    private fun isSetGanado(juegosJugador: Int, juegosOponente: Int): Boolean {
        return when {
            // Ganó en tie-break (7-6)
            juegosJugador == 7 && juegosOponente == 6 -> true
            // Ganó por 6 juegos con ventaja de 2
            juegosJugador >= 6 && juegosJugador - juegosOponente >= 2 -> true
            else -> false
        }
    }

    // Array de puntuación para mostrar
    val puntosDePadel: Array<String>
        get() = arrayOf("0", "15", "30", "40")

    // Texto de puntos para mostrar en UI
    val puntosTextoA: String
        get() = if (enTieBreak) puntosTieBreakA.toString()
                else if (puntosA < puntosDePadel.size) puntosDePadel[puntosA] else "40"

    val puntosTextoB: String
        get() = if (enTieBreak) puntosTieBreakB.toString()
                else if (puntosB < puntosDePadel.size) puntosDePadel[puntosB] else "40"

    // Verificar si estamos en "Punto de Oro" (40-40)
    val esPuntoDeOro: Boolean
        get() = !enTieBreak && puntosA == 3 && puntosB == 3

    // Obtener juegos del set actual
    fun getJuegosDelSetActual(): Pair<Int, Int> {
        return when (setActual) {
            1 -> Pair(set1_A, set1_B)
            2 -> Pair(set2_A, set2_B)
            3 -> Pair(set3_A, set3_B)
            else -> Pair(0, 0)
        }
    }

    // Verificar si se debe iniciar tie-break
    val debeIniciarTieBreak: Boolean
        get() {
            val (juegosA, juegosB) = getJuegosDelSetActual()
            return juegosA == 6 && juegosB == 6
        }

    // Texto del marcador para mostrar en UI
    val marcadorTexto: String
        get() {
            return if (enTieBreak) {
                "Tie-Break - Set $setActual"
            } else if (esPuntoDeOro) {
                "Punto de Oro - Set $setActual"
            } else {
                "Set $setActual"
            }
        }

    // Información del saque
    val infoSaque: String
        get() {
            val nombreSacador = if (jugadorQueSaca == "A") jugadorA_nombre else jugadorB_nombre
            return "Saca: $nombreSacador"
        }
}
