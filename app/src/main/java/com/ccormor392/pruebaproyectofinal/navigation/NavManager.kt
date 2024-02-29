package com.ccormor392.pruebaproyectofinal.navigation

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
import com.ccormor392.pruebaproyectofinal.presentation.crearPartido.CrearPartido
import com.ccormor392.pruebaproyectofinal.presentation.crearPartido.CreateMatchViewModel
import com.ccormor392.pruebaproyectofinal.presentation.inicio.Inicio
import com.ccormor392.pruebaproyectofinal.presentation.inicio.InicioViewModel
import com.ccormor392.pruebaproyectofinal.presentation.manejoDeUsuarios.yaAutenticados.CerrarSesion
import com.ccormor392.pruebaproyectofinal.presentation.manejoDeUsuarios.sinAuntenticar.InicioSesion
import com.ccormor392.pruebaproyectofinal.presentation.manejoDeUsuarios.sinAuntenticar.InicioSinRegistro
import com.ccormor392.pruebaproyectofinal.presentation.manejoDeUsuarios.LoginViewModel
import com.ccormor392.pruebaproyectofinal.presentation.manejoDeUsuarios.sinAuntenticar.RegisteredManager
import com.ccormor392.pruebaproyectofinal.presentation.manejoDeUsuarios.sinAuntenticar.Registro
import com.ccormor392.pruebaproyectofinal.presentation.misPartidos.MisPartidos
import com.ccormor392.pruebaproyectofinal.presentation.misPartidos.MisPartidosViewModel
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
@Composable
fun NavManager(
    loginViewModel: LoginViewModel,
    partidoViewModel: CreateMatchViewModel,
    inicioViewModel: InicioViewModel,
    unirsePartidoViewModel: UnirsePartidoViewModel,
    misPartidosViewModel: MisPartidosViewModel
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
        composable(Routes.CerrarSesion.route) {
            // Pantalla para cerrar sesión
            CerrarSesion(navController, loginViewModel)
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
            UnirsePartido(unirsePartidoViewModel, idPartido, nombreCreador, navController)
        }
        composable(CrearPartido.route) {
            // Pantalla para la creación de un nuevo partido
            CrearPartido(partidoViewModel, navController)
        }
        composable(Routes.MisPartidos.route) {
            // Pantalla para ver los partidos creados
            MisPartidos(navController, misPartidosViewModel)
        }
    }
}
