package ir.example1.weather.data.fake

import ir.example1.weather.data.local.dao.CityDao
import ir.example1.weather.data.local.entity.CityEntity
import ir.example1.weather.data.local.entity.ForecastEntity
import ir.example1.weather.data.local.entity.WeatherEntity
import ir.example1.weather.data.local.relation.CityFullData



class FakeCityDao : CityDao {

    private val cities = mutableListOf<CityEntity>()
    private val weathers = mutableListOf<WeatherEntity>()
    private val forecasts = mutableListOf<ForecastEntity>()

    var insertedCity: CityEntity? = null
    var insertedWeather: WeatherEntity? = null
    var insertedForecasts: List<ForecastEntity> = emptyList()

    var fakeFullData: CityFullData? = null


    private fun validateCityExists(cityId: Long) {
        if (cities.none { it.id == cityId }) {
            throw IllegalStateException(
                "FOREIGN KEY constraint failed. City with id=$cityId does not exist."
            )
        }
    }
    override suspend fun insertCity(city: CityEntity): Long {
        insertedCity = city
        cities.removeAll { it.id == city.id }
        cities.add(city)
        return city.id
    }

    override suspend fun insertWeather(weather: WeatherEntity) {
        validateCityExists(weather.cityId)

        insertedWeather = weather
        weathers.removeAll { it.cityId == weather.cityId }
        weathers.add(weather)
    }

    override suspend fun insertForecasts(forecasts: List<ForecastEntity>) {
        if(forecasts.isEmpty()) return

        val cityId= forecasts.first().cityId
//        validateCityExists(cityId)
        if (forecasts.any { it.cityId != cityId }) {
            throw IllegalArgumentException("All forecasts must belong to the same city.")
        }


        insertedForecasts = forecasts
        this.forecasts.removeAll { f -> forecasts.any { it.cityId == f.cityId } }
        this.forecasts.addAll(forecasts)
    }

    override suspend fun getAllCities(): List<CityEntity> = cities

    override suspend fun deleteCityById(cityId: Long?) {
        if (cityId == null) return

        cities.removeIf { it.id == cityId }
        cascadeDelete(cityId)
    }

    private fun cascadeDelete(cityId: Long) {
        weathers.removeIf { it.cityId == cityId }
        forecasts.removeIf { it.cityId == cityId }
    }
    override suspend fun getLastInsertedId(): Long =
        cities.maxByOrNull { it.selectedAt }?.id ?: 0L

    override suspend fun getLastSelectedCity(cityId: Long): CityEntity? =
        cities.find { it.id == cityId }

    override suspend fun getLastSelectedCityFullData(cityId: Long): CityFullData? {
        return fakeFullData
    }
    override suspend fun getCityFullData(cityId: Long): CityFullData {
        throw NotImplementedError()
    }

    override suspend fun deleteWeatherByCityId(cityId: Long) {
        weathers.removeIf { it.cityId == cityId }
    }

    override suspend fun deleteForecastsByCityId(cityId: Long) {
        forecasts.removeIf { it.cityId == cityId }
    }

    override suspend fun updateWeatherForecasts(
        cityId: Long,
        weather: WeatherEntity,
        forecasts: List<ForecastEntity>
    ) {
        deleteWeatherByCityId(cityId)
        deleteForecastsByCityId(cityId)
        insertWeather(weather)
        insertForecasts(forecasts)
    }
}