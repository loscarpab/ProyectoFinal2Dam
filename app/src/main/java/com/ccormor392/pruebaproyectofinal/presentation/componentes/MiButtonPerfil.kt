package com.ccormor392.pruebaproyectofinal.presentation.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ccormor392.pruebaproyectofinal.ui.theme.maincolor

@Composable
fun MiButtonPerfil(onClickButton:()->Unit, texto:String){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(29.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(maincolor)
            .clickable { onClickButton() },
        contentAlignment = Alignment.Center
    ) {
        MiTexto(
            string = texto,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )

    }
}