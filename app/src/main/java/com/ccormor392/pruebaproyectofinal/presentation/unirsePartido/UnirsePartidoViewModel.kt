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
 * @property showAlert Estado que indica si se debe mostrar la alerta al usuario.
 * @property partido Flujo de estado que contiene la información del partido al que se unirá el usuario.
 * @property user Flujo de estado que contiene la información del usuario en el contexto del partido.
 */
class UnirsePartidoViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val firestore = Firebase.firestore

    // Estado para mostrar la alerta de jugador ya unido
    var showAlert by mutableStateOf(false)
        private set

    // Estado para controlar la activación de clics en la interfaz
    var clickable by mutableStateOf(true)

    // Estado que indica el equipo seleccionado por el usuario
    var equipo2 by mutableStateOf(false)
        private set

    // Flujo mutable para almacenar el partido al que se unirá el usuario
    private val _partido = MutableStateFlow(Partido())
    val partido: StateFlow<Partido> = _partido

    // Flujo mutable para almacenar el usuario del partido
    private val _user = MutableStateFlow(JugadorPartido())
    val user: StateFlow<JugadorPartido> = _user

    /**
     * Obtiene la información de un partido específico desde Firestore basado en su ID.
     *
     * @param idPartido ID del partido que se desea obtener.
     */
    fun getPartidobyId(idPartido: String) {
        // Escucha los cambios en el documento del partido en Firestore
        firestore.collection("Partidos").whereEqualTo("idPartido", idPartido)
            .addSnapshotListener { querySnapshot, error ->
                if (error != null) {
                    // Manejar errores si ocurrieron durante la consulta
                    return@addSnapshotListener
                }
                if (querySnapshot != null) {
                    for (document in querySnapshot) {
                        // Recupera y actualiza la información del partido
                        val partidos = document.toObject(Partido::class.java)
                        _partido.value = partidos
                    }
                }
            }
    }

    /**
     * Recupera el nombre de usuario y otros detalles del jugador desde Firestore
     * y los almacena en el flujo de usuario (_user).
     *
     * @param userId ID del usuario cuyo nombre se desea recuperar.
     * @param posicion Posición del jugador en el partido.
     * @param equipo Equipo al que pertenece el jugador.
     * @param onSuccess Acción a realizar después de recuperar los datos del jugador.
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
                        // Actualiza la información del jugador en el flujo de usuario
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
     * @param posicion Posición del jugador en el partido.
     * @param equipo Equipo al que pertenece el jugador.
     * @param navController Controlador de navegación para gestionar las transiciones entre pantallas.
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

                                        // Actualiza la lista de jugadores en Firestore
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
                                        // Añade un nuevo jugador a la lista de jugadores en Firestore
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
                                    // Navega a la pantalla de perfil del jugador si la posición está ocupada
                                    clickable = true
                                    navController.navigate("${Routes.MiPerfil.route}/${jugadores.filter { it.equipo == !equipo2 }.first { it.posicion == posicion }.userId}")
                                }
                            }
                        } else {
                            clickable = true
                        }
                    }
                    .addOnFailureListener { exception ->
                        // Manejar errores al consultar Firestore
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

    /**
     * Recupera el enlace de la foto de un jugador específico en el partido.
     *
     * @param index Índice del jugador en la lista de jugadores.
     * @param equipo2 Indica si se está mostrando el equipo 2 en la interfaz.
     * @return Enlace de la foto del jugador o null si no se encuentra.
     */
    fun recuperarFoto(index: Int, equipo2: Boolean): String? {
        return try {
            _partido.value.jugadores.filter { it.equipo == !equipo2 }.find { it.posicion == index }!!.avatar
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Cambia el estado del equipo seleccionado (Equipo 1 o Equipo 2).
     */
    fun changeSegmentedButton() {
        equipo2 = !equipo2
    }
}
