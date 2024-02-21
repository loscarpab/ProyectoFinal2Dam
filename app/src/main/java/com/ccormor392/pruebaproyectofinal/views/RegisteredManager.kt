package com.ccormor392.pruebaproyectofinal.views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.ccormor392.pruebaproyectofinal.navigation.Routes
import com.google.firebase.auth.FirebaseAuth

@Composable
fun RegisteredManager(navController: NavController){
    LaunchedEffect(Unit){
        if (!FirebaseAuth.getInstance().currentUser?.email.isNullOrEmpty()){
            navController.navigate(Routes.Inicio.route)
        }else{
            navController.navigate(Routes.InicioSinRegistro.route)
        }
    }
}