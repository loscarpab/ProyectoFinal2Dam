package com.ccormor392.pruebaproyectofinal.presentation.crearPartido

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ccormor392.pruebaproyectofinal.data.model.Partido
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.util.Date

/**
 * ViewModel para la creación de partidos.
 * Esta clase maneja la lógica relacionada con la creación de nuevos partidos, incluida la validación de datos y la interacción con Firestore.
 *
 * @property numPartidosCreados Contador de partidos creados por el usuario.
 * @property fecha La fecha del partido.
 * @property hora La hora del partido.
 * @property nombreSitio El nombre del sitio del partido.
 */
class CreateMatchViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val firestore = Firebase.firestore
    private val storageRef = Firebase.storage.reference
    // Contador de partidos creados por el usuario
    private var numPartidosCreados by mutableLongStateOf(7)

    // Propiedades para la fecha, hora y nombre del sitio del partido
    var showAlert by mutableStateOf(false)
        private set
    var fecha by mutableStateOf("")
        private set
    var hora by mutableStateOf("")
        private set
    var nombreSitio by mutableStateOf("")
        private set
    var foto by mutableStateOf("")
        private set
    var imageUri by mutableStateOf<Uri?>(null)
        private set
    private var _showTimePicker = MutableStateFlow(false)
    var showTimePicker :StateFlow<Boolean> = _showTimePicker
    private var _showDatePicker = MutableStateFlow(false)
    var showDatePicker :StateFlow<Boolean> = _showDatePicker

    private var _timestamp by mutableStateOf(Date(System.currentTimeMillis()))





    /**
     * Obtiene el número de partidos creados por el usuario autenticado.
     * Esta función realiza una consulta a Firestore para obtener el número de partidos creados por el usuario actual.
     */
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

    /**
     * Crea un nuevo partido.
     * Esta función valida los datos del partido y lo guarda en Firestore si los datos son válidos.
     *
     * @param onSuccess Callback para manejar el éxito de la creación del partido.
     */
    fun crearPartido(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                if (fecha.isEmpty() || hora.isEmpty() || nombreSitio.isEmpty()) {
                    showAlert = true
                } else {
                    val idPartido = userId + numPartidosCreados
                    val partido = Partido(userId, fecha, hora, idPartido, nombreSitio = nombreSitio, timestamp = _timestamp)
                    try {
                        partido.jugadores += userId
                        firestore.collection("Partidos").add(partido).addOnSuccessListener {
                            numPartidosCreados++
                            actualizarPartidosCreadosPorId(userId, numPartidosCreados)
                            onSuccess.invoke()
                            Log.d("CreateMatchViewModel", "Partido creado con éxito.")
                        }

                    } catch (e: Exception) {
                        Log.e("CreateMatchViewModel", "Error al crear el partido: ${e.message}")
                    }
                }
            } else {
                Log.e("CreateMatchViewModel", "El usuario no está autenticado.")
            }
        }
    }

    fun uploadImageToStorage(filename: Uri?) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (filename != null) {
                    var nombreArchivo = auth.currentUser?.uid + (numPartidosCreados)
                    var uri = storageRef.child("images").child(auth.uid.toString()).child("partidos")
                        .child(nombreArchivo).putFile(filename)
                        .await().storage.downloadUrl.await()
                    imageUri = uri
                    if (imageUri != null) {
                        firestore.collection("Partidos")
                            .whereEqualTo("idPartido", nombreArchivo)
                            .addSnapshotListener { querySnapshot, error ->
                                if (error != null) {
                                    // Manejar el error aquí si es necesario
                                    return@addSnapshotListener
                                }
                                if (querySnapshot != null) {
                                    for (document in querySnapshot) {
                                        // El usuario no está en la lista de jugadores, se puede unir al partido
                                        val partidoRef =
                                            firestore.collection("Partidos").document(document.id)
                                        partidoRef.update("foto", imageUri)
                                            .addOnSuccessListener {
                                                // El usuario se ha cambiado el avatar exitosamente
                                                //onSuccess()
                                            }
                                            .addOnFailureListener { exception ->
                                                // Manejar errores al cambiar el partido
                                                println("Error al cambiar el partido: $exception")
                                            }

                                    }
                                }
                            }
                    }
                }

            } catch (e: Exception) {
            }
        }

    }
    /**
     * Actualiza el número de partidos creados en Firestore.
     * Esta función actualiza el número de partidos creados por el usuario en Firestore.
     *
     * @param userIdParam ID del usuario.
     * @param nuevosPartidos Nuevo valor del contador de partidos creados.
     */
    private fun actualizarPartidosCreadosPorId(userIdParam: String, nuevosPartidos: Long) {
        firestore.collection("Users")
            .whereEqualTo("userId", userIdParam)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val userDocument = querySnapshot.documents[0]
                    val userId = userDocument.id
                    val userRef = firestore.collection("Users").document(userId)
                    userRef.update("partidosCreados", nuevosPartidos)
                }
            }
    }


    /**
     * Actualiza el nombre del lugar del partido.
     * Esta función actualiza el nombre del sitio del partido.
     *
     * @param lugar Nuevo nombre del lugar del partido.
     */
    fun changeLugar(lugar: String) {
        this.nombreSitio = lugar
    }

    /**
     * Actualiza la hora del partido.
     * Esta función actualiza la hora del partido.
     *
     * @param hora Nueva hora del partido.
     */
    fun changeHora(hora: Int, minutos:Int) {
        this.hora = "$hora:$minutos"
    }

    /**
     * Actualiza la fecha del partido.
     * Esta función actualiza la fecha del partido.
     *
     * @param dia Nuevo dia del partido.
     * @param mes Nuevo mes del partido.
     * @param ano Nuevo año del partido.
     */
    fun changeTimestamp(date:Date) {
        this._timestamp =date

    }
    fun changeFecha(string:String) {
        this.fecha = string
    }

    /**
     * Cierra el diálogo de alerta.
     * Esta función cierra el diálogo de alerta cuando se muestra.
     */
    fun closeAlert() {
        showAlert = false
    }

    fun changeHoraPicker() {
        _showTimePicker.value = !_showTimePicker.value
    }
    fun changeDatePicker() {
        _showDatePicker.value = !_showDatePicker.value
    }

}
