package com.farmaexpress.app.network

data class MedicamentoResponse(
    val status: String,
    val data: List<Medicamento>
)

data class Medicamento(
    val id: String,
    val nombre: String,
    val precio: String,
    val imagen_url: String
)
