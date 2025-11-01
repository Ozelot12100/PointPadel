package com.pointpadel.app.ui.screens.configuracion

data class ConfiguracionState(
    val jugadorA_nombre: String = "",
    val jugadorB_nombre: String = "",
    val jugadorQueSacaPrimero: String = "A", // "A" o "B"
    val puedeIniciarPartido: Boolean = false
) {
    fun validarCampos(): ConfiguracionState {
        return copy(
            puedeIniciarPartido = jugadorA_nombre.isNotBlank() && jugadorB_nombre.isNotBlank()
        )
    }
}
