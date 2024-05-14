package com.ccormor392.pruebaproyectofinal.presentation.inicio

import android.annotation.SuppressLint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.ccormor392.pruebaproyectofinal.data.model.Partido
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
@SuppressLint("MutableCollectionMutableState")
class InicioViewModel : ViewModel() {
    private val firestore = Firebase.firestore
    private var listaPartidos = MutableStateFlow(mutableListOf<Partido>())

    private var _listaPartidosConNombreUsuario = MutableStateFlow<MutableList<Pair<Partido, String>>>(mutableListOf())
    val listaPartidosConNombreUsuario: StateFlow<MutableList<Pair<Partido, String>>> = _listaPartidosConNombreUsuario

    fun pedirTodosLosPartidos() {
        firestore.collection("Partidos")
            .addSnapshotListener { querySnapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                val documents = mutableListOf<Partido>()
                if (querySnapshot != null) {
                    for (document in querySnapshot) {
                        val jugadores = document.get("jugadores") as List<String>
                        val creador = document.getString("creador")
                        val fecha = document.getString("fecha")
                        val hora = document.getString("hora")
                        val idPartido = document.getString("idPartido")
                        val nombreSitio = document.getString("nombreSitio")

                        if (creador != null && fecha != null && hora != null && idPartido != null && nombreSitio != null) {
                            val partido = Partido(creador, fecha, hora, idPartido, jugadores, nombreSitio)
                            documents.add(partido)
                        }
                    }
                }
                listaPartidos.value = documents
                asignarUsernameCreadorAPartido()
            }
    }

    private fun getNombreUserById(id: String, callback: (String) -> Unit) {
        firestore.collection("Users").whereEqualTo("userId", id).get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot != null) {
                    for (document in querySnapshot) {
                        val username = document.getString("username") ?: ""
                        callback(username)
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Handle failure if necessary
            }
    }

    private fun asignarUsernameCreadorAPartido() {
        _listaPartidosConNombreUsuario.value = mutableListOf()
        val currentPartidos = listaPartidos.value

        if (currentPartidos.isNotEmpty()) {
            currentPartidos.forEach { partido ->
                getNombreUserById(partido.creador) { nombreUsuario ->
                    val updatedList = _listaPartidosConNombreUsuario.value.toMutableList()
                    updatedList.add(Pair(partido, nombreUsuario))
                    _listaPartidosConNombreUsuario.value = updatedList
                }
            }
        }
    }
}
