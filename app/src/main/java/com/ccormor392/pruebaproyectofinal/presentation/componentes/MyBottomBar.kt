package com.ccormor392.pruebaproyectofinal.presentation.componentes

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.ccormor392.pruebaproyectofinal.bottombar.BottomBar
import com.ccormor392.pruebaproyectofinal.navigation.Routes

@Composable
fun MyBottomBar(navHostController: NavHostController) {
    BottomBar(
        modifier = Modifier.fillMaxWidth(),
        onClickUsuario = {navHostController.navigate(Routes.MiPerfil.route)  },
        onClickInicio ={navHostController.navigate(Routes.Inicio.route)},
        onClickExplorar = {navHostController.navigate(Routes.Sitios.route)},
        onClickAmigos = { navHostController.navigate(Routes.Amigos.route)}
    )
}

