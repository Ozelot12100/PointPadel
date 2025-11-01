package com.pointpadel.app.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PartidoDao {

    @Insert
    suspend fun insertar(partido: Partido): Long

    @Query("SELECT * FROM partidos ORDER BY fecha DESC")
    fun obtenerHistorial(): Flow<List<Partido>>

    @Query("SELECT * FROM partidos WHERE id = :id")
    suspend fun obtenerPartidoPorId(id: Long): Partido?

    @Delete
    suspend fun eliminar(partido: Partido)

    @Query("DELETE FROM partidos")
    suspend fun eliminarTodo()
}
