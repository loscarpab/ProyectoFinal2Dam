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
    // Firebase Firestore
    private val firestore = Firebase.firestore

    // Lista de partidos disponibles
    private var listaPartidos = MutableStateFlow (mutableListOf<Partido>())

    // Lista de partidos con nombre de usuario del creador
    var listaPartidosConNombreUsuario by mutableStateOf(mutableListOf<Pair<Partido,String>>())
        private set

    /**
     * Obtiene todos los partidos de Firestore.
     * Esta función consulta Firestore para obtener la lista de todos los partidos disponibles y luego asigna los nombres de usuario
     * del creador a cada partido.
     */
    fun pedirTodosLosPartidos() {
        listaPartidos.value = mutableListOf()
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

                        // Verifica si los campos necesarios no son nulos antes de crear el partido
                        if (creador != null && fecha != null && hora != null && idPartido!=null && nombreSitio!= null) {
                            val partido = Partido(creador, fecha, hora, idPartido, jugadores, nombreSitio)
                            documents.add(partido)
                        }
                    }
                }
                listaPartidos.value = documents
                asignarUsernameCreadorAPartido()
            }
    }

    /**
     * Obtiene el nombre de usuario dado un ID de usuario.
     *
     * @param id ID de usuario.
     * @param callback Callback para manejar el nombre de usuario obtenido.
     */
    private fun getNombreUserById(id: String, callback: (String) -> Unit) {
        firestore.collection("Users").whereEqualTo("userId", id)
            .addSnapshotListener { querySnapshot, error ->
                if (error != null) {
                    // Manejar el error aquí si es necesario
                    return@addSnapshotListener
                }
                if (querySnapshot != null) {
                    for (document in querySnapshot){
                        val username = document.getString("username") ?: ""
                        callback(username)
                    }
                }
            }
    }

    /**
     * Asigna el nombre de usuario del creador a cada partido.
     * Esta función itera sobre la lista de partidos disponibles y obtiene el nombre de usuario del creador para cada partido,
     * luego crea una lista de pares de partido y nombre de usuario y la asigna a [listaPartidosConNombreUsuario].
     */
    private fun asignarUsernameCreadorAPartido(){
        listaPartidosConNombreUsuario = mutableListOf()
        for (partido in listaPartidos.value) {
            // Obtener el nombre de usuario del creador y añadirlo a la lista de partidos con el nombre de usuario
            getNombreUserById(partido.creador) { nombreUsuario ->
                listaPartidosConNombreUsuario.add(Pair(partido, nombreUsuario))
            }
        }
    }
}
