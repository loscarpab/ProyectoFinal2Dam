package com.ccormor392.pruebaproyectofinal.presentation.crearPartido

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ccormor392.pruebaproyectofinal.R
import com.ccormor392.pruebaproyectofinal.botonmas.BotonMas
import com.ccormor392.pruebaproyectofinal.navigation.Routes
import com.ccormor392.pruebaproyectofinal.presentation.componentes.Alert
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyBottomBar
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyTextField
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyTopBar
import com.ccormor392.pruebaproyectofinal.textotopscreenlogs.TextoTopScreenLogs
import com.ccormor392.pruebaproyectofinal.ui.theme.PurpleGrey40

/**
 * Función componible para crear un nuevo partido.
 * Esta función permite a los usuarios crear un nuevo partido proporcionando los detalles necesarios.
 *
 * @param partidoViewModel ViewModel para gestionar el proceso de creación del partido.
 * @param navController Controlador de navegación para navegar entre los composables.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearPartido(partidoViewModel: CreateMatchViewModel, navController: NavHostController) {
    // Utiliza LaunchedEffect para ejecutar código cuando se lanza este composable
    LaunchedEffect(Unit) {
        partidoViewModel.numeroPartidosUsuarioAutenticado()
    }

    Scaffold(
        topBar = {
            MyTopBar()
        },
        content = {
            // Área de contenido principal
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 78.dp)
                    .background(PurpleGrey40),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Componentes de texto para título y subtítulo
                TextoTopScreenLogs(
                    textTitulo = stringResource(R.string.crear_partido),
                    textSubtitulo = stringResource(R.string.sub_crear_partido),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, bottom = 32.dp)
                )

                // Fila para contener los campos de entrada de los detalles del partido
                Row(
                    modifier = Modifier
                        .height(300.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Columna para organizar los campos de entrada verticalmente
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Campo de texto para ingresar la ubicación del partido
                        MyTextField(
                            value = partidoViewModel.nombreSitio,
                            onValueChange = { partidoViewModel.changeLugar(it) },
                            string = stringResource(R.string.lugar)
                        )
                        // Campo de texto para ingresar la fecha del partido
                        MyTextField(
                            value = partidoViewModel.fecha,
                            onValueChange = { partidoViewModel.changeFecha(it) },
                            string = stringResource(R.string.fecha)
                        )
                        // Campo de texto para ingresar la hora del partido
                        MyTextField(
                            value = partidoViewModel.hora,
                            onValueChange = { partidoViewModel.changeHora(it) },
                            string = stringResource(R.string.hora)
                        )
                    }
                }

                // Botón para crear un nuevo partido
                BotonMas(
                    textButton = "Crea un partido",
                    onClickButton = {
                        // Llama a la función para crear el partido y navegar a la pantalla de inicio en caso de éxito
                        partidoViewModel.crearPartido {
                            navController.navigate(Routes.Inicio.route)
                        }
                    },
                    modifier = Modifier.padding(top = 32.dp)
                )

                // Muestra un diálogo de alerta si showAlert es verdadero
                if (partidoViewModel.showAlert) {
                    Alert(
                        title = stringResource(R.string.alerta),
                        message = stringResource(R.string.alert_crear_partido),
                        confirmText = stringResource(R.string.aceptar),
                        onConfirmClick = { partidoViewModel.closeAlert() },
                        onDismissClick = { }
                    )
                }
            }
        }, bottomBar = {
            MyBottomBar(navHostController = navController)
        }
    )
}
