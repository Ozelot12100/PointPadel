package com.pointpadel.app.data.repository

import com.pointpadel.app.data.local.Partido
import com.pointpadel.app.data.local.PartidoDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MatchRepository @Inject constructor(
    private val partidoDao: PartidoDao
) {

    suspend fun insertarPartido(partido: Partido): Long {
        return partidoDao.insertar(partido)
    }

    fun obtenerHistorialPartidos(): Flow<List<Partido>> {
        return partidoDao.obtenerHistorial()
    }

    suspend fun obtenerPartidoPorId(id: Long): Partido? {
        return partidoDao.obtenerPartidoPorId(id)
    }

    suspend fun eliminarPartido(partido: Partido) {
        partidoDao.eliminar(partido)
    }

    suspend fun eliminarTodosLosPartidos() {
        partidoDao.eliminarTodo()
    }
}
