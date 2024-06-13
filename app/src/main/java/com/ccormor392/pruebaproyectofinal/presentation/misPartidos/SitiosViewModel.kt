package com.ccormor392.pruebaproyectofinal.presentation.misPartidos

import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentValues
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ccormor392.pruebaproyectofinal.data.model.Partido
import com.ccormor392.pruebaproyectofinal.data.model.Sitio
import com.ccormor392.pruebaproyectofinal.data.model.UserInicio
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date
import kotlin.random.Random

/**
 * ViewModel para la gestión de sitios en la aplicación.
 *
 * @param application Instancia de la aplicación Android.
 */
class SitiosViewModel(application: Application) : AndroidViewModel(application) {
    // Firebase Firestore
    private val firestore = Firebase.firestore
    private val auth = Firebase.auth
    private val storageRef = Firebase.storage.reference

    @SuppressLint("StaticFieldLeak")
    val context = getApplication<Application>().applicationContext!!

    // Lista mutable de sitios disponibles
    private var _listaSitios = MutableStateFlow(mutableListOf<Sitio>())
    private var _sitios = MutableStateFlow(listOf<Sitio>())
    val sitios: StateFlow<List<Sitio>> = _sitios

    private var _selectedSitio = MutableStateFlow(Sitio())
    val selectedSitio: StateFlow<Sitio> = _selectedSitio

    // MutableStateFlow para la lista de partidos
    private var listaPartidos = MutableStateFlow(mutableListOf<Partido>())

    // MutableStateFlow para la lista de partidos con el nombre del usuario creador
    private var _listaPartidosConNombreUsuario =
        MutableStateFlow<MutableList<Pair<Partido, UserInicio>>>(mutableListOf())
    val listaPartidosConNombreUsuario: StateFlow<MutableList<Pair<Partido, UserInicio>>> =
        _listaPartidosConNombreUsuario

    // Estado mutable para el nombre de búsqueda
    var nombre by mutableStateOf("")
        private set

    // Estado mutable para la URI de la imagen seleccionada
    var imageUri by mutableStateOf<Uri?>(null)
        private set

    // Identificador de foto generado aleatoriamente
    private var idFoto by mutableStateOf("")

    // Estado mutable para el nombre de la petición
    var namePeticion by mutableStateOf("")
        private set

    // Estado mutable para el nombre completo de la petición
    var nombreCompletoPeticion by mutableStateOf("")
        private set

    // Estado mutable para el tipo de petición
    var tipo by mutableStateOf("")
        private set

    // Estado mutable para el menú expandido del campo de texto
    var expandedMenuTextField by mutableStateOf(false)
        private set

    // Estado mutable para mostrar la alerta
    var showAlert by mutableStateOf(false)
        private set

    // Estado mutable para mostrar el indicador de carga
    var showLoading by mutableStateOf(false)
        private set

    // Posición del nuevo marcador en el mapa
    var newMarkerPosition by mutableStateOf<LatLng?>(null)
        private set

    // Lista de tipos de sitios disponibles
    var listaTipos by mutableStateOf(listOf("futsal", "fut7", "futbol"))

    // Estado mutable para el botón segmentado
    var segmentedButton by mutableStateOf(true)
        private set

    /**
     * Reinicia todos los campos de entrada.
     */
    fun restartFields(){
        imageUri = null
        namePeticion = ""
        nombreCompletoPeticion = ""
        tipo = ""
        newMarkerPosition = null
    }

    /**
     * Función para obtener todos los sitios desde Firestore.
     *
     * @param esAdmin Indica si el usuario es administrador.
     */
    fun pedirTodosLosSitios(esAdmin: Boolean) {
        // Consulta Firestore para obtener los sitios
        var collection = firestore.collection("Sitios").whereEqualTo("peticion", false)
        if (esAdmin) {
            collection = firestore.collection("Sitios")
        }
        collection.get()
            .addOnSuccessListener { querySnapshot ->
                // Si se obtienen resultados, extraer todos los sitios de la colección
                val listaTemp = mutableListOf<Sitio>()
                if (querySnapshot != null) {
                    for (document in querySnapshot) {
                        val sitio = document.toObject(Sitio::class.java)
                        listaTemp.add(sitio)
                    }
                }
                _listaSitios.value = listaTemp
                devolverLista(esAdmin)
            }
    }

    /**
     * Selecciona un sitio específico.
     *
     * @param newSitio Sitio seleccionado.
     */
    fun seleccionarSitio(newSitio: Sitio) {
        _selectedSitio.value = newSitio
    }

    /**
     * Cambia el nombre de búsqueda de sitios.
     *
     * @param nuevoNombre Nuevo nombre de búsqueda.
     * @param esAdmin Indica si el usuario es administrador.
     */
    fun changeNombre(nuevoNombre: String, esAdmin: Boolean) {
        nombre = nuevoNombre
        devolverLista(esAdmin)
    }

    /**
     * Obtiene todos los partidos relacionados con el sitio seleccionado.
     * Escucha cambios en la colección "Partidos" de Firestore.
     */
    fun pedirTodosLosPartidos() {
        listaPartidos.value = mutableListOf()
        _listaPartidosConNombreUsuario.value = mutableListOf()
        firestore.collection("Partidos")
            .addSnapshotListener { querySnapshot, error ->
                // Si ocurre un error, retornar sin hacer nada
                if (error != null) {
                    return@addSnapshotListener
                }
                // Lista temporal para almacenar los documentos obtenidos
                val documents = mutableListOf<Partido>()
                if (querySnapshot != null) {
                    // Iterar sobre cada documento en el snapshot
                    for (document in querySnapshot) {
                        // Obtener los campos del documento
                        val partido = document.toObject(Partido::class.java)
                        documents.add(partido)
                    }
                }
                // Actualizar la lista de partidos
                listaPartidos.value = documents.filter {
                    it.sitio == _selectedSitio.value && it.timestamp > Date(System.currentTimeMillis())
                }.sortedBy { it.timestamp }.toMutableList()
                // Asignar nombres de usuario a los partidos
                asignarUsernameCreadorAPartido()
            }
    }

    /**
     * Asigna el nombre de usuario al creador de cada partido en la lista.
     */
    private fun asignarUsernameCreadorAPartido() {
        // Inicializar la lista de partidos con nombre de usuario como una lista vacía
        _listaPartidosConNombreUsuario.value = mutableListOf()
        val currentPartidos = listaPartidos.value

        // Si hay partidos en la lista, iterar sobre cada uno para asignar el nombre de usuario
        if (currentPartidos.isNotEmpty()) {
            currentPartidos.forEach { partido ->
                // Obtener el nombre de usuario para el creador del partido
                getNombreUserById(partido.creador) { userInicio ->
                    // Actualizar la lista con el partido y el nombre de usuario
                    val updatedList = _listaPartidosConNombreUsuario.value.toMutableList()
                    updatedList.add(Pair(partido, userInicio))
                    _listaPartidosConNombreUsuario.value = updatedList
                }
            }
        }
    }

    /**
     * Obtiene el nombre de usuario basado en el ID del usuario.
     *
     * @param id ID del usuario.
     * @param callback Función de callback para manejar el resultado del nombre de usuario.
     */
    private fun getNombreUserById(id: String, callback: (UserInicio) -> Unit) {
        // Consultar la colección "Users" para obtener el usuario con el ID especificado
        firestore.collection("Users").whereEqualTo("userId", id).get()
            .addOnSuccessListener { querySnapshot ->
                // Si se obtienen resultados, extraer el nombre de usuario y llamarlo en el callback
                if (querySnapshot != null) {
                    for (document in querySnapshot) {
                        val username = document.getString("username") ?: ""
                        val avatar = document.getString("avatar") ?: ""
                        val user = UserInicio(username, avatar)
                        callback(user)
                    }
                }
            }
            .addOnFailureListener { _ ->
                // Manejar el fallo si es necesario
            }
    }

    /**
     * Sube una imagen seleccionada al almacenamiento de Firebase.
     *
     * @param filename Nombre de archivo de la imagen.
     * @param onSuccess Callback que se llama al completar la subida exitosamente.
     */
    private fun uploadImageToStorage(filename: Uri?, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (filename != null) {
                    val uri = storageRef.child("images")
                        .child(auth.uid.toString())
                        .child("${idFoto}fotositio").putFile(filename)
                        .await().storage.downloadUrl.await()
                            imageUri = uri
                            onSuccess()
                }

            } catch (e: Exception) {
                Log.e("SitiosViewModel", "Error al subir la imagen al almacenamiento: ${e.message}")
            }
        }
    }

    /**
     * Crea una nueva petición de sitio en Firestore.
     *
     * @param onSuccess Callback que se llama al crear la petición exitosamente.
     * @param isAdmin Indica si el usuario es administrador.
     */
    fun crearPeticion(onSuccess: () -> Unit, isAdmin: Boolean) {
        showLoading = true
        viewModelScope.launch {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                // Validar que todos los campos necesarios estén completos
                if (imageUri == null || tipo.isEmpty() || namePeticion.isEmpty() || nombreCompletoPeticion.isEmpty() || newMarkerPosition == null) {
                    showLoading = false
                    showAlert = true
                } else {
                    try {
                        // Subir la imagen al almacenamiento de Firebase
                        uploadImageToStorage(imageUri) {
                            // Construir el objeto Sitio para la petición
                            val peticion = Sitio(
                                imageUri.toString(),
                                namePeticion,
                                nombreCompletoPeticion,
                                GeoPoint(
                                    newMarkerPosition!!.latitude,
                                    newMarkerPosition!!.longitude
                                ),
                                tipo,
                                !isAdmin // Invertir la petición si no es administrador
                            )
                            // Guardar la petición en Firestore
                            firestore.collection("Sitios").add(peticion).addOnSuccessListener {
                                showLoading = false
                                onSuccess.invoke()
                                Log.d("SitiosViewModel", "Petición de sitio creada con éxito.")
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("SitiosViewModel", "Error al crear la petición de sitio: ${e.message}")
                    }
                }
            } else {
                Log.e("SitiosViewModel", "El usuario no está autenticado.")
            }
        }
    }

    /**
     * Borra el sitio seleccionado de Firestore.
     *
     * @param onSuccess Callback que se llama al borrar el sitio exitosamente.
     */
    fun borrarSitio(onSuccess: () -> Unit) {
        viewModelScope.launch {
            showLoading = true
            // Consulta a Firestore para obtener el sitio que se va a borrar
            firestore.collection("Sitios")
                .addSnapshotListener { querySnapshot, error ->
                    if (error != null) {
                        showLoading = false
                        // Manejar el error en la consulta y registrar en el log
                        Log.e(ContentValues.TAG, "Error al obtener el sitio a borrar: $error")
                        return@addSnapshotListener
                    }
                    querySnapshot?.let { snapshot ->
                        // Iterar sobre los documentos obtenidos en la consulta
                        for (document in snapshot.documents) {
                            val sitio = document.toObject(Sitio::class.java)
                            if (sitio != null) {
                                // Comparar el sitio con el sitio seleccionado y borrar si son iguales
                                if (sitio.peticion == _selectedSitio.value.peticion &&
                                    sitio.tipo == _selectedSitio.value.tipo &&
                                    sitio.nombre == _selectedSitio.value.nombre &&
                                    sitio.foto == _selectedSitio.value.foto &&
                                    sitio.ubicacion == _selectedSitio.value.ubicacion &&
                                    sitio.nombreLargo == _selectedSitio.value.nombreLargo) {

                                    // Borrar el documento de Firestore
                                    document.reference.delete()
                                        .addOnSuccessListener {
                                            showLoading = false
                                            onSuccess()
                                        }
                                        .addOnFailureListener { exception ->
                                            showLoading = false
                                            // Manejar el fallo al borrar el sitio de Firestore y registrar en el log
                                            Log.e(ContentValues.TAG, "Error al borrar el sitio de Firestore: $exception")
                                        }
                                }
                            }
                        }
                    }
                }
        }
    }

    /**
     * Confirma la petición de sitio seleccionada en Firestore.
     *
     * @param onSuccess Callback que se llama al confirmar la petición exitosamente.
     */
    fun confirmarPeticion(onSuccess: () -> Unit) {
        showLoading = true
        viewModelScope.launch {
            firestore.collection("Sitios")
                .whereEqualTo("ubicacion", _selectedSitio.value.ubicacion)
                .get().addOnSuccessListener { querySnapshot ->
                    if (querySnapshot != null) {
                        for (document in querySnapshot) {
                            val peticion = document.toObject(Sitio::class.java)
                            if (peticion.peticion == _selectedSitio.value.peticion &&
                                peticion.tipo == _selectedSitio.value.tipo &&
                                peticion.foto == _selectedSitio.value.foto &&
                                peticion.nombre == _selectedSitio.value.nombre &&
                                peticion.nombreLargo == _selectedSitio.value.nombreLargo) {

                                // Actualizar la petición en Firestore para marcarla como no pendiente
                                val peticionAceptada = _selectedSitio.value.copy(peticion = false).toMap()
                                firestore.collection("Sitios").document(document.id).update(peticionAceptada)
                                    .addOnSuccessListener {
                                        showLoading = false
                                        onSuccess()
                                    }
                                    .addOnFailureListener {
                                        showLoading = false
                                        Log.d("fallo", "falló")
                                    }
                            }
                        }
                    }
                }
                .addOnFailureListener {
                    showLoading = false
                    Log.d("fallo", "falló")
                }
        }
    }

    /**
     * Genera un ID aleatorio para la foto del sitio.
     */
    fun randomId() {
        val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        idFoto = (1..12)
            .map { Random.nextInt(0, charPool.size).let { charPool[it] } }
            .joinToString("")
    }

    /**
     * Cambia el nombre de la petición.
     *
     * @param nuevoName Nuevo nombre de la petición.
     */
    fun changename(nuevoName: String) {
        namePeticion = nuevoName
    }

    /**
     * Cambia el nombre completo de la petición.
     *
     * @param nuevoName Nuevo nombre completo de la petición.
     */
    fun changeNameCompleto(nuevoName: String) {
        nombreCompletoPeticion = nuevoName
    }

    /**
     * Cambia el tipo de la petición.
     *
     * @param nuevotipo Nuevo tipo de la petición.
     */
    fun changeTipo(nuevotipo: String) {
        tipo = nuevotipo
    }

    /**
     * Cambia el estado del menú expandido del campo de texto.
     */
    fun changeExpanded() {
        expandedMenuTextField = !expandedMenuTextField
    }

    /**
     * Cambia la URI de la imagen seleccionada.
     *
     * @param nuevoNombre Nueva URI de la imagen.
     */
    fun changeImageUri(nuevoNombre: Uri) {
        imageUri = nuevoNombre
    }

    /**
     * Cambia la ubicación del nuevo marcador en el mapa.
     *
     * @param newLocation Nueva ubicación del marcador.
     */
    fun changeLocation(newLocation: LatLng) {
        newMarkerPosition = newLocation
    }

    /**
     * Cierra la alerta mostrada.
     */
    fun closeAlert() {
        showAlert = false
    }

    /**
     * Cambia el estado del botón segmentado y actualiza la lista de sitios.
     *
     * @param esAdmin Indica si el usuario es administrador.
     */
    fun changeSegmentedButton(esAdmin: Boolean) {
        segmentedButton = !segmentedButton
        devolverLista(esAdmin)
    }

    /**
     * Filtra y devuelve la lista de sitios según los criterios especificados.
     *
     * @param esAdmin Indica si el usuario es administrador.
     */
    private fun devolverLista(esAdmin: Boolean) {
        _sitios.value = if (esAdmin) {
            _listaSitios.value.filter { it.peticion == !segmentedButton }.filter { sitio2 ->
                sitio2.nombre.trim().removeAccents().contains(
                    nombre.trim().removeAccents(),
                    ignoreCase = true
                )
            }
        } else {
            _listaSitios.value.filter { sitio2 ->
                sitio2.nombre.trim().removeAccents().contains(
                    nombre.trim().removeAccents(), ignoreCase = true
                )
            }
        }
    }

}
