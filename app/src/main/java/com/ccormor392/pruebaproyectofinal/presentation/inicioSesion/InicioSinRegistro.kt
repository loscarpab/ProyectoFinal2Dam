package com.ccormor392.pruebaproyectofinal.presentation.inicioSesion

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.ccormor392.pruebaproyectofinal.R
import com.ccormor392.pruebaproyectofinal.botonsininiciarsesion.BotonSinIniciarSesion
import com.ccormor392.pruebaproyectofinal.logoapp.LogoApp
import com.ccormor392.pruebaproyectofinal.navigation.Routes.*

import com.ccormor392.pruebaproyectofinal.ui.theme.PruebaProyectoFinalTheme
import com.ccormor392.pruebaproyectofinal.xxlargexbold.poppins


@Composable
@Preview
fun InicioSinRegistroPreview(){
    val navHostController = NavHostController(LocalContext.current)
    InicioSinRegistro(navController = navHostController)
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun InicioSinRegistro(navController: NavHostController) {
    PruebaProyectoFinalTheme {
        Scaffold(
            topBar = {
                LogoApp(
                    textNombreApp = "QuickMatch",
                    modifier = Modifier.padding(start = 32.dp, top = 40.dp)
                )
            },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.foto_inicio),
                        contentDescription = "Jugadora con balon",
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        text = "Bienvenido a QuickMatch",
                        fontFamily = poppins,
                        fontSize = 40.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 60.sp

                    )

                    BotonSinIniciarSesion(
                        textBoton = stringResource(R.string.registrate),
                        textSubButtonNotClickable = stringResource(R.string.ya_tienes_cuenta),
                        textSubButtonClickable = stringResource(R.string.inicia_sesion),
                        onClickButton = {navController.navigate(Registro.route)},
                        onClickSubButton = {navController.navigate(InicioSesion.route)}
                    )

                }

            },
            bottomBar = {

            }
        )

    }
}