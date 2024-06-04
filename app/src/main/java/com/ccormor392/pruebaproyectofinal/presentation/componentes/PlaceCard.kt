package com.ccormor392.pruebaproyectofinal.presentation.componentes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ccormor392.pruebaproyectofinal.cartapartido.poppins

@Composable
fun PlaceCard(
    onClick: () -> Unit,
    imagenSitio: String,
    nombreLugar: String,
    tipoPartido : String
) {
    val tipo = when(tipoPartido){
        "fut7" -> "Futbol 7"
        "futsal" -> "Futbol Sala"
        else -> "Futbol"
    }
    Card(
        Modifier
            .width(180.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(10, 17, 26))
    ) {

        AsyncImage(
            model = imagenSitio,
            contentDescription = "Imagen del sitio",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(116.dp)
                .padding(top = 8.dp, start = 8.dp, end = 8.dp)
                .clip(
                    RoundedCornerShape(12.dp)
                )
        )
        Text(
            text = nombreLugar,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp, start = 8.dp, end = 8.dp),
            textAlign = TextAlign.Center,
            fontFamily = poppins,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            maxLines = 1
        )
        Text(
            text = tipo,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp, start = 8.dp, end = 8.dp),
            textAlign = TextAlign.Center,
            fontFamily = poppins,
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            color = Color.White
        )
    }
}