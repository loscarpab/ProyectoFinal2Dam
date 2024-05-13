package com.ccormor392.pruebaproyectofinal.presentation.manejoDeUsuarios


import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ccormor392.pruebaproyectofinal.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * ViewModel responsable de gestionar la lógica de autenticación de usuarios.
 * Proporciona funciones para el inicio de sesión y registro de usuarios utilizando Firebase Auth.
 * Mantiene el estado de la UI relevante para la autenticación, como los campos de entrada y las alertas.
 *
 * @property auth Instancia de FirebaseAuth utilizada para las operaciones de autenticación.
 * @property showAlert Estado que determina si se debe mostrar una alerta de error en la UI.
 * @property email Email del usuario, utilizado para el inicio de sesión y registro.
 * @property password Contraseña del usuario, utilizada para el inicio de sesión y registro.
 * @property userName Nombre de usuario, utilizado solo en el proceso de registro.
 */
class LoginViewModel : ViewModel() {
    //Definición de variables y funciones para manejar el inicio de sesión y registro de usuarios.

    private val auth: FirebaseAuth = Firebase.auth
    private val firestore = Firebase.firestore
    private val storageRef = Firebase.storage.reference

    var showAlert by mutableStateOf(false)
        private set
    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var userName by mutableStateOf("")
        private set
    var imageUri by mutableStateOf<Uri?>(null)
        private set

    private val _usuarioAutenticado = MutableStateFlow(User("", "", ""))
    val usuarioAutenticado: StateFlow<User> = _usuarioAutenticado


    /**
     * Intenta iniciar sesión con el email y la contraseña proporcionados.
     * Si el inicio de sesión es exitoso, ejecuta la acción de éxito proporcionada.
     * En caso de error, actualiza el estado para mostrar una alerta.
     *
     * @param onSuccess Acción a ejecutar si el inicio de sesión es exitoso.
     */
    fun login(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                // Utiliza el servicio de autenticación de Firebase para validar al usuario por email y contraseña
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            onSuccess()
                        } else {
                            Log.d("ERROR EN FIREBASE", "Usuario y/o contrasena incorrectos")
                            showAlert = true
                        }
                    }
            } catch (e: Exception) {
                Log.d("ERROR EN JETPACK", "ERROR: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Crea un nuevo usuario con el email y la contraseña proporcionados.
     * Si el registro es exitoso, guarda la información del usuario y ejecuta la acción de éxito proporcionada.
     * En caso de error, actualiza el estado para mostrar una alerta.
     *
     * @param onSuccess Acción a ejecutar si el registro es exitoso.
     */
    fun createUser(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                //Utiliza el servicio de autenticación de Firebase para registrar al usuario por email y contraseña
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            //Si se realiza con éxito, almacenamos el usuario en la colección "Users"
                            saveUser(userName)
                            onSuccess()
                        } else {
                            Log.d("ERROR EN FIREBASE", "Error al crear usuario")
                            showAlert = true
                        }
                    }
            } catch (e: Exception) {
                Log.d("ERROR CREAR USUARIO", "ERROR: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Guarda la información del usuario recién registrado en Firestore.
     *
     * @param username Nombre de usuario a guardar.
     */
    private fun saveUser(username: String) {
        val id = auth.currentUser?.uid
        val email = auth.currentUser?.email

        viewModelScope.launch(Dispatchers.IO) {
            val user = User(
                userId = id.toString(),
                email = email.toString(),
                username = username
            )
            //Añade el usuario a la colección "Users" en la base de datos Firestore
            firestore.collection("Users")
                .add(user)
                .addOnSuccessListener {
                    Log.d(
                        "GUARDAR OK",
                        "Se guardó el usuario correctamente en Firestore"
                    )
                }
                .addOnFailureListener { Log.d("ERROR AL GUARDAR", "ERROR al guardar en Firestore") }
        }
    }


    fun uploadImageToStorage(filename: Uri?) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (filename != null) {
                    var ultimabarra = filename.toString().lastIndexOf("%2F") + 3
                    var uri = storageRef.child("images").child(auth.uid.toString())
                        .child(filename.toString().substring(ultimabarra)).putFile(filename)
                        .await().storage.downloadUrl.await()
                    imageUri = uri
                    if (imageUri != null) {
                        firestore.collection("Users")
                            .whereEqualTo("userId", auth.uid)
                            .addSnapshotListener { querySnapshot, error ->
                                if (error != null) {
                                    // Manejar el error aquí si es necesario
                                    return@addSnapshotListener
                                }
                                if (querySnapshot != null) {
                                    for (document in querySnapshot) {
                                        // El usuario no está en la lista de jugadores, se puede unir al partido
                                        val partidoRef =
                                            firestore.collection("Users").document(document.id)
                                        partidoRef.update("avatar", imageUri)
                                            .addOnSuccessListener {
                                                // El usuario se ha cambiado el avatar exitosamente
                                                //onSuccess()
                                            }
                                            .addOnFailureListener { exception ->
                                                // Manejar errores al cambiar el partido
                                                println("Error al cambiar el partido: $exception")
                                            }

                                    }
                                }
                            }
                    }
                }

            } catch (e: Exception) {
            }
        }

    }

    /**
     * Cierra el diálogo de alerta de error mostrada en la UI.
     */
    fun closeAlert() {
        showAlert = false
    }

    /**
     * Actualiza el email del usuario.
     *
     * @param email Nuevo email a establecer.
     */
    fun changeEmail(email: String) {
        this.email = email
    }

    /**
     * Actualiza la contraseña del usuario.
     *
     * @param password Nueva contraseña a establecer.
     */
    fun changePassword(password: String) {
        this.password = password
    }

    /**
     * Actualiza el nombre de usuario.
     *
     * @param userName Nuevo nombre de usuario a establecer.
     */
    fun changeUserName(userName: String) {
        this.userName = userName
    }

    /**
     * Cierra la sesión del usuario actual en Firebase Auth.
     */
    fun signOut(onSuccess: () -> Unit) {
        auth.signOut()
        onSuccess()
    }

    /**
     * Recupera los datos del usuario ya autenticado
     */
    fun conseguirDatosUsuarioAutenticado() {
        // Realizar una consulta para obtener el documento del usuario basado en el nombre de usuario
        firestore.collection("Users").whereEqualTo("userId", auth.currentUser!!.uid)
            .addSnapshotListener { querySnapshot, error ->
                if (error != null) {
                    // Manejar el error aquí si es necesario
                    return@addSnapshotListener
                }
                if (querySnapshot != null) {
                    for (document in querySnapshot) {
                        val id = document.getString("userId")
                        val email = document.getString("email")
                        val username = document.getString("username")
                        val partidosCreados = document.getLong("partidosCreados")
                        var avatar = document.getString("avatar")
                        if (avatar == null){
                            avatar = ""
                        }
                        _usuarioAutenticado.value =
                            User(id!!, email!!, username!!, partidosCreados!!, avatar = avatar)
                        imageUri = avatar.toUri()
                    }
                }
            }
    }
}