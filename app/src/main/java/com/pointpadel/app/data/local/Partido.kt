package com.pointpadel.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "partidos")
data class Partido(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val jugadorA_nombre: String,
    val jugadorB_nombre: String,

    val set1_A: Int,
    val set1_B: Int,

    val set2_A: Int,
    val set2_B: Int,

    val set3_A: Int,
    val set3_B: Int,

    val fecha: Long // Usaremos Long, no Date
)
