package com.ccormor392.pruebaproyectofinal.presentation.amigos

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.ccormor392.pruebaproyectofinal.botonmas.BotonMas
import com.ccormor392.pruebaproyectofinal.navigation.Routes
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MiTexto
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyBottomBar
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyTextField
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyTopBar
import com.ccormor392.pruebaproyectofinal.presentation.componentes.RowUser
import com.ccormor392.pruebaproyectofinal.ui.theme.PurpleGrey40

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Amigos(amigosViewModel: AmigosViewModel, navController: NavHostController) {
    // Observa el StateFlow _users y conviértelo en un State que puedas utilizar
    val usersState = amigosViewModel.users.collectAsState()
    val isLoading = amigosViewModel.isLoading.collectAsState()
    LaunchedEffect(Unit) {
        amigosViewModel.restart()
    }
    Scaffold(
        topBar = {
            MyTopBar()
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 78.dp)
                    .background(PurpleGrey40),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MiTexto(
                    string = "Amigos",
                    modifier = Modifier.padding(top = 24.dp),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 32.dp, end = 32.dp, top = 16.dp)
                ) {
                    MyTextField(
                        value = amigosViewModel.nombre,
                        onValueChange = { amigosViewModel.changeNombre(it) },
                        iconName = "buscar",
                        onClickSearchIcon = { amigosViewModel.buscarAmigo() },
                        modifier = Modifier.fillMaxWidth()
                    )
                }


                Row(
                    modifier = Modifier
                        .height(300.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    if(isLoading.value && usersState.value.isNotEmpty()){
                        CircularProgressIndicator()
                    }
                    else if(!isLoading.value && usersState.value.isEmpty()){
                        MiTexto(string = "No se encontraron resultados", modifier = Modifier.padding(top = 16.dp))
                    }
                    else{
                        // Aquí accedes a la lista de usuarios desde el State
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start
                        ) {
                            usersState.value.forEach { (user, bool) ->
                                item {
                                    val lambda =
                                    if (bool){
                                        { amigosViewModel.desagregarUsuario(user.userId) }
                                    }
                                    else{
                                        {amigosViewModel.agregarUsuario(user.userId)}
                                    }
                                    RowUser(
                                        username = user.username,
                                        avatar = user.avatar,
                                        onClickRow = { navController.navigate("${Routes.MiPerfil.route}/${user.userId}") },
                                        onClickButton = lambda,
                                        leSigo = bool
                                    )
                                }
                            }
                        }
                    }
                }
                BotonMas(
                    textButton = "Buscar",
                    onClickButton = {
                        amigosViewModel.buscarAmigo()
                    },
                    modifier = Modifier.padding(top = 32.dp)
                )
            }
        },
        bottomBar = {
            MyBottomBar(navHostController = navController)
        }
    )
}
