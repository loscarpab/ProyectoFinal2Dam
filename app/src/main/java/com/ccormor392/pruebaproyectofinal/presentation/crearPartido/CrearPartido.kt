package com.ccormor392.pruebaproyectofinal.presentation.crearPartido

import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.ccormor392.pruebaproyectofinal.R
import com.ccormor392.pruebaproyectofinal.botonmas.BotonMas
import com.ccormor392.pruebaproyectofinal.logoapp.poppins
import com.ccormor392.pruebaproyectofinal.navigation.Routes
import com.ccormor392.pruebaproyectofinal.presentation.componentes.Alert
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MiTexto
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyBottomBar
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyTextField
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyTopBar
import com.ccormor392.pruebaproyectofinal.presentation.componentes.TimePickerDialogs
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
    val timestamp =
        java.sql.Timestamp((datePickerState.selectedDateMillis ?: System.currentTimeMillis()))
    // Convierte el timestamp a objeto Date
    val date = Date(timestamp.time)
    // Formatea la fecha
    val format = SimpleDateFormat("dd/MM/yy")
    val formattedDate = format.format(date)


    // Efecto lanzado cuando se crea el composable
    LaunchedEffect(Unit) {
        // Consulta el número de partidos del usuario autenticado
        partidoViewModel.restart()
        partidoViewModel.numeroPartidosUsuarioAutenticado()
        partidoViewModel.getAllSitios()
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
                navController = navController
            )
        },
        // Barra inferior
        bottomBar = { MyBottomBar(navHostController = navController) })
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
    navController: NavHostController
) {
    val foto by partidoViewModel.foto.collectAsState("https://firebasestorage.googleapis.com/v0/b/proyectofinal-f110d.appspot.com/o/images%2FSelectImagePartido.png?alt=media&token=d50d1619-99b3-4414-a068-2c54673d5c33")

    // Columna principal que contiene todos los elementos
    Column(
        modifier = Modifier
            .fillMaxSize() // Ocupa todo el tamaño disponible
            .padding(top = 79.dp)
        ,horizontalAlignment = Alignment.CenterHorizontally, // Alineación horizontal centrada,
    ) {
        // Componente del selector de hora
        TimePickerComponent(partidoViewModel, showTimePicker, timePickerState, datePickerState)
        // Componente del selector de fecha
        DatePickerComponent(
            partidoViewModel, showDatePicker, datePickerState, formattedDate, timePickerState
        )

        // Componentes de texto para el título y el subtítulo
        AsyncImage(
            model =foto ,
            contentDescription = "foto partido",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(224.dp)

        )
        Column(horizontalAlignment = Alignment.Start) {
            MiTexto(string = "Crear Partido", fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.padding(top = 8.dp, start = 16.dp))
            MiTexto(string = "Rellena los campos para crear tu partido", fontWeight = FontWeight.Medium, fontSize = 12.sp,modifier=Modifier.padding(top = 8.dp, start = 16.dp))
            HorizontalDivider(thickness = 0.5.dp, color = Color.White, modifier = Modifier.padding(top = 16.dp))
        }


        // Fila para los campos de entrada de los detalles del partido
        InputFields(partidoViewModel)

        // Botón para crear un nuevo partido
        BotonMas(
            textButton = "Crea un partido", onClickButton = {
                partidoViewModel.crearPartido {
                    navController.navigate(Routes.Inicio.route)
                    Toast.makeText(partidoViewModel.context, "Partido creado con éxito", Toast.LENGTH_SHORT).show()
                }
            }, modifier = Modifier.padding(top = 32.dp) // Padding superior
        )

        // Muestra un diálogo de alerta si showAlert es verdadero
        AnimatedVisibility(partidoViewModel.showAlert, enter = expandHorizontally { it }) {
            Alert(title = stringResource(R.string.alerta),
                message = stringResource(R.string.alert_crear_partido),
                confirmText = stringResource(R.string.aceptar),
                onConfirmClick = { partidoViewModel.closeAlert() },
                onDismissClick = { })
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
        TimePickerDialogs(timePickerState = timePickerState,
            onDismissRequest = { partidoViewModel.changeHoraPicker() }, // Acción al cerrar el selector
            confirmButton = {
                Button(onClick = {
                    partidoViewModel.changeHora(timePickerState.hour, timePickerState.minute)
                    partidoViewModel.changeTimestamp(
                        combinarFechaYHora(
                            datePickerState.selectedDateMillis ?: System.currentTimeMillis(),
                            timePickerState.hour,
                            timePickerState.minute
                        )
                    )
                    partidoViewModel.changeHoraPicker()
                }){
                    Text(text = "Confirmar", color = Color.White)
                }
            })
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
                    Text(text = "Confirmar", color = Color.White)
                }
            }, modifier = Modifier.padding(15.dp) // Padding,
            , colors = DatePickerDefaults.colors(containerColor = xdark)
        ) {
            DatePicker(
                state = datePickerState, colors = colorsDatePickerDialog()
            ) // Selector de fecha
        }
    }
}

/**
 * Campos de entrada para los detalles del partido.
 *
 * @param partidoViewModel ViewModel para gestionar el proceso de creación del partido.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputFields(
    partidoViewModel: CreateMatchViewModel) {
    //Observa el estado actual del texto de búsqueda ingresado por el usuario.
    val query by partidoViewModel.query.collectAsState()
    //Observa el estado de la barra de búsqueda para saber si está activa.
    val active by partidoViewModel.active.collectAsState()
    val sitios by partidoViewModel.sitios.collectAsState()
    Row(
        modifier = Modifier
            .height(210.dp) // Altura de la fila
            .fillMaxWidth(), // Ancho completo
        horizontalArrangement = Arrangement.Center // Arreglo horizontal centrado
    ) {
        Column(
            modifier = Modifier.fillMaxSize(), // Ocupa todo el tamaño disponible
            verticalArrangement = Arrangement.SpaceEvenly, // Espacio uniforme entre los elementos
            horizontalAlignment = Alignment.CenterHorizontally // Alineación horizontal centrada
        ) {
            // Campo de texto para el nombre del sitio
            SearchBar(
                query = query,
                modifier = Modifier
                    .padding(horizontal = 56.dp)
                    .clip(RoundedCornerShape(25.dp)),
                onQueryChange = { partidoViewModel.setQuery(it) },
                onSearch = { partidoViewModel.setActive(false) },
                active = active,
                placeholder = {
                    Text(
                        text = "Lugar",
                        fontFamily = poppins,
                        fontSize = 14.sp,
                        color = xdark,
                        fontWeight = FontWeight.Medium
                    )
                },
                colors = SearchBarDefaults.colors(
                    containerColor = Color.White, dividerColor = xdark,
                    inputFieldColors = TextFieldDefaults.colors(
                        disabledTextColor = Color.DarkGray,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        cursorColor = xdark,
                        focusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedLabelColor = xdark,
                        disabledLabelColor = Color.DarkGray,
                        focusedTextColor = xdark,
                        focusedSupportingTextColor = xdark,
                        unfocusedTextColor = xdark,
                    ),
                ),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = "Location Icon",
                        tint = xdark
                    )
                },
                onActiveChange = { partidoViewModel.setActive(it) }
            ) {
                if (query.isNotEmpty()) {
                    // Filtra los sitios que coinciden con el texto de búsqueda.
                    val filterSitio = sitios.filter { it.nombre.contains(query, ignoreCase = true) }
                    // Muestra cada sitio filtrado como un texto clickeable.
                    filterSitio.forEach {
                        Row (Modifier.padding(vertical = 8.dp, horizontal = 16.dp).fillMaxWidth().clickable {
                            partidoViewModel.setQuery(it.nombre)
                            partidoViewModel.changeLugar(it)
                            partidoViewModel.setActive(false)
                            partidoViewModel.setFoto(it.foto)
                        }){
                            Text(
                                text = it.nombre,
                                fontSize = 16.sp
                            )
                        }

                    }
                }
            }
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
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun colorsDatePickerDialog(): DatePickerColors {
    return DatePickerDefaults.colors(
        containerColor = xdark,
        titleContentColor = Color.White,
        headlineContentColor = Color.White,
        weekdayContentColor = Color.White,
        subheadContentColor = Color.White,
        yearContentColor = Color.White,
        currentYearContentColor = Color.White,
        selectedYearContentColor = Color.White,
        selectedYearContainerColor = maincolor,
        dayContentColor = Color.White,
        disabledDayContentColor = maincolor.copy(alpha = 0.38f),
        selectedDayContentColor = Color.White,
        disabledSelectedDayContentColor = maincolor.copy(alpha = 0.38f),
        selectedDayContainerColor = maincolor,
        disabledSelectedDayContainerColor = maincolor.copy(alpha = 0.38f),
        todayContentColor = Color.White,
        todayDateBorderColor = maincolor,
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
