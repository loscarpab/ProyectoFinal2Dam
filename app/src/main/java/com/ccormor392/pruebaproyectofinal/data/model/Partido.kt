package com.ccormor392.pruebaproyectofinal.data.model

import android.net.Uri

data class Partido(
    val creador: String,
    val fecha: String,
    val hora: String,
    val idPartido:String,
    var jugadores: List<String> = listOf(),
    val nombreSitio: String,
    val urlImagen: String
)

