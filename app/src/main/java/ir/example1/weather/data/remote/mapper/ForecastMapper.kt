// data/remote/mapper/ForecastMapper.kt
package ir.example1.weather.data.remote.mapper

import ir.example1.weather.data.remote.response.ForecastResponse
import ir.example1.weather.domain.model.Forecast
import javax.inject.Inject

class ForecastMapper @Inject constructor() {
    fun mapToForecastList(response: ForecastResponse): List<Forecast> {
        return response.list?.mapNotNull { item ->
            item?.let {
                Forecast(
                    id = it.dt ?: 0,
                    dateTime = (it.dt ?: 0).toLong() * 1000,
                    dateText = it.dtTxt ?: "",
                    temperature = it.main?.temp ?: 0.0,
                    feelsLike = it.main?.feelsLike ?: 0.0,
                    minTemp = it.main?.tempMin ?: 0.0,
                    maxTemp = it.main?.tempMax ?: 0.0,
                    humidity = it.main?.humidity ?: 0,
                    pressure = it.main?.pressure ?: 0,
                    windSpeed = it.wind?.speed ?: 0.0,
                    description = it.weather?.firstOrNull()?.description ?: "",
                    icon = it.weather?.firstOrNull()?.icon ?: "",
                    condition = it.weather?.firstOrNull()?.main ?: "",
                    clouds = it.clouds?.all ?: 0,
                    rain = it.rain?.h,
                    pop = it.pop
                )
            }
        } ?: emptyList()
    }
}