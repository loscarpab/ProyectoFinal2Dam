package com.ccormor392.pruebaproyectofinal.presentation.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
fun RowUser(username: String, avatar: String, onClickButton: (() -> Unit)? = null, onClickRow: () -> Unit, leSigo:Boolean = false) {
    Row (
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween){
        Row (verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onClickRow() }){
            AsyncImage(
                model = avatar,
                contentDescription = "Avatar del usuario",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(52.dp)
                    .clip(
                        RoundedCornerShape(26.dp)
                    )
            )
            Text(
                text = username,
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                fontFamily = poppins,
                color = Color.White,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        if (onClickButton != null) {
            IconButton(onClick = {
                onClickButton()
            }) {
                if (leSigo){
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .border(2.dp, maincolor, shape = RoundedCornerShape(18.dp))
                            .size(40.dp), contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.baseline_person_remove_24),
                            contentDescription = "icono agregar",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                else{
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

    }

}