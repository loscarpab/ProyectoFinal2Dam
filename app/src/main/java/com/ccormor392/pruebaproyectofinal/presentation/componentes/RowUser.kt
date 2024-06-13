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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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

/**
 * Composable que muestra una fila con el avatar del usuario, nombre de usuario y un botón opcional.
 *
 * @param username Nombre de usuario a mostrar.
 * @param avatar URL del avatar del usuario.
 * @param onClickButton Acción a realizar cuando se hace clic en el botón (opcional).
 * @param onClickRow Acción a realizar cuando se hace clic en la fila.
 * @param leSigo Indica si el usuario actual sigue al usuario representado en la fila.
 */
@Composable
fun RowUser(
    username: String,
    avatar: String,
    onClickButton: (() -> Unit)? = null,
    onClickRow: () -> Unit,
    leSigo: Boolean = false
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Parte izquierda de la fila: avatar y nombre de usuario clicables
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onClickRow() }
        ) {
            // Avatar del usuario
            AsyncImage(
                model = avatar,
                contentDescription = "Avatar del usuario",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(26.dp))
            )
            // Nombre de usuario
            Text(
                text = username,
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                fontFamily = poppins,
                color = Color.White,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // Parte derecha de la fila: botón opcional (se muestra solo si onClickButton no es nulo)
        onClickButton?.let {
            IconButton(onClick = { onClickButton() }) {
                // Icono y estilo dependiendo de si el usuario sigue o no al representado
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (leSigo) maincolor else Color.Transparent)
                        .border(
                            width = if (leSigo) 2.dp else 0.dp,
                            color = maincolor,
                            shape = RoundedCornerShape(18.dp)
                        )
                        .size(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Icono de añadir o remover usuario según corresponda
                    Icon(
                        imageVector = ImageVector.vectorResource(
                            id = if (leSigo) R.drawable.baseline_person_remove_24 else R.drawable.baseline_person_add_24
                        ),
                        contentDescription = "Icono de acción",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
