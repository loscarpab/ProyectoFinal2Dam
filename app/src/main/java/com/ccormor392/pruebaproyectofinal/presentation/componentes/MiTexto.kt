package com.ccormor392.pruebaproyectofinal.presentation.componentes

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import com.ccormor392.pruebaproyectofinal.infopartido.poppins

/**
 * Composable que muestra texto con una fuente específica y estilo.
 *
 * @param modifier Modificador para personalizar la apariencia del texto.
 * @param string Texto que se muestra.
 * @param fontWeight Peso de la fuente (normal, semibold, bold, etc.).
 * @param fontSize Tamaño de la fuente.
 */
@Composable
fun MiTexto(
    modifier: Modifier = Modifier,
    string: String,
    fontWeight: FontWeight = FontWeight.Normal,
    fontSize: TextUnit = TextUnit.Unspecified
) {
    Text(
        text = string,
        fontWeight = fontWeight,
        fontSize = fontSize,
        fontFamily = poppins,
        color = Color.White,
        modifier = modifier
    )
}
