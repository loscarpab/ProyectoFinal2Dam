package com.ccormor392.pruebaproyectofinal.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ccormor392.pruebaproyectofinal.navigation.Routes.CrearPartido
import com.ccormor392.pruebaproyectofinal.navigation.Routes.Inicio
import com.ccormor392.pruebaproyectofinal.navigation.Routes.InicioSesion
import com.ccormor392.pruebaproyectofinal.navigation.Routes.InicioSinRegistro
import com.ccormor392.pruebaproyectofinal.navigation.Routes.RegisteredManager
import com.ccormor392.pruebaproyectofinal.navigation.Routes.Registro
import com.ccormor392.pruebaproyectofinal.navigation.Routes.UnirsePartido
import com.ccormor392.pruebaproyectofinal.presentation.amigos.Amigos
import com.ccormor392.pruebaproyectofinal.presentation.amigos.AmigosViewModel
import com.ccormor392.pruebaproyectofinal.presentation.crearPartido.CrearPartido
import com.ccormor392.pruebaproyectofinal.presentation.crearPartido.CreateMatchViewModel
import com.ccormor392.pruebaproyectofinal.presentation.inicio.Inicio
import com.ccormor392.pruebaproyectofinal.presentation.inicio.InicioViewModel
import com.ccormor392.pruebaproyectofinal.presentation.manejoDeUsuarios.LoginViewModel
import com.ccormor392.pruebaproyectofinal.presentation.manejoDeUsuarios.sinAuntenticar.InicioSesion
import com.ccormor392.pruebaproyectofinal.presentation.manejoDeUsuarios.sinAuntenticar.InicioSinRegistro
import com.ccormor392.pruebaproyectofinal.presentation.manejoDeUsuarios.sinAuntenticar.RegisteredManager
import com.ccormor392.pruebaproyectofinal.presentation.manejoDeUsuarios.sinAuntenticar.Registro
import com.ccormor392.pruebaproyectofinal.presentation.manejoDeUsuarios.yaAutenticados.EditarPerfil
import com.ccormor392.pruebaproyectofinal.presentation.manejoDeUsuarios.yaAutenticados.MiPerfil
import com.ccormor392.pruebaproyectofinal.presentation.misPartidos.MisPartidos
import com.ccormor392.pruebaproyectofinal.presentation.misPartidos.MisPartidosViewModel
import com.ccormor392.pruebaproyectofinal.presentation.misPartidos.Peticion
import com.ccormor392.pruebaproyectofinal.presentation.misPartidos.Sitio
import com.ccormor392.pruebaproyectofinal.presentation.misPartidos.Sitios
import com.ccormor392.pruebaproyectofinal.presentation.misPartidos.SitiosViewModel
import com.ccormor392.pruebaproyectofinal.presentation.unirsePartido.UnirsePartido
import com.ccormor392.pruebaproyectofinal.presentation.unirsePartido.UnirsePartidoViewModel

/**
 * Gestor de navegación de la aplicación que define las rutas y las pantallas correspondientes a cada ruta.
 * También se encarga de pasar los ViewModels necesarios a las pantallas que los requieren.
 *
 * @param loginViewModel ViewModel para la gestión de inicio de sesión.
 * @param partidoViewModel ViewModel para la creación y gestión de partidos.
 * @param inicioViewModel ViewModel para la pantalla de inicio.
 * @param unirsePartidoViewModel ViewModel para la funcionalidad de unirse a un partido.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavManager(
    loginViewModel: LoginViewModel,
    partidoViewModel: CreateMatchViewModel,
    inicioViewModel: InicioViewModel,
    unirsePartidoViewModel: UnirsePartidoViewModel,
    misPartidosViewModel: MisPartidosViewModel,
    amigosViewModel: AmigosViewModel,
    sitiosViewModel: SitiosViewModel
) {
    val navController = rememberNavController()

    // Definición de las rutas y sus pantallas correspondientes
    NavHost(navController = navController, startDestination = RegisteredManager.route) {
        composable(InicioSinRegistro.route) {
            // Pantalla de inicio sin registro
            InicioSinRegistro(navController)
        }
        composable(InicioSesion.route) {
            // Pantalla de inicio de sesión
            InicioSesion(navController, loginViewModel)
        }
        composable(Registro.route) {
            // Pantalla de registro
            Registro(navController, loginViewModel)
        }
        composable(Inicio.route) {
            // Pantalla de inicio
            Inicio(navController, inicioViewModel)
        }
        composable(RegisteredManager.route) {
            // Pantalla para la gestión de usuarios ya autenticados
            RegisteredManager(navController)
        }
        composable(CrearPartido.route) {
            // Pantalla para la creación de un nuevo partido
            CrearPartido(partidoViewModel, navController)
        }
        composable("${Routes.MiPerfil.route}/{idUser}", enterTransition = { slideInVertically() }, exitTransition = { slideOutVertically { it } }, arguments = listOf(
            navArgument("idUser") { type = NavType.StringType }
        ) ){navBackStackEntry->
            // Pantalla para cerrar sesión
            val idPartido = navBackStackEntry.arguments?.getString("idUser") ?: ""
            MiPerfil(navController, loginViewModel, idPartido, amigosViewModel)
        }
        composable(Routes.MiPerfil.route, enterTransition = { slideInVertically ()}, exitTransition = { slideOutVertically { it } }) {
            MiPerfil(navController, loginViewModel)
        }
        composable(Routes.EditarPerfil.route) {
            // Pantalla para cerrar sesión
            EditarPerfil(navController, loginViewModel)
        }
        composable(
            "${UnirsePartido.route}/{idPartido}/{nombreCreador}",
            arguments = listOf(
                navArgument("idPartido") { type = NavType.StringType },
                navArgument("nombreCreador") { type = NavType.StringType }
            )
        ) { navBackStackEntry ->
            // Pantalla para unirse a un partido
            val idPartido = navBackStackEntry.arguments?.getString("idPartido") ?: ""
            val nombreCreador = navBackStackEntry.arguments?.getString("nombreCreador") ?: ""
            UnirsePartido(unirsePartidoViewModel, sitiosViewModel,idPartido, nombreCreador, navController)
        }
        composable(CrearPartido.route) {
            // Pantalla para la creación de un nuevo partido
            CrearPartido(partidoViewModel, navController)
        }
        composable(Routes.MisPartidos.route) {
            // Pantalla para ver los partidos creados
            MisPartidos(navController, misPartidosViewModel)
        }
        composable(Routes.Amigos.route) {
            // Pantalla para ver los partidos creados
            Amigos(amigosViewModel,navController)
        }
        composable(Routes.Sitios.route) {
            // Pantalla para ver los partidos creados
            Sitios(navController, sitiosViewModel, loginViewModel)
        }
        composable(Routes.Sitio.route) {
            // Pantalla para ver los partidos creados
            Sitio(sitiosViewModel, navController,loginViewModel)
        }
        composable(Routes.Peticion.route) {
            // Pantalla para ver los partidos creados
            Peticion(navController,sitiosViewModel, loginViewModel)
        }
    }
}
