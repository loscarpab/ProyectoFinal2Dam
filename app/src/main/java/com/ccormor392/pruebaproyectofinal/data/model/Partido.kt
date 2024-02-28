package com.ccormor392.pruebaproyectofinal.data.model


data class Partido(
    val creador: String = "",
    val fecha: String = "",
    val hora: String = "",
    val idPartido:String = "",
    var jugadores: List<String> = listOf(),
    val nombreSitio: String = "",
){
}

