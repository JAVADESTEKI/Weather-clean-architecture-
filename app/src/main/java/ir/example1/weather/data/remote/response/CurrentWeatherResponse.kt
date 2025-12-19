// data/remote/response/CurrentWeatherResponse.kt
package ir.example1.weather.data.remote.response

import com.google.gson.annotations.SerializedName

data class CurrentWeatherResponse(
    @SerializedName("base") val base: String? = null,
    @SerializedName("clouds") val clouds: Clouds? = null,
    @SerializedName("cod") val cod: Int? = null,
    @SerializedName("coord") val coord: Coord? = null,
    @SerializedName("dt") val dt: Int? = null,
    @SerializedName("id") val id: Int? = null,
    @SerializedName("main") val main: Main? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("rain") val rain: Rain? = null,
    @SerializedName("sys") val sys: Sys? = null,
    @SerializedName("timezone") val timezone: Int? = null,
    @SerializedName("visibility") val visibility: Int? = null,
    @SerializedName("weather") val weather: List<Weather?>? = emptyList(),
    @SerializedName("wind") val wind: Wind? = null
) {
    data class Clouds(@SerializedName("all") val all: Int? = null)

    data class Coord(
        @SerializedName("lat") val lat: Double? = null,
        @SerializedName("lon") val lon: Double? = null
    )

    data class Main(
        @SerializedName("feels_like") val feelsLike: Double? = null,
        @SerializedName("grnd_level") val grndLevel: Int? = null,
        @SerializedName("humidity") val humidity: Int? = null,
        @SerializedName("pressure") val pressure: Int? = null,
        @SerializedName("sea_level") val seaLevel: Int? = null,
        @SerializedName("temp") val temp: Double? = null,
        @SerializedName("temp_max") val tempMax: Double? = null,
        @SerializedName("temp_min") val tempMin: Double? = null
    )

    data class Rain(@SerializedName("1h") val h: Double? = null)

    data class Sys(
        @SerializedName("country") val country: String? = null,
        @SerializedName("id") val id: Int? = null,
        @SerializedName("sunrise") val sunrise: Int? = null,
        @SerializedName("sunset") val sunset: Int? = null,
        @SerializedName("type") val type: Int? = null
    )

    data class Weather(
        @SerializedName("description") val description: String? = null,
        @SerializedName("icon") val icon: String? = null,
        @SerializedName("id") val id: Int? = null,
        @SerializedName("main") val main: String? = null
    )

    data class Wind(
        @SerializedName("deg") val deg: Int? = null,
        @SerializedName("gust") val gust: Double? = null,
        @SerializedName("speed") val speed: Double? = null
    )
}