package com.ccormor392.pruebaproyectofinal.presentation.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ccormor392.pruebaproyectofinal.ui.theme.maincolor

/**
 * Composable que muestra un botón personalizado para el perfil.
 *
 * @param onClickButton Acción a realizar cuando se hace clic en el botón.
 * @param texto Texto que se muestra dentro del botón.
 */
@Composable
fun MiButtonPerfil(
    onClickButton: () -> Unit,
    texto: String
) {
    // Contenedor con fondo redondeado y color de fondo principal, con clic habilitado
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(29.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(maincolor)
            .clickable { onClickButton() },
        contentAlignment = Alignment.Center
    ) {
        // Texto dentro del botón, con fuente en negrita y tamaño específico
        MiTexto(
            string = texto,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}
