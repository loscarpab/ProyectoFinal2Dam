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
import com.ccormor392.pruebaproyectofinal.data.model.Partido
import com.ccormor392.pruebaproyectofinal.lineaseparadoratopbar.LineaSeparadoraTopbar
import com.ccormor392.pruebaproyectofinal.navigation.Routes
import com.ccormor392.pruebaproyectofinal.presentation.amigos.AmigosViewModel
import com.ccormor392.pruebaproyectofinal.presentation.componentes.CardMatch
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MiButtonPerfil
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MiTexto
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyBottomBar
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyTopBar
import com.ccormor392.pruebaproyectofinal.presentation.manejoDeUsuarios.LoginViewModel
import com.ccormor392.pruebaproyectofinal.ui.theme.maincolor

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "StateFlowValueCalledInComposition")
@Composable
fun MiPerfil(navController: NavHostController, loginViewModel: LoginViewModel, idUser:String? = null, amigosViewModel: AmigosViewModel? = null) {
    val seguidores by loginViewModel.seguidores.collectAsState()
    val seguidoresAmigos = remember { mutableIntStateOf(0) }
    val isFollowingState = remember { mutableStateOf(false) }
    val pasados by loginViewModel.listaPartidosPasados.collectAsState()
    val proximamente  by loginViewModel.listaPartidosProximamente.collectAsState()

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
    LaunchedEffect(Unit) {
        amigosViewModel?.let {
            it.actualizarSeguidores(idUser!!)
            it.seguidores.collect() { following ->
                seguidoresAmigos.intValue = following
            }
        }
    }
    Scaffold(
        topBar = {
            MyTopBar()
        },
        content = {
            Column(
                Modifier
                    .padding(top = 84.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    Modifier
                        .height(200.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        Modifier
                            .height(196.dp)
                            .padding(horizontal = 24.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = loginViewModel.imageUri,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(end = 40.dp)
                                .height(136.dp)
                                .width(136.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop,
                        )
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .height(158.dp)
                                .offset(y = (-2).dp),
                            verticalArrangement = Arrangement.SpaceEvenly,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            MiTexto(
                                string = loginViewModel.usuarioAutenticado.value.username,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                ColumnaSeguidores(
                                    texto = stringResource(R.string.seguidores),
                                    numero = if (amigosViewModel!=null) seguidoresAmigos.intValue else seguidores
                                )
                                ColumnaSeguidores(
                                    texto = stringResource(R.string.siguiendo),
                                    numero = loginViewModel.usuarioAutenticado.value.amigos.count()
                                )

                            }
                            if (loginViewModel.esMiPerfil()){
                                MiButtonPerfil(onClickButton = { navController.navigate(Routes.EditarPerfil.route) }, texto = "Editar Perfil")
                            }
                            else if (amigosViewModel != null) {
                                if (!isFollowingState.value) {
                                    MiButtonPerfil(onClickButton = { amigosViewModel.agregarUsuario(idUser!!) }, texto = "Seguir")
                                } else {
                                    MiButtonPerfil(onClickButton = { amigosViewModel.desagregarUsuario(idUser!!) }, texto = "Dejar de seguir")
                                }
                            }
                        }

                    }
                    LineaSeparadoraTopbar()

                }
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                        .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 8.dp)
                ) {
                    SegmentedButton(
                        selected = !loginViewModel.segmentedButton,
                        onClick = { loginViewModel.changeSegmentedButton() },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = 0,
                            count = 2
                        ), colors = mySegmentedButtonColors()
                    ) {
                        MiTexto(
                            string = "Creados",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    SegmentedButton(
                        selected = loginViewModel.segmentedButton,
                        onClick = { loginViewModel.changeSegmentedButton() },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = 1,
                            count = 2
                        ),
                        colors =  mySegmentedButtonColors()
                    ) {
                        MiTexto(
                            string = "Proximamente",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                // Lista de partidos disponibles
                LazyVerticalGrid(
                    horizontalArrangement = Arrangement.Center,
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 80.dp)
                    ,
                ) {
                    var lista = mutableListOf<Partido>()
                    lista = if (loginViewModel.segmentedButton){
                        proximamente
                    } else{
                        pasados
                    }
                    items(lista) { partidoConNombreUsuario ->
                        Box (modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp), contentAlignment = Alignment.Center){
                            CardMatch(
                                onClick = { navController.navigate("${Routes.UnirsePartido.route}/${partidoConNombreUsuario.idPartido}/${loginViewModel.usuarioAutenticado.value.username}") },
                                imagenPartido = partidoConNombreUsuario.foto,
                                nombreLugar = partidoConNombreUsuario.nombreSitio,
                                fechaPartido = partidoConNombreUsuario.fecha,
                                horaPartido =  partidoConNombreUsuario.hora,
                                avatarUsuario =  loginViewModel.usuarioAutenticado.value.avatar,
                                nombreUsuario = loginViewModel.usuarioAutenticado.value.username
                            )
                        }
                    }
                    // Itera sobre los elementos de la lista de partidos y muestra una tarjeta para cada uno

                }
            }

        },
        bottomBar = {
            MyBottomBar(navHostController = navController)
        }
    )
}

@Composable
fun ColumnaSeguidores(texto: String, numero: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        MiTexto(
            string = texto,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 4.dp)
        )
        MiTexto(
            string = numero.toString(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun mySegmentedButtonColors(): SegmentedButtonColors {
    return SegmentedButtonDefaults.colors(
        activeContainerColor = maincolor,
        activeContentColor = Color.White,
        activeBorderColor = maincolor,
        inactiveContainerColor = Color.Transparent,
        inactiveContentColor = maincolor,
        inactiveBorderColor = maincolor,
        )

}