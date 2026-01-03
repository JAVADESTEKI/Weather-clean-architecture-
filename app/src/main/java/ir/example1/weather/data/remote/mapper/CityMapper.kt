// data/remote/mapper/CityMapper.kt
package ir.example1.weather.data.remote.mapper

import ir.example1.weather.data.remote.response.CityResponse
import ir.example1.weather.domain.model.City
import javax.inject.Inject

class CityMapper @Inject constructor() {
    fun mapToCityList(response: List<CityResponse.CityItem>): List<City> {
        return response.map {
            City(
                id = null,
                name = it.name ?: "",
                country = it.country ?: "",
                lat = it.lat ?: 0.0,
                lon = it.lon ?: 0.0,
                localName = it.localNames?.fa ?: it.localNames?.en ?: it.name,
                selectetAt = 0

            )
        }
    }
}