// domain/model/City.kt
package ir.example1.weather.domain.model

data class City(
    val name: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val localName: String? = null
)