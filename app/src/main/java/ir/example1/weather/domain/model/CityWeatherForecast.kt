package ir.example1.weather.domain.model

data class CityWeatherForecast(
    val city: City,
    val weather: Weather?,
    val forecasts: List<Forecast>?
)