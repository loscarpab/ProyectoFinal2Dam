package com.ccormor392.pruebaproyectofinal.presentation.misPartidos

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ccormor392.pruebaproyectofinal.R
import com.ccormor392.pruebaproyectofinal.cartapartido.CartaPartido
import com.ccormor392.pruebaproyectofinal.cartapartido.Variante
import com.ccormor392.pruebaproyectofinal.presentation.componentes.Alert
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyBottomBar
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyScaffoldContent
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyTopBar
import com.ccormor392.pruebaproyectofinal.textotopscreenlogs.TextoTopScreenLogs
import com.ccormor392.pruebaproyectofinal.xxlargexbold.poppins
/**
 * Composable que muestra la pantalla de Mis Partidos.
 * Esta pantalla muestra una lista de los partidos creados por el usuario, con la opción de eliminarlos.
 *
 * @param navHostController controlador de navegación para manejar las transiciones entre pantallas.
 * @param misPartidosViewModel ViewModel que contiene la lógica de negocio relacionada con la pantalla de Mis Partidos.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisPartidos(navHostController: NavHostController, misPartidosViewModel: MisPartidosViewModel) {
    // Se carga la lista de partidos al inicio de la pantalla
    LaunchedEffect(Unit) {
        misPartidosViewModel.pedirTodosLosPartidosDelUsuario()
    }

    // Scaffold que contiene la estructura básica de la pantalla
    Scaffold(
        topBar = {
            // Barra superior de la pantalla
            MyTopBar()
        },
        content = {
            // Contenido principal de la pantalla
            MyScaffoldContent {
                // Encabezado de la pantalla
                TextoTopScreenLogs(textTitulo = stringResource(R.string.mis_partidos), textSubtitulo = "")

                // Lista de partidos disponibles
                if (misPartidosViewModel.listaMisPartidos.value.isEmpty()) {
                    Text(
                        text = stringResource(R.string.no_tienes_ningun_partido_creado),
                        fontFamily = poppins,
                        color = Color.White
                    )
                }
                // Columna perezosa para la lista de partidos
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 80.dp)
                ) {
                    // Itera sobre los elementos de la lista de partidos y muestra una tarjeta para cada uno
                    items(misPartidosViewModel.listaMisPartidos.value) { partido ->
                        CartaPartido(
                            textLugar = partido.nombreSitio,
                            textFecha = partido.fecha,
                            textHora = partido.hora,
                            textUsuario = partido.creador,
                            variante = Variante.Variante6,
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth(),
                            onClickDelete = {
                                // Abre el diálogo de confirmación para borrar el partido
                                misPartidosViewModel.openAlert(partido.idPartido)
                            }
                        )
                    }
                }
            }
            // Si se muestra la alerta de confirmación de eliminación
            if (misPartidosViewModel.showAlert) {
                Alert(
                    title =stringResource(id = R.string.alerta),
                    message = stringResource(R.string.alerta_borrar),
                    confirmText = stringResource(id = R.string.aceptar),
                    // Cuando se confirma la eliminación del partido
                    onConfirmClick = {
                        // Se pide de nuevo la lista de partidos
                        misPartidosViewModel.pedirTodosLosPartidosDelUsuario()
                        // Se borra el partido
                        misPartidosViewModel.borrarPartido()
                        // Se cierra la alerta
                        misPartidosViewModel.closeAlert()
                    },
                    onDismissClick = { } // Ninguna acción en onDismissClick para que no oculte el diálogo
                )
            }
        },
        bottomBar = {
            // Barra de la parte inferior con opciones de navegación
            MyBottomBar(navHostController = navHostController)
        }
    )
}
