package com.ccormor392.pruebaproyectofinal.presentation.unirsePartido

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ccormor392.pruebaproyectofinal.data.model.Partido
import com.ccormor392.pruebaproyectofinal.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel que gestiona la funcionalidad de unirse a un partido existente.
 * Proporciona la lógica para obtener información del partido, incluidos los jugadores,
 * y permite al usuario unirse al partido si es posible.
 *
 * @property showAlert Estado de la alerta que indica si el usuario ya está entre los jugadores del partido.
 * @property partido Flujo de estado que contiene la información del partido al que se va a unir el usuario.
 * @property users Flujo de estado que contiene la lista de usuarios del partido.
 */
class UnirsePartidoViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val firestore = Firebase.firestore

    // Estado para mostrar la alerta
    var showAlert by mutableStateOf(false)
        private set

    // Flujo mutable para almacenar el partido al que se va a unir el usuario
    private val _partido = MutableStateFlow(Partido())
    val partido: StateFlow<Partido> = _partido

    // Flujo mutable para almacenar la lista de usuarios del partido
    private val _users = MutableStateFlow(mutableListOf<User>())
    val users: StateFlow<MutableList<User>> = _users

    /**
     * Obtiene un partido específico de Firestore basado en su ID.
     *
     * @param idPartido ID del partido que se va a obtener.
     */
    fun getPartidobyId(idPartido: String) {
        // Obtiene la información del partido desde Firestore
        firestore.collection("Partidos").whereEqualTo("idPartido", idPartido)
            .addSnapshotListener { querySnapshot, error ->
                if (error != null) {
                    // Manejar el error aquí si es necesario
                    return@addSnapshotListener
                }
                if (querySnapshot != null) {
                    for (document in querySnapshot) {
                        // Recupera los datos del partido desde Firestore
                        val partidos = document.toObject(Partido::class.java)
                        _partido.value = partidos
                        recuperarNombreJugadores()

                    }
                }
            }
    }

    /**
     * Recupera el nombre de los jugadores del partido y los almacena en el flujo de usuarios.
     */
    private fun recuperarNombreJugadores() {
        viewModelScope.launch {
            // Limpia la lista de usuarios antes de agregar nuevos
            _users.value = mutableListOf()
            for (idJugador in _partido.value.jugadores) {
                firestore.collection("Users").whereEqualTo("userId", idJugador)
                    .addSnapshotListener { querySnapshot, error ->
                        if (error != null) {
                            // Manejar el error aquí si es necesario
                            return@addSnapshotListener
                        }
                        if (querySnapshot != null) {
                            for (document in querySnapshot) {
                                // Recupera los datos del usuario desde Firestore
                                val userId = document.getString("userId")
                                val email = document.getString("email")
                                val username = document.getString("username")
                                val partidosCreados = document.getLong("partidosCreados")
                                val amigos = document.get("amigos") as List<String>
                                val avatar = document.getString("avatar")
                                if (userId != null && email != null && username != null && partidosCreados != null) {
                                    val usuario = User(userId, email, username, partidosCreados, amigos?: mutableListOf(), avatar?:"")
                                    _users.value.add(usuario)
                                }
                            }
                        }
                    }
            }
        }
    }

    /**
     * Permite al usuario unirse al partido actual.
     *
     * @param onSuccess Acción a realizar después de unirse exitosamente al partido.
     */
    fun unirseAPartido(onSuccess: () -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            // Verifica si el usuario está autenticado y puede unirse al partido
            firestore.collection("Partidos")
                .whereEqualTo("idPartido", _partido.value.idPartido)
                .addSnapshotListener { querySnapshot, error ->
                    if (error != null) {
                        // Manejar el error aquí si es necesario
                        return@addSnapshotListener
                    }
                    if (querySnapshot != null) {
                        for (document in querySnapshot) {
                            val jugadores = document.get("jugadores") as? List<String>
                            val userId = auth.currentUser?.uid
                            if (jugadores != null && userId != null && userId !in jugadores) {
                                // El usuario no está en la lista de jugadores, se puede unir al partido
                                val partidoRef =
                                    firestore.collection("Partidos").document(document.id)
                                partidoRef.update("jugadores", FieldValue.arrayUnion(userId))
                                    .addOnSuccessListener {
                                        // El usuario se ha unido al partido exitosamente
                                        onSuccess()
                                    }
                                    .addOnFailureListener { exception ->
                                        // Manejar errores al unirse al partido
                                        println("Error al unirse al partido: $exception")
                                    }
                            } else if (jugadores != null) {
                                if (userId!! in jugadores) {
                                    // El usuario ya está en la lista de jugadores del partido
                                    showAlert = true
                                } else {
                                    // El usuario no está autenticado o ya está en la lista de jugadores
                                    println("El usuario no está autenticado o ya está en la lista de jugadores.")
                                }
                            }
                        }
                    }
                }
        }
    }

    /**
     * Cierra la alerta mostrada al usuario.
     */
    fun closeAlert() {
        showAlert = false
    }
}
