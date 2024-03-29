package com.ccormor392.pruebaproyectofinal.presentation.unirsePartido

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.ccormor392.pruebaproyectofinal.botonmas.BotonMas
import com.ccormor392.pruebaproyectofinal.infopartido.InfoPartido
import com.ccormor392.pruebaproyectofinal.lineaseparadoratopbar.LineaSeparadoraTopbar
import com.ccormor392.pruebaproyectofinal.logoapp.poppins
import com.ccormor392.pruebaproyectofinal.navigation.Routes
import com.ccormor392.pruebaproyectofinal.presentation.componentes.Alert
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyBottomBar
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyScaffoldContent
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyTopBar
import com.ccormor392.pruebaproyectofinal.usuarioitem.UsuarioItem
/**
 * Composable que representa la pantalla para unirse a un partido existente.
 * Muestra la información del partido y la lista de jugadores, permitiendo al usuario unirse al partido.
 *
 * @param unirsePartidoViewModel ViewModel que gestiona la lógica de la pantalla de unirse a un partido.
 * @param idPartido ID único del partido al que se desea unir el usuario.
 * @param nombreCreador Nombre del creador del partido.
 * @param navHostController Controlador de navegación para gestionar las transiciones entre pantallas.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnirsePartido(
    unirsePartidoViewModel: UnirsePartidoViewModel,
    idPartido: String,
    nombreCreador: String,
    navHostController: NavHostController
) {
    // Efecto de lanzamiento para obtener la información del partido por su ID
    LaunchedEffect(Unit) {
        unirsePartidoViewModel.getPartidobyId(idPartido)
    }
    // Estructura del diseño de la pantalla para unirse a un partido
    Scaffold(
        topBar = {
            // Barra superior personalizada
            MyTopBar()
        },
        content = {
            // Contenido principal de la pantalla
            MyScaffoldContent {
                // Información del partido (lugar, creador, fecha y hora)
                InfoPartido(
                    textLugar = unirsePartidoViewModel.partido.value.nombreSitio,
                    textUsername = nombreCreador,
                    textFecha = unirsePartidoViewModel.partido.value.fecha,
                    textHora = unirsePartidoViewModel.partido.value.hora,
                    modifier = Modifier.padding(bottom = 22.dp)
                )
                // Línea separadora para la barra superior
                LineaSeparadoraTopbar(modifier = Modifier.height(2.dp))
                // Título de la sección de jugadores
                Text(
                    text = "Jugadores",
                    fontSize = 20.sp,
                    fontFamily = poppins,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, start = 24.dp),
                    textAlign = TextAlign.Left,
                    color = androidx.compose.ui.graphics.Color.White
                )
                // Lista de jugadores que participan en el partido
                LazyColumn(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start){
                    items(unirsePartidoViewModel.users.value){
                        UsuarioItem(textUsername = it.username, modifier = Modifier.padding(top = 24.dp, start = 32.dp))
                    }
                }
            }
            // Botón para unirse al partido
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 100.dp), contentAlignment = Alignment.BottomCenter){
                BotonMas(textButton = "Unirse al partido", onClickButton = {unirsePartidoViewModel.unirseAPartido{
                    navHostController.navigate(Routes.Inicio.route)
                } })
            }
            // Alerta que se muestra si el usuario ya está entre los jugadores del partido
            if (unirsePartidoViewModel.showAlert) {
                Alert(title = "Alerta",
                    message = "Ya estás entre los jugadores",
                    confirmText = "Aceptar",
                    onConfirmClick = { unirsePartidoViewModel.closeAlert() },
                    onDismissClick = { } ) // Ninguna acción en onDismissClick para que no oculte el diálogo
            }
        },
        bottomBar = {
           MyBottomBar(navHostController = navHostController)
        }
    )
}
