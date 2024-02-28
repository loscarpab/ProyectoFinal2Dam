package com.ccormor392.pruebaproyectofinal.navigation
/**
 * Clase sellada que define las rutas disponibles en la aplicación, junto con sus correspondientes nombres de ruta.
 *
 * @param route Nombre de la ruta.
 */
sealed class Routes(val route: String) {
    object InicioSinRegistro : Routes("InicioSinRegistro")
    object Registro : Routes("Registro")
    object InicioSesion : Routes("InicioSesion")
    object Inicio : Routes("Inicio")
    object RegisteredManager : Routes("RegisteredManager")
    object CrearPartido : Routes("CrearPartido")
    object UnirsePartido : Routes("UnirsePartido")
    object CerrarSesion : Routes("CerrarSesion")
}
