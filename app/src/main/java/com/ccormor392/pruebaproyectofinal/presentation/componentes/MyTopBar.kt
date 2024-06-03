package com.ccormor392.pruebaproyectofinal.presentation.componentes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ccormor392.pruebaproyectofinal.R
import com.ccormor392.pruebaproyectofinal.logoapp.LogoApp

/**
 * Barra superior usada en toda la aplicacion, contiene el logo y el nombre de la aplicacion.
 */
@Composable
fun MyTopBar(){
    Column() {
        LogoApp(
            textNombreApp = stringResource(R.string.nombreApp),
            modifier = Modifier.padding(start = 24.dp, top = 24.dp)
        )
        Divider(color = Color.White , modifier = Modifier.padding(top = 22.dp) , thickness = 0.5.dp)
    }
}
