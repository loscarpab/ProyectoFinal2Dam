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

/**
 * Composable que muestra una tarjeta de lugar con imagen, nombre y tipo de partido.
 *
 * @param onClick Acción a realizar cuando se hace clic en la tarjeta.
 * @param imagenSitio URL de la imagen del lugar.
 * @param nombreLugar Nombre del lugar.
 * @param tipoPartido Tipo de partido ("fut7", "futsal", u otro).
 */
@Composable
fun PlaceCard(
    onClick: () -> Unit,
    imagenSitio: String,
    nombreLugar: String,
    tipoPartido: String
) {
    // Convierte el tipo de partido a un nombre más legible
    val tipo = when (tipoPartido) {
        "fut7" -> "Futbol 7"
        "futsal" -> "Futbol Sala"
        else -> "Futbol"
    }

    // Tarjeta que muestra la imagen del lugar, el nombre y el tipo de partido
    Card(
        Modifier
            .width(180.dp)
            .clickable { onClick() }, // Hace clic en la tarjeta para invocar onClick
        colors = CardDefaults.cardColors(containerColor = Color(10, 17, 26)) // Colores personalizados de la tarjeta
    ) {
        AsyncImage(
            model = imagenSitio,
            contentDescription = "Imagen del sitio", // Descripción de la imagen para accesibilidad
            contentScale = ContentScale.Crop, // Escala de contenido para la imagen
            modifier = Modifier
                .fillMaxWidth()
                .height(116.dp)
                .padding(top = 8.dp, start = 8.dp, end = 8.dp)
                .clip(
                    RoundedCornerShape(12.dp) // Forma de esquina redondeada para la imagen
                )
        )
        Text(
            text = nombreLugar, // Nombre del lugar
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp, start = 8.dp, end = 8.dp),
            textAlign = TextAlign.Center,
            fontFamily = poppins, // Fuente personalizada
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            maxLines = 1 // Máximo de líneas para el nombre
        )
        Text(
            text = tipo, // Tipo de partido (por ejemplo, "Futbol 7")
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp, start = 8.dp, end = 8.dp),
            textAlign = TextAlign.Center,
            fontFamily = poppins, // Fuente personalizada
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            color = Color.White
        )
    }
}
