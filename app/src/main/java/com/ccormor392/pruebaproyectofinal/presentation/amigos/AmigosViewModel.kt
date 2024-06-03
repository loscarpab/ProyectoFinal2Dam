package com.ccormor392.pruebaproyectofinal.presentation.amigos

import android.util.Log
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

    private val _isFollowing = MutableStateFlow(false)
    val isFollowing: StateFlow<Boolean> = _isFollowing

    private var _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _seguidores = MutableStateFlow(0)
    val seguidores: StateFlow<Int> = _seguidores

    fun buscarAmigo() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val userList = mutableListOf<User>()
                firestore.collection("Users")
                    .orderBy("username")
                    .startAt(nombre)
                    .endAt(nombre+'\uf8ff')
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
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
                        _isLoading.value = false
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

    fun agregarUsuario(idPersonaString: String) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("Users")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        val amigos = document.get("amigos") as? List<String>
                        if (amigos != null && idPersonaString !in amigos) {
                            val userRef = firestore.collection("Users").document(document.id)
                            userRef.update("amigos", FieldValue.arrayUnion(idPersonaString))
                                .addOnSuccessListener {
                                    _isFollowing.value = true
                                    actualizarSeguidores(idPersonaString)
                                    Log.d("AMIGO", "Agregado correctamente")
                                }
                                .addOnFailureListener { exception ->
                                    Log.d("AMIGO", "Error al agregar al amigo: $exception")
                                }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("AMIGO", "Error al obtener los datos del usuario: $exception")
                }
        }
    }

    fun desagregarUsuario(idPersonaString: String) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("Users")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        val amigos = document.get("amigos") as? List<String>
                        if (amigos != null && idPersonaString in amigos) {
                            val userRef = firestore.collection("Users").document(document.id)
                            userRef.update("amigos", FieldValue.arrayRemove(idPersonaString))
                                .addOnSuccessListener {
                                    _isFollowing.value = false
                                    actualizarSeguidores(idPersonaString)
                                    Log.d("AMIGO", "Amigo desagregado exitosamente.")
                                }
                                .addOnFailureListener { exception ->
                                    Log.d("AMIGO", "Error al desagregar al amigo: $exception")
                                }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("AMIGO", "Error al obtener los datos del usuario: $exception")
                }
        }
    }

     fun actualizarSeguidores(idPersonaString: String) {
        firestore.collection("Users").whereArrayContains("amigos", idPersonaString)
            .get()
            .addOnSuccessListener { querySnapshot ->
                _seguidores.value = querySnapshot.size()
            }
            .addOnFailureListener { exception ->
                Log.d("AMIGO", "Error al actualizar el número de seguidores: $exception")
            }
    }

    fun checkIfFollowing(idPersonaString: String) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("Users")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        val amigos = document.get("amigos") as? List<String>
                        _isFollowing.value = amigos?.contains(idPersonaString) ?: false
                    }
                }
        }
    }

    fun changeNombre(nombre: String) {
        this.nombre = nombre
    }

    fun restart(){
        _users.value = mutableListOf<User>()
        nombre = ""
        _isLoading.value = true
    }
}
