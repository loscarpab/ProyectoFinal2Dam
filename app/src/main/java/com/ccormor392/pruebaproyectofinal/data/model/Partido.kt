package com.ccormor392.pruebaproyectofinal.data.model

import java.util.Date


data class Partido(
    val creador: String = "",
    val fecha: String = "",
    val hora: String = "",
    val idPartido:String = "",
    var jugadores: List<JugadorPartido> = listOf(),
    val sitio: Sitio = Sitio(),
    val timestamp: Date = Date(System.currentTimeMillis()),
){
}

