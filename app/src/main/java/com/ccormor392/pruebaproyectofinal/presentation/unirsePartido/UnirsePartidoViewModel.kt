package com.ccormor392.pruebaproyectofinal.presentation.unirsePartido

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.ccormor392.pruebaproyectofinal.data.model.JugadorPartido
import com.ccormor392.pruebaproyectofinal.data.model.Partido
import com.ccormor392.pruebaproyectofinal.data.model.User
import com.ccormor392.pruebaproyectofinal.navigation.Routes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

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
    var clickable by mutableStateOf(true)
    var equipo2 by mutableStateOf<Boolean>(false)
        private set

    // Flujo mutable para almacenar el partido al que se va a unir el usuario
    private val _partido = MutableStateFlow(Partido())
    val partido: StateFlow<Partido> = _partido

    // Flujo mutable para almacenar el usuario del partido
    private val _user = MutableStateFlow(JugadorPartido())
    val user: StateFlow<JugadorPartido> = _user


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
                    }
                }
            }
    }

    /**
     * Recupera el nombre de los jugadores del partido y los almacena en el flujo de usuarios.
     */
    private fun recuperarNombreJugadores(
        userId: String,
        posicion: Int = 0,
        equipo: Boolean = true,
        onSuccess: () -> Unit
    ) {
        firestore.collection("Users").whereEqualTo("userId", userId).get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot != null) {
                    for (document in querySnapshot) {
                        val usuario = document.toObject(User::class.java)
                        _user.value = JugadorPartido(
                            usuario.userId,
                            usuario.username,
                            usuario.avatar,
                            posicion,
                            equipo
                        )
                        onSuccess()
                    }
                }
            }
    }


    /**
     * Permite al usuario unirse al partido actual.
     *
     * @param onSuccess Acción a realizar después de unirse exitosamente al partido.
     */
    fun unirseAPartido(
        onSuccess: () -> Unit,
        posicion: Int,
        equipo: Boolean,
        navController: NavController
    ) {
        val userId = auth.currentUser?.uid
        val userAnterior = _partido.value.jugadores.find { it.userId == userId }
        if (userId != null) {
            recuperarNombreJugadores(userId, posicion, equipo) {
                firestore.collection("Partidos")
                    .whereEqualTo("idPartido", _partido.value.idPartido).get()
                    .addOnSuccessListener { querySnapshot ->
                        if (querySnapshot != null) {
                            for (document in querySnapshot) {
                                val partido = document.toObject(Partido::class.java)
                                val jugadores = partido.jugadores
                                val posiciones = jugadores.filter { it.equipo == !equipo2 }.map { it.posicion }
                                val partidoRef = firestore.collection("Partidos").document(document.id)

                                if (posicion !in posiciones) {
                                    if (userAnterior != null) {
                                        val updatedJugadores = jugadores.map { jugador ->
                                            if (jugador.userId == userId) {
                                                jugador.copy(posicion = posicion, equipo = equipo)
                                            } else {
                                                jugador
                                            }
                                        }

                                        partidoRef.update("jugadores", updatedJugadores)
                                            .addOnSuccessListener {
                                                // El usuario se ha unido al partido exitosamente
                                                clickable = true
                                                onSuccess()
                                            }
                                            .addOnFailureListener { exception ->
                                                // Manejar errores al unirse al partido
                                                clickable = true
                                                println("Error al unirse al partido: $exception")
                                            }
                                    } else {
                                        val nuevoJugador = JugadorPartido(
                                            userId = userId,
                                            username = _user.value.username,
                                            avatar = _user.value.avatar,
                                            posicion = posicion,
                                            equipo = equipo
                                        )
                                        partidoRef.update("jugadores", FieldValue.arrayUnion(nuevoJugador))
                                            .addOnSuccessListener {
                                                // El usuario se ha unido al partido exitosamente
                                                clickable = true
                                                onSuccess()
                                            }
                                            .addOnFailureListener { exception ->
                                                // Manejar errores al unirse al partido
                                                clickable = true
                                                println("Error al unirse al partido: $exception")
                                            }
                                    }
                                } else {
                                    // Manejo de la navegación si la posición ya está ocupada
                                    clickable = true
                                    navController.navigate("${Routes.MiPerfil.route}/${jugadores.filter { it.equipo == !equipo2 }.first { it.posicion == posicion }.userId}")
                                }
                            }
                        } else {
                            clickable = true
                        }
                    }
                    .addOnFailureListener { exception ->
                        // Manejar errores de la consulta Firestore
                        clickable = true
                        println("Error en la consulta Firestore: $exception")
                    }
            }
        } else {
            clickable = true
        }
    }


    /**
     * Cierra la alerta mostrada al usuario.
     */
    fun closeAlert() {
        showAlert = false
    }

    fun recuperarFoto(index: Int, equipo2: Boolean): String? {
        return try {
            _partido.value.jugadores.filter { it.equipo == !equipo2 }.find { it.posicion == index }!!.avatar
        } catch (e: Exception) {
            null
        }

    }
    fun changeSegmentedButton(){
        equipo2 = !equipo2
    }
}
