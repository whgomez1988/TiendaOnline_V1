package com.example.tiendaonline

data class Producto(
    var nombre: String = "",
    var precio: Double = 0.0,
    var descripcion: String = "",
    var imageUri: String? = null, // Agregar este campo
    var id: String? = null
)

