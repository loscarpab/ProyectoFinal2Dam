package com.ccormor392.pruebaproyectofinal.presentation.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ccormor392.pruebaproyectofinal.ui.theme.PurpleGrey40

@Composable
fun MyScaffoldContent(content: @Composable () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .background(PurpleGrey40)
            .padding(top = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        content()
    }
}