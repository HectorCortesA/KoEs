
package com.hector.koes.model

data class Oracion(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val estructuras: List<EstructuraOracion> = emptyList(),
    val significados: List<SignificadoPalabra> = emptyList(),
    val ejemplos: List<EjemploOracion> = emptyList(),
    val comparaciones: List<ComparacionVerbo> = emptyList()
)

data class EstructuraOracion(
    val titulo: String,
    val palabras: List<PalabraEstructura>
)

data class PalabraEstructura(
    val palabra: String,
    val funcion: String
)

data class SignificadoPalabra(
    val coreano: String,
    val pronunciacion: String,
    val significado: String
)

data class EjemploOracion(
    val coreano: String,
    val espanol: String
)

data class ComparacionVerbo(
    val verbo: String,
    val presente: String,
    val pasado: String,
    val futuro: String
)
