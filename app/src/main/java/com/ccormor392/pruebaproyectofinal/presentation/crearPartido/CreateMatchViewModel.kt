package com.ccormor392.pruebaproyectofinal.presentation.crearPartido

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ccormor392.pruebaproyectofinal.data.model.JugadorPartido
import com.ccormor392.pruebaproyectofinal.data.model.Partido
import com.ccormor392.pruebaproyectofinal.data.model.Sitio
import com.ccormor392.pruebaproyectofinal.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

/**
 * ViewModel para la creación de partidos.
 * Esta clase maneja la lógica relacionada con la creación de nuevos partidos, incluida la validación de datos y la interacción con Firestore.
 *
 * @property numPartidosCreados Contador de partidos creados por el usuario.
 * @property fecha La fecha del partido.
 * @property hora La hora del partido.
 */
class CreateMatchViewModel(application: Application) : AndroidViewModel(application) {
    private val auth: FirebaseAuth = Firebase.auth
    private val firestore = Firebase.firestore

    // Contador de partidos creados por el usuario
    private var numPartidosCreados by mutableLongStateOf(0) // Inicialización corregida

    @SuppressLint("StaticFieldLeak")
    val context = getApplication<Application>().applicationContext!!

    // Propiedades para la interfaz de usuario
    var showAlert by mutableStateOf(false)
        private set
    private var showLoading by mutableStateOf(false)
    var fecha by mutableStateOf("")
        private set
    var hora by mutableStateOf("")
        private set
    var sitio by mutableStateOf(Sitio())
        private set

    // Flujo de consulta para la búsqueda
    val query = MutableStateFlow("")
    val active = MutableStateFlow(false)

    private val _sitios = MutableStateFlow<List<Sitio>>(emptyList())
    val sitios: StateFlow<List<Sitio>> = _sitios.asStateFlow()

    private var _showTimePicker = MutableStateFlow(false)
    val showTimePicker: StateFlow<Boolean> = _showTimePicker

    private var _showDatePicker = MutableStateFlow(false)
    val showDatePicker: StateFlow<Boolean> = _showDatePicker

    private var _foto = MutableStateFlow("")
    val foto: StateFlow<String> = _foto

    private var _timestamp by mutableStateOf(Date(System.currentTimeMillis()))

    // Flujo mutable para almacenar información del usuario en el partido
    private val _user = MutableStateFlow(JugadorPartido())

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
                        Log.e("CreateMatchViewModel", "Error al obtener el documento: $exception")
                    }
            } catch (e: FirebaseFirestoreException) {
                Log.e("CreateMatchViewModel", "Error al acceder a Firestore: ${e.message}")
            }
        }
    }

    /**
     * Recupera el nombre de los jugadores del partido y los almacena en el flujo de usuarios.
     *
     * @param userId ID del usuario del que se recupera el nombre.
     * @param posicion Posición del jugador en el partido.
     * @param equipo Indica si el jugador está en el equipo.
     * @param onSuccess Callback para manejar el éxito de la operación.
     */
    private fun recuperarNombreJugadores(
        userId: String,
        posicion: Int = 0,
        equipo: Boolean = true,
        onSuccess: () -> Unit
    ) {
        firestore.collection("Users").whereEqualTo("userId", userId).get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    for (document in querySnapshot) {
                        val usuario = document.toObject(User::class.java)
                        _user.value = JugadorPartido(usuario.userId, usuario.username, usuario.avatar, posicion, equipo)
                        onSuccess()
                    }
                }
            }.addOnFailureListener { exception ->
                Log.e("CreateMatchViewModel", "Error al obtener datos del usuario: $exception")
            }
    }

    /**
     * Crea un nuevo partido.
     * Esta función valida los datos del partido y lo guarda en Firestore si los datos son válidos.
     *
     * @param onSuccess Callback para manejar el éxito de la creación del partido.
     */
    fun crearPartido(onSuccess: () -> Unit) {
        showLoading = true
        viewModelScope.launch {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                if (fecha.isEmpty() || hora.isEmpty() || sitio.nombre.isEmpty()) {
                    showLoading = false
                    showAlert = true
                } else {
                    val idPartido = userId + numPartidosCreados.toString() // Corrección para concatenar como string
                    val partido = Partido(userId, fecha, hora, idPartido, sitio = sitio, timestamp = _timestamp)
                    try {
                        recuperarNombreJugadores(userId) {
                            partido.jugadores += _user.value
                            firestore.collection("Partidos").add(partido).addOnSuccessListener {
                                numPartidosCreados++
                                actualizarPartidosCreadosPorId(userId, numPartidosCreados)
                                onSuccess.invoke()
                                showLoading = false
                                Log.d("CreateMatchViewModel", "Partido creado con éxito.")
                            }
                        }
                    } catch (e: Exception) {
                        showLoading = false
                        Log.e("CreateMatchViewModel", "Error al crear el partido: ${e.message}")
                    }
                }
            } else {
                showLoading = false
                Log.e("CreateMatchViewModel", "El usuario no está autenticado.")
            }
        }
    }

    /**
     * Actualiza el número de partidos creados en Firestore.
     *
     * @param userIdParam ID del usuario.
     * @param nuevosPartidos Nuevo valor del contador de partidos creados.
     */
    private fun actualizarPartidosCreadosPorId(userIdParam: String, nuevosPartidos: Long) {
        firestore.collection("Users").whereEqualTo("userId", userIdParam).get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val userDocument = querySnapshot.documents[0]
                    val userId = userDocument.id
                    val userRef = firestore.collection("Users").document(userId)
                    userRef.update("partidosCreados", nuevosPartidos)
                }
            }.addOnFailureListener { exception ->
                Log.e("CreateMatchViewModel", "Error al actualizar partidos creados: $exception")
            }
    }

    /**
     * Obtiene todos los sitios disponibles para los partidos desde Firestore.
     */
    fun getAllSitios() {
        viewModelScope.launch {
            try {
                firestore.collection("Sitios").get()
                    .addOnSuccessListener { documents ->
                        val sitiosTemp = mutableListOf<Sitio>()
                        for (document in documents) {
                            val sitio = document.toObject(Sitio::class.java)
                            sitiosTemp.add(sitio)
                        }
                        _sitios.value = sitiosTemp
                    }.addOnFailureListener { exception ->
                        Log.e("CreateMatchViewModel", "Error al obtener sitios: $exception")
                    }
            } catch (e: FirebaseFirestoreException) {
                Log.e("CreateMatchViewModel", "Error al acceder a Firestore: ${e.message}")
            }
        }
    }

    /**
     * Actualiza el lugar del partido.
     *
     * @param lugar Nuevo lugar del partido.
     */
    fun changeLugar(lugar: Sitio) {
        this.sitio = lugar
    }

    /**
     * Actualiza la hora del partido.
     *
     * @param hora Nueva hora del partido.
     * @param minutos Nuevos minutos del partido.
     */
    fun changeHora(hora: Int, minutos: Int) {
        var minutosstr = minutos.toString()
        if (minutos in 0..9) {
            minutosstr = "0$minutosstr"
        }
        this.hora = "$hora:$minutosstr"
    }

    /**
     * Actualiza la fecha del partido.
     *
     * @param date Nueva fecha del partido.
     */
    fun changeTimestamp(date: Date) {
        this._timestamp = date
    }

/**
 * Actualiza la fecha del partido como string.
 *
 * @param string Nueva fecha en formato
 * string para el partido.
 */
fun changeFecha(string: String) {
    this.fecha = string
}

    /**
     * Cierra el diálogo de alerta.
     * Esta función cierra el diálogo de alerta cuando se muestra.
     */
    fun closeAlert() {
        showAlert = false
    }

    /**
     * Cambia el estado del selector de hora entre visible e invisible.
     */
    fun changeHoraPicker() {
        _showTimePicker.value = !_showTimePicker.value
    }

    /**
     * Cambia el estado del selector de fecha entre visible e invisible.
     */
    fun changeDatePicker() {
        _showDatePicker.value = !_showDatePicker.value
    }

    /**
     * Actualiza la consulta de búsqueda actual.
     *
     * @param newQuery La nueva cadena de texto de consulta para la búsqueda.
     */
    fun setQuery(newQuery: String) {
        query.value = newQuery
    }

    /**
     * Establece si la búsqueda está activa o no.
     *
     * @param newActive El nuevo estado booleano que indica si la búsqueda está activa.
     */
    fun setActive(newActive: Boolean) {
        active.value = newActive
    }

    /**
     * Establece la URL de la foto del partido.
     *
     * @param newUrl La nueva URL de la foto del partido.
     */
    fun setFoto(newUrl: String) {
        _foto.value = newUrl
    }

    /**
     * Reinicia el estado de la ViewModel.
     * Se restablecen valores a los estados iniciales para una nueva creación de partido.
     */
    fun restart() {
        _foto.value = "https://firebasestorage.googleapis.com/v0/b/proyectofinal-f110d.appspot.com/o/images%2FSelectImagePartido.png?alt=media&token=d50d1619-99b3-4414-a068-2c54673d5c33"
        sitio = Sitio()
        hora = ""
        fecha = ""
        query.value = ""
    }
}
