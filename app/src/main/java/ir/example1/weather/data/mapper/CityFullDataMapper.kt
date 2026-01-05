package ir.example1.weather.data.mapper

import ir.example1.weather.data.local.relation.CityFullData
import ir.example1.weather.domain.model.City
import ir.example1.weather.domain.model.CityWeatherForecast
import ir.example1.weather.domain.model.Forecast
import ir.example1.weather.domain.model.Weather

fun CityFullData.toDomain(): CityWeatherForecast {
    if(weather!=null&& forecasts!=null){
        return CityWeatherForecast(
            city = City(
                id= city.id,
                name= city.name,
                country= city.country ,
                lat= city.lat ,
                lon= city.lon,
                selectetAt = city.selectedAt
            ),
            weather = Weather(
                cityName = weather.cityName ,
                temperature = weather.temperature ,
                feelsLike = weather.feelsLike ,
                minTemp = weather.minTemp ,
                maxTemp = weather.maxTemp ,
                humidity =weather.humidity ,
                rain = weather.rain ,
                pressure =weather.pressure ,
                windSpeed = weather.windSpeed ,
                windDegree =weather.windDegree ,
                description = weather.description ,
                icon = weather.icon ,
                condition = weather.condition ,
                clouds =weather.clouds ,
                visibility = weather?.visibility ,
                sunrise = weather?.sunrise ,
                sunset = weather?.sunset ,
                country = weather.country ,
                lat = weather.lat,
                lon = weather.lon,
                timestamp = weather.timestamp
            ),
            forecasts = forecasts.map {
                Forecast(
                    cityId= it.cityId ,
                    dateTime= it.dateTime ,
                    dateText= it.dateText ,
                    temperature= it.temperature ,
                    feelsLike= it.feelsLike ,
                    minTemp= it.minTemp ,
                    maxTemp= it.maxTemp ,
                    humidity= it.humidity ,
                    pressure= it.pressure ,
                    windSpeed= it.windSpeed ,
                    description= it.description ,
                    icon= it.icon ,
                    condition= it.condition ,
                    clouds= it.clouds ,
                    rain= it.rain ,
                    pop= it.pop
                )
            }
        )
    }
    else
        return CityWeatherForecast(
            city = City(
                id= city.id,
                name= city.name,
                country= city.country ,
                lat= city.lat ,
                lon= city.lon,
                selectetAt = city.selectedAt
            ),
            weather=null,
            forecasts=null
        )

}
