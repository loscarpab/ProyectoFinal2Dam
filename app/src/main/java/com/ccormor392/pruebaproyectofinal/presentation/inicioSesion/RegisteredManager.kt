package com.ccormor392.pruebaproyectofinal.presentation.inicioSesion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.ccormor392.pruebaproyectofinal.navigation.Routes
import com.ccormor392.pruebaproyectofinal.presentation.inicio.InicioViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun RegisteredManager(navController: NavController, inicioViewModel: InicioViewModel){
    LaunchedEffect(Unit){
        if (!FirebaseAuth.getInstance().currentUser?.email.isNullOrEmpty()){
            inicioViewModel.pedirTodosLosPartidos {
                navController.navigate(Routes.Inicio.route)
            }
        }else{
            navController.navigate(Routes.InicioSinRegistro.route)
        }
    }
}