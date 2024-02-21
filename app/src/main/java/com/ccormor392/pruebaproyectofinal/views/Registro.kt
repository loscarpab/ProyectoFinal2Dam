package com.ccormor392.pruebaproyectofinal.views

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ccormor392.pruebaproyectofinal.R
import com.ccormor392.pruebaproyectofinal.botonsininiciarsesion.BotonSinIniciarSesion
import com.ccormor392.pruebaproyectofinal.botonsininiciarsesion.Property1
import com.ccormor392.pruebaproyectofinal.logoapp.LogoApp
import com.ccormor392.pruebaproyectofinal.navigation.Routes
import com.ccormor392.pruebaproyectofinal.textotopscreenlogs.TextoTopScreenLogs
import com.ccormor392.pruebaproyectofinal.ui.theme.PurpleGrey40
import com.ccormor392.pruebaproyectofinal.viewModels.LoginViewModel
import com.ccormor392.pruebaproyectofinal.views.componentes.Alert

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Registro(navController: NavHostController, loginViewModel: LoginViewModel) {
    Scaffold(
        topBar = {
            LogoApp(
                textNombreApp = "QuickMatch",
                modifier = Modifier.padding(start = 32.dp, top = 40.dp)
            )
        },
        content = {
            Column(
                Modifier
                    .background(PurpleGrey40)
                    .fillMaxSize()
                    .padding(top = 100.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly,
            ) {
                TextoTopScreenLogs(
                    textTitulo = "Registrate",
                    textSubtitulo = "Rellena los campos con tus datos para crear una cuenta"
                )
                Column(
                    modifier = Modifier.height(280.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    MyTextField(
                        value = loginViewModel.userName,
                        onValueChange = { loginViewModel.changeUserName(it) }) {
                        Text(text = stringResource(R.string.nombre_de_usuario))
                    }
                    MyTextField(
                        value = loginViewModel.email,
                        onValueChange = { loginViewModel.changeEmail(it) },
                        keyboardType = KeyboardType.Email
                    ) {
                        Text(text = stringResource(R.string.email))
                    }
                    MyTextField(
                        value = loginViewModel.password,
                        onValueChange = { loginViewModel.changePassword(it) },
                        keyboardType = KeyboardType.Password,
                        visualTransformation = PasswordVisualTransformation()
                    ) {
                        Text(text = stringResource(R.string.contrasena))
                    }
                }

                BotonSinIniciarSesion(
                    textButton = "Registrate",
                    textSubButtonNotClickable = "¿Ya tienes cuenta?",
                    textSubButtonClickable = "Inicia Sesión",
                    property1 = Property1.Smal,
                    onClickButton = { loginViewModel.createUser { navController.navigate(Routes.Inicio.route) } },
                    onClickSubButton = { navController.navigate(Routes.InicioSesion.route) }
                )
                if (loginViewModel.showAlert) {
                    Alert(title = "Alerta",
                        message = "Usuario no creado correctamente",
                        confirmText = "Aceptar",
                        onConfirmClick = { loginViewModel.closeAlert() },
                        onDismissClick = { }) // DCS - ninguna acción en onDismissClick para que no oculte el diálogo
                }
            }
        },
        bottomBar = {
        }
    )

}