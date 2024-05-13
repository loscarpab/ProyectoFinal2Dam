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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerState
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialogs(
    title: String = "Elegir Hora",
    onDismissRequest: () -> Unit,
    confirmButton: @Composable (() -> Unit),
    dismissButton: @Composable (() -> Unit)? = null,
    containerColor: Color = Color(10, 17, 26),
    timePickerState: TimePickerState) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
    ) {
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
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = title,
                    color = Color.White
                )
                if (timePickerState != null){
                    TimePicker(timePickerState, colors = colorsTimePickerDialog())
                }



                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    dismissButton?.invoke()
                    confirmButton()
                }
            }
        }
    }
}
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun colorsTimePickerDialog():TimePickerColors{
    return TimePickerDefaults.colors(
        clockDialColor = xdark,
        selectorColor = maincolor,
        containerColor = xdark,
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
