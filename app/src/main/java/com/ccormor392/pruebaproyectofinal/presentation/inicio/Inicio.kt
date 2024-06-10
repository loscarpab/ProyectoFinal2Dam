package com.ccormor392.pruebaproyectofinal.presentation.inicio

import android.annotation.SuppressLint
import android.location.Location
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.ccormor392.pruebaproyectofinal.R
import com.ccormor392.pruebaproyectofinal.botonmas.BotonMas
import com.ccormor392.pruebaproyectofinal.data.model.Partido
import com.ccormor392.pruebaproyectofinal.data.model.UserInicio
import com.ccormor392.pruebaproyectofinal.navigation.Routes
import com.ccormor392.pruebaproyectofinal.presentation.componentes.CardMatch
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MiTexto
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyBottomBar
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyScaffoldContent
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyTopBar
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.GeoPoint
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "MissingPermission")
/**
 * Composable para la pantalla de inicio.
 * Esta pantalla muestra una lista de partidos disponibles y permite al usuario crear un nuevo partido.
 *
 * @param navController Controlador de navegación para la navegación entre pantallas.
 * @param inicioViewModel ViewModel que maneja la lógica relacionada con la pantalla de inicio.
 */
@Composable
fun Inicio(navController: NavHostController, inicioViewModel: InicioViewModel) {
    val lista = inicioViewModel.listaPartidosConNombreUsuario.collectAsState()
    val locationPermissionState = rememberPermissionState(
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(inicioViewModel.context) }
    var location by remember { mutableStateOf<Location?>(null) }

    LaunchedEffect(Unit) {
        if (!locationPermissionState.status.isGranted){
            locationPermissionState.launchPermissionRequest()
        }else{
            fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                location = loc
            }
        }
        inicioViewModel.pedirTodosLosPartidos()
    }
    Scaffold(
        topBar = {
            MyTopBar()
        },
        content = {
            // Contenido principal de la pantalla
            MyScaffoldContent {
                // Botón para crear un nuevo partido
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    BotonMas(
                        textButton = stringResource(id = R.string.crear_partido),
                        onClickButton = { navController.navigate(Routes.CrearPartido.route) }
                    )

                }
                if (lista.value.isNotEmpty()) {
                    LazyColumn(modifier = Modifier.padding(bottom = 88.dp, top = 16.dp)){
                        if (locationPermissionState.status.isGranted && location != null){
                            item {
                                FilaInicio(
                                    lista = compareDistances(location!!, lista.value.distinct()),
                                    navController = navController,
                                    Icons.Outlined.Place,
                                    "Más cercanos",
                                    4.dp
                                )
                            }
                        }
                        item {
                            FilaInicio(
                                lista = lista.value.distinct().filter { it.first.timestamp > Date(System.currentTimeMillis()) }.sortedBy { it.first.timestamp },
                                navController = navController,
                                Icons.Outlined.DateRange,
                                "Proximamente"
                            )
                        }
                        item {
                            FilaInicio(
                                lista = lista.value.distinct().sortedByDescending { it.first.jugadores.size },
                                navController = navController,
                                Icons.Outlined.Warning,
                                "Pocos huecos"
                            )
                        }
                        item {
                            FilaInicio(
                                lista = lista.value.distinct().filter { equipo -> equipo.first.jugadores.any { it.userId == inicioViewModel.getUserId() } },
                                navController = navController,
                                Icons.Outlined.AccountCircle,
                                "Participando"
                            )
                        }
                    }



                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(100.dp))
                    }
                }


            }
        },
        bottomBar = {
            // Barra de la parte inferior con opciones de navegación
            MyBottomBar(navHostController = navController)
        }
    )
}

@Composable
fun FilaInicio(lista: List<Pair<Partido, UserInicio>>, navController: NavHostController,icono:ImageVector, textoTitulo:String, paddingTop: Dp = 20.dp) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = paddingTop, bottom = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icono,
            contentDescription = "fecha",
            tint = Color.White,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        MiTexto(string = textoTitulo, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
    // Lista de partidos disponibles
    LazyRow(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp)
    ) {
        // Itera sobre los elementos de la lista de partidos y muestra una tarjeta para cada uno
        items(lista) { partidoConNombreUsuario ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp), contentAlignment = Alignment.Center
            ) {
                CardMatch(
                    onClick = { navController.navigate("${Routes.UnirsePartido.route}/${partidoConNombreUsuario.first.idPartido}/${partidoConNombreUsuario.second.username}") },
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

fun getLocationFromMyLocation(myLocation: GeoPoint): Location {
    return Location("").apply {
        latitude = myLocation.latitude
        longitude = myLocation.longitude
    }
}
// Calcular distancias y ordenar la lista
fun compareDistances(
    referenceLocation: Location,
    partidos: List<Pair<Partido, UserInicio>>
): List<Pair<Partido, UserInicio>> {
    return partidos
        .map { (partido, userInicio) ->
            val partidoLocation = getLocationFromMyLocation(partido.sitio.ubicacion)
            val distance = referenceLocation.distanceTo(partidoLocation)
            Triple(partido, userInicio, distance)
        }
        .sortedBy { it.third } // Ordenar por distancia
        .map { (partido, userInicio, _) ->
            partido to userInicio
        }
}

