package com.ccormor392.pruebaproyectofinal.presentation.componentes

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ccormor392.pruebaproyectofinal.logoapp.poppins
import com.ccormor392.pruebaproyectofinal.ui.theme.PurpleGrey40
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    string: String,
    enabled: Boolean = true
) {
    TextField(
        label = { Text(text = string, fontFamily = poppins, fontSize = 14.sp) },
        value = value,
        enabled = enabled,
        onValueChange = onValueChange,
        shape = RoundedCornerShape(25.dp),
        colors = TextFieldDefaults.textFieldColors(
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            containerColor = Color.White,
            disabledTextColor = Color.DarkGray ,
            disabledLabelColor = Color.DarkGray ,
            textColor = PurpleGrey40,
            focusedLabelColor = PurpleGrey40,
            cursorColor = PurpleGrey40
        ),
        textStyle = TextStyle(fontFamily = poppins),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation
    )
}