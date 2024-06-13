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
    val galleryLauncher =
        rememberLauncherForActivityResult(PickImageFromGallery()) { imageUri ->
            imageUri?.let {
                sitiosViewModel.changeImageUri(imageUri)
            }
        }
    val permissionState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    )
    val mark = LatLng(40.420730042432076, -3.6901972115238824)
    var markState = rememberMarkerState(position = mark)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(mark, 10f)
    }
    var location by remember { mutableStateOf<Location?>(null) }
    LaunchedEffect(Unit) {
        sitiosViewModel.randomId()
        sitiosViewModel.restartFields()
    }
    // Use LaunchedEffect to handle permissions logic when the composition is launched.
    LaunchedEffect(key1 = permissionState) {
        // Check if all previously granted permissions are revoked.
        val allPermissionsRevoked =
            permissionState.permissions.size == permissionState.revokedPermissions.size

        // Filter permissions that need to be requested.
        val permissionsToRequest = permissionState.permissions.filter {
            !it.status.isGranted
        }

        // If there are permissions to request, launch the permission request.
        if (permissionsToRequest.isNotEmpty()) permissionState.launchMultiplePermissionRequest()

        // Execute callbacks based on permission status.
        if (allPermissionsRevoked) {
            Toast.makeText(
                sitiosViewModel.context,
                "Has rechazado los permisos",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            if (permissionState.allPermissionsGranted) {
                val fusedLocationProviderClient =
                    LocationServices.getFusedLocationProviderClient(sitiosViewModel.context)
                // Determine the accuracy priority based on the 'priority' parameter
                val accuracy = Priority.PRIORITY_HIGH_ACCURACY
                // Retrieve the current location asynchronously
                fusedLocationProviderClient.getCurrentLocation(
                    accuracy, CancellationTokenSource().token,
                ).addOnSuccessListener { loc ->
                    location = loc
                    loc?.let {
                        cameraPositionState.position =
                            CameraPosition.fromLatLngZoom(LatLng(it.latitude, it.longitude), 14f)
                        markState.position = LatLng(it.latitude, it.longitude)
                    }
                }.addOnFailureListener { exception ->
                    // If an error occurs, invoke the failure callback with the exception
                    Toast.makeText(
                        sitiosViewModel.context,
                        "No se ha podido conseguir la ubicación",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
            }
        }
    }
    Scaffold(
        // Barra superior
        topBar = { MyTopBar() },
        // Contenido principal
        content = {
            LazyColumn(Modifier.padding(top = 80.dp, bottom = 100.dp)) {
                item {
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
                    // Componentes de texto para el título y el subtítulo

                }
                item {
                    Column(horizontalAlignment = Alignment.Start) {
                        MiTexto(
                            string = "Envianos un sitio",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(top = 8.dp, start = 16.dp)
                        )
                        MiTexto(
                            string = "Rellena los campos para enviar una peticion para añadir un nuevo sitio",
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
                    Column(Modifier.padding(top = 16.dp)) {
                        MiTexto(
                            string = "Seleccionar ubicación",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
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
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        // Botón para crear un nuevo partido
                        BotonMas(
                            textButton = "Crea una peticion", onClickButton = {
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
                            }, modifier = Modifier.padding(top = 32.dp) // Padding superior
                        )
                    }
                }
                item {
                    if (sitiosViewModel.showAlert) {
                        Alert(title = stringResource(R.string.alerta),
                            message = stringResource(R.string.alert_crear_partido),
                            confirmText = stringResource(R.string.aceptar),
                            onConfirmClick = { sitiosViewModel.closeAlert() },
                            onDismissClick = { })
                    }
                }
                item {
                    if (sitiosViewModel.showLoading) {
                        Dialog(onDismissRequest = { }) {
                            CircularProgressIndicator()
                        }
                    }

                }
            }

        },
        // Barra inferior
        bottomBar = { MyBottomBar(navHostController = navController) })
}