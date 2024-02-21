package com.ccormor392.pruebaproyectofinal.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ccormor392.pruebaproyectofinal.views.InicioSinRegistro
import com.ccormor392.pruebaproyectofinal.navigation.Routes.*
import com.ccormor392.pruebaproyectofinal.viewModels.LoginViewModel
import com.ccormor392.pruebaproyectofinal.views.Inicio
import com.ccormor392.pruebaproyectofinal.views.InicioSesion
import com.ccormor392.pruebaproyectofinal.views.RegisteredManager
import com.ccormor392.pruebaproyectofinal.views.Registro

@Composable
fun NavManager(loginViewModel: LoginViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination =  RegisteredManager.route){
        composable(InicioSinRegistro.route){
            InicioSinRegistro(navController)
        }
        composable(InicioSesion.route){
            InicioSesion(navController, loginViewModel)
        }
        composable(Registro.route){
            Registro(navController, loginViewModel)
        }
        composable(Inicio.route){
            Inicio(navController)
        }
        composable(RegisteredManager.route){
            RegisteredManager(navController)
        }
    }
}