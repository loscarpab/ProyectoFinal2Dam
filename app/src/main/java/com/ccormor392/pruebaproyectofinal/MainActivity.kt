package com.ccormor392.pruebaproyectofinal

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.ccormor392.pruebaproyectofinal.navigation.NavManager
import com.ccormor392.pruebaproyectofinal.presentation.amigos.AmigosViewModel
import com.ccormor392.pruebaproyectofinal.presentation.crearPartido.CreateMatchViewModel
import com.ccormor392.pruebaproyectofinal.presentation.inicio.InicioViewModel
import com.ccormor392.pruebaproyectofinal.presentation.manejoDeUsuarios.LoginViewModel
import com.ccormor392.pruebaproyectofinal.presentation.misPartidos.MisPartidosViewModel
import com.ccormor392.pruebaproyectofinal.presentation.misPartidos.SitiosViewModel
import com.ccormor392.pruebaproyectofinal.presentation.unirsePartido.UnirsePartidoViewModel
import com.ccormor392.pruebaproyectofinal.ui.theme.PruebaProyectoFinalTheme

/**
 * Actividad principal que inicializa la aplicación.
 * Se encarga de configurar el contenido de la actividad y establecer el tema principal.
 *
 * Incluye la creación de los ViewModel necesarios para la gestión de la aplicación.
 */
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Creación de los ViewModel necesarios para la gestión de la aplicación
        val loginViewModel: LoginViewModel by viewModels()
        val partidoViewModel: CreateMatchViewModel by viewModels()
        val inicioViewModel: InicioViewModel by viewModels()
        val unirsePartidoViewModel: UnirsePartidoViewModel by viewModels()
        val misPartidoViewModel: MisPartidosViewModel by viewModels()
        val amigosViewModel: AmigosViewModel by viewModels()
        val sitiosViewModel: SitiosViewModel by viewModels()


        setContent {
            PruebaProyectoFinalTheme {
                // Contenedor principal de la interfaz de usuario

                    // Gestión de la navegación entre pantallas
                    NavManager(
                        loginViewModel,
                        partidoViewModel,
                        inicioViewModel,
                        unirsePartidoViewModel,
                        misPartidoViewModel,
                        amigosViewModel,
                        sitiosViewModel
                    )

            }
        }
    }
}