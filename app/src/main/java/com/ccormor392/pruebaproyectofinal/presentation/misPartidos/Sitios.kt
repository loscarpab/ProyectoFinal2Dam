package com.ccormor392.pruebaproyectofinal.presentation.misPartidos

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.ccormor392.pruebaproyectofinal.navigation.Routes
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MiTexto
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyBottomBar
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyScaffoldContent
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyTextField
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyTopBar
import com.ccormor392.pruebaproyectofinal.presentation.componentes.PlaceCard
import com.ccormor392.pruebaproyectofinal.presentation.manejoDeUsuarios.LoginViewModel
import com.ccormor392.pruebaproyectofinal.presentation.manejoDeUsuarios.yaAutenticados.mySegmentedButtonColors
import com.ccormor392.pruebaproyectofinal.ui.theme.maincolor
import java.text.Normalizer

/**
 * Composable que muestra la lista de sitios y peticiones disponibles.
 *
 * @param navController Controlador de navegación de Compose.
 * @param viewmodel ViewModel que gestiona la lógica de la pantalla de sitios.
 * @param loginViewModel ViewModel que gestiona la autenticación del usuario.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun Sitios(navController: NavHostController, viewmodel: SitiosViewModel, loginViewModel: LoginViewModel) {
    // Recolecta el estado de los sitios desde el ViewModel
    val sitios by viewmodel.sitios.collectAsState()

    // Efecto lanzado al inicio para pedir los sitios al ViewModel
    LaunchedEffect(Unit) {
        viewmodel.pedirTodosLosSitios(loginViewModel.isAdmin())
    }

    // Composición del Scaffold que define la estructura de la pantalla
    Scaffold(
        topBar = { MyTopBar() },
        content = {
            // Box para contener el FloatingActionButton en la esquina inferior derecha
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(bottom = 100.dp, end = 20.dp)
                    .zIndex(2F), contentAlignment = Alignment.BottomEnd) {
                // FloatingActionButton para agregar un nuevo sitio o petición
                FloatingActionButton(
                    onClick = { navController.navigate(Routes.Peticion.route)},
                    containerColor = maincolor,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Outlined.Add, "Floating action button.")
                }
            }

            // Contenido personalizado de la pantalla dentro de MyScaffoldContent
            MyScaffoldContent {
                // Verifica si el usuario es administrador para mostrar los botones de segmentos
                if (loginViewModel.isAdmin()){
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
                    ) {
                        // Botón segmentado para Sitios
                        SegmentedButton(
                            selected = viewmodel.segmentedButton,
                            onClick = { viewmodel.changeSegmentedButton(loginViewModel.isAdmin()) },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = 0,
                                count = 2
                            ), colors = mySegmentedButtonColors()
                        ) {
                            MiTexto(
                                string = "Sitios",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        // Botón segmentado para Peticiones
                        SegmentedButton(
                            selected = !viewmodel.segmentedButton,
                            onClick = { viewmodel.changeSegmentedButton(loginViewModel.isAdmin()) },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = 1,
                                count = 2
                            ),
                            colors =  mySegmentedButtonColors()
                        ) {
                            MiTexto(
                                string = "Peticiones",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Campo de texto para buscar sitios por nombre
                MyTextField(
                    value = viewmodel.nombre,
                    onValueChange = { viewmodel.changeNombre(it, loginViewModel.isAdmin()) },
                    iconName = "buscar",
                    onClickSearchIcon = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )

                // Lista de sitios disponibles mostrada en LazyVerticalGrid
                LazyVerticalGrid(
                    horizontalArrangement = Arrangement.Center,
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 80.dp),
                ) {
                    // Verifica si la lista de sitios no está vacía
                    if (sitios.isNotEmpty()) {
                        // Verifica si el campo de búsqueda está vacío para mostrar todos los sitios
                        if (viewmodel.nombre == "") {
                            // Itera sobre los elementos de la lista de sitios y muestra una tarjeta para cada uno
                            items(sitios) { sitio ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    PlaceCard(
                                        onClick = {
                                            viewmodel.seleccionarSitio(sitio)
                                            navController.navigate(Routes.Sitio.route)
                                        },
                                        imagenSitio = sitio.foto,
                                        nombreLugar = sitio.nombre,
                                        tipoPartido = sitio.tipo
                                    )
                                }
                            }
                        } else {
                            // Itera sobre los elementos de la lista de sitios filtrados por nombre y muestra una tarjeta para cada uno
                            items(sitios) { sitio ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    PlaceCard(
                                        onClick = {
                                            viewmodel.seleccionarSitio(sitio)
                                            navController.navigate(Routes.Sitio.route)
                                        },
                                        imagenSitio = sitio.foto,
                                        nombreLugar = sitio.nombre,
                                        tipoPartido = sitio.tipo
                                    )
                                }
                            }
                        }

                    }
                }
            }
        },
        bottomBar = {
            // Barra inferior personalizada con MyBottomBar
            MyBottomBar(navHostController = navController)

        })
}

/**
 * Extensión de String para remover acentos.
 *
 * @return String sin caracteres con acentos.
 */
fun String.removeAccents(): String {
    val unaccentedString = StringBuilder()
    val normalizedString = Normalizer.normalize(this, Normalizer.Form.NFD)
    normalizedString.toCharArray().forEach { char ->
        if (char <= '\u007F') {
            unaccentedString.append(char)
        }
    }
    return unaccentedString.toString()
}
