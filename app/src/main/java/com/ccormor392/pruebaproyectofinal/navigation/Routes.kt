package com.ccormor392.pruebaproyectofinal.navigation

sealed class Routes(val route:String) {
    object InicioSinRegistro:Routes("InicioSinRegistro")
    object Registro:Routes("Registro")
    object InicioSesion:Routes("InicioSesion")
    object Inicio:Routes("Inicio")
    object RegisteredManager:Routes("RegisteredManager")

}