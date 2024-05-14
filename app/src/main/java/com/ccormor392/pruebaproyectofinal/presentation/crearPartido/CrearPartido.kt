package com.ccormor392.pruebaproyectofinal.presentation.crearPartido

import android.annotation.SuppressLint
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ccormor392.pruebaproyectofinal.R
import com.ccormor392.pruebaproyectofinal.botonmas.BotonMas
import com.ccormor392.pruebaproyectofinal.navigation.Routes
import com.ccormor392.pruebaproyectofinal.presentation.componentes.Alert
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyBottomBar
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyTextField
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyTopBar
import com.ccormor392.pruebaproyectofinal.presentation.componentes.TimePickerDialogs
import com.ccormor392.pruebaproyectofinal.textotopscreenlogs.TextoTopScreenLogs
import com.ccormor392.pruebaproyectofinal.ui.theme.PurpleGrey40
import com.ccormor392.pruebaproyectofinal.ui.theme.maincolor
import com.ccormor392.pruebaproyectofinal.ui.theme.xdark
import java.security.Timestamp
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

/**
 * Función componible para crear un nuevo partido.
 * Esta función permite a los usuarios crear un nuevo partido proporcionando los detalles necesarios.
 *
 * @param partidoViewModel ViewModel para gestionar el proceso de creación del partido.
 * @param navController Controlador de navegación para navegar entre los composables.
 */
@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearPartido(partidoViewModel: CreateMatchViewModel, navController: NavHostController) {
    val showTimePicker = partidoViewModel.showTimePicker.collectAsState()
    val showDatePicker = partidoViewModel.showDatePicker.collectAsState()
    val dateTime = LocalDateTime.now()
    val timePickerState = rememberTimePickerState(dateTime.hour, dateTime.minute, true)
    val datePickerState = rememberDatePickerState()
    val timestamp = java.sql.Timestamp(datePickerState.selectedDateMillis ?: System.currentTimeMillis())
    val date = Date(timestamp.time)
    val format = SimpleDateFormat("dd/MM/yy")
    val pepe = format.format(date)


    // Utiliza LaunchedEffect para ejecutar código cuando se lanza este composable
    LaunchedEffect(Unit) {
        partidoViewModel.numeroPartidosUsuarioAutenticado()
    }

    Scaffold(
        topBar = {
            MyTopBar()
        },
        content = {
            // Área de contenido principal
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 78.dp)
                    .background(PurpleGrey40),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // time picker component
                if (showTimePicker.value) {
                    TimePickerDialogs(
                        timePickerState = timePickerState,
                        onDismissRequest = { partidoViewModel.changeHoraPicker() },
                        confirmButton = {
                            Text(text = "Confirmar", modifier = Modifier.clickable {
                                partidoViewModel.changeHora(
                                    timePickerState.hour,
                                    timePickerState.minute
                                )
                                partidoViewModel.changeHoraPicker()
                            })

                        })
                }
                if (showDatePicker.value) {
                    DatePickerDialog(
                        onDismissRequest = { partidoViewModel.changeDatePicker() },
                        confirmButton = {
                            Button(onClick = {
                                partidoViewModel.changeFecha(pepe)
                                partidoViewModel.changeDatePicker()
                            }) {
                                Text(text = "Confirmar")
                            }
                        },
                        modifier = Modifier.padding(15.dp)
                    ) {
                        DatePicker(
                            state = datePickerState,
                            colors = colorsDatePickerDialog()
                        )
                    }
                }
                // Componentes de texto para título y subtítulo
                TextoTopScreenLogs(
                    textTitulo = stringResource(R.string.crear_partido),
                    textSubtitulo = stringResource(R.string.sub_crear_partido),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, bottom = 32.dp)
                )

                // Fila para contener los campos de entrada de los detalles del partido
                Row(
                    modifier = Modifier
                        .height(300.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Columna para organizar los campos de entrada verticalmente
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Campo de texto para ingresar la ubicación del partido
                        MyTextField(
                            value = partidoViewModel.nombreSitio,
                            onValueChange = { partidoViewModel.changeLugar(it) },
                            string = stringResource(R.string.lugar)
                        )
                        // Campo de texto para ingresar la fecha del partido
                        MyTextField(
                            value = partidoViewModel.fecha,
                            onValueChange = { },
                            string = stringResource(R.string.fecha),
                            onClickDateIcon = { partidoViewModel.changeDatePicker() },
                            iconName = "fecha",
                            enabled = false
                        )
                        MyTextField(
                            value = partidoViewModel.hora,
                            onValueChange = { },
                            string = stringResource(R.string.hora),
                            onClickClockIcon = { partidoViewModel.changeHoraPicker() },
                            iconName = "hora",
                            enabled = false
                        )

                        /*
                        // Campo de texto para ingresar la hora del partido
                        MyTextField(
                            value = partidoViewModel.hora,
                            onValueChange = { partidoViewModel.changeHora(it) },
                            string = stringResource(R.string.hora)
                        )

                         */

                    }
                }

                // Botón para crear un nuevo partido
                BotonMas(
                    textButton = "Crea un partido",
                    onClickButton = {
                        // Llama a la función para crear el partido y navegar a la pantalla de inicio en caso de éxito
                        partidoViewModel.crearPartido {
                            navController.navigate(Routes.Inicio.route)
                        }
                    },
                    modifier = Modifier.padding(top = 32.dp)
                )

                // Muestra un diálogo de alerta si showAlert es verdadero
                if (partidoViewModel.showAlert) {
                    Alert(
                        title = stringResource(R.string.alerta),
                        message = stringResource(R.string.alert_crear_partido),
                        confirmText = stringResource(R.string.aceptar),
                        onConfirmClick = { partidoViewModel.closeAlert() },
                        onDismissClick = { }
                    )
                }
            }
        }, bottomBar = {
            MyBottomBar(navHostController = navController)
        }
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun colorsDatePickerDialog(): DatePickerColors {
    return DatePickerDefaults.colors(
        containerColor = xdark,
        titleContentColor = maincolor,
        headlineContentColor = maincolor,
        weekdayContentColor = maincolor,
        subheadContentColor = maincolor,
        yearContentColor = maincolor,
        currentYearContentColor = maincolor,
        selectedYearContentColor = maincolor,
        selectedYearContainerColor = maincolor,
        dayContentColor = maincolor,
        disabledDayContentColor = maincolor.copy(alpha = 0.38f),
        selectedDayContentColor = maincolor,
        disabledSelectedDayContentColor = maincolor.copy(alpha = 0.38f),
        selectedDayContainerColor = maincolor,
        disabledSelectedDayContainerColor = maincolor.copy(alpha = 0.38f),
        todayContentColor = Color.White,
        todayDateBorderColor = Color.White,
        dayInSelectionRangeContentColor = maincolor,
        dayInSelectionRangeContainerColor = maincolor
    )
}
