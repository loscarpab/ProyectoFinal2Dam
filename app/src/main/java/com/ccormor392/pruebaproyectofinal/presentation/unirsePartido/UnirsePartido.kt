package com.ccormor392.pruebaproyectofinal.presentation.unirsePartido

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.ccormor392.pruebaproyectofinal.R
import com.ccormor392.pruebaproyectofinal.infopartido.InfoPartido
import com.ccormor392.pruebaproyectofinal.lineaseparadoratopbar.LineaSeparadoraTopbar
import com.ccormor392.pruebaproyectofinal.logoapp.poppins
import com.ccormor392.pruebaproyectofinal.navigation.Routes
import com.ccormor392.pruebaproyectofinal.presentation.componentes.Alert
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyBottomBar
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyTopBar
import com.ccormor392.pruebaproyectofinal.presentation.componentes.RowUser

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
    val partido by unirsePartidoViewModel.partido.collectAsState()
    // Efecto de lanzamiento para obtener la información del partido por su ID
    LaunchedEffect(Unit) {
        unirsePartidoViewModel.getPartidobyId(idPartido)
    }
    // Estructura del diseño de la pantalla para unirse a un partido
    Scaffold(topBar = {
        // Barra superior personalizada
        MyTopBar()
    }, content = {

            // Contenido principal de la pantalla
            LazyColumn(Modifier.padding(top = 79.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                item {
                    //Información del partido (lugar, creador, fecha y hora)
                    InfoPartido(
                        textLugar = unirsePartidoViewModel.partido.value.sitio.nombre,
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
                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(unbounded = false)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.campofutbol),
                            contentDescription = "fondo campo futbol",
                            modifier = Modifier
                                .fillMaxWidth()
                                .zIndex(0f)
                                .padding(8.dp),
                            contentScale = ContentScale.Crop
                        )
                        val realHeight = maxWidth*0.755f
                        val sizeIcon = maxWidth*0.16f
                        val offsets = listOf(
                            Offset((maxWidth / 2 - sizeIcon / 2).value, (realHeight - sizeIcon / 2).value), // portero
                            Offset((maxWidth * 0.75f - sizeIcon / 2).value, (realHeight * 0.70f - sizeIcon / 2).value), // dfd
                            Offset((maxWidth / 2 - sizeIcon / 2).value, (realHeight * 0.75f - sizeIcon / 2).value), // dfc
                            Offset((maxWidth * 0.25f - sizeIcon / 2).value, (realHeight * 0.70f - sizeIcon / 2).value), // dfi
                            Offset((maxWidth * 0.65f - sizeIcon / 2).value, (realHeight * 0.40f - sizeIcon / 2).value), // cd
                            Offset((maxWidth * 0.35f - sizeIcon / 2).value, (realHeight * 0.40f - sizeIcon / 2).value), // ci
                            Offset((maxWidth * 0.5f - sizeIcon / 2).value, (realHeight * 0.18f - sizeIcon / 2).value)  // del
                        )

                        offsets.forEachIndexed() {index, offset ->
                            OffsetImage(
                                offset = offset,
                                sizeIcon = sizeIcon,
                                enlace = unirsePartidoViewModel.recuperarFoto(index),
                                posicion = index,
                                unirsePartidoViewModel,
                                navHostController,
                                idPartido = idPartido
                            )
                        }

                    }
                    // Lista de jugadores que participan en el partido
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(), horizontalAlignment = Alignment.Start
                    ) {
                        partido.jugadores.forEach {
                            RowUser(username = it.username,
                                avatar = it.avatar,
                                onClickRow = { navHostController.navigate("${Routes.MiPerfil}${it.userId}") })
                        }
                    }

                }
            }



        // Alerta que se muestra si el usuario ya está entre los jugadores del partido
        if (unirsePartidoViewModel.showAlert) {
            Alert(title = "Alerta",
                message = "Ya estás entre los jugadores",
                confirmText = "Aceptar",
                onConfirmClick = { unirsePartidoViewModel.closeAlert() },
                onDismissClick = { }) // Ninguna acción en onDismissClick para que no oculte el diálogo
        }
    }, bottomBar = {
        MyBottomBar(navHostController = navHostController)
    })
}
@Composable
fun OffsetImage(
    offset: Offset,
    sizeIcon: Dp,
    enlace: String? = null,
    posicion: Int,
    viewModel: UnirsePartidoViewModel,
    navHostController: NavHostController,
    idPartido: String
) {
    AsyncImage(
        model = enlace ?: R.drawable.select_image_partido_objetivo,
        contentDescription = "icono",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(sizeIcon)
            .offset(x = offset.x.dp, y = offset.y.dp)
            .clip(RoundedCornerShape(sizeIcon * 0.7f))
            .clickable {
                if (viewModel.clickable) {
                    viewModel.clickable = false // Desactivar clics adicionales
                    viewModel.unirseAPartido(
                        onSuccess = { viewModel.getPartidobyId(idPartido) },
                        posicion = posicion,
                        equipo = true,
                        navController = navHostController
                    )
                }
            }
    )
}
