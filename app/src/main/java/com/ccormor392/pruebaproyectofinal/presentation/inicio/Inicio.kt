package com.ccormor392.pruebaproyectofinal.presentation.inicio

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.ccormor392.pruebaproyectofinal.R
import com.ccormor392.pruebaproyectofinal.botonmas.BotonMas
import com.ccormor392.pruebaproyectofinal.cartapartido.CartaPartido
import com.ccormor392.pruebaproyectofinal.cartapartido.Variante
import com.ccormor392.pruebaproyectofinal.navigation.Routes
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyBottomBar
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyScaffoldContent
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyTopBar

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
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
    LaunchedEffect(Unit) {
        inicioViewModel.pedirTodosLosPartidos()
        Log.d("Inicio", "Lista de partidos solicitada.")
    }

    // Agregar impresión de registro para el tamaño de la lista
    Log.d("Inicio", "Tamaño de la lista de partidos: ${lista.value.size}")
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
                if (lista.value.isNotEmpty()){
                    // Lista de partidos disponibles
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 80.dp)
                    ) {
                        // Itera sobre los elementos de la lista de partidos y muestra una tarjeta para cada uno
                        items(lista.value) { partidoConNombreUsuario ->
                            AsyncImage(model = partidoConNombreUsuario.first.foto, contentDescription = "fotopartido")
                            CartaPartido(
                                textLugar = partidoConNombreUsuario.first.nombreSitio,
                                textFecha = partidoConNombreUsuario.first.fecha,
                                textHora = partidoConNombreUsuario.first.hora,
                                textUsuario = partidoConNombreUsuario.second,
                                variante = Variante.SinImagen,
                                modifier = Modifier.padding(8.dp),
                                // Navega a la pantalla de unirse al partido al hacer clic en la tarjeta
                                onClickCard = {
                                    navController.navigate("${Routes.UnirsePartido.route}/${partidoConNombreUsuario.first.idPartido}/${partidoConNombreUsuario.second}")
                                }
                            )
                        }
                    }
                }
                else{
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
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
