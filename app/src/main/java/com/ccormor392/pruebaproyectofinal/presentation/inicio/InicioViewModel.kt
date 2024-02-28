package com.ccormor392.pruebaproyectofinal.presentation.inicio

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ccormor392.pruebaproyectofinal.data.model.Partido
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

@SuppressLint("MutableCollectionMutableState")
class InicioViewModel : ViewModel() {
    //Definición de variables y funciones para manejar el inicio de sesión y registro de usuarios.

    private val firestore = Firebase.firestore
    var listaPartidos by mutableStateOf(mutableListOf<Partido>())
        private set
    var listaPartidosConNombreUsuario by mutableStateOf(mutableListOf<Pair<Partido,String>>())
        private set

    fun pedirTodosLosPartidos(onSuccess:()->Unit) {
        firestore.collection("partidos")
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
                        val urlImagen = document.getString("urlImagen")
                        if (creador != null && fecha != null && hora != null && idPartido!=null && nombreSitio!= null && urlImagen!=null) {
                            val partido = Partido(creador, fecha, hora, idPartido, jugadores, nombreSitio, urlImagen)
                            documents.add(partido)
                        }

                    }

                }
                listaPartidos = documents
                asignarUsernameCreadorAPartido()
                onSuccess()
            }
    }
    fun getNombreUserById(id: String, callback: (String) -> Unit) {
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
    fun asignarUsernameCreadorAPartido(){
        for (partido in listaPartidos) {
            getNombreUserById(partido.creador) { nombreUsuario ->
                listaPartidosConNombreUsuario.add(Pair(partido, nombreUsuario))
            }
        }
    }
}