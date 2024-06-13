package com.ccormor392.pruebaproyectofinal.presentation.manejoDeUsuarios.yaAutenticados

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonColors
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.ccormor392.pruebaproyectofinal.R
import com.ccormor392.pruebaproyectofinal.navigation.Routes
import com.ccormor392.pruebaproyectofinal.presentation.amigos.AmigosViewModel
import com.ccormor392.pruebaproyectofinal.presentation.componentes.CardMatch
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MiButtonPerfil
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MiTexto
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyBottomBar
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyTopBar
import com.ccormor392.pruebaproyectofinal.presentation.manejoDeUsuarios.LoginViewModel
import com.ccormor392.pruebaproyectofinal.ui.theme.maincolor

/**
 * Composable que representa la pantalla de perfil del usuario autenticado.
 * Permite al usuario ver y gestionar su información de perfil, incluidos sus partidos creados y próximos.
 *
 * @param navController Controlador de navegación para gestionar las transiciones entre pantallas.
 * @param loginViewModel ViewModel que gestiona la lógica de la pantalla de perfil.
 * @param idUser ID del usuario del cual se muestra el perfil.
 * @param amigosViewModel ViewModel para gestionar las interacciones con los amigos del usuario.
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "StateFlowValueCalledInComposition")
@Composable
fun MiPerfil(navController: NavHostController, loginViewModel: LoginViewModel, idUser: String? = null, amigosViewModel: AmigosViewModel? = null) {
    // Estado para la cantidad de seguidores del usuario autenticado
    val seguidores by loginViewModel.seguidores.collectAsState()
    // Estado mutable para la cantidad de seguidores del perfil de usuario actual
    val seguidoresAmigos = remember { mutableIntStateOf(0) }
    // Estado para el estado de seguimiento del perfil de usuario actual
    val isFollowingState = remember { mutableStateOf(false) }
    // Estado para los partidos pasados del usuario autenticado
    val pasados by loginViewModel.listaPartidosPasados.collectAsState()
    // Estado para los próximos partidos del usuario autenticado
    val proximamente by loginViewModel.listaPartidosProximamente.collectAsState()

    // Efecto de lanzamiento para obtener los datos necesarios del usuario y los partidos relacionados
    LaunchedEffect(Unit) {
        loginViewModel.recuperarPartidos(idUser)
        loginViewModel.conseguirDatosUsuarioAutenticado(idUser)
        amigosViewModel?.let {
            it.checkIfFollowing(idUser!!)
            it.isFollowing.collect { following ->
                isFollowingState.value = following
            }
        }
    }

    // Efecto de lanzamiento para actualizar los seguidores del perfil de usuario
    LaunchedEffect(Unit) {
        amigosViewModel?.let {
            it.actualizarSeguidores(idUser!!)
            it.seguidores.collect { following ->
                seguidoresAmigos.intValue = following
            }
        }
    }

    // Estructura del diseño de la pantalla de perfil
    Scaffold(
        topBar = {
            // Barra superior personalizada
            MyTopBar()
        },
        content = {
            Column(
                Modifier
                    .padding(top = 84.dp) // Añade espacio superior
                    .fillMaxWidth() // Ocupa todo el ancho disponible
            ) {
                // Sección de información del perfil
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp) // Añade espacio inferior
                ) {
                    // Row para la imagen de perfil y detalles de usuario
                    Row(
                        Modifier
                            .height(196.dp) // Altura fija
                            .padding(horizontal = 24.dp) // Añade relleno horizontal
                            .fillMaxWidth(), // Ocupa todo el ancho disponible
                        verticalAlignment = Alignment.CenterVertically // Alineación vertical al centro
                    ) {
                        // Imagen de perfil del usuario
                        AsyncImage(
                            model = loginViewModel.imageUri,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(end = 40.dp) // Añade relleno a la derecha
                                .height(136.dp) // Altura de la imagen
                                .width(136.dp) // Ancho de la imagen
                                .clip(RoundedCornerShape(12.dp)), // Forma de esquina redondeada
                            contentScale = ContentScale.Crop, // Escala de contenido recortado
                        )
                        // Columna para detalles de usuario
                        Column(
                            Modifier
                                .fillMaxWidth() // Ocupa todo el ancho disponible
                                .height(158.dp) // Altura fija
                                .offset(y = (-2).dp), // Desplazamiento vertical
                            verticalArrangement = Arrangement.SpaceEvenly, // Espaciado uniforme vertical
                            horizontalAlignment = Alignment.CenterHorizontally // Alineación horizontal al centro
                        ) {
                            // Nombre de usuario
                            MiTexto(
                                string = loginViewModel.usuarioAutenticado.value.username,
                                fontSize = 24.sp, // Tamaño de fuente
                                fontWeight = FontWeight.Bold // Peso de la fuente
                            )
                            // Row para mostrar seguidores y siguiendo
                            Row(
                                horizontalArrangement = Arrangement.SpaceEvenly, // Espaciado uniforme horizontal
                                modifier = Modifier.fillMaxWidth() // Ocupa todo el ancho disponible
                            ) {
                                // Columna para mostrar el número de seguidores
                                ColumnaSeguidores(
                                    texto = stringResource(R.string.seguidores), // Texto de seguidores
                                    numero = if (amigosViewModel != null) seguidoresAmigos.intValue else seguidores // Número de seguidores
                                )
                                // Columna para mostrar el número de usuarios seguidos
                                ColumnaSeguidores(
                                    texto = stringResource(R.string.siguiendo), // Texto de usuarios seguidos
                                    numero = loginViewModel.usuarioAutenticado.value.amigos.count() // Número de usuarios seguidos
                                )
                            }
                            // Botón para editar perfil si es el propio perfil
                            if (loginViewModel.esMiPerfil()) {
                                MiButtonPerfil(
                                    onClickButton = { navController.navigate(Routes.EditarPerfil.route) }, // Navegar a la pantalla de edición de perfil
                                    texto = "Editar Perfil" // Texto del botón
                                )
                            } else if (amigosViewModel != null) {
                                // Botón para seguir o dejar de seguir si se visualiza el perfil de otro usuario
                                if (!isFollowingState.value) {
                                    MiButtonPerfil(
                                        onClickButton = { amigosViewModel.agregarUsuario(idUser!!) }, // Función para seguir al usuario
                                        texto = "Seguir" // Texto del botón
                                    )
                                } else {
                                    MiButtonPerfil(
                                        onClickButton = { amigosViewModel.desagregarUsuario(idUser!!) }, // Función para dejar de seguir al usuario
                                        texto = "Dejar de seguir" // Texto del botón
                                    )
                                }
                            }
                        }
                    }
                    // Separador entre secciones de información
                    HorizontalDivider(
                        modifier = Modifier.padding(top = 22.dp), // Añade relleno en la parte superior
                        thickness = 0.5.dp, // Grosor del separador
                        color = Color.White // Color del separador
                    )
                }

                // Fila de botones segmentados para cambiar entre partidos creados y próximos
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                        .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 8.dp) // Añade relleno a los lados y arriba y abajo
                ) {
                    // Botón segmentado para partidos creados
                    SegmentedButton(
                        selected = !loginViewModel.segmentedButton, // Seleccionado si el botón segmentado no está activo
                        onClick = { loginViewModel.changeSegmentedButton() }, // Función para cambiar el estado del botón segmentado
                        shape = SegmentedButtonDefaults.itemShape(
                            index = 0,
                            count = 2
                        ), // Forma del botón segmentado
                        colors = mySegmentedButtonColors() // Colores personalizados para el botón segmentado
                    ) {
                        // Texto del botón segmentado
                        MiTexto(
                            string = "Creados", // Texto del botón
                            fontSize = 16.sp, // Tamaño de fuente
                            fontWeight = FontWeight.Medium // Peso de la fuente
                        )
                    }
                    // Botón segmentado para próximos partidos
                    SegmentedButton(
                        selected = loginViewModel.segmentedButton, // Seleccionado si el botón segmentado está activo
                        onClick = { loginViewModel.changeSegmentedButton() }, // Función para cambiar el estado del botón segmentado
                        shape = SegmentedButtonDefaults.itemShape(
                            index = 1,
                            count = 2
                        ), // Forma del botón segmentado
                        colors = mySegmentedButtonColors() // Colores personalizados para el botón segmentado
                    ) {
                        // Texto del botón segmentado
                        MiTexto(
                            string = "Proximamente", // Texto del botón
                            fontSize = 16.sp, // Tamaño de fuente
                            fontWeight = FontWeight.Medium // Peso de la fuente
                        )
                    }
                }

                // Lista de partidos disponibles
                LazyVerticalGrid(
                    horizontalArrangement = Arrangement.Center, // Alineación horizontal al centro
                    columns = GridCells.Fixed(2), // Número fijo de columnas
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 80.dp) // Añade relleno arriba y abajo
                ) {
                    // Lista de partidos a mostrar
                    val lista = if (loginViewModel.segmentedButton) {
                        proximamente // Partidos próximos si está activo el botón segmentado de próximamente
                    } else {
                        pasados // Partidos pasados si está activo el botón segmentado de creados
                    }
                    // Itera sobre la lista de partidos y muestra una tarjeta para cada uno
                    items(lista) { partidoConNombreUsuario ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp), // Añade relleno vertical
                            contentAlignment = Alignment.Center // Alineación de contenido al centro
                        ) {
                            // Tarjeta de partido para cada elemento de la lista
                            CardMatch(
                                onClick = {
                                    // Navegar a la pantalla de unirse al partido con ID y nombre de usuario
                                    navController.navigate("${Routes.UnirsePartido.route}/${partidoConNombreUsuario.idPartido}/${loginViewModel.usuarioAutenticado.value.username}")
                                },
                                imagenPartido = partidoConNombreUsuario.sitio.foto, // Imagen del lugar del partido
                                nombreLugar = partidoConNombreUsuario.sitio.nombre, // Nombre del lugar del partido
                                fechaPartido = partidoConNombreUsuario.fecha, // Fecha del partido
                                horaPartido = partidoConNombreUsuario.hora, // Hora del partido
                                avatarUsuario = loginViewModel.usuarioAutenticado.value.avatar, // Avatar del usuario autenticado
                                nombreUsuario = loginViewModel.usuarioAutenticado.value.username, // Nombre de usuario autenticado
                                jugadoresInscritos = partidoConNombreUsuario.jugadores.size.toString(), // Número de jugadores inscritos
                                jugadoresTotales = if (partidoConNombreUsuario.sitio.tipo == "fut7") "14" else "10" // Número total de jugadores según tipo de sitio
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            // Barra inferior personalizada
            MyBottomBar(navHostController = navController)
        }
    )
}

/**
 * Composable que muestra una columna con texto de seguidores y su número correspondiente.
 *
 * @param texto Texto a mostrar para el tipo de seguidores.
 * @param numero Número correspondiente de seguidores.
 */
@Composable
fun ColumnaSeguidores(texto: String, numero: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Texto del tipo de seguidores
        MiTexto(
            string = texto,
            fontSize = 12.sp, // Tamaño de fuente
            fontWeight = FontWeight.Medium, // Peso de la fuente
            modifier = Modifier.padding(bottom = 4.dp) // Añade relleno en la parte inferior
        )
        // Número de seguidores
        MiTexto(
            string = numero.toString(),
            fontSize = 20.sp, // Tamaño de fuente
            fontWeight = FontWeight.Bold // Peso de la fuente
        )
    }
}

/**
 * Función que devuelve los colores personalizados para los botones segmentados.
 *
 * @return Colores personalizados para los botones segmentados.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun mySegmentedButtonColors(): SegmentedButtonColors {
    return SegmentedButtonDefaults.colors(
        activeContainerColor = maincolor, // Color activo del contenedor
        activeContentColor = Color.White, // Color activo del contenido
        activeBorderColor = maincolor, // Color activo del borde
        inactiveContainerColor = Color.Transparent, // Color inactivo del contenedor
        inactiveContentColor = maincolor, // Color inactivo del contenido
        inactiveBorderColor = maincolor, // Color inactivo del borde
    )
}

