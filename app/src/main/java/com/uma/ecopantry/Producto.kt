package com.uma.ecopantry

data class Producto(
    var id: Int = 0,
    var nombre: String,
    var categoria: String,
    var fechaCaducidad: Long,
    var estado: Int = 0
)