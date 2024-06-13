package com.ccormor392.pruebaproyectofinal.presentation.inicio

import android.annotation.SuppressLint
import android.location.Location
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
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
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.GeoPoint
import java.util.Date

@OptIn(ExperimentalPermissionsApi::class)
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
    // Recolectar la lista de partidos con nombres de usuario como estado actual
    val lista = inicioViewModel.listaPartidosConNombreUsuario.collectAsState()

    // Estado de permiso para la ubicación actual del usuario
    val locationPermissionState = rememberPermissionState(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    // Cliente para obtener la ubicación actual
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(inicioViewModel.context)
    }

    // Estado mutable para almacenar la ubicación actual del usuario
    var location by remember { mutableStateOf<Location?>(null) }

    LaunchedEffect(Unit) {
        // Solicitar permiso de ubicación si aún no está concedido
        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        } else {
            // Obtener la última ubicación conocida del usuario si el permiso está concedido
            fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                location = loc
            }
        }

        // Cargar todos los partidos al iniciar la pantalla
        inicioViewModel.pedirTodosLosPartidos()
    }

    // Estructura principal de la pantalla utilizando Scaffold
    Scaffold(
        topBar = {
            MyTopBar()
        },
        content = {
            // Contenido principal de la pantalla dentro de MyScaffoldContent
            MyScaffoldContent {
                // Row que contiene el botón para crear un nuevo partido
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    BotonMas(
                        textButton = stringResource(id = R.string.crear_partido),
                        onClickButton = { navController.navigate(Routes.CrearPartido.route) }
                    )
                }

                // Verificar si la lista de partidos no está vacía antes de mostrarla
                if (lista.value.isNotEmpty()) {
                    LazyColumn(modifier = Modifier.padding(bottom = 88.dp, top = 16.dp)) {
                        // Mostrar la fila de partidos más cercanos si se concede el permiso de ubicación
                        if (locationPermissionState.status.isGranted && location != null) {
                            item {
                                FilaInicio(
                                    lista = compareDistances(location!!, lista.value.distinct()),
                                    navController = navController,
                                    icono = Icons.Outlined.Place,
                                    textoTitulo = "Más cercanos",
                                    paddingTop = 4.dp
                                )
                            }
                        }

                        // Mostrar la fila de partidos próximos en el tiempo
                        item {
                            FilaInicio(
                                lista = lista.value.distinct()
                                    .filter { it.first.timestamp > Date(System.currentTimeMillis()) }
                                    .sortedBy { it.first.timestamp },
                                navController = navController,
                                icono = Icons.Outlined.DateRange,
                                textoTitulo = "Próximamente"
                            )
                        }

                        // Mostrar la fila de partidos con pocos huecos disponibles
                        item {
                            FilaInicio(
                                lista = lista.value.distinct().sortedByDescending { it.first.jugadores.size },
                                navController = navController,
                                icono = Icons.Outlined.Warning,
                                textoTitulo = "Pocos huecos"
                            )
                        }

                        // Mostrar la fila de partidos en los que el usuario participa
                        item {
                            FilaInicio(
                                lista = lista.value.distinct()
                                    .filter { equipo ->
                                        equipo.first.jugadores.any { it.userId == inicioViewModel.getUserId() }
                                    },
                                navController = navController,
                                icono = Icons.Outlined.AccountCircle,
                                textoTitulo = "Participando"
                            )
                        }
                    }
                } else {
                    // Mostrar un indicador de carga si la lista está vacía
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(100.dp))
                    }
                }
            }
        },
        bottomBar = {
            // Barra inferior de navegación
            MyBottomBar(navHostController = navController)
        }
    )
}

/**
 * Composable para mostrar una fila de inicio con una lista de partidos.
 *
 * @param lista Lista de pares de Partido y UserInicio para mostrar en la fila.
 * @param navController Controlador de navegación para la navegación entre pantallas.
 * @param icono Icono a mostrar al inicio de la fila.
 * @param textoTitulo Texto del título que describe el contenido de la fila.
 * @param paddingTop Espacio de relleno en la parte superior de la fila.
 */
@Composable
fun FilaInicio(
    lista: List<Pair<Partido, UserInicio>>,
    navController: NavHostController,
    icono: ImageVector,
    textoTitulo: String,
    paddingTop: Dp = 20.dp
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 16.dp, top = paddingTop, bottom = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icono que representa el tipo de información mostrada en la fila
        Icon(
            icono,
            contentDescription = "fecha",
            tint = Color.White,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        // Texto que describe el contenido o propósito de la fila
        MiTexto(string = textoTitulo, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }

    // LazyRow que muestra tarjetas individuales para cada partido en la lista
    LazyRow(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp)
    ) {
        // Iterar sobre los elementos de la lista y mostrar una tarjeta para cada partido
        items(lista) { partidoConNombreUsuario ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                // Tarjeta personalizada que muestra detalles del partido y permite navegar a él
                CardMatch(
                    onClick = {
                        navController.navigate("${Routes.UnirsePartido.route}/${partidoConNombreUsuario.first.idPartido}/${partidoConNombreUsuario.second.username}")
                    },
                    imagenPartido = partidoConNombreUsuario.first.sitio.foto,
                    nombreLugar = partidoConNombreUsuario
                        .first.sitio.nombre,
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

/**
 * Convierte una GeoPoint en una Location.
 *
 * @param myLocation GeoPoint que representa la ubicación.
 * @return Location equivalente a la ubicación dada.
 */
fun getLocationFromMyLocation(myLocation: GeoPoint): Location {
    return Location("").apply {
        latitude = myLocation.latitude
        longitude = myLocation.longitude
    }
}

/**
 * Compara distancias entre una ubicación de referencia y una lista de partidos.
 * Ordena la lista de partidos por distancia desde la ubicación de referencia.
 *
 * @param referenceLocation Ubicación de referencia para calcular distancias.
 * @param partidos Lista de pares de Partido y UserInicio para ordenar por distancia.
 * @return Lista ordenada de pares de Partido y UserInicio por distancia ascendente.
 */
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
        .sortedBy { it.third } // Ordenar por distancia ascendente
        .map { (partido, userInicio, _) ->
            partido to userInicio
        }
}
