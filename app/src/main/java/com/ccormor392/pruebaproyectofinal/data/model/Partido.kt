package com.ccormor392.pruebaproyectofinal.data.model

import com.google.firebase.Timestamp
import java.time.Instant
import java.util.Date


data class Partido(
    val creador: String = "",
    val fecha: String = "",
    val hora: String = "",
    val idPartido:String = "",
    var jugadores: List<String> = listOf(),
    val nombreSitio: String = "",
    val timestamp: Date = Date(System.currentTimeMillis()),
    val foto:String = ""
){
}

