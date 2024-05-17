package com.ccormor392.pruebaproyectofinal.presentation.manejoDeUsuarios.yaAutenticados


import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.ccormor392.pruebaproyectofinal.R
import com.ccormor392.pruebaproyectofinal.botonmas.BotonMas
import com.ccormor392.pruebaproyectofinal.botonmas.Property1
import com.ccormor392.pruebaproyectofinal.navigation.Routes
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MiTexto
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyBottomBar
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyTextField
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyTopBar
import com.ccormor392.pruebaproyectofinal.presentation.componentes.PickImageFromGallery
import com.ccormor392.pruebaproyectofinal.presentation.manejoDeUsuarios.LoginViewModel
import com.ccormor392.pruebaproyectofinal.ui.theme.PurpleGrey40
import com.ccormor392.pruebaproyectofinal.ui.theme.maincolor

/**
 * Composable que representa la pantalla de cierre de sesión de usuario.
 * Muestra los datos del usuario autenticado y proporciona la opción para cerrar sesión.
 *
 * @param navController Controlador de navegación para gestionar las transiciones entre pantallas.
 * @param loginViewModel ViewModel que gestiona la lógica de la pantalla de cierre de sesión.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "StateFlowValueCalledInComposition")
@Composable
fun EditarPerfil(navController: NavHostController, loginViewModel: LoginViewModel) {
    val galleryLauncher =
        rememberLauncherForActivityResult(PickImageFromGallery()) { imageUri ->
            imageUri?.let {
                loginViewModel.uploadImageToStorage(imageUri)
            }
        }
    // Efecto de lanzamiento para obtener los datos del usuario autenticado
    LaunchedEffect(Unit) {
        loginViewModel.conseguirDatosUsuarioAutenticado()
    }
    // Estructura del diseño de la pantalla de cierre de sesión
    Scaffold(
        topBar = {
            // Barra superior personalizada
            MyTopBar()
        },
        content = {
            // Contenido principal de la pantalla
            Column(
                Modifier
                    .padding(top = 78.dp)
                    .fillMaxSize() // Ocupa todo el espacio disponible
                    // Añade un espacio superior
                    .background(PurpleGrey40), // Fondo de color gris morado
                horizontalAlignment = Alignment.CenterHorizontally // Alineación horizontal al centro
            ) {
                // Título y subtítulo de la pantalla
                MiTexto(
                    string = "Editar Perfil",
                    modifier = Modifier.padding(top = 24.dp),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                // Contenido central de la pantalla
                Row(
                    modifier = Modifier
                        .height(360.dp) // Altura fija
                        .fillMaxWidth(), // Ancho completo
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically // Alineación horizontal al centro
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(), // Ocupa todo el espacio disponible
                        verticalArrangement = Arrangement.SpaceEvenly, // Espaciado vertical uniforme
                        horizontalAlignment = Alignment.CenterHorizontally // Alineación horizontal al centro
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 24.dp)) {
                            AsyncImage(
                                model = loginViewModel.imageUri,
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(4.dp)
                                    .height(120.dp)
                                    .width(120.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop,
                            )
                            IconButton(
                                onClick = { galleryLauncher.launch() },
                                Modifier.padding(start = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Edit,
                                    contentDescription = "Edit",
                                    tint = maincolor
                                )
                            }
                        }

                        // Campo de texto para el nombre de usuario (no editable)
                        MyTextField(
                            value = loginViewModel.usuarioAutenticado.value.username,
                            onValueChange = { },
                            string = stringResource(id = R.string.nombre_de_usuario),
                            enabled = false // No editable
                        )
                        // Campo de texto para el correo electrónico (no editable)
                        MyTextField(
                            value = loginViewModel.usuarioAutenticado.value.email,
                            onValueChange = { },
                            string = stringResource(id = R.string.email),
                            enabled = false // No editable
                        )

                    }
                }
                // Botón para cerrar sesión
                BotonMas(
                    textButton = stringResource(R.string.cerrar_sesion),
                    property1 = Property1.Variant3, // Propiedad de estilo del botón
                    onClickButton = {
                        loginViewModel.signOut {
                            navController.navigate(Routes.InicioSinRegistro.route) // Navegar a la pantalla de inicio sin registro
                        }
                    },
                    modifier = Modifier
                        .padding(top = 48.dp)
                        .width(200.dp) // Añade un espacio superior y establece el ancho del botón
                )
            }
        }, bottomBar = {
            MyBottomBar(navHostController = navController)
        }
    )
}





