package com.ccormor392.pruebaproyectofinal.presentation.misPartidos

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.ccormor392.pruebaproyectofinal.R
import com.ccormor392.pruebaproyectofinal.botonmas.BotonMas
import com.ccormor392.pruebaproyectofinal.navigation.Routes
import com.ccormor392.pruebaproyectofinal.presentation.componentes.Alert
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MiTexto
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyBottomBar
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyTextField
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyTopBar
import com.ccormor392.pruebaproyectofinal.presentation.componentes.PickImageFromGallery
import com.ccormor392.pruebaproyectofinal.presentation.manejoDeUsuarios.LoginViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "MissingPermission")
@Composable
fun Peticion(
    navController: NavHostController,
    sitiosViewModel: SitiosViewModel,
    loginViewModel: LoginViewModel
) {
    // Launcher para seleccionar imagen de la galería
    val galleryLauncher =
        rememberLauncherForActivityResult(PickImageFromGallery()) { imageUri ->
            imageUri?.let {
                sitiosViewModel.changeImageUri(imageUri)
            }
        }

    // Estado de los permisos de ubicación
    val permissionState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    )

    // Marcador inicial en Madrid
    val mark = LatLng(40.420730042432076, -3.6901972115238824)
    val markState = rememberMarkerState(position = mark)

    // Estado de la posición de la cámara en el mapa
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(mark, 10f)
    }

    // Estado de la ubicación actual del usuario
    var location by remember { mutableStateOf<Location?>(null) }

    LaunchedEffect(Unit) {
        // Iniciar la lógica cuando se lanza la composición
        sitiosViewModel.randomId()
        sitiosViewModel.restartFields()
    }

    LaunchedEffect(key1 = permissionState) {
        // Manejar lógica de permisos cuando se lanza la composición
        val allPermissionsRevoked =
            permissionState.permissions.size == permissionState.revokedPermissions.size

        val permissionsToRequest = permissionState.permissions.filter {
            !it.status.isGranted
        }

        // Si se necesitan permisos, solicitarlos
        if (permissionsToRequest.isNotEmpty()) permissionState.launchMultiplePermissionRequest()

        // Manejar casos según el estado de los permisos
        if (allPermissionsRevoked) {
            Toast.makeText(
                sitiosViewModel.context,
                "Has rechazado los permisos",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            if (permissionState.allPermissionsGranted) {
                // Acceder al proveedor de ubicación para obtener la ubicación actual
                val fusedLocationProviderClient =
                    LocationServices.getFusedLocationProviderClient(sitiosViewModel.context)
                val accuracy = Priority.PRIORITY_HIGH_ACCURACY
                fusedLocationProviderClient.getCurrentLocation(
                    accuracy, CancellationTokenSource().token,
                ).addOnSuccessListener { loc ->
                    location = loc
                    loc?.let {
                        // Actualizar posición de cámara y marcador en el mapa
                        cameraPositionState.position =
                            CameraPosition.fromLatLngZoom(LatLng(it.latitude, it.longitude), 14f)
                        markState.position = LatLng(it.latitude, it.longitude)
                    }
                }.addOnFailureListener { _ ->
                    // Manejar errores al obtener la ubicación
                    Toast.makeText(
                        sitiosViewModel.context,
                        "No se ha podido conseguir la ubicación",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // Scaffold que contiene la estructura de la pantalla
    Scaffold(
        // Barra superior personalizada
        topBar = { MyTopBar() },
        // Contenido principal de la pantalla
        content = {
            LazyColumn(Modifier.padding(top = 80.dp, bottom = 100.dp)) {
                item {
                    // Box con imagen editable desde la galería
                    Box(
                        Modifier
                            .wrapContentSize()
                            .clickable { galleryLauncher.launch() },
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = sitiosViewModel.imageUri
                                ?: "https://firebasestorage.googleapis.com/v0/b/proyectofinal-f110d.appspot.com/o/images%2FSelectImagePartido.png?alt=media&token=d50d1619-99b3-4414-a068-2c54673d5c33",
                            contentDescription = "foto partido",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(224.dp)
                        )
                        Image(
                            painter = painterResource(id = R.drawable.icon_add_photo),
                            contentDescription = "icon add photo",
                            Modifier
                                .zIndex(2f)
                                .size(50.dp)
                        )
                    }
                }
                item {
                    // Título y descripción de la pantalla
                    Column(horizontalAlignment = Alignment.Start) {
                        MiTexto(
                            string = "Envianos un sitio",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(top = 8.dp, start = 16.dp)
                        )
                        MiTexto(
                            string = "Rellena los campos para enviar una petición para añadir un nuevo sitio",
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 8.dp, start = 16.dp)
                        )
                        HorizontalDivider(
                            thickness = 0.5.dp,
                            color = Color.White,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                }
                item {
                    // Campos de texto para nombre y tipo de sitio
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp, horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        MyTextField(
                            value = sitiosViewModel.namePeticion,
                            onValueChange = { sitiosViewModel.changename(it) },
                            string = "Nombre",
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                        MyTextField(
                            value = sitiosViewModel.nombreCompletoPeticion,
                            onValueChange = { sitiosViewModel.changeNameCompleto(it) },
                            string = "Nombre completo",
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        // Dropdown para seleccionar el tipo de sitio
                        ExposedDropdownMenuBox(
                            expanded = sitiosViewModel.expandedMenuTextField,
                            onExpandedChange = { sitiosViewModel.changeExpanded() }) {
                            MyTextField(
                                value = sitiosViewModel.tipo,
                                onValueChange = { sitiosViewModel.changeTipo(it) },
                                modifier = Modifier.menuAnchor(),
                                string = "Tipo",
                                desplegado = sitiosViewModel.expandedMenuTextField,
                                iconName = "desplegado"
                            )
                            ExposedDropdownMenu(
                                expanded = sitiosViewModel.expandedMenuTextField,
                                onDismissRequest = { sitiosViewModel.changeExpanded() },
                            ) {
                                sitiosViewModel.listaTipos.forEach { option ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                option,
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                        },
                                        onClick = { sitiosViewModel.changeTipo(option) },
                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                    )
                                }
                            }
                        }
                    }
                }
                item {
                    // Sección para seleccionar ubicación en el mapa
                    Column(Modifier.padding(top = 16.dp)) {
                        MiTexto(
                            string = "Seleccionar ubicación",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        // Mapa de Google para seleccionar la ubicación del sitio
                        GoogleMap(
                            modifier = Modifier
                                .height(260.dp)
                                .padding(vertical = 16.dp, horizontal = 20.dp)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(5.dp)),
                            cameraPositionState = cameraPositionState,
                            onMapClick = { latLng ->
                                sitiosViewModel.changeLocation(latLng)
                            }
                        ) {
                            // Marcador en la ubicación actual y marcador nuevo si se selecciona una ubicación
                            Marker(
                                state = markState,
                                title = "Tu ubicación",
                                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                            )
                            sitiosViewModel.newMarkerPosition?.let {
                                Marker(
                                    state = MarkerState(position = it),
                                    title = "Nuevo marcador"
                                )
                            }
                        }
                    }
                }
                item {
                    // Botón para enviar la petición de nuevo sitio
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        BotonMas(
                            textButton = "Crea una petición",
                            onClickButton = {
                                sitiosViewModel.crearPeticion(
                                    isAdmin = loginViewModel.isAdmin(),
                                    onSuccess = {
                                        navController.navigate(Routes.Sitios.route)
                                        Toast.makeText(
                                            sitiosViewModel.context,
                                            "Petición creada",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    })
                            },
                            modifier = Modifier.padding(top = 32.dp)
                        )
                    }
                }
                item {
                    // Alerta si se muestra un mensaje
                    if (sitiosViewModel.showAlert) {
                        Alert(
                            title = stringResource(R.string.alerta),
                            message = stringResource(R.string.alert_crear_partido),
                            confirmText = stringResource(R.string.aceptar),
                            onConfirmClick = { sitiosViewModel.closeAlert() },
                            onDismissClick = { }
                        )
                    }
                }
                item {
                    // Diálogo de carga mientras se procesa la petición
                    if (sitiosViewModel.showLoading) {
                        Dialog(onDismissRequest = { }) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        },
        // Barra inferior personalizada
        bottomBar = { MyBottomBar(navHostController = navController) }
    )
}
