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
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ccormor392.pruebaproyectofinal.R
import com.ccormor392.pruebaproyectofinal.botonmas.BotonMas
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyBottomBar
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyTextField
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyTopBar
import com.ccormor392.pruebaproyectofinal.presentation.componentes.RowUser
import com.ccormor392.pruebaproyectofinal.presentation.componentes.TextTitlePage
import com.ccormor392.pruebaproyectofinal.textotopscreenlogs.TextoTopScreenLogs
import com.ccormor392.pruebaproyectofinal.ui.theme.PurpleGrey40
import com.ccormor392.pruebaproyectofinal.usuarioitem.UsuarioItem

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Amigos(amigosViewModel: AmigosViewModel, navController: NavHostController) {
    // Observa el StateFlow _users y conviértelo en un State que puedas utilizar
    val usersState = amigosViewModel.users.collectAsState()
    LaunchedEffect(Unit){
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
                TextTitlePage(string = "Amigos")
                Row (
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 32.dp, end = 32.dp, top = 16.dp)){
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

                        // Aquí accedes a la lista de usuarios desde el State
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start
                        ) {
                            items(usersState.value) { user ->
                                /*
                                // Utiliza cada usuario para crear un elemento de la lista
                                UsuarioItem(
                                    textUsername = user.username,
                                    modifier = Modifier.padding(top = 24.dp, start = 32.dp)
                                )
                                Button(onClick = { amigosViewModel.agregarUsuario(user.userId) }) {
                                    Text(text = "Agregar")
                                }

                                 */
                                RowUser(username = user.username, avatar = user.avatar)
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
