package com.ccormor392.pruebaproyectofinal.presentation.misPartidos

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.ccormor392.pruebaproyectofinal.data.model.Partido
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel para la pantalla de Mis Partidos.
 * Esta ViewModel maneja la lógica relacionada con la pantalla de Mis Partidos, incluyendo
 * la obtención de la lista de partidos del usuario y la eliminación de partidos.
 */
@SuppressLint("MutableCollectionMutableState")
class MisPartidosViewModel : ViewModel() {
    // Firebase Firestore
    private val firestore = Firebase.firestore
    private val auth = Firebase.auth

    // Lista mutable de partidos disponibles
    private var _listaMisPartidos = MutableStateFlow(mutableListOf<Partido>())
    val listaMisPartidos: StateFlow<MutableList<Partido>> = _listaMisPartidos

    // Estado para mostrar el diálogo de confirmación de eliminación de partido
    var showAlert by mutableStateOf(false)
        private set
    // ID del partido a borrar
    private var idPartidoABorrar by mutableStateOf("")

    /**
     * Función para obtener todos los partidos del usuario desde Firestore.
     * Consulta Firestore para obtener la lista de partidos del usuario actual y actualiza [_listaMisPartidos].
     */
    fun pedirTodosLosPartidosDelUsuario() {
        // Se inicializa la lista de partidos antes de obtenerlos
        _listaMisPartidos.value = mutableListOf()
        // Consulta a Firestore para obtener los partidos del usuario actual
        firestore.collection("Partidos").whereEqualTo("creador", auth.currentUser!!.uid)
            .addSnapshotListener { querySnapshot, error ->
                if (error != null) {
                    // Si hay un error en la consulta, se ignora y se retorna
                    return@addSnapshotListener
                }
                // Lista mutable donde se almacenarán los partidos obtenidos de Firestore
                val documents = mutableListOf<Partido>()
                // Procesamiento de los documentos obtenidos de la consulta
                if (querySnapshot != null) {
                    for (document in querySnapshot) {
                        val jugadores = document.get("jugadores") as List<String>
                        val creador = document.getString("creador")
                        val fecha = document.getString("fecha")
                        val hora = document.getString("hora")
                        val idPartido = document.getString("idPartido")
                        val nombreSitio = document.getString("nombreSitio")

                        // Se verifica que los campos necesarios no sean nulos antes de crear el partido
                        if (creador != null && fecha != null && hora != null && idPartido != null && nombreSitio != null) {
                            val partido = Partido(creador, fecha, hora, idPartido, jugadores, nombreSitio = nombreSitio)
                            documents.add(partido)
                        }
                    }
                }
                // Se actualiza la lista de partidos con los documentos obtenidos
                _listaMisPartidos.value = documents
            }
    }

    /**
     * Función para borrar un partido de Firestore y actualizar la lista local de partidos.
     */
    fun borrarPartido() {
        // Consulta a Firestore para obtener el partido que se va a borrar
        firestore.collection("Partidos").whereEqualTo("idPartido", idPartidoABorrar)
            .addSnapshotListener { querySnapshot, error ->
                if (error != null) {
                    // Si hay un error en la consulta, se registra en el log y se ignora
                    Log.e(TAG, "Error al obtener el partido a borrar: $error")
                    return@addSnapshotListener
                }
                querySnapshot?.let { snapshot ->
                    // Bucle sobre los documentos obtenidos en la consulta
                    for (document in snapshot.documents) {
                        // Borrado del documento de Firebase
                        document.reference.delete()
                            .addOnSuccessListener {
                                // Éxito al borrar el partido de Firebase, se actualiza la lista local de partidos
                                val listaActualizada = _listaMisPartidos.value.toMutableList()
                                val partidoBorrado =
                                    listaActualizada.find { it.idPartido == idPartidoABorrar }
                                partidoBorrado?.let {
                                    listaActualizada.remove(it)
                                    _listaMisPartidos.value = listaActualizada
                                }
                            }
                            .addOnFailureListener { exception ->
                                // Manejo del fallo al borrar el partido de Firebase, se registra en el log
                                Log.e(TAG, "Error al borrar el partido de Firebase: $exception")
                            }
                    }
                }
            }
    }

    /**
     * Función para cerrar el diálogo de confirmación de eliminación de partido.
     */
    fun closeAlert() {
        showAlert = false
    }

    /**
     * Función para abrir el diálogo de confirmación de eliminación de partido.
     * @param idPartido ID del partido que se va a borrar.
     */
    fun openAlert(idPartido: String) {
        // Se establece el ID del partido que se va a borrar y se muestra el diálogo de confirmación
        idPartidoABorrar = idPartido
        showAlert = true
    }
}
