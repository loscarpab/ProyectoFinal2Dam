package com.ccormor392.pruebaproyectofinal.presentation.amigos

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ccormor392.pruebaproyectofinal.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AmigosViewModel: ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val firestore = Firebase.firestore

    var nombre by mutableStateOf("")
        private set

    private val _users = MutableStateFlow(mutableListOf<User>())
    val users: StateFlow<MutableList<User>> = _users

    fun buscarAmigo() {
        viewModelScope.launch {
            try {
                val userList = mutableListOf<User>() // Lista temporal para almacenar los usuarios
                firestore.collection("Users")
                    .orderBy("username")
                    .startAt(nombre)
                    .endAt(nombre+'\uf8ff')
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            // Recupera los datos del usuario desde Firestore
                            val userId = document.getString("userId")
                            val email = document.getString("email")
                            val username = document.getString("username")
                            val partidosCreados = document.getLong("partidosCreados")
                            val avatar = document.getString("avatar")
                            if (userId != null && email != null && username != null && partidosCreados != null) {
                                val user = User(userId, email, username, partidosCreados, listOf(), avatar?:"")
                                userList.add(user)
                            }
                        }
                        // Asignar la nueva lista de usuarios al MutableStateFlow
                        _users.value = userList
                    }
                    .addOnFailureListener { exception ->
                        println("Error al obtener el documento: $exception")
                    }
            } catch (e: FirebaseFirestoreException) {
                println("Error al acceder a Firestore: ${e.message}")
            }
        }
    }

    /**
     * Permite al usuario unirse al partido actual.
     *
     * @param onSuccess Acción a realizar después de unirse exitosamente al partido.
     */
    fun agregarUsuario( idPersonaString: String) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            // Verifica si el usuario está autenticado y puede unirse al partido
            firestore.collection("Users")
                .whereEqualTo("userId", userId)
                .addSnapshotListener { querySnapshot, error ->
                    if (error != null) {
                        // Manejar el error aquí si es necesario
                        return@addSnapshotListener
                    }
                    if (querySnapshot != null) {
                        for (document in querySnapshot) {
                            val amigos = document.get("amigos") as? List<String>
                            val userId = auth.currentUser?.uid
                            if (amigos != null && userId != null && userId !in amigos) {
                                // El usuario no está en la lista de jugadores, se puede unir al partido
                                val partidoRef =
                                    firestore.collection("Users").document(document.id)
                                partidoRef.update("amigos", FieldValue.arrayUnion(idPersonaString))
                                    .addOnSuccessListener {
                                        // El usuario se ha unido al partido exitosamente
                                        //onSuccess()
                                    }
                                    .addOnFailureListener { exception ->
                                        // Manejar errores al unirse al partido
                                        println("Error al unirse al partido: $exception")
                                    }
                            } else if (amigos != null) {
                                if (userId!! in amigos) {
                                    // El usuario ya está en la lista de jugadores del partido
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
     * Permite al usuario eliminar a un amigo de su lista de amigos.
     *
     * @param idPersonaString ID de la persona que se desea desagregar.
     */
    fun desagregarUsuario(idPersonaString: String) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            // Verifica si el usuario está autenticado y puede eliminar un amigo
            firestore.collection("Users")
                .whereEqualTo("userId", userId)
                .addSnapshotListener { querySnapshot, error ->
                    if (error != null) {
                        // Manejar el error aquí si es necesario
                        println("Error al obtener los datos del usuario: $error")
                        return@addSnapshotListener
                    }
                    if (querySnapshot != null) {
                        for (document in querySnapshot) {
                            val amigos = document.get("amigos") as? List<String>
                            if (amigos != null && idPersonaString in amigos) {
                                // El amigo está en la lista de amigos, se puede desagregar
                                val userRef = firestore.collection("Users").document(document.id)
                                userRef.update("amigos", FieldValue.arrayRemove(idPersonaString))
                                    .addOnSuccessListener {
                                        // El amigo se ha desagregado exitosamente
                                        println("Amigo desagregado exitosamente.")
                                    }
                                    .addOnFailureListener { exception ->
                                        // Manejar errores al desagregar al amigo
                                        println("Error al desagregar al amigo: $exception")
                                    }
                            } else {
                                // El amigo no está en la lista de amigos
                                println("El amigo no está en la lista de amigos.")
                            }
                        }
                    }
                }
        } else {
            // El usuario no está autenticado
            println("El usuario no está autenticado.")
        }
    }




    /**
     * Actualiza el nombre de usuario.
     *
     * @param nombre Nuevo nombre de usuario a establecer.
     */
    fun changeNombre(nombre: String) {
        this.nombre = nombre
    }
    fun restart(){
        _users.value = mutableListOf<User>()
        nombre = ""
    }
}