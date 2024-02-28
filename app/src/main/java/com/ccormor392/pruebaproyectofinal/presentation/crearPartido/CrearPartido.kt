package com.ccormor392.pruebaproyectofinal.presentation.crearPartido

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.ccormor392.pruebaproyectofinal.R
import com.ccormor392.pruebaproyectofinal.botonmas.BotonMas
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyTopBar
import com.ccormor392.pruebaproyectofinal.presentation.inicioSesion.MyTextField

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearPartido(partidoViewModel: CreateMatchViewModel, navController: NavHostController) {
    val launcher = rememberLauncherForActivityResult(
        contract =
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        partidoViewModel.imageUri = uri
    }

    Scaffold(
        topBar = {
            MyTopBar()
        },
        content = {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(top = 78.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clickable { launcher.launch("image/*")
                                   },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (partidoViewModel.imageUri == null) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.partidopordefecto),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Box {
                                Image(
                                    painter = painterResource(id = R.drawable.icon_add_photo),
                                    contentDescription = null
                                )
                            }

                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { launcher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = partidoViewModel.imageUri,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Box {
                                Image(
                                    painter = painterResource(id = R.drawable.icon_add_photo),
                                    contentDescription = null
                                )
                            }

                        }
                    }
                }
                MyTextField(
                    value = partidoViewModel.nombreSitio,
                    onValueChange = { partidoViewModel.changeLugar(it) },
                    string = "Lugar"
                )
                MyTextField(
                    value = partidoViewModel.fecha,
                    onValueChange = { partidoViewModel.changeFecha(it) },
                    string = partidoViewModel.numeroPartidosUsuarioAutenticado().toString()
                )
                MyTextField(
                    value = partidoViewModel.hora,
                    onValueChange = { partidoViewModel.changeHora(it) },
                    string = "Hora"
                )
                BotonMas(
                    textButton = "Crea un partido",
                    onClickButton = { partidoViewModel.crearPartido() })

            }
        })

}







