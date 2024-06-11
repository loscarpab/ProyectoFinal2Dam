package com.ccormor392.pruebaproyectofinal.presentation.misPartidos

import android.annotation.SuppressLint
import android.app.Application
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

class SitiosViewModel(application: Application) : AndroidViewModel(application) {
    // Firebase Firestore
    private val firestore = Firebase.firestore
    private val auth = Firebase.auth
    private val storageRef = Firebase.storage.reference


    @SuppressLint("StaticFieldLeak")
    val context = getApplication<Application>().applicationContext

    // Lista mutable de partidos disponibles
    private var _listaSitios = MutableStateFlow(mutableListOf<Sitio>())
    val listaSitios: StateFlow<MutableList<Sitio>> = _listaSitios
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
    var nombre by mutableStateOf("")
        private set
    var imageUri by mutableStateOf<Uri?>(null)
        private set
    var idFoto by mutableStateOf("")
        private set
    var namePeticion by mutableStateOf("")
        private set
    var nombreCompletoPeticion by mutableStateOf("")
        private set
    var tipo by mutableStateOf("")
        private set
    var expandedMenuTextField by mutableStateOf(false)
        private set
    var showAlert by mutableStateOf(false)
        private set
    var showLoading by mutableStateOf(false)
        private set
    var newMarkerPosition by mutableStateOf<LatLng?>(null)
        private set
    var listaTipos by mutableStateOf(listOf("futsal", "fut7", "futbol"))
    var segmentedButton by mutableStateOf<Boolean>(true)
        private set


    /**
     * Función para obtener todos los partidos del usuario desde Firestore.
     * Consulta Firestore para obtener la lista de partidos del usuario actual y actualiza [_listaMisPartidos].
     */
    fun pedirTodosLosSitios(esAdmin:Boolean) {
        // Consulta a Firestore para obtener los sitios
        var collection = firestore.collection("Sitios").whereEqualTo("peticion", false)
        if (esAdmin){
            collection = firestore.collection("Sitios")
        }
        collection.get()
            .addOnSuccessListener { querySnapshot ->
                // Si se obtienen resultados, extraer todos los sitios de la colección
                var listaTemp = mutableListOf<Sitio>()
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

    fun seleccionarSitio(newSitio: Sitio) {
        _selectedSitio.value = newSitio
    }

    fun changeNombre(nuevoNombre: String, esAdmin: Boolean) {
        nombre = nuevoNombre
        devolverLista(esAdmin)
    }

    fun pedirTodosLosPartidos() {
        listaPartidos.value = mutableListOf()
        _listaPartidosConNombreUsuario.value = mutableListOf()
        // Escuchar cambios en la colección "Partidos" de Firestore
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
     * Método para obtener el nombre de usuario basado en el ID del usuario.
     * Este método consulta la colección "Users" en Firestore.
     *
     * @param id El ID del usuario.
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

    fun uploadImageToStorage(filename: Uri?, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (filename != null) {
                    var uri = storageRef.child("images").child(auth.uid.toString())
                        .child("${idFoto}fotositio").putFile(filename)
                        .await().storage.downloadUrl.await()
                    imageUri = uri
                    onSuccess()
                }

            } catch (e: Exception) {
            }
        }
    }

    fun crearPeticion(onSuccess: () -> Unit, isAdmin:Boolean) {
        showLoading = true
        viewModelScope.launch {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                if (imageUri == null || tipo.isEmpty() || namePeticion.isEmpty() || nombreCompletoPeticion.isEmpty() || newMarkerPosition == null) {
                    showLoading = false
                    showAlert = true
                } else {
                    try {
                        uploadImageToStorage(imageUri) {
                            val peticion = Sitio(
                                imageUri.toString(),
                                namePeticion,
                                nombreCompletoPeticion,
                                GeoPoint(
                                    newMarkerPosition!!.latitude,
                                    newMarkerPosition!!.longitude
                                ),
                                tipo,
                                !isAdmin
                            )
                            firestore.collection("Sitios").add(peticion).addOnSuccessListener {
                                showLoading = false
                                onSuccess.invoke()
                                Log.d("CreateMatchViewModel", "Partido creado con éxito.")
                            }
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

    fun randomId() {
        val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        idFoto = (1..12)
            .map { Random.nextInt(0, charPool.size).let { charPool[it] } }
            .joinToString("")
    }

    fun changename(nuevoName: String) {
        namePeticion = nuevoName
    }

    fun changeNameCompleto(nuevoName: String) {
        nombreCompletoPeticion = nuevoName
    }

    fun changeTipo(nuevotipo: String) {
        tipo = nuevotipo
    }

    fun changeExpanded() {
        expandedMenuTextField = !expandedMenuTextField
    }

    fun changeImageUri(nuevoNombre: Uri) {
        imageUri = nuevoNombre
    }

    fun changeLocation(newLocation: com.google.android.gms.maps.model.LatLng) {
        newMarkerPosition = newLocation
    }

    fun closeAlert() {
        showAlert = false
    }
    fun changeSegmentedButton(esAdmin: Boolean){
        segmentedButton = !segmentedButton
        devolverLista(esAdmin)
    }
    fun devolverLista(esAdmin: Boolean){
        _sitios.value = if (esAdmin){
            _listaSitios.value.filter { it.peticion == !segmentedButton }.filter { sitio2 ->
            sitio2.nombre.trim().removeAccents().contains(
                nombre.trim().removeAccents(),
                ignoreCase = true)
        }
        }else{
            _listaSitios.value.filter { sitio2 ->
                sitio2.nombre.trim().removeAccents().contains(
                    nombre.trim().removeAccents(), ignoreCase = true)
            }
        }
    }

}