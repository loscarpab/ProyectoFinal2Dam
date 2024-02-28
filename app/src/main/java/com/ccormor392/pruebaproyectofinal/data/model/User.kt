package com.ccormor392.pruebaproyectofinal.data.model

data class User(
    val userId: String,
    val email: String,
    val username: String,
    val partidosCreados:Long = 0
)