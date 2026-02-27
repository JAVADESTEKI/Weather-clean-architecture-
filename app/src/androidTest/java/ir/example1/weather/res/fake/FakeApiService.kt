package ir.example1.weather.data.fake

import ir.example1.weather.data.remote.api.ApiServices

class FakeApiService : ApiServices {

    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        unit: String,
        apiKey: String
    ) = throw NotImplementedError()

    override suspend fun getForecastWeather(
        lat: Double,
        lon: Double,
        unit: String,
        apiKey: String
    ) = throw NotImplementedError()

    override suspend fun getCitiesList(
        q: String,
        limit: Int,
        apiKey: String
    ) = throw NotImplementedError()

    override suspend fun getCitiesListByLatLon(
        lat: Double,
        lon: Double,
        limit: Int,
        apiKey: String
    ) = throw NotImplementedError()
}