package com.pointpadel.app.utils

import com.pointpadel.app.data.local.Partido

object TestDataUtils {

    /**
     * Crea datos de prueba para la aplicación
     */
    fun crearPartidosDePrueba(): List<Partido> {
        val fechaActual = System.currentTimeMillis()
        val unDia = 24 * 60 * 60 * 1000L

        return listOf(
            // Partido 1: Ganó A en 2 sets
            Partido(
                id = 1,
                jugadorA_nombre = "Juan",
                jugadorB_nombre = "Carlos",
                set1_A = 6,
                set1_B = 3,
                set2_A = 6,
                set2_B = 4,
                set3_A = 0,
                set3_B = 0,
                fecha = fechaActual - unDia
            ),

            // Partido 2: Ganó B en 3 sets
            Partido(
                id = 2,
                jugadorA_nombre = "María",
                jugadorB_nombre = "Ana",
                set1_A = 6,
                set1_B = 7,
                set2_A = 6,
                set2_B = 2,
                set3_A = 4,
                set3_B = 6,
                fecha = fechaActual - (2 * unDia)
            ),

            // Partido 3: Ganó A con tie-break
            Partido(
                id = 3,
                jugadorA_nombre = "Pedro",
                jugadorB_nombre = "Luis",
                set1_A = 7,
                set1_B = 6,
                set2_A = 6,
                set2_B = 4,
                set3_A = 0,
                set3_B = 0,
                fecha = fechaActual - (3 * unDia)
            )
        )
    }

    /**
     * Crea un partido simple para pruebas rápidas
     */
    fun crearPartidoSimple(
        jugadorA: String = "Jugador A",
        jugadorB: String = "Jugador B",
        setsGanadosA: Int = 2,
        setsGanadosB: Int = 0
    ): Partido {
        return when {
            setsGanadosA >= 2 -> Partido(
                jugadorA_nombre = jugadorA,
                jugadorB_nombre = jugadorB,
                set1_A = 6,
                set1_B = 4,
                set2_A = 6,
                set2_B = 3,
                set3_A = 0,
                set3_B = 0,
                fecha = System.currentTimeMillis()
            )
            setsGanadosB >= 2 -> Partido(
                jugadorA_nombre = jugadorA,
                jugadorB_nombre = jugadorB,
                set1_A = 4,
                set1_B = 6,
                set2_A = 3,
                set2_B = 6,
                set3_A = 0,
                set3_B = 0,
                fecha = System.currentTimeMillis()
            )
            else -> Partido(
                jugadorA_nombre = jugadorA,
                jugadorB_nombre = jugadorB,
                set1_A = 6,
                set1_B = 4,
                set2_A = 4,
                set2_B = 6,
                set3_A = 6,
                set3_B = 3,
                fecha = System.currentTimeMillis()
            )
        }
    }

    /**
     * Valida si un resultado de set es válido según las reglas de pádel
     */
    fun esResultadoSetValido(juegosA: Int, juegosB: Int): Boolean {
        return when {
            // Tie-break (7-6)
            (juegosA == 7 && juegosB == 6) || (juegosA == 6 && juegosB == 7) -> true
            // Set normal (6+ juegos con ventaja de 2+)
            juegosA >= 6 && juegosA - juegosB >= 2 -> true
            juegosB >= 6 && juegosB - juegosA >= 2 -> true
            // Set incompleto (todavía jugando) - cualquier combinación menor a 6-6
            juegosA < 6 && juegosB < 6 -> true
            // Casos especiales como 5-5, 6-5, etc.
            (juegosA == 5 && juegosB == 5) ||
            (juegosA == 6 && juegosB == 5) ||
            (juegosA == 5 && juegosB == 6) -> true
            else -> false
        }
    }

    /**
     * Calcula el ganador de un partido
     */
    fun calcularGanadorPartido(partido: Partido): String? {
        var setsGanadosA = 0
        var setsGanadosB = 0

        // Verificar set 1
        if (esSetGanado(partido.set1_A, partido.set1_B)) setsGanadosA++
        else if (esSetGanado(partido.set1_B, partido.set1_A)) setsGanadosB++

        // Verificar set 2
        if (esSetGanado(partido.set2_A, partido.set2_B)) setsGanadosA++
        else if (esSetGanado(partido.set2_B, partido.set2_A)) setsGanadosB++

        // Verificar set 3 (si se jugó)
        if (partido.set3_A > 0 || partido.set3_B > 0) {
            if (esSetGanado(partido.set3_A, partido.set3_B)) setsGanadosA++
            else if (esSetGanado(partido.set3_B, partido.set3_A)) setsGanadosB++
        }

        return when {
            setsGanadosA >= 2 -> "A"
            setsGanadosB >= 2 -> "B"
            else -> null // Partido incompleto
        }
    }

    private fun esSetGanado(juegosJugador: Int, juegosOponente: Int): Boolean {
        return when {
            // Ganó en tie-break (7-6)
            juegosJugador == 7 && juegosOponente == 6 -> true
            // Ganó por 6+ juegos con ventaja de 2+
            juegosJugador >= 6 && juegosJugador - juegosOponente >= 2 -> true
            else -> false
        }
    }
}

