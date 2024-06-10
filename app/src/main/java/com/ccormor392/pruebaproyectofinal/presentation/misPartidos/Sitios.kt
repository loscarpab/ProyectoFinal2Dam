package com.ccormor392.pruebaproyectofinal.presentation.misPartidos

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ccormor392.pruebaproyectofinal.navigation.Routes
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyBottomBar
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyScaffoldContent
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyTextField
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyTopBar
import com.ccormor392.pruebaproyectofinal.presentation.componentes.PlaceCard
import java.text.Normalizer

@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun Sitios(navController:NavHostController, viewmodel:SitiosViewModel){
    val sitios by viewmodel.listaSitios.collectAsState()

    LaunchedEffect(Unit) {
        viewmodel.pedirTodosLosSitios()
    }
    Scaffold(
        topBar = {
            MyTopBar()
        },
        content = {
            MyScaffoldContent {
                MyTextField(
                    value = viewmodel.nombre,
                    onValueChange = { viewmodel.changeNombre(it) },
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
                        .padding(top = 16.dp, bottom = 80.dp)
                    ,
                ) {
                    if (sitios.isNotEmpty()){
                        if (viewmodel.nombre == ""){
                            items(sitios) { sitio ->
                                Box (modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp), contentAlignment = Alignment.Center){
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
                        else{
                            // Itera sobre los elementos de la lista de sitios y muestra una tarjeta para cada uno
                            items(sitios.filter  { sitio2 ->
                                sitio2.nombre.trim().removeAccents().contains(viewmodel.nombre.trim().removeAccents(), ignoreCase = true)
                            }) { sitio ->
                                Box (modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp), contentAlignment = Alignment.Center){
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