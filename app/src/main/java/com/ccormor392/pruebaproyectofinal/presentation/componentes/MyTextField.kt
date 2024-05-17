package com.ccormor392.pruebaproyectofinal.presentation.componentes

import android.annotation.SuppressLint
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Search
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
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {

    TextField(
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
        value = value,
        enabled = enabled,
        onValueChange = onValueChange,
        shape = RoundedCornerShape(25.dp),
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
        textStyle = TextStyle(fontFamily = poppins),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
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
            }
        },
        leadingIcon = if(iconName == "buscar"){
            {
                IconButton(
                    onClick ={
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
        }else null,
        modifier = modifier
    )

}
