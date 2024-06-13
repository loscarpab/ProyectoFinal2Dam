package com.ccormor392.pruebaproyectofinal.presentation.componentes

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract

/**
 * Contrato personalizado para seleccionar una imagen desde la galería.
 *
 * Este contrato maneja la creación del intent para abrir la galería y la interpretación del resultado
 * obtenido después de seleccionar una imagen.
 */
class PickImageFromGallery : ActivityResultContract<Unit, Uri?>() {

    /**
     * Crea un intent para abrir la galería de imágenes del dispositivo.
     *
     * @param context El contexto actual desde el que se crea el intent.
     * @param input Parámetro de entrada (en este caso, no se utiliza).
     * @return Intent configurado para abrir la galería y seleccionar imágenes.
     */
    override fun createIntent(context: Context, input: Unit): Intent {
        return Intent(Intent.ACTION_PICK).apply {
            type = "image/*"  // Selecciona cualquier tipo de imagen
        }
    }

    /**
     * Analiza el resultado obtenido después de que el usuario selecciona una imagen desde la galería.
     *
     * @param resultCode Código de resultado devuelto por la actividad de selección de imágenes.
     * @param intent Intent que contiene los datos devueltos por la actividad de selección de imágenes.
     * @return Uri de la imagen seleccionada, o null si no se seleccionó ninguna imagen o hubo un error.
     */
    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return intent?.data  // Devuelve el Uri de la imagen seleccionada
    }
}
