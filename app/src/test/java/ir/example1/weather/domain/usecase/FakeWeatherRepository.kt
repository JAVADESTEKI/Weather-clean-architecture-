package ir.example1.weather.domain.usecase

import ir.example1.weather.domain.model.City
import ir.example1.weather.domain.model.CityWeatherForecast
import ir.example1.weather.domain.model.Forecast
import ir.example1.weather.domain.model.Weather
import ir.example1.weather.domain.repository.WeatherRepository

class FakeWeatherRepository : WeatherRepository {
    override suspend fun getCurrentWea(
        lat: Double,
        lon: Double,
        name: String,
        unit: String
    ): Result<Weather> {
        TODO("Not yet implemented")
    }

    override suspend fun getForecastWea(
        lat: Double,
        lon: Double,
        unit: String
    ): Result<List<Forecast>> {
        TODO("Not yet implemented")
    }

    override suspend fun searchCities(
        query: String,
        limit: Int
    ): Result<List<City>> {
        TODO("Not yet implemented")
    }

    override suspend fun searchCitiesReverse(
        lat: Double,
        lon: Double,
        limit: Int
    ): Result<List<City>> {
        TODO("Not yet implemented")
    }

    override suspend fun getLastSelectedCityFullData(cityId: Long): CityWeatherForecast? {
        TODO("Not yet implemented")
    }

    override suspend fun saveCityFullData(
        city: City,
        weather: Weather,
        forecasts: List<Forecast>
    ): Long {
        TODO("Not yet implemented")
    }

    override suspend fun getSavedCities(): List<City> {
        return listOf(
            City(
                id = 1,
                name = "Tehran",
                country = "IR",
                lat = 50.0,
                lon = 51.0,
                selectetAt = 13454652424,
                localName = "teh"
            ),
            City(id = 2,
                name = "Shiraz",
                country = "IR",
                lat = 80.0,
                lon = 20.0,
                selectetAt = 1123546123124,
                localName = "shiraaz")
        )
    }


    override suspend fun deleteCity(cityId: Long?) {
        TODO("Not yet implemented")
    }

    override suspend fun getLastInsertedIdUseCase(): Long {
        TODO("Not yet implemented")
    }

    override suspend fun updateCityFullData(
        cityId: Long,
        weather: Weather,
        forecasts: List<Forecast>
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun getLastSelectedCity(cityId: Long): City? {
        TODO("Not yet implemented")
    }
}
