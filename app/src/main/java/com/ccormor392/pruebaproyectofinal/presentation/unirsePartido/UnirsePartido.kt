package com.ccormor392.pruebaproyectofinal.presentation.unirsePartido

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
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
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MiTexto
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyBottomBar
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyTopBar
import com.ccormor392.pruebaproyectofinal.presentation.componentes.RowUser
import com.ccormor392.pruebaproyectofinal.presentation.manejoDeUsuarios.yaAutenticados.mySegmentedButtonColors
import com.ccormor392.pruebaproyectofinal.presentation.misPartidos.SitiosViewModel
import com.ccormor392.pruebaproyectofinal.ui.theme.maincolor

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
    sitiosViewModel: SitiosViewModel,
    idPartido: String,
    nombreCreador: String,
    navHostController: NavHostController
) {
    val partido by unirsePartidoViewModel.partido.collectAsState()
    val tipo = when(partido.sitio.tipo){
        "fut7" -> "Futbol 7"
        "futsal" -> "Futbol Sala"
        else -> "Futbol"
    }
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
            Column(
                Modifier
                    .padding(top = 79.dp, bottom = 79.dp)
                    .verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(Modifier.wrapContentSize(), contentAlignment = Alignment.BottomEnd){
                    AsyncImage(
                        model = partido.sitio.foto ,
                        contentDescription = "foto sitio",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(224.dp)
                            .zIndex(1f)

                    )
                    Box(Modifier.padding(16.dp).zIndex(2f)){
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(18.dp))
                                .background(maincolor)
                                .size(40.dp).zIndex(2f).clickable {
                                    sitiosViewModel.seleccionarSitio(partido.sitio)
                                    navHostController.navigate(Routes.Sitio.route)
                                }, contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.LocationOn,
                                contentDescription = "icono agregar",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                }

                Column(horizontalAlignment = Alignment.Start) {
                    MiTexto(string = partido.sitio.nombreLargo, fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.padding(top = 8.dp, start = 16.dp))
                    MiTexto(string = tipo, fontWeight = FontWeight.Medium, fontSize = 12.sp,modifier= Modifier.padding(top = 8.dp, start = 16.dp))
                    MiTexto(string = nombreCreador, fontWeight = FontWeight.Medium, fontSize = 12.sp,modifier= Modifier.padding(top = 8.dp, start = 16.dp))
                    HorizontalDivider(thickness = 0.5.dp, color = Color.White, modifier = Modifier.padding(top = 16.dp, bottom = 16.dp))
                }

                    // Título de la sección de jugadores
                    Text(
                        text = "Jugadores",
                        fontSize = 20.sp,
                        fontFamily = poppins,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp),
                        textAlign = TextAlign.Left,
                        color = androidx.compose.ui.graphics.Color.White
                    )
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
                        SingleChoiceSegmentedButtonRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 8.dp)
                        ) {
                            SegmentedButton(
                                selected = !unirsePartidoViewModel.equipo2,
                                onClick = { unirsePartidoViewModel.changeSegmentedButton() },
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = 0,
                                    count = 2
                                ), colors = mySegmentedButtonColors()
                            ) {
                                MiTexto(
                                    string = "Equipo 1",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            SegmentedButton(
                                selected = unirsePartidoViewModel.equipo2,
                                onClick = { unirsePartidoViewModel.changeSegmentedButton() },
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = 1,
                                    count = 2
                                ),
                                colors =  mySegmentedButtonColors()
                            ) {
                                MiTexto(
                                    string = "Equipo 2",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxWidth()
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
                        val offsets = if (partido.sitio.tipo == "fut7"){
                            listOf(
                                Offset((maxWidth / 2 - sizeIcon / 2).value, (realHeight - sizeIcon / 2).value), // portero
                                Offset((maxWidth * 0.75f - sizeIcon / 2).value, (realHeight * 0.70f - sizeIcon / 2).value), // dfd
                                Offset((maxWidth / 2 - sizeIcon / 2).value, (realHeight * 0.75f - sizeIcon / 2).value), // dfc
                                Offset((maxWidth * 0.25f - sizeIcon / 2).value, (realHeight * 0.70f - sizeIcon / 2).value), // dfi
                                Offset((maxWidth * 0.65f - sizeIcon / 2).value, (realHeight * 0.40f - sizeIcon / 2).value), // cd
                                Offset((maxWidth * 0.35f - sizeIcon / 2).value, (realHeight * 0.40f - sizeIcon / 2).value), // ci
                                Offset((maxWidth * 0.5f - sizeIcon / 2).value, (realHeight * 0.18f - sizeIcon / 2).value)  // del
                            )
                        }else{
                            listOf(
                                Offset((maxWidth / 2 - sizeIcon / 2).value, (realHeight - sizeIcon / 2).value), // portero
                                Offset((maxWidth * 0.70f - sizeIcon / 2).value, (realHeight * 0.45f - sizeIcon / 2).value), // dfd
                                Offset((maxWidth / 2 - sizeIcon / 2).value, (realHeight * 0.70f - sizeIcon / 2).value), // dfc
                                Offset((maxWidth * 0.30f - sizeIcon / 2).value, (realHeight * 0.45f - sizeIcon / 2).value), // dfi
                                Offset((maxWidth * 0.5f - sizeIcon / 2).value, (realHeight * 0.20f - sizeIcon / 2).value)  // del
                            )
                        }

                        offsets.forEachIndexed() {index, offset ->
                            OffsetImage(
                                offset = offset,
                                sizeIcon = sizeIcon,
                                enlace = unirsePartidoViewModel.recuperarFoto(index, unirsePartidoViewModel.equipo2),
                                posicion = index,
                                unirsePartidoViewModel,
                                navHostController,
                                idPartido = idPartido
                            )
                        }

                    }
                    // Título de la sección de jugadores
                    Text(
                        text = "Lista",
                        fontSize = 20.sp,
                        fontFamily = poppins,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp, start = 24.dp),
                        textAlign = TextAlign.Left,
                        color = androidx.compose.ui.graphics.Color.White
                    )
                    // Lista de jugadores que participan en el partido
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(), horizontalAlignment = Alignment.Start,
                    ) {
                        partido.jugadores.filter { it.equipo == !unirsePartidoViewModel.equipo2 }.forEach {
                            RowUser(username = it.username,
                                avatar = it.avatar,
                                onClickRow = { navHostController.navigate("${Routes.MiPerfil}${it.userId}") })
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
                        equipo = !viewModel.equipo2,
                        navController = navHostController
                    )
                }
            }
    )
}
