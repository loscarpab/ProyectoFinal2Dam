package com.ccormor392.pruebaproyectofinal.presentation.componentes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ccormor392.pruebaproyectofinal.cartapartido.poppins


@Composable
fun CardMatch(
    onClick: () -> Unit,
    imagenPartido: String,
    nombreLugar: String,
    fechaPartido: String,
    horaPartido: String,
    avatarUsuario: String,
    nombreUsuario: String,
    jugadoresInscritos:String,
    jugadoresTotales: String,
    paddingEnd:Dp = 0.dp
) {

    Card(
        Modifier
            .width(180.dp)
            .clickable { onClick() }
            .padding(end = paddingEnd),
        colors = CardDefaults.cardColors(containerColor = Color(10, 17, 26))
    ) {

        AsyncImage(
            model = imagenPartido,
            contentDescription = "Imagen del partido",
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
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 1.dp, start = 8.dp, end = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = fechaPartido,
                modifier = Modifier
                    .padding(top = 2.dp, end = 10.dp),
                textAlign = TextAlign.Center,
                fontFamily = poppins,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White
            )
            Text(
                text = horaPartido,
                modifier = Modifier
                    .padding(top = 2.dp, start = 10.dp),
                textAlign = TextAlign.Center,
                fontFamily = poppins,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White
            )
        }
        Row (Modifier
            .fillMaxWidth()
            .padding(top = 1.dp, start = 8.dp, end = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween){
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = avatarUsuario,
                    contentDescription = "Avatar del usuario",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(28.dp)
                        .width(32.dp)
                        .padding(end = 6.dp)
                        .clip(
                            RoundedCornerShape(18.dp)
                        )
                )
                Text(
                    text = nombreUsuario,
                    modifier = Modifier,
                    textAlign = TextAlign.Center,
                    fontFamily = poppins,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
            Text(
                text = "$jugadoresInscritos/$jugadoresTotales",
                modifier = Modifier
                    .padding(top = 2.dp, start = 10.dp),
                textAlign = TextAlign.Center,
                fontFamily = poppins,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White
            )
        }

    }
}
