package com.ccormor392.pruebaproyectofinal.presentation.inicio

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.ccormor392.pruebaproyectofinal.data.model.Partido
import com.ccormor392.pruebaproyectofinal.data.model.UserInicio
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel para la pantalla de inicio.
 * Esta ViewModel maneja la lógica relacionada con la pantalla de inicio, incluida la obtención de la lista de partidos disponibles
 * y la asignación de nombres de usuario a cada partido.
 *
 * @property listaPartidos Lista de partidos disponibles.
 * @property listaPartidosConNombreUsuario Lista de partidos con nombre de usuario del creador.
 */
@Suppress("UNCHECKED_CAST")
@SuppressLint("MutableCollectionMutableState")
class InicioViewModel(application: Application) : AndroidViewModel(application) {
    // Instancia de Firestore
    private val firestore = Firebase.firestore
    private val auth = Firebase.auth
    @SuppressLint("StaticFieldLeak")
    val context = getApplication<Application>().applicationContext

    // MutableStateFlow para la lista de partidos
    private var listaPartidos = MutableStateFlow(mutableListOf<Partido>())

    // MutableStateFlow para la lista de partidos con el nombre del usuario creador
    private var _listaPartidosConNombreUsuario =
        MutableStateFlow<MutableList<Pair<Partido, UserInicio>>>(mutableListOf())
    val listaPartidosConNombreUsuario: StateFlow<MutableList<Pair<Partido, UserInicio>>> =
        _listaPartidosConNombreUsuario

    /**
     * Método para obtener todos los partidos desde la colección "Partidos" en Firestore.
     * Los datos obtenidos se almacenan en listaPartidos` y se llama a `asignarUsernameCreadorAPartido` para
     * asignar los nombres de usuario a cada partido.
     */
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
                listaPartidos.value = documents.sortedBy { it.timestamp }.toMutableList()
                // Asignar nombres de usuario a los partidos
                asignarUsernameCreadorAPartido()
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

    /**
     * Método para asignar el nombre de usuario creador a cada partido.
     * Este método actualiza `_listaPartidosConNombreUsuario` con la lista de partidos y sus respectivos nombres de usuario.
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
    fun getUserId(): String {
        return auth.currentUser?.uid ?: ""
    }
}
