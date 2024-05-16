package com.ccormor392.pruebaproyectofinal.presentation.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ccormor392.pruebaproyectofinal.R
import com.ccormor392.pruebaproyectofinal.infopartido.poppins
import com.ccormor392.pruebaproyectofinal.ui.theme.maincolor

@Composable
fun RowUser(username: String, avatar: String) {
    Row (
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween){
        Row (verticalAlignment = Alignment.CenterVertically){
            AsyncImage(
                model = avatar,
                contentDescription = "Avatar del usuario",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(52.dp)
                    .padding(end = 8.dp)
                    .clip(
                        RoundedCornerShape(20.dp)
                    )
            )
            Text(
                text = username,
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                fontFamily = poppins,
                color = Color.White
            )
        }

        IconButton(onClick = { /*TODO*/ }) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(maincolor)
                    .size(40.dp), contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.baseline_person_add_24),
                    contentDescription = "icono agregar",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }

}