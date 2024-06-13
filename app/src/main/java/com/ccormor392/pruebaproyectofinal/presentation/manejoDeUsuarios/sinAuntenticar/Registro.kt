package com.ccormor392.pruebaproyectofinal.presentation.manejoDeUsuarios.sinAuntenticar

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.ccormor392.pruebaproyectofinal.R
import com.ccormor392.pruebaproyectofinal.botonsininiciarsesion.BotonSinIniciarSesion
import com.ccormor392.pruebaproyectofinal.botonsininiciarsesion.Tipo
import com.ccormor392.pruebaproyectofinal.logoapp.LogoApp
import com.ccormor392.pruebaproyectofinal.navigation.Routes
import com.ccormor392.pruebaproyectofinal.presentation.componentes.Alert
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyTextField
import com.ccormor392.pruebaproyectofinal.presentation.manejoDeUsuarios.LoginViewModel
import com.ccormor392.pruebaproyectofinal.textotopscreenlogs.TextoTopScreenLogs
import com.ccormor392.pruebaproyectofinal.ui.theme.PurpleGrey40

/**
 * Composable que representa la pantalla de registro de usuario.
 * Permite al usuario completar un formulario con sus datos para crear una cuenta.
 *
 * @param navController Controlador de navegación para gestionar las transiciones entre pantallas.
 * @param loginViewModel ViewModel que gestiona la lógica de la pantalla de registro.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Registro(
    navController: NavHostController,
    loginViewModel: LoginViewModel
) {
    LaunchedEffect(Unit){
        loginViewModel.showLoadingtoFalse()
    }
    // Estructura del diseño de la pantalla de registro
    Scaffold(
        topBar = {
            // Barra superior con el logo de la aplicación
            LogoApp(
                textNombreApp = stringResource(id = R.string.nombreApp),
                modifier = Modifier.padding(start = 32.dp, top = 40.dp)
            )
        },
        content = {
            // Contenido principal de la pantalla de registro
            Column(
                Modifier
                    .background(PurpleGrey40) // Fondo de color gris morado
                    .fillMaxSize() // Ocupa todo el espacio disponible
                    .padding(top = 100.dp), // Añade un espacio superior
                horizontalAlignment = Alignment.CenterHorizontally, // Alineación horizontal al centro
                verticalArrangement = Arrangement.SpaceEvenly, // Espaciado vertical uniforme
            ) {
                // Título y subtítulo de la pantalla de registro
                TextoTopScreenLogs(
                    textTitulo = stringResource(id = R.string.registrate),
                    textSubtitulo = stringResource(R.string.sub_registrate)
                )
                // Columna con los campos de texto y el botón de registro
                Column(
                    modifier = Modifier.height(240.dp), // Altura fija de la columna
                    verticalArrangement = Arrangement.SpaceBetween // Espaciado vertical uniforme
                ) {
                    // Campo de texto para el nombre de usuario
                    MyTextField(
                        value = loginViewModel.userName,
                        onValueChange = { loginViewModel.changeUserName(it) },
                        string = stringResource(R.string.nombre_de_usuario)
                    )
                    // Campo de texto para el correo electrónico
                    MyTextField(
                        value = loginViewModel.email,
                        onValueChange = { loginViewModel.changeEmail(it) },
                        keyboardType = KeyboardType.Email, // Teclado específico para email
                        string = stringResource(R.string.email)
                    )
                    // Campo de texto para la contraseña
                    MyTextField(
                        value = loginViewModel.password,
                        onValueChange = { loginViewModel.changePassword(it) },
                        keyboardType = KeyboardType.Password, // Teclado de tipo contraseña
                        visualTransformation = PasswordVisualTransformation(), // Transformación visual para contraseñas
                        string = stringResource(R.string.contrasena)
                    )
                }
                // Botón para realizar el registro de usuario
                BotonSinIniciarSesion(
                    textBoton = stringResource(id = R.string.registrate),
                    textSubButtonNotClickable = stringResource(id = R.string.ya_tienes_cuenta),
                    textSubButtonClickable = stringResource(id = R.string.inicia_sesion),
                    tipo = Tipo.Small,
                    onClickButton = {
                        loginViewModel.createUser { navController.navigate(Routes.Inicio.route) }
                    },
                    onClickSubButton = { navController.navigate(Routes.InicioSesion.route) }
                )
                // Mostrar un diálogo de alerta en caso de error
                if (loginViewModel.showAlert) {
                    Alert(
                        title = stringResource(id = R.string.alerta),
                        message = stringResource(R.string.alerta_user_created),
                        confirmText = stringResource(id = R.string.aceptar),
                        onConfirmClick = { loginViewModel.closeAlert() },
                        onDismissClick = { }
                    ) // Ninguna acción en onDismissClick para que no oculte el diálogo
                }
                if (loginViewModel.showLoading){
                    Dialog(onDismissRequest = { }) {
                        CircularProgressIndicator()
                    }
                }
            }
        },
        bottomBar = {
            // Barra inferior vacía
        }
    )
}
