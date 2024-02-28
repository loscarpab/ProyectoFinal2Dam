package com.ccormor392.pruebaproyectofinal.presentation.componentes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ccormor392.pruebaproyectofinal.lineaseparadoratopbar.LineaSeparadoraTopbar
import com.ccormor392.pruebaproyectofinal.logoapp.LogoApp

@Composable
fun MyTopBar(){
    Column(modifier = Modifier.height(100.dp)) {
        LogoApp(
            textNombreApp = "QuickMatch",
            modifier = Modifier.padding(start = 24.dp, top = 24.dp)
        )
        LineaSeparadoraTopbar()
    }
}
