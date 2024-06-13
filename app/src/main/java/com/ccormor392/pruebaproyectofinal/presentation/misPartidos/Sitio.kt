package com.ccormor392.pruebaproyectofinal.presentation.misPartidos

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.ccormor392.pruebaproyectofinal.navigation.Routes
import com.ccormor392.pruebaproyectofinal.presentation.componentes.CardMatch
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MiTexto
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyBottomBar
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyTopBar
import com.ccormor392.pruebaproyectofinal.presentation.manejoDeUsuarios.LoginViewModel
import com.ccormor392.pruebaproyectofinal.ui.theme.maincolor
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

/**
 * Composable para mostrar detalles de un sitio seleccionado.
 *
 * Muestra la imagen, detalles del sitio, ubicación en el mapa y lista de próximos partidos.
 *
 * @param sitiosViewModel ViewModel para manejar datos relacionados con sitios.
 * @param navHostController Controlador de navegación de Jetpack Compose.
 * @param loginViewModel ViewModel para manejar la autenticación y roles de usuario.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Sitio(
    sitiosViewModel: SitiosViewModel,
    navHostController: NavHostController,
    loginViewModel: LoginViewModel
) {
    // Obtiene el sitio seleccionado del ViewModel y los partidos relacionados
    val selectedSitio by sitiosViewModel.selectedSitio.collectAsState()
    val listaPartidos by sitiosViewModel.listaPartidosConNombreUsuario.collectAsState()

    // Crea un marcador en el mapa basado en la ubicación del sitio seleccionado
    val mark = LatLng(selectedSitio.ubicacion.latitude, selectedSitio.ubicacion.longitude)
    val markState = MarkerState(position = mark)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(mark, 15f)
    }

    // Determina el tipo de sitio para mostrar en la interfaz de usuario
    val tipo = when (selectedSitio.tipo) {
        "fut7" -> "Futbol 7"
        "futsal" -> "Futbol Sala"
        else -> "Futbol"
    }

    // Lanza un efecto cuando se carga el composable para pedir los partidos del sitio
    LaunchedEffect(Unit) {
        sitiosViewModel.pedirTodosLosPartidos()
    }

    // Construcción del Scaffold para la pantalla
    Scaffold(
        // Barra superior personalizada
        topBar = { MyTopBar() },
        // Contenido principal
        content = {
            // Lógica para mostrar los botones de administrador si el usuario es administrador
            if (loginViewModel.isAdmin()) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(bottom = 100.dp, end = 20.dp)
                        .zIndex(2F), contentAlignment = Alignment.BottomEnd
                ) {
                    Row {
                        // Botón para confirmar una petición de sitio
                        if (selectedSitio.peticion) {
                            FloatingActionButton(
                                onClick = {
                                    sitiosViewModel.confirmarPeticion {
                                        navHostController.navigate(Routes.Sitios.route)
                                        Toast.makeText(
                                            sitiosViewModel.context,
                                            "Petición aceptada",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                containerColor = maincolor,
                                contentColor = Color.White,
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Icon(Icons.Outlined.Check, "Floating action button.")
                            }
                        }

                        // Botón para eliminar un sitio o una petición
                        FloatingActionButton(
                            onClick = {
                                sitiosViewModel.borrarSitio {
                                    navHostController.navigate(Routes.Sitios.route)
                                    if (selectedSitio.peticion) {
                                        Toast.makeText(
                                            sitiosViewModel.context,
                                            "Petición denegada",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            sitiosViewModel.context,
                                            "Sitio borrado",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            },
                            containerColor = Color.Red,
                            contentColor = Color.White
                        ) {
                            Icon(Icons.Outlined.Close, "Floating action button.")
                        }
                    }
                }
            }

            // Lista desplazable de columnas para mostrar detalles del sitio y partidos
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 80.dp, bottom = 80.dp),
            ) {
                // Elemento de la imagen del sitio
                item {
                    AsyncImage(
                        model = selectedSitio.foto,
                        contentDescription = "foto sitio",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(224.dp)
                    )
                }

                // Elemento del nombre y tipo del sitio
                item {
                    Column(horizontalAlignment = Alignment.Start) {
                        MiTexto(
                            string = selectedSitio.nombreLargo,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(top = 8.dp, start = 16.dp)
                        )
                        MiTexto(
                            string = tipo,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 8.dp, start = 16.dp)
                        )
                        HorizontalDivider(
                            thickness = 0.5.dp,
                            color = Color.White,
                            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
                        )
                    }
                }

                // Elemento para mostrar la ubicación en un mapa
                item {
                    Column {
                        MiTexto(
                            string = "Ubicación",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        GoogleMap(
                            modifier = Modifier
                                .height(260.dp)
                                .padding(top = 16.dp, bottom = 16.dp)
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            cameraPositionState = cameraPositionState
                        ) {
                            Marker(
                                state = markState,
                                title = selectedSitio.nombre
                            )
                        }
                    }
                }

                // Elemento para mostrar la lista de próximos partidos, solo si no es una petición
                if (!selectedSitio.peticion) {
                    item {
                        Column {
                            MiTexto(
                                string = "Próximos Partidos",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            if (listaPartidos.isEmpty()) {
                                MiTexto(
                                    string = "No se han encontrado partidos",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            } else {
                                // Lista horizontal de partidos disponibles
                                LazyRow(
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 16.dp, top = 16.dp)
                                ) {
                                    // Itera sobre los elementos de la lista de partidos y muestra una tarjeta para cada uno
                                    items(listaPartidos) { partidoConNombreUsuario ->
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CardMatch(
                                                onClick = {
                                                    navHostController.navigate(
                                                        "${Routes.UnirsePartido.route}/${partidoConNombreUsuario.first.idPartido}/${partidoConNombreUsuario.second.username}"
                                                    )
                                                },
                                                imagenPartido = partidoConNombreUsuario.first.sitio.foto,
                                                nombreLugar = partidoConNombreUsuario.first.sitio.nombre,
                                                fechaPartido = partidoConNombreUsuario.first.fecha,
                                                horaPartido = partidoConNombreUsuario.first.hora,
                                                avatarUsuario = partidoConNombreUsuario.second.avatar,
                                                nombreUsuario = partidoConNombreUsuario.second.username,
                                                jugadoresInscritos = partidoConNombreUsuario.first.jugadores.size.toString(),
                                                jugadoresTotales = if (partidoConNombreUsuario.first.sitio.tipo == "fut7") "14" else "10"
                                            )
                                        }

                                    }
                                }
                            }

                        }
                    }
                }

                // Elemento para mostrar un indicador de carga si está activo
                item {
                    if (sitiosViewModel.showLoading) {
                        Dialog(onDismissRequest = { }) {
                            CircularProgressIndicator()
                        }
                    }
                }

            }
        },
        // Barra inferior personalizada
        bottomBar = { MyBottomBar(navHostController = navHostController) }
    )
}
