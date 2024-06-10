package com.ccormor392.pruebaproyectofinal.presentation.misPartidos

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.ccormor392.pruebaproyectofinal.data.model.Partido
import com.ccormor392.pruebaproyectofinal.data.model.Sitio
import com.ccormor392.pruebaproyectofinal.data.model.UserInicio
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Date

class SitiosViewModel : ViewModel() {
    // Firebase Firestore
    private val firestore = Firebase.firestore
    private val auth = Firebase.auth

    // Lista mutable de partidos disponibles
    private var _listaSitios = MutableStateFlow(mutableListOf<Sitio>())
    val listaSitios: StateFlow<MutableList<Sitio>> = _listaSitios

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

    /**
     * Función para obtener todos los partidos del usuario desde Firestore.
     * Consulta Firestore para obtener la lista de partidos del usuario actual y actualiza [_listaMisPartidos].
     */
    fun pedirTodosLosSitios() {
        // Consulta a Firestore para obtener los sitios
        firestore.collection("Sitios").get()
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
            }
    }
    fun seleccionarSitio(newSitio:Sitio){
        _selectedSitio.value = newSitio
    }
    fun changeNombre(nuevoNombre:String){
        nombre = nuevoNombre
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
                listaPartidos.value = documents.filter { it.sitio == _selectedSitio.value &&  it.timestamp > Date(System.currentTimeMillis()) }.sortedBy { it.timestamp }.toMutableList()
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
}
