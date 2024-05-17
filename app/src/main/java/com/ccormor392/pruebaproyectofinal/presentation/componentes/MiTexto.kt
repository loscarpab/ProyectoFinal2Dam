package com.ccormor392.pruebaproyectofinal.presentation.componentes

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import com.ccormor392.pruebaproyectofinal.infopartido.poppins

@Composable
fun MiTexto(modifier: Modifier = Modifier, string: String, fontWeight: FontWeight= FontWeight.Normal, fontSize:TextUnit= TextUnit.Unspecified){
    Text(
        text = string,
        fontWeight = fontWeight,
        fontSize = fontSize,
        fontFamily = poppins,
        color = Color.White,
        modifier = modifier
    )
}