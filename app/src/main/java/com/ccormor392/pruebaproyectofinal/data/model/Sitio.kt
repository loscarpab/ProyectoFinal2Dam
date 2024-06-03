package com.ccormor392.pruebaproyectofinal.data.model

import com.google.firebase.firestore.GeoPoint

data class Sitio(
    val foto: String = "",
    val nombre: String = "",
    val ubicacion: GeoPoint = GeoPoint(0.0, 0.0),
) {
}