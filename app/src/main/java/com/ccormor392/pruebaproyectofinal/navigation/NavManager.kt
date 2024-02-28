package com.ccormor392.pruebaproyectofinal.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ccormor392.pruebaproyectofinal.presentation.inicioSesion.InicioSinRegistro
import com.ccormor392.pruebaproyectofinal.navigation.Routes.*
import com.ccormor392.pruebaproyectofinal.presentation.crearPartido.CreateMatchViewModel
import com.ccormor392.pruebaproyectofinal.presentation.inicioSesion.LoginViewModel
import com.ccormor392.pruebaproyectofinal.presentation.crearPartido.CrearPartido
import com.ccormor392.pruebaproyectofinal.presentation.inicio.Inicio
import com.ccormor392.pruebaproyectofinal.presentation.inicio.InicioViewModel
import com.ccormor392.pruebaproyectofinal.presentation.inicioSesion.InicioSesion
import com.ccormor392.pruebaproyectofinal.presentation.inicioSesion.RegisteredManager
import com.ccormor392.pruebaproyectofinal.presentation.inicioSesion.Registro

@Composable
fun NavManager(
    loginViewModel: LoginViewModel,
    partidoViewModel: CreateMatchViewModel,
    inicioViewModel: InicioViewModel
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination =  RegisteredManager.route){
        composable(InicioSinRegistro.route){
            InicioSinRegistro(navController)
        }
        composable(InicioSesion.route){
            InicioSesion(navController, loginViewModel, inicioViewModel)
        }
        composable(Registro.route){
            Registro(navController, loginViewModel,inicioViewModel)
        }
        composable(Inicio.route){
            Inicio(navController, inicioViewModel)
        }
        composable(RegisteredManager.route){
            RegisteredManager(navController, inicioViewModel)
        }
        composable(CrearPartido.route){
            CrearPartido(partidoViewModel, navController)
        }
    }
}