package com.ccormor392.pruebaproyectofinal.presentation.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ccormor392.pruebaproyectofinal.ui.theme.PurpleGrey40

/**
 * Composable que define el contenido del scaffold con un fondo gris púrpura y padding opcional.
 *
 * @param padding Espacio de relleno en la parte superior del contenido.
 * @param content El contenido que se va a mostrar dentro del scaffold.
 */
@Composable
fun MyScaffoldContent(padding: Int = 100, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .background(PurpleGrey40)
            .padding(top = padding.dp)
            .wrapContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        content()
    }
}
