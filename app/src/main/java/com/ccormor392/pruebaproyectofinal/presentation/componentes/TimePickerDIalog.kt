package com.ccormor392.pruebaproyectofinal.presentation.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerColors
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ccormor392.pruebaproyectofinal.ui.theme.maincolor
import com.ccormor392.pruebaproyectofinal.ui.theme.xdark

/**
 * Composable que muestra un diálogo con un TimePicker para elegir la hora.
 *
 * @param title Título del diálogo.
 * @param onDismissRequest Acción a realizar cuando se cierra el diálogo.
 * @param confirmButton Composable para el botón de confirmar.
 * @param dismissButton Composable opcional para el botón de descartar (cerrar el diálogo).
 * @param containerColor Color de fondo del diálogo.
 * @param timePickerState Estado del TimePicker.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialogs(
    title: String = "Elegir Hora",
    onDismissRequest: () -> Unit,
    confirmButton: @Composable (() -> Unit),
    dismissButton: @Composable (() -> Unit)? = null,
    containerColor: Color = Color(10, 17, 26),
    timePickerState: TimePickerState
) {
    // Diálogo modal
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
    ) {
        // Superficie con sombra y forma extra grande
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = containerColor
                ),
            color = containerColor
        ) {
            // Columna principal del diálogo
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Título del diálogo
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = title,
                    color = Color.White
                )
                // TimePicker para seleccionar la hora
                if (timePickerState != null) {
                    TimePicker(
                        timePickerState,
                        colors = colorsTimePickerDialog()
                    )
                }

                // Fila para los botones de acción (confirmar y descartar)
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    // Espaciador para alinear los botones a los extremos
                    Spacer(modifier = Modifier.weight(1f))
                    // Botón de descartar si está definido
                    dismissButton?.invoke()
                    // Botón de confirmar
                    confirmButton()
                }
            }
        }
    }
}

/**
 * Definición de colores personalizados para el TimePicker en el diálogo.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun colorsTimePickerDialog(): TimePickerColors {
    return TimePickerDefaults.colors(
        clockDialColor = Color.White,
        selectorColor = maincolor,
        containerColor = Color.White,
        periodSelectorBorderColor = Color.White,
        clockDialSelectedContentColor = Color.White,
        clockDialUnselectedContentColor = xdark,
        periodSelectorSelectedContainerColor = Color.White,
        periodSelectorUnselectedContainerColor = xdark,
        periodSelectorSelectedContentColor = maincolor,
        periodSelectorUnselectedContentColor = Color.White,
        timeSelectorSelectedContainerColor = maincolor,
        timeSelectorUnselectedContainerColor = xdark,
        timeSelectorSelectedContentColor = Color.White,
        timeSelectorUnselectedContentColor = Color.White
    )
}
