package com.ccormor392.pruebaproyectofinal.views

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ccormor392.pruebaproyectofinal.R
import com.ccormor392.pruebaproyectofinal.botonsininiciarsesion.BotonSinIniciarSesion
import com.ccormor392.pruebaproyectofinal.botonsininiciarsesion.Property1
import com.ccormor392.pruebaproyectofinal.logoapp.LogoApp
import com.ccormor392.pruebaproyectofinal.logoapp.poppins
import com.ccormor392.pruebaproyectofinal.navigation.Routes
import com.ccormor392.pruebaproyectofinal.textotopscreenlogs.TextoTopScreenLogs
import com.ccormor392.pruebaproyectofinal.ui.theme.PurpleGrey40
import com.ccormor392.pruebaproyectofinal.viewModels.LoginViewModel
import com.ccormor392.pruebaproyectofinal.views.componentes.Alert

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun InicioSesion(navController: NavHostController, loginViewModel: LoginViewModel) {
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
                    textTitulo = "Inicia Sesión",
                    textSubtitulo = "Rellena los campos con los datos de tu cuenta para iniciar sesión"
                )
                Column(
                    modifier = Modifier.height(140.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    MyTextField(
                        value = loginViewModel.email,
                        onValueChange = { loginViewModel.changeEmail(it) },
                        keyboardType = KeyboardType.Email,
                        label = { Text(text = stringResource(R.string.email)) })
                    MyTextField(
                        value = loginViewModel.password,
                        keyboardType = KeyboardType.Password,
                        visualTransformation = PasswordVisualTransformation(),
                        onValueChange = { loginViewModel.changePassword(it) },
                        label = { Text(text = stringResource(R.string.contrasena)) })
                }

                BotonSinIniciarSesion(
                    textButton = "Inicia Sesión",
                    textSubButtonNotClickable = "¿No tienes cuenta?",
                    textSubButtonClickable = "Registrate",
                    property1 = Property1.Smal,
                    onClickButton = { loginViewModel.login { navController.navigate(Routes.Inicio.route) } },
                    onClickSubButton = { navController.navigate(Routes.Registro.route) }
                )
                if (loginViewModel.showAlert) {
                    Alert(title = "Alerta",
                        message = "Usuario y/o contrasena incorrectos",
                        confirmText = "Aceptar",
                        onConfirmClick = { loginViewModel.closeAlert() },
                        onDismissClick = { } ) // DCS - ninguna acción en onDismissClick para que no oculte el diálogo
                }
            }
        },
        bottomBar = {
        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    label: (@Composable() (() -> Unit))

) {
    TextField(
        label = label,
        value = value,
        onValueChange = onValueChange, shape = RoundedCornerShape(25.dp),
        colors = TextFieldDefaults.textFieldColors(
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            textColor = PurpleGrey40,
            containerColor = Color.White,
            focusedLabelColor = PurpleGrey40,
            cursorColor = PurpleGrey40
        ),
        textStyle = TextStyle(fontFamily = poppins),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation
    )
}