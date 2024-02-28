package com.ccormor392.pruebaproyectofinal.presentation.inicio

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ccormor392.pruebaproyectofinal.botonmas.BotonMas
import com.ccormor392.pruebaproyectofinal.bottombar.BottomBar
import com.ccormor392.pruebaproyectofinal.cartapartido.CartaPartido
import com.ccormor392.pruebaproyectofinal.cartapartido.Variante
import com.ccormor392.pruebaproyectofinal.navigation.Routes
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyScaffoldContent
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyTopBar

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Inicio(navController: NavHostController, inicioViewModel: InicioViewModel) {
    Scaffold(
        topBar = {
            MyTopBar()
        },
        content = {
            MyScaffoldContent {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    BotonMas(
                        textButton = "Crea un partido",
                        onClickButton = { navController.navigate(Routes.CrearPartido.route)

                        })
                }
                LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier
                    .fillMaxWidth()
                    .padding(top= 16.dp, bottom = 80.dp)) {
                    items(inicioViewModel.listaPartidosConNombreUsuario) {
                        CartaPartido(
                            textLugar = it.first.nombreSitio,
                            textFecha = it.first.fecha,
                            textHora = it.first.hora,
                            textUsuario = it.second,
                            variante = Variante.SinImagen,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        },
        bottomBar = {
            BottomBar(modifier = Modifier.fillMaxWidth())
        }
    )

}