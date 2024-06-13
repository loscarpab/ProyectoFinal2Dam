package com.ccormor392.pruebaproyectofinal.presentation.componentes

import android.annotation.SuppressLint
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ccormor392.pruebaproyectofinal.R
import com.ccormor392.pruebaproyectofinal.logoapp.poppins
import com.ccormor392.pruebaproyectofinal.ui.theme.xdark

/**
 * Composable para un campo de texto personalizado con opciones adicionales para iconos de acción.
 *
 * @param value El valor actual del campo de texto.
 * @param onValueChange La lambda para manejar cambios en el valor del campo de texto.
 * @param onClickClockIcon Lambda para manejar clics en el ícono de reloj.
 * @param onClickDateIcon Lambda para manejar clics en el ícono de fecha.
 * @param onClickSearchIcon Lambda para manejar clics en el ícono de búsqueda.
 * @param keyboardType Tipo de teclado para el campo de texto.
 * @param visualTransformation Transformación visual aplicada al texto ingresado.
 * @param string Texto para la etiqueta del campo de texto.
 * @param enabled Indica si el campo de texto está habilitado para interacción.
 * @param iconName Nombre del ícono a mostrar (opciones: "hora", "fecha", "buscar", "desplegado").
 * @param desplegado Booleano que indica si el menú desplegable está expandido o no.
 * @param modifier Modificador para personalizar la apariencia y el comportamiento del campo.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onClickClockIcon: (() -> Unit)? = null,
    onClickDateIcon: (() -> Unit)? = null,
    onClickSearchIcon: (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    string: String? = null,
    enabled: Boolean = true,
    iconName: String = "",
    desplegado: Boolean? = null,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {

    // Configuración del TextField con los parámetros proporcionados
    TextField(
        // Etiqueta del campo de texto, si se proporciona un texto
        label = if (!string.isNullOrEmpty()) {
            {
                Text(
                    text = string,
                    fontFamily = poppins,
                    fontSize = 14.sp,
                    color = xdark,
                    fontWeight = FontWeight.Medium
                )
            }
        } else null,

        // Valor actual del campo de texto y función de cambio de valor
        value = value,
        onValueChange = onValueChange,

        // Habilitar o deshabilitar la interacción con el campo de texto
        enabled = enabled,

        // Forma del borde del campo de texto
        shape = RoundedCornerShape(25.dp),

        // Colores personalizados para diferentes estados del campo de texto
        colors = TextFieldDefaults.colors(
            disabledTextColor = Color.DarkGray,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            cursorColor = xdark,
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedLabelColor = xdark,
            disabledLabelColor = Color.DarkGray,
            focusedTextColor = xdark,
            unfocusedTextColor = xdark,
        ),

        // Estilo de texto personalizado
        textStyle = TextStyle(fontFamily = poppins),

        // Opciones de teclado para el campo de texto
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),

        // Transformación visual aplicada al texto ingresado
        visualTransformation = visualTransformation,

        // Icono al final del campo de texto, según el nombre proporcionado
        trailingIcon = {
            when (iconName) {
                "hora" -> {
                    if (onClickClockIcon != null) {
                        IconButton(
                            onClick = onClickClockIcon,
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.clock),
                                contentDescription = "Clock Icon",
                                tint = xdark
                            )
                        }
                    }
                }
                "fecha" -> {
                    if (onClickDateIcon != null) {
                        IconButton(
                            onClick = onClickDateIcon,
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.DateRange,
                                contentDescription = "Date Icon",
                                tint = xdark
                            )
                        }
                    }
                }
                "desplegado" -> {
                    if (desplegado != null) {
                        // Icono para indicar el estado desplegado del menú
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = desplegado)
                    }
                }
            }
        },

        // Icono al inicio del campo de texto, específicamente para la búsqueda
        leadingIcon = if (iconName == "buscar") {
            {
                IconButton(
                    onClick = {
                        if (onClickSearchIcon != null) {
                            onClickSearchIcon()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Clock Icon",
                        tint = xdark
                    )
                }
            }
        } else null,

        // Modificador adicional para personalizar la apariencia y comportamiento del TextField
        modifier = modifier
    )

}
