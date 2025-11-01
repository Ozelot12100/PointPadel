package com.pointpadel.app.ui.screens.configuracion

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ConfiguracionViewModel @Inject constructor() : ViewModel() {

    var state by mutableStateOf(ConfiguracionState())
        private set

    fun actualizarJugadorA(nombre: String) {
        state = state.copy(jugadorA_nombre = nombre).validarCampos()
    }

    fun actualizarJugadorB(nombre: String) {
        state = state.copy(jugadorB_nombre = nombre).validarCampos()
    }

    fun seleccionarSacadorInicial(jugador: String) {
        state = state.copy(jugadorQueSacaPrimero = jugador)
    }

    fun reiniciarConfiguracion() {
        state = ConfiguracionState()
    }
}
