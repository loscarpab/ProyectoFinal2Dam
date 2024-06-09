package com.ccormor392.pruebaproyectofinal.presentation.misPartidos

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MiTexto
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyBottomBar
import com.ccormor392.pruebaproyectofinal.presentation.componentes.MyTopBar
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Sitio(
    sitiosViewModel: SitiosViewModel,
    navHostController: NavHostController
) {
    val selectedSitio by sitiosViewModel.selectedSitio.collectAsState()
    // Componentes de texto para el título y el subtítulo
    val mark = LatLng(selectedSitio.ubicacion.latitude, selectedSitio.ubicacion.longitude)
    val markState = MarkerState(position = mark)
    val cameraPositionState = rememberCameraPositionState{
        position = CameraPosition.fromLatLngZoom(mark, 15f)
    }
    val tipo = when(selectedSitio.tipo){
        "fut7" -> "Futbol 7"
        "futsal" -> "Futbol Sala"
        else -> "Futbol"
    }
    Scaffold(
        // Barra superior
        topBar = { MyTopBar() },
        content = {
            Column(Modifier.fillMaxSize().padding(top = 79.dp)) {
                AsyncImage(
                    model = selectedSitio.foto ,
                    contentDescription = "foto sitio",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(224.dp)

                )
                Column(horizontalAlignment = Alignment.Start) {
                    MiTexto(string = selectedSitio.nombreLargo, fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.padding(top = 8.dp, start = 16.dp))
                    MiTexto(string = tipo, fontWeight = FontWeight.Medium, fontSize = 12.sp,modifier= Modifier.padding(top = 8.dp, start = 16.dp))
                    HorizontalDivider(thickness = 0.5.dp, color = Color.White, modifier = Modifier.padding(top = 16.dp, bottom = 16.dp))
                }
                GoogleMap(
                    modifier = Modifier
                        .height(260.dp)
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    cameraPositionState = cameraPositionState
                ){
                    Marker(
                        state = markState,
                        title = selectedSitio.nombre
                    )
                }
            }

        },
        bottomBar = { MyBottomBar(navHostController = navHostController) })

}

