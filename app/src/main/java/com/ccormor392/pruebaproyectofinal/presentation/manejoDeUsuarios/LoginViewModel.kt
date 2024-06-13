package com.ccormor392.pruebaproyectofinal.presentation.manejoDeUsuarios

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ccormor392.pruebaproyectofinal.data.model.Partido
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
import java.util.Date
import java.util.Locale

/**
 * ViewModel responsable de gestionar la lógica de autenticación de usuarios.
 * Proporciona funciones para el inicio de sesión y registro de usuarios utilizando Firebase Auth.
 * Mantiene el estado de la UI relevante para la autenticación, como los campos de entrada y las alertas.
 *
 * @property auth Instancia de FirebaseAuth utilizada para las operaciones de autenticación.
 * @property showAlert Estado que determina si se debe mostrar una alerta de error en la UI.
 * @property showLoading Estado que determina si se debe mostrar un indicador de carga en la UI.
 * @property email Email del usuario, utilizado para el inicio de sesión y registro.
 * @property password Contraseña del usuario, utilizada para el inicio de sesión y registro.
 * @property userName Nombre de usuario, utilizado solo en el proceso de registro.
 * @property imageUri URI de la imagen seleccionada por el usuario para su avatar.
 * @property segmentedButton Estado del botón segmentado para alternar entre partidos creados y próximos.
 * @property seguidores Flujo de estado que contiene el número de seguidores del usuario autenticado.
 * @property listaPartidosPasados Flujo de estado que contiene la lista de partidos pasados del usuario autenticado.
 * @property listaPartidosProximamente Flujo de estado que contiene la lista de partidos próximos del usuario autenticado.
 * @property soySeguidor Estado que indica si el usuario autenticado sigue al usuario visitado.
 * @property usuarioAutenticado Flujo de estado que contiene la información del usuario autenticado.
 */
class LoginViewModel : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth
    private val firestore = Firebase.firestore
    private val storageRef = Firebase.storage.reference

    var showAlert by mutableStateOf(false)
        private set
    var showLoading by mutableStateOf(false)
        private set
    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var userName by mutableStateOf("")
        private set
    var imageUri by mutableStateOf<Uri?>(null)
        private set
    var segmentedButton by mutableStateOf(true)
        private set

    private val _seguidores = MutableStateFlow(0)
    val seguidores: StateFlow<Int> = _seguidores

    private val _listaPartidosPasados = MutableStateFlow(mutableListOf<Partido>())
    val listaPartidosPasados: StateFlow<MutableList<Partido>> = _listaPartidosPasados

    private val _listaPartidosProximamente = MutableStateFlow(mutableListOf<Partido>())
    val listaPartidosProximamente: StateFlow<MutableList<Partido>> = _listaPartidosProximamente

    private var soySeguidor by mutableStateOf(false)

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
        showLoading = true
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            onSuccess()
                        } else {
                            Log.d("ERROR EN FIREBASE", "Usuario y/o contraseña incorrectos")
                            showLoading = false
                            showAlert = true
                        }
                    }
            } catch (e: Exception) {
                showLoading = false
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
        showLoading = true
        viewModelScope.launch {
            try {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            saveUser(userName.lowercase(Locale.ROOT))
                            showLoading = false
                            onSuccess()
                        } else {
                            Log.d("ERROR EN FIREBASE", "Error al crear usuario")
                            showAlert = true
                            showLoading = false
                        }
                    }
            } catch (e: Exception) {
                showLoading = false
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
                username = username,
                amigos = mutableListOf(),
                avatar = "https://firebasestorage.googleapis.com/v0/b/proyectofinal-f110d.appspot.com/o/images%2FdefaultAvatar.png?alt=media&token=36c16579-0a2e-4f75-a75b-1b2a37e6f732"
            )

            firestore.collection("Users")
                .add(user)
                .addOnSuccessListener {
                    Log.d("GUARDAR OK", "Se guardó el usuario correctamente en Firestore")
                }
                .addOnFailureListener { Log.d("ERROR AL GUARDAR", "ERROR al guardar en Firestore") }
        }
    }

    /**
     * Verifica si el usuario actual es un administrador.
     *
     * @return true si el usuario es administrador, false de lo contrario.
     */
    fun isAdmin(): Boolean {
        val email = auth.currentUser?.email
        return email == "admin@admin.com"
    }

    /**
     * Sube la imagen seleccionada por el usuario al almacenamiento de Firebase y actualiza el avatar del usuario en Firestore.
     *
     * @param filename URI de la imagen seleccionada por el usuario.
     */
    fun uploadImageToStorage(filename: Uri?) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (filename != null) {
                    val uri = storageRef.child("images").child(auth.uid.toString())
                        .child("${auth.uid.toString()}foto").putFile(filename)
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
                                        val partidoRef =
                                            firestore.collection("Users").document(document.id)
                                        partidoRef.update("avatar", imageUri)
                                            .addOnSuccessListener {
                                                // El avatar del usuario se ha actualizado exitosamente
                                            }
                                            .addOnFailureListener { exception ->
                                                Log.e("ERROR", "Error al actualizar el avatar del usuario: $exception")
                                            }

                                    }
                                }
                            }
                    }
                }

            } catch (e: Exception) {
                Log.e("ERROR", "Error al subir imagen al almacenamiento: $e")
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
     *
     * @param onSuccess Acción a ejecutar si el cierre de sesión es exitoso.
     */
    fun signOut(onSuccess: () -> Unit) {
        auth.signOut()
        onSuccess()
    }

    /**
     * Obtiene los datos del usuario autenticado desde Firestore.
     * Si se proporciona un ID específico, busca los datos de ese usuario; de lo contrario, obtiene los datos del usuario actualmente autenticado.
     *
     * @param idparam ID del usuario a buscar (opcional).
     */
    fun conseguirDatosUsuarioAutenticado(idparam: String? = null) {
        val idAbuscar = idparam ?: auth.currentUser?.uid
        firestore.collection("Users").whereEqualTo("userId", idAbuscar)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    val id = document.getString("userId")!!
                    val email = document.getString("email")!!
                    val username = document.getString("username")!!
                    val partidosCreados = document.getLong("partidosCreados") ?: 0
                    val avatar = document.getString("avatar") ?: ""
                    val amigos = document.get("amigos") as? List<String> ?: mutableListOf()

                    _usuarioAutenticado.value = User(id, email, username, partidosCreados, amigos, avatar)
                    imageUri = avatar.toUri()
                    buscarSeguidores(idAbuscar!!)
                    soySeguidor(idAbuscar)
                }
            }
    }

    /**
     * Recupera los partidos creados por el usuario autenticado desde Firestore.
     * Si se proporciona un ID específico, recupera los partidos creados por ese usuario; de lo contrario, recupera los partidos del usuario actualmente autenticado.
     *
     * @param idparam ID del usuario a buscar (opcional).
     */
    fun recuperarPartidos(idparam: String? = null){
        val idAbuscar = idparam ?: auth.currentUser?.uid
        _listaPartidosPasados.value = mutableListOf()
        _listaPartidosProximamente.value = mutableListOf()
        viewModelScope.launch {
            try {
                firestore.collection("Partidos").whereEqualTo("creador", idAbuscar)
                    .get()
                    .addOnSuccessListener {querySnapshot ->
                        for(document in querySnapshot){
                            val partido = document.toObject(Partido::class.java)
                            if (partido.timestamp <= Date(System.currentTimeMillis())){
                                _listaPartidosPasados.value.add(partido)
                            }
                            else{
                                _listaPartidosProximamente.value.add(partido)
                            }
                        }
                    }
            }catch (e:Exception){
                Log.e("ERROR", "Error al recuperar partidos: ${e.message}")
            }
        }

    }

    /**
     * Busca y cuenta los seguidores de un usuario específico en Firestore.
     *
     * @param idparam ID del usuario del cual se van a contar los seguidores.
     */
    private fun buscarSeguidores(idparam: String) {
        firestore.collection("Users").whereArrayContains("amigos", idparam)
            .get()
            .addOnSuccessListener { querySnapshot ->
                _seguidores.value = querySnapshot?.count() ?: 0
            }
            .addOnFailureListener { e ->
                Log.e("ERROR", "Error al buscar seguidores: $e")
            }
    }

    /**
     * Verifica si el usuario autenticado sigue a otro usuario específico en Firestore.
     *
     * @param idparam ID del usuario que se desea verificar si es seguido por el usuario autenticado.
     */
    private fun soySeguidor(idparam: String) {
        firestore.collection("Users").whereEqualTo("userId", auth.currentUser?.uid)
            .whereArrayContains("amigos", idparam)
            .get()
            .addOnSuccessListener { querySnapshot ->
                soySeguidor = !querySnapshot.isEmpty
            }
            .addOnFailureListener { e ->
                Log.e("ERROR", "Error al verificar si soy seguidor: $e")
            }
    }

    /**
     * Alterna el estado del botón segmentado entre los partidos creados y los próximos.
     */
    fun changeSegmentedButton(){
        segmentedButton = !segmentedButton
    }

    /**
     * Verifica si el perfil actualmente visualizado es el perfil del usuario autenticado.
     *
     * @return true si es el perfil del usuario autenticado, false de lo contrario.
     */
    fun esMiPerfil(): Boolean {
        return auth.currentUser?.uid == usuarioAutenticado.value.userId
    }

    /**
     * Establece el estado de carga en falso.
     * Se utiliza para ocultar el indicador de carga cuando no es necesario mostrarlo.
     */
    fun showLoadingtoFalse(){
        showLoading = false
    }
}
