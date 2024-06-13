package com.ccormor392.pruebaproyectofinal.presentation.componentes

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign

/**
 * Composable para mostrar un diálogo de alerta personalizado.
 *
 * @param title Título del diálogo.
 * @param message Mensaje de cuerpo del diálogo.
 * @param confirmText Texto del botón de confirmación.
 * @param onConfirmClick Acción a realizar cuando se presiona el botón de confirmación.
 * @param onDismissClick Acción a realizar cuando se descarta el diálogo.
 */
@Composable
fun Alert(
    title: String,
    message: String,
    confirmText: String,
    onConfirmClick: () -> Unit,
    onDismissClick: () -> Unit
) {
    // Inicializa el estado de desplazamiento para el contenido del mensaje
    val scroll = rememberScrollState(0)

    // Configuración y estructura del diálogo de alerta
    AlertDialog(
        onDismissRequest = {
            // Acción a realizar cuando se solicita descartar el diálogo
            onDismissClick()
        },
        title = {
            // Texto del título del diálogo
            Text(text = title)
        },
        text = {
            // Texto del mensaje del diálogo con desplazamiento vertical habilitado
            Text(
                text = message,
                textAlign = TextAlign.Justify,
                modifier = Modifier.verticalScroll(scroll)
            )
        },
        confirmButton = {
            // Botón de confirmación del diálogo
            Button(onClick = {
                // Acción a realizar cuando se presiona el botón de confirmación
                onConfirmClick()
            }) {
                // Texto del botón de confirmación
                Text(text = confirmText, color = Color.White)
            }
        }
    )
}
