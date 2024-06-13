package com.ccormor392.pruebaproyectofinal.data.model

import com.google.firebase.firestore.GeoPoint

data class Sitio(
    val foto: String = "",
    val nombre: String = "",
    val nombreLargo: String = "",
    val ubicacion: GeoPoint = GeoPoint(0.0, 0.0),
    val tipo: String ="",
    val peticion:Boolean = true
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "foto" to foto,
            "nombre" to nombre,
            "nombreLargo" to nombreLargo,
            "ubicacion" to ubicacion,
            "tipo" to tipo,
            "peticion" to peticion
        )
    }
}