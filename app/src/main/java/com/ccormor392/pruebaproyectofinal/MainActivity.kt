package com.ccormor392.pruebaproyectofinal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.ccormor392.pruebaproyectofinal.navigation.NavManager
import com.ccormor392.pruebaproyectofinal.ui.theme.PruebaProyectoFinalTheme
import com.ccormor392.pruebaproyectofinal.presentation.crearPartido.CreateMatchViewModel
import com.ccormor392.pruebaproyectofinal.presentation.inicio.InicioViewModel
import com.ccormor392.pruebaproyectofinal.presentation.inicioSesion.LoginViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val loginViewModel: LoginViewModel by viewModels()
        val partidoViewModel: CreateMatchViewModel by viewModels()
        val inicioViewModel: InicioViewModel by viewModels()
        setContent {
            PruebaProyectoFinalTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavManager(loginViewModel, partidoViewModel, inicioViewModel)
                }
            }
        }
    }
}


