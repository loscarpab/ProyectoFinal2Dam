package com.ccormor392.pruebaproyectofinal.presentation.crearPartido

import android.annotation.SuppressLint
import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ccormor392.pruebaproyectofinal.data.model.Partido
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CreateMatchViewModel(application: Application) : AndroidViewModel(application) {
    private val auth: FirebaseAuth = Firebase.auth
    private val firestore = Firebase.firestore
    private val storage = Firebase.storage
    private lateinit var downloadUrl: Uri
    var numPartidosCreados: Long = 7


    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext
    var imageUri by mutableStateOf<Uri?>(null)

    var fecha by mutableStateOf("")
        private set

    var hora by mutableStateOf("")
        private set
    var nombreSitio by mutableStateOf("")
        private set


    fun numeroPartidosUsuarioAutenticado() {
        viewModelScope.launch {
            try {
                firestore.collection("Users").whereEqualTo("userId", auth.uid).get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            numPartidosCreados = document.get("partidosCreados") as Long
                        }
                    }.addOnFailureListener { exception ->
                        println("Error al obtener el documento: $exception")
                    }
            } catch (e: FirebaseFirestoreException) {
                println("Error al acceder a Firestore: ${e.message}")
            }
        }

    }

    fun crearPartido() {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                val idPartido = userId + numPartidosCreados
                val partido = Partido(userId, fecha, hora, idPartido, nombreSitio = nombreSitio, urlImagen = ""
                )
                try {
                    partido.jugadores += userId
                    firestore.collection("partidos").add(partido).await()
                    subirImagentoStorage(idPartido)
                } catch (e: Exception) {
                    // Manejar errores al crear el partido
                }
            } else {
                // El usuario no está autenticado, manejar el error o mostrar un mensaje al usuario
            }
        }
    }

    suspend fun subirImagentoStorage(idPartido: String) {
        try {
            val downloadUrl = storage.reference.child("images")
                .child("${auth.currentUser?.uid}$numPartidosCreados.jpg").putFile(imageUri!!)
                .await().storage.downloadUrl.await()
            actualizarPartidoConImageUrl(idPartido, downloadUrl)
        } catch (e: Exception) {
            // Manejar errores al subir la imagen
        }
    }

    fun actualizarPartidoConImageUrl(idPartido: String, url: Uri) {
        try {
            firestore.collection("partidos").whereEqualTo("idPartido", idPartido).get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        document.reference.update("urlImagen", url)
                    }

                }
        } catch (e: Exception) {
            // Manejar errores al actualizar el partido con la URL de la imagen
        }
    }


    /**
     * Actualiza el email del usuario.
     *
     * @param email Nuevo email a establecer.
     */
    fun changeLugar(lugar: String) {
        this.nombreSitio = lugar
    }

    /**
     * Actualiza la contraseña del usuario.
     *
     * @param password Nueva contraseña a establecer.
     */
    fun changeHora(hora: String) {
        this.hora = hora
    }

    /**
     * Actualiza el nombre de usuario.
     *
     * @param userName Nuevo nombre de usuario a establecer.
     */
    fun changeFecha(fecha: String) {
        this.fecha = fecha
    }
}