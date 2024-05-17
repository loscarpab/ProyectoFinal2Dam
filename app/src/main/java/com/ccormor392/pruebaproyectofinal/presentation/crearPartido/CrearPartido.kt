package com.ccormor392.pruebaproyectofinal.presentation.crearPartido

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.launch
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
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
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TimePickerState
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
import com.ccormor392.pruebaproyectofinal.presentation.componentes.PickImageFromGallery
import com.ccormor392.pruebaproyectofinal.presentation.componentes.TimePickerDialogs
import com.ccormor392.pruebaproyectofinal.textotopscreenlogs.TextoTopScreenLogs
import com.ccormor392.pruebaproyectofinal.ui.theme.PurpleGrey40
import com.ccormor392.pruebaproyectofinal.ui.theme.maincolor
import com.ccormor392.pruebaproyectofinal.ui.theme.xdark
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Date

/**
 * Función componible para crear un nuevo partido.
 *
 * @param partidoViewModel ViewModel para gestionar el proceso de creación del partido.
 * @param navController Controlador de navegación para navegar entre los composables.
 */
@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "SimpleDateFormat")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearPartido(partidoViewModel: CreateMatchViewModel, navController: NavHostController) {
    // Estado de visibilidad del selector de hora
    val showTimePicker = partidoViewModel.showTimePicker.collectAsState()
    // Estado de visibilidad del selector de fecha
    val showDatePicker = partidoViewModel.showDatePicker.collectAsState()
    // Obtiene la fecha y hora actual
    val dateTime = LocalDateTime.now()
    // Estado del selector de hora
    val timePickerState = rememberTimePickerState(dateTime.hour, dateTime.minute, true)
    // Estado del selector de fecha
    val datePickerState = rememberDatePickerState()
    // Convierte la fecha seleccionada a timestamp
    val timestamp = java.sql.Timestamp((datePickerState.selectedDateMillis ?: System.currentTimeMillis()))
    // Convierte el timestamp a objeto Date
    val date = Date(timestamp.time)
    // Formatea la fecha
    val format = SimpleDateFormat("dd/MM/yy")
    val formattedDate = format.format(date)
    // Lanzador para seleccionar una imagen de la galería
    val galleryLauncher = rememberLauncherForActivityResult(PickImageFromGallery()) { imageUri ->
        imageUri?.let { partidoViewModel.uploadImageToStorage(it) }
    }

    // Efecto lanzado cuando se crea el composable
    LaunchedEffect(Unit) {
        // Consulta el número de partidos del usuario autenticado
        partidoViewModel.numeroPartidosUsuarioAutenticado()
    }

    Scaffold(
        // Barra superior
        topBar = { MyTopBar() },
        // Contenido principal
        content = {
            MainContent(
                partidoViewModel = partidoViewModel,
                showTimePicker = showTimePicker.value,
                showDatePicker = showDatePicker.value,
                timePickerState = timePickerState,
                datePickerState = datePickerState,
                formattedDate = formattedDate,
                galleryLauncher = galleryLauncher,
                navController = navController
            )
        },
        // Barra inferior
        bottomBar = { MyBottomBar(navHostController = navController) }
    )
}

/**
 * Contenido principal de la pantalla para crear un partido.
 *
 * @param partidoViewModel ViewModel para gestionar el proceso de creación del partido.
 * @param showTimePicker Estado de visibilidad del selector de hora.
 * @param showDatePicker Estado de visibilidad del selector de fecha.
 * @param timePickerState Estado del selector de hora.
 * @param datePickerState Estado del selector de fecha.
 * @param formattedDate Fecha formateada en cadena.
 * @param galleryLauncher Lanzador de actividad para la galería.
 * @param navController Controlador de navegación para navegar entre los composables.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(
    partidoViewModel: CreateMatchViewModel,
    showTimePicker: Boolean,
    showDatePicker: Boolean,
    timePickerState: TimePickerState,
    datePickerState: DatePickerState,
    formattedDate: String,
    galleryLauncher: ManagedActivityResultLauncher<Unit, Uri?>,
    navController: NavHostController
) {
    // Columna principal que contiene todos los elementos
    Column(
        modifier = Modifier
            .fillMaxSize() // Ocupa todo el tamaño disponible
            .padding(top = 78.dp) // Padding superior
            .background(PurpleGrey40), // Fondo
        horizontalAlignment = Alignment.CenterHorizontally // Alineación horizontal centrada
    ) {
        // Componente del selector de hora
        TimePickerComponent(partidoViewModel, showTimePicker, timePickerState, datePickerState)
        // Componente del selector de fecha
        DatePickerComponent(partidoViewModel, showDatePicker, datePickerState, formattedDate, timePickerState)

        // Componentes de texto para el título y el subtítulo
        TextoTopScreenLogs(
            textTitulo = stringResource(R.string.crear_partido), // Título
            textSubtitulo = stringResource(R.string.sub_crear_partido), // Subtítulo
            modifier = Modifier
                .fillMaxWidth() // Ocupa todo el ancho disponible
                .padding(top = 24.dp, bottom = 32.dp) // Padding superior e inferior
        )

        // Fila para los campos de entrada de los detalles del partido
        InputFields(partidoViewModel, galleryLauncher)

        // Botón para crear un nuevo partido
        BotonMas(
            textButton = "Crea un partido",
            onClickButton = {
                partidoViewModel.crearPartido { navController.navigate(Routes.Inicio.route) }
            },
            modifier = Modifier.padding(top = 32.dp) // Padding superior
        )

        // Muestra un diálogo de alerta si showAlert es verdadero
        AnimatedVisibility(partidoViewModel.showAlert, enter = expandHorizontally { it }) {
            Alert(
                title = stringResource(R.string.alerta),
                message = stringResource(R.string.alert_crear_partido),
                confirmText = stringResource(R.string.aceptar),
                onConfirmClick = { partidoViewModel.closeAlert() },
                onDismissClick = { }
            )
        }
    }
}

/**
 * Componente del selector de hora.
 *
 * @param partidoViewModel ViewModel para gestionar el proceso de creación del partido.
 * @param showTimePicker Estado de visibilidad del selector de hora.
 * @param timePickerState Estado del selector de hora.
 * @param datePickerState Estado del selector de fecha.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerComponent(
    partidoViewModel: CreateMatchViewModel,
    showTimePicker: Boolean,
    timePickerState: TimePickerState,
    datePickerState: DatePickerState
) {
    if (showTimePicker) {
        TimePickerDialogs(
            timePickerState = timePickerState,
            onDismissRequest = { partidoViewModel.changeHoraPicker() }, // Acción al cerrar el selector
            confirmButton = {
                Text(
                    text = "Confirmar",
                    modifier = Modifier.clickable {
                        partidoViewModel.changeHora(timePickerState.hour, timePickerState.minute)
                        partidoViewModel.changeTimestamp(
                            combinarFechaYHora(
                                datePickerState.selectedDateMillis ?: System.currentTimeMillis(),
                                timePickerState.hour,
                                timePickerState.minute
                            )
                        )
                        partidoViewModel.changeHoraPicker()
                    }
                )
            }
        )
    }
}

/**
 * Componente del selector de fecha.
 *
 * @param partidoViewModel ViewModel para gestionar el proceso de creación del partido.
 * @param showDatePicker Estado de visibilidad del selector de fecha.
 * @param datePickerState Estado del selector de fecha.
 * @param formattedDate Fecha formateada en cadena.
 * @param timePickerState Estado del selector de hora.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerComponent(
    partidoViewModel: CreateMatchViewModel,
    showDatePicker: Boolean,
    datePickerState: DatePickerState,
    formattedDate: String,
    timePickerState: TimePickerState
) {
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { partidoViewModel.changeDatePicker() }, // Acción al cerrar el selector
            confirmButton = {
                Button(onClick = {
                    partidoViewModel.changeFecha(formattedDate)
                    partidoViewModel.changeTimestamp(
                        combinarFechaYHora(
                            datePickerState.selectedDateMillis ?: System.currentTimeMillis(),
                            timePickerState.hour,
                            timePickerState.minute
                        )
                    )
                    partidoViewModel.changeDatePicker()
                }) {
                    Text(text = "Confirmar")
                }
            },
            modifier = Modifier.padding(15.dp) // Padding
        ) {
            DatePicker(state = datePickerState, colors = colorsDatePickerDialog()) // Selector de fecha
        }
    }
}

/**
 * Campos de entrada para los detalles del partido.
 *
 * @param partidoViewModel ViewModel para gestionar el proceso de creación del partido.
 * @param galleryLauncher Lanzador de actividad para la galería.
 */
@Composable
fun InputFields(partidoViewModel: CreateMatchViewModel, galleryLauncher: ManagedActivityResultLauncher<Unit, Uri?>) {
    Row(
        modifier = Modifier
            .height(300.dp) // Altura de la fila
            .fillMaxWidth(), // Ancho completo
        horizontalArrangement = Arrangement.Center // Arreglo horizontal centrado
    ) {
        Column(
            modifier = Modifier.fillMaxSize(), // Ocupa todo el tamaño disponible
            verticalArrangement = Arrangement.SpaceEvenly, // Espacio uniforme entre los elementos
            horizontalAlignment = Alignment.CenterHorizontally // Alineación horizontal centrada
        ) {
            // Campo de texto para el nombre del sitio
            MyTextField(
                value = partidoViewModel.nombreSitio,
                onValueChange = { partidoViewModel.changeLugar(it) },
                string = stringResource(R.string.lugar)
            )
            // Campo de texto para la fecha
            MyTextField(
                value = partidoViewModel.fecha,
                onValueChange = { },
                string = stringResource(R.string.fecha),
                onClickDateIcon = { partidoViewModel.changeDatePicker() }, // Acción al hacer clic en el icono de fecha
                iconName = "fecha",
                enabled = false // Deshabilitado para edición manual
            )
            // Campo de texto para la hora
            MyTextField(
                value = partidoViewModel.hora,
                onValueChange = { },
                string = stringResource(R.string.hora),
                onClickClockIcon = { partidoViewModel.changeHoraPicker() }, // Acción al hacer clic en el icono de hora
                iconName = "hora",
                enabled = false // Deshabilitado para edición manual
            )
            // Botón para seleccionar una imagen de la galería
            Button(onClick = { galleryLauncher.launch() }) {
                Text(text = "Seleccionar imagen")
            }
        }
    }
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

fun combinarFechaYHora(fechaMillis: Long, hora: Int, minutos: Int): Date {
    // Convertir la fecha de Long a Date
    val fecha = Date(fechaMillis)

    // Obtener el calendario y establecer la fecha
    val calendario = Calendar.getInstance()
    calendario.time = fecha

    // Establecer la hora y los minutos
    calendario.set(Calendar.HOUR_OF_DAY, hora)
    calendario.set(Calendar.MINUTE, minutos)

    // Devolver la fecha combinada
    return calendario.time
}
