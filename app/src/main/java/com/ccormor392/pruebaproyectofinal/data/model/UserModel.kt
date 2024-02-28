package com.ccormor392.pruebaproyectofinal.data.model

data class UserModel(
    val userId: String,
    val email: String,
    val username: String,
    val partidosCreados:Int = 0
)