package com.pointpadel.app.ui.screens.historial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pointpadel.app.data.local.Partido
import com.pointpadel.app.data.repository.MatchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistorialViewModel @Inject constructor(
    private val matchRepository: MatchRepository
) : ViewModel() {

    private val _partidos = MutableStateFlow<List<Partido>>(emptyList())
    val partidos: StateFlow<List<Partido>> = _partidos.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        cargarHistorial()
    }

    private fun cargarHistorial() {
        viewModelScope.launch {
            _isLoading.value = true
            matchRepository.obtenerHistorialPartidos().collect { listaPartidos ->
                _partidos.value = listaPartidos
                _isLoading.value = false
            }
        }
    }

    fun eliminarPartido(partido: Partido) {
        viewModelScope.launch {
            matchRepository.eliminarPartido(partido)
        }
    }

    fun eliminarTodosLosPartidos() {
        viewModelScope.launch {
            matchRepository.eliminarTodosLosPartidos()
        }
    }
}
