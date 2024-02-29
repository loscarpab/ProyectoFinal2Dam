package com.ccormor392.pruebaproyectofinal.presentation.manejoDeUsuarios.sinAuntenticar

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
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
import com.ccormor392.pruebaproyectofinal.botonsininiciarsesion.Tipo
import com.ccormor392.pruebaproyectofinal.logoapp.LogoApp
import com.ccormor392.pruebaproyectofinal.navigation.Routes
import com.ccormor392.pruebaproyectofinal.presentation.componentes.Alert
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyTextField
import com.ccormor392.pruebaproyectofinal.presentation.manejoDeUsuarios.LoginViewModel
import com.ccormor392.pruebaproyectofinal.textotopscreenlogs.TextoTopScreenLogs
import com.ccormor392.pruebaproyectofinal.ui.theme.PurpleGrey40
/**
 * Composable que representa la pantalla de inicio de sesión cuando el usuario no está autenticado.
 * Permite al usuario iniciar sesión en su cuenta proporcionando su correo electrónico y contraseña.
 *
 * @param navController Controlador de navegación para gestionar las transiciones entre pantallas.
 * @param loginViewModel ViewModel que contiene la lógica de la pantalla de inicio de sesión.
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun InicioSesion(
    navController: NavHostController, // Controlador de navegación
    loginViewModel: LoginViewModel // ViewModel para la pantalla de inicio de sesión
) {
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
                Modifier
                    .background(PurpleGrey40)
                    .fillMaxSize()
                    .padding(top = 100.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly,
            ) {
                // Título y subtítulo de la pantalla
                TextoTopScreenLogs(
                    textTitulo = stringResource(id = R.string.inicia_sesion),
                    textSubtitulo = stringResource(R.string.sub_iniciar_sesion)
                )
                // Columna que contiene campos de correo electrónico y contraseña
                Column(
                    modifier = Modifier.height(140.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Campo de texto para el correo electrónico
                    MyTextField(
                        value = loginViewModel.email,
                        onValueChange = { loginViewModel.changeEmail(it) },
                        keyboardType = KeyboardType.Email,
                        string = stringResource(R.string.email)
                    )
                    // Campo de texto para la contraseña
                    MyTextField(
                        value = loginViewModel.password,
                        keyboardType = KeyboardType.Password,
                        visualTransformation = PasswordVisualTransformation(),
                        onValueChange = { loginViewModel.changePassword(it) },
                        string = stringResource(R.string.contrasena)
                    )
                }

                // Botón para iniciar sesión
                BotonSinIniciarSesion(
                    textBoton = stringResource(id = R.string.inicia_sesion),
                    textSubButtonNotClickable = stringResource(R.string.no_tienes_cuenta),
                    textSubButtonClickable = stringResource(id = R.string.registrate),
                    tipo = Tipo.Small,
                    // Acción al hacer clic en el botón de inicio de sesión
                    onClickButton = {
                        loginViewModel.login { navController.navigate(Routes.Inicio.route) }
                    },
                    // Acción al hacer clic en el botón de registro
                    onClickSubButton = { navController.navigate(Routes.Registro.route) }
                )

                // Mostrar alerta en caso de usuario/contraseña incorrectos
                if (loginViewModel.showAlert) {
                    Alert(title = stringResource(id = R.string.alerta),
                        message = stringResource(R.string.alerta_user),
                        confirmText = stringResource(id = R.string.aceptar),
                        onConfirmClick = { loginViewModel.closeAlert() },
                        onDismissClick = { }) // No realizar ninguna acción en onDismissClick para no ocultar el diálogo
                }
            }
        },
        bottomBar = {
            // No se muestra ningún elemento en la barra inferior en esta pantalla
        }
    )
}
