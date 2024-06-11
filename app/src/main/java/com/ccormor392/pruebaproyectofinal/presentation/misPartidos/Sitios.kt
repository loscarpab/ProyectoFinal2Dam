package com.ccormor392.pruebaproyectofinal.presentation.misPartidos

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.ccormor392.pruebaproyectofinal.data.model.Sitio
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun Sitios(navController: NavHostController, viewmodel: SitiosViewModel, loginViewModel: LoginViewModel) {
    val sitios by viewmodel.sitios.collectAsState()

    LaunchedEffect(Unit) {
        viewmodel.pedirTodosLosSitios(loginViewModel.isAdmin())
    }
    Scaffold(
        topBar = {
            MyTopBar()
        },
        content = {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(bottom = 100.dp, end = 20.dp)
                    .zIndex(2F), contentAlignment = Alignment.BottomEnd) {
                FloatingActionButton(
                    onClick = { navController.navigate(Routes.Peticion.route)},
                    containerColor = maincolor,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Outlined.Add, "Floating action button.")
                }
            }

            MyScaffoldContent {
                if (loginViewModel.isAdmin()){
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
                    ) {
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

                MyTextField(
                    value = viewmodel.nombre,
                    onValueChange = { viewmodel.changeNombre(it, loginViewModel.isAdmin()) },
                    iconName = "buscar",
                    onClickSearchIcon = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )
                // Lista de sitios disponibles
                LazyVerticalGrid(
                    horizontalArrangement = Arrangement.Center,
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 80.dp),
                ) {
                    if (sitios.isNotEmpty()) {
                        if (viewmodel.nombre == "") {
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
                        }

                    }
                }
            }
        },
        bottomBar = {
            MyBottomBar(navHostController = navController)

        })
}

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