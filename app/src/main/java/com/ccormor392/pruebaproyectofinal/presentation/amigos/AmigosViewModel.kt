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

/**
 * ViewModel para la gestión de amigos en la aplicación.
 *
 * @property auth Instancia de FirebaseAuth para autenticación.
 * @property firestore Instancia de FirebaseFirestore para acceder a la base de datos.
 * @property nombre Nombre del amigo a buscar.
 * @property users Estado que almacena los usuarios encontrados y si se siguen mutuamente.
 * @property isFollowing Estado que indica si el usuario actual sigue a otro usuario.
 * @property isLoading Estado de carga.
 * @property seguidores Estado que almacena el número de seguidores.
 */
class AmigosViewModel: ViewModel() {
    // Instancia de autenticación de Firebase
    private val auth: FirebaseAuth = Firebase.auth
    // Instancia de Firestore
    private val firestore = Firebase.firestore

    // Nombre del amigo a buscar, observable por la UI
    var nombre by mutableStateOf("")
        private set

    // Estado para almacenar los usuarios encontrados y si se siguen mutuamente
    private val _users = MutableStateFlow(mapOf<User, Boolean>())
    val users: StateFlow<Map<User, Boolean>> = _users

    // Estado para verificar si el usuario actual sigue a otro usuario
    private val _isFollowing = MutableStateFlow(false)
    val isFollowing: StateFlow<Boolean> = _isFollowing

    // Estado de carga
    private var _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Estado para el número de seguidores
    private val _seguidores = MutableStateFlow(0)
    val seguidores: StateFlow<Int> = _seguidores

    /**
     * Busca amigos en Firestore cuyo nombre coincida con el valor de [nombre].
     */
    fun buscarAmigo() {
        viewModelScope.launch {
            try {
                // Inicia el estado de carga
                _isLoading.value = true
                // Lista para almacenar los usuarios encontrados
                val userList = mutableListOf<User>()
                // Realiza la consulta a Firestore
                firestore.collection("Users")
                    .orderBy("username")
                    .startAt(nombre)
                    .endAt(nombre + '\uf8ff')
                    .get()
                    .addOnSuccessListener { documents ->
                        // Recorre los documentos obtenidos
                        for (document in documents) {
                            val userId = document.getString("userId")
                            val email = document.getString("email")
                            val username = document.getString("username")
                            val partidosCreados = document.getLong("partidosCreados")
                            val avatar = document.getString("avatar")
                            // Verifica que los campos necesarios no sean nulos
                            if (userId != null && email != null && username != null && partidosCreados != null) {
                                // Crea un objeto User y lo agrega a la lista
                                val user = User(userId, email, username, partidosCreados, listOf(), avatar ?: "")
                                userList.add(user)
                            }
                        }
                        // Finaliza el estado de carga
                        _isLoading.value = false
                        // Verifica si los usuarios encontrados son seguidos por el usuario actual
                        checkIfFollowing(userList)
                    }
                    .addOnFailureListener { exception ->
                        // Maneja el error en caso de fallo en la consulta
                        println("Error al obtener el documento: $exception")
                    }
            } catch (e: FirebaseFirestoreException) {
                // Maneja la excepción de Firestore
                println("Error al acceder a Firestore: ${e.message}")
            }
        }
    }

    /**
     * Agrega un usuario a la lista de amigos del usuario actual.
     *
     * @param idPersonaString El ID del usuario a agregar.
     */
    fun agregarUsuario(idPersonaString: String) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            // Consulta para obtener el usuario actual
            firestore.collection("Users")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        // Obtiene el objeto User del documento
                        val usuario = document.toObject(User::class.java)
                        val amigos = usuario.amigos
                        // Verifica que el usuario no esté ya en la lista de amigos
                        if (idPersonaString !in amigos) {
                            val userRef = firestore.collection("Users").document(document.id)
                            // Actualiza la lista de amigos del usuario actual
                            userRef.update("amigos", FieldValue.arrayUnion(idPersonaString))
                                .addOnSuccessListener {
                                    // Actualiza el estado de seguimiento y los seguidores
                                    _isFollowing.value = true
                                    actualizarSeguidores(idPersonaString)
                                    checkIfFollowing(_users.value.keys.toMutableList())
                                    Log.d("AMIGO", "Agregado correctamente")
                                }
                                .addOnFailureListener { exception ->
                                    // Maneja el error en caso de fallo en la actualización
                                    Log.d("AMIGO", "Error al agregar al amigo: $exception")
                                }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    // Maneja el error en caso de fallo en la consulta
                    Log.d("AMIGO", "Error al obtener los datos del usuario: $exception")
                }
        }
    }

    /**
     * Elimina un usuario de la lista de amigos del usuario actual.
     *
     * @param idPersonaString El ID del usuario a eliminar.
     */
    fun desagregarUsuario(idPersonaString: String) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            // Consulta para obtener el usuario actual
            firestore.collection("Users")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        // Obtiene el objeto User del documento
                        val usuario = document.toObject(User::class.java)
                        val amigos = usuario.amigos
                        // Verifica que el usuario esté en la lista de amigos
                        if (idPersonaString in amigos) {
                            val userRef = firestore.collection("Users").document(document.id)
                            // Actualiza la lista de amigos del usuario actual
                            userRef.update("amigos", FieldValue.arrayRemove(idPersonaString))
                                .addOnSuccessListener {
                                    // Actualiza el estado de seguimiento y los seguidores
                                    _isFollowing.value = false
                                    actualizarSeguidores(idPersonaString)
                                    checkIfFollowing(_users.value.keys.toMutableList())
                                    Log.d("AMIGO", "Amigo desagregado exitosamente.")
                                }
                                .addOnFailureListener { exception ->
                                    // Maneja el error en caso de fallo en la actualización
                                    Log.d("AMIGO", "Error al desagregar al amigo: $exception")
                                }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    // Maneja el error en caso de fallo en la consulta
                    Log.d("AMIGO", "Error al obtener los datos del usuario: $exception")
                }
        }
    }

    /**
     * Actualiza el número de seguidores de un usuario.
     *
     * @param idPersonaString El ID del usuario cuyos seguidores se actualizarán.
     */
    fun actualizarSeguidores(idPersonaString: String) {
        // Consulta para obtener los usuarios que siguen al usuario dado
        firestore.collection("Users").whereArrayContains("amigos", idPersonaString)
            .get()
            .addOnSuccessListener { querySnapshot ->
                // Actualiza el estado de seguidores con el tamaño del resultado
                _seguidores.value = querySnapshot.size()
            }
            .addOnFailureListener { exception ->
                // Maneja el error en caso de fallo en la consulta
                Log.d("AMIGO", "Error al actualizar el número de seguidores: $exception")
            }
    }

    /**
     * Verifica si el usuario actual sigue a otro usuario.
     *
     * @param idPersonaString El ID del usuario a verificar.
     */
    fun checkIfFollowing(idPersonaString: String) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            // Consulta para obtener el usuario actual
            firestore.collection("Users")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        // Obtiene el objeto User del documento
                        val usuario = document.toObject(User::class.java)
                        val amigos = usuario.amigos
                        // Actualiza el estado de seguimiento
                        _isFollowing.value = amigos.contains(idPersonaString)
                    }
                }
        }
    }

    /**
     * Verifica si el usuario actual sigue a los usuarios de una lista.
     *
     * @param lista Lista de usuarios a verificar.
     */
    private fun checkIfFollowing(lista: MutableList<User>) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            // Consulta para obtener el usuario actual
            firestore.collection("Users")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    // Obtiene el objeto User del documento
                    val usuario = querySnapshot.documents[0].toObject(User::class.java)!!
                    val amigos = usuario.amigos
                    // Mapea la lista de usuarios a sus IDs
                    val ids = lista.map { it.userId }
                    // Filtra los IDs de los amigos que están en la lista
                    val amigosEnBusqueda = amigos.filter { it in ids }
                    // Actualiza el estado de usuarios con la información de seguimiento
                    _users.value = lista.associateWith { it.userId in amigosEnBusqueda }
                    println(_users.value)
                }
        }
    }

    /**
     * Cambia el valor del nombre del amigo a buscar.
     *
     * @param nombre El nuevo valor del nombre.
     */
    fun changeNombre(nombre: String) {
        // Actualiza el nombre del amigo a buscar
        this.nombre = nombre
    }

    /**
     * Reinicia los valores del ViewModel.
     */
    fun restart() {
        // Reinicia el estado de los usuarios, nombre y estado de carga
        _users.value = mapOf()
        nombre = ""
        _isLoading.value = true
    }
}
