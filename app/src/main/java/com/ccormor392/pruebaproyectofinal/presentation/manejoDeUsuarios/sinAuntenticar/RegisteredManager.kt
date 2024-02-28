package com.ccormor392.pruebaproyectofinal.presentation.manejoDeUsuarios.sinAuntenticar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.ccormor392.pruebaproyectofinal.navigation.Routes
import com.google.firebase.auth.FirebaseAuth

/**
 * Composable que gestiona la redirección del usuario según su estado de registro.
 * Si el usuario está registrado, lo dirige a la pantalla de inicio; de lo contrario, lo lleva a la pantalla de inicio sin registro.
 *
 * @param navController Controlador de navegación para gestionar las transiciones entre pantallas.
 */
@Composable
fun RegisteredManager(navController: NavController){
    // Efecto de lanzamiento para determinar el estado de registro del usuario
    LaunchedEffect(Unit){
        // Verificar si el usuario está registrado mediante la autenticación de Firebase
        if (!FirebaseAuth.getInstance().currentUser?.email.isNullOrEmpty()){
            // Si el usuario está registrado, navegar a la pantalla de inicio
            navController.navigate(Routes.Inicio.route)
        }else{
            // Si el usuario no está registrado, navegar a la pantalla de inicio sin registro
            navController.navigate(Routes.InicioSinRegistro.route)
        }
    }
}
