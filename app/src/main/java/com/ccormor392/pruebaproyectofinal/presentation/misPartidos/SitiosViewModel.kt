package com.ccormor392.pruebaproyectofinal.presentation.misPartidos

import androidx.lifecycle.ViewModel
import com.ccormor392.pruebaproyectofinal.data.model.Sitio
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SitiosViewModel : ViewModel() {
    // Firebase Firestore
    private val firestore = Firebase.firestore
    private val auth = Firebase.auth

    // Lista mutable de partidos disponibles
    private var _listaSitios = MutableStateFlow(mutableListOf<Sitio>())
    val listaSitios: StateFlow<MutableList<Sitio>> = _listaSitios

    private var _selectedSitio = MutableStateFlow(Sitio())
    val selectedSitio: StateFlow<Sitio> = _selectedSitio

    /**
     * Función para obtener todos los partidos del usuario desde Firestore.
     * Consulta Firestore para obtener la lista de partidos del usuario actual y actualiza [_listaMisPartidos].
     */
    fun pedirTodosLosSitios() {
        // Consulta a Firestore para obtener los sitios
        firestore.collection("Sitios").get()
            .addOnSuccessListener { querySnapshot ->
                // Si se obtienen resultados, extraer todos los sitios de la colección
                var listaTemp = mutableListOf<Sitio>()
                if (querySnapshot != null) {
                    for (document in querySnapshot) {
                        val sitio = document.toObject(Sitio::class.java)
                        listaTemp.add(sitio)
                    }
                }
                _listaSitios.value = listaTemp
            }
    }
    fun seleccionarSitio(newSitio:Sitio){
        _selectedSitio.value = newSitio
    }
}
