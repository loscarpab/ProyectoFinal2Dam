package com.ccormor392.pruebaproyectofinal.presentation.manejoDeUsuarios.sinAuntenticar

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.ccormor392.pruebaproyectofinal.R
import com.ccormor392.pruebaproyectofinal.botonsininiciarsesion.BotonSinIniciarSesion
import com.ccormor392.pruebaproyectofinal.logoapp.LogoApp
import com.ccormor392.pruebaproyectofinal.navigation.Routes.*
import com.ccormor392.pruebaproyectofinal.xxlargexbold.poppins


/**
 * Composable que representa la pantalla de inicio cuando el usuario no está registrado.
 * Proporciona opciones para registrarse e iniciar sesión.
 *
 * @param navController Controlador de navegación para gestionar las transiciones entre pantallas.
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun InicioSinRegistro(navController: NavHostController) {
    Scaffold(
        // Barra superior que muestra el logotipo de la aplicación
        topBar = {
            LogoApp(
                textNombreApp = stringResource(id = R.string.nombreApp),
                modifier = Modifier.padding(start = 32.dp, top = 40.dp)
            )
        },
        // Contenido principal de la pantalla
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Imagen de bienvenida
                Image(
                    painter = painterResource(id = R.drawable.foto_inicio),
                    contentDescription = stringResource(R.string.jugadora_con_balon),
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
                // Texto de bienvenida
                Text(
                    text = stringResource(R.string.bienvenido_a_quickmatch),
                    fontFamily = poppins,
                    fontSize = 40.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 60.sp
                )

                // Botón para registrarse o iniciar sesión
                BotonSinIniciarSesion(
                    textBoton = stringResource(R.string.registrate),
                    textSubButtonNotClickable = stringResource(R.string.ya_tienes_cuenta),
                    textSubButtonClickable = stringResource(R.string.inicia_sesion),
                    // Acción al hacer clic en el botón de registro
                    onClickButton = { navController.navigate(Registro.route) },
                    // Acción al hacer clic en el botón de inicio de sesión
                    onClickSubButton = { navController.navigate(InicioSesion.route) }
                )
            }

        },
        bottomBar = {
            // No se muestra ningún elemento en la barra inferior en esta pantalla
        }
    )

}

