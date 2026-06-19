# Weather App ☀️🌧️

A modern Android Weather application built with **Clean Architecture**, **MVVM**, **XML Layouts**, **Hilt**, **Retrofit**, **Room**, and **Coroutines**.

The application provides real-time weather information, forecast data, city search functionality, and local persistence while following Clean Architecture principles.

## Repository

🔗 Repository: https://github.com/JAVADESTEKI/WeatherApp-XML_Layouts_Clean_Architecture

---

## Screenshots

![Shot](ScreenShot/Shot.png)

---

## Features

* Search cities
* Display current weather
* Display 5-day weather forecast
* Save cities locally
* Delete saved cities
* Offline persistence using Room
* Last selected city persistence using DataStore
* MVVM Architecture
* Clean Architecture
* Dependency Injection with Hilt
* Kotlin Coroutines & Flow

---

## Tech Stack

### Presentation Layer

- XML Layouts
- Activities
- RecyclerView
- ViewModel
- StateFlow
- ViewBinding

### Domain Layer

* Use Cases
* Repository Contracts
* Business Logic

### Data Layer

* Retrofit
* Room Database
* DataStore
* Repository Implementations
* DTO ↔ Domain Mappers

### Dependency Injection

* Hilt

### Testing

* JUnit
* MockK
* DAO Tests
* Repository Tests
* Integration Tests

---

## API

This project uses the OpenWeather API:

https://openweathermap.org/api

Features powered by the API include:

* Current weather conditions
* 5-day weather forecast
* City search and geolocation

Network requests are handled with Retrofit and mapped into domain models through dedicated mapper layers.

---

## Architecture

The project follows **Clean Architecture** principles:

```text
Presentation (XML + Fragments)
            │
            ▼
         Domain
            │
            ▼
          Data
```

### Project Structure

```text
weather
│
├── data
│   ├── local
│   │   ├── dao
│   │   │   └── CityDao.kt
│   │   │
│   │   ├── database
│   │   │   └── WeatherDatabase.kt
│   │   │
│   │   ├── entity
│   │   │   ├── CityEntity.kt
│   │   │   ├── ForecastEntity.kt
│   │   │   └── WeatherEntity.kt
│   │   │
│   │   └── relation
│   │       └── CityFullData.kt
│   │
│   ├── remote
│   │   ├── api
│   │   │   ├── ApiClient.kt
│   │   │   └── ApiServices.kt
│   │   │
│   │   ├── mapper
│   │   │   ├── CityMapper.kt
│   │   │   ├── ForecastMapper.kt
│   │   │   └── WeatherMapper.kt
│   │   │
│   │   └── response
│   │       ├── CityResponse.kt
│   │       ├── CurrentWeatherResponse.kt
│   │       └── ForecastResponse.kt
│   │
│   ├── mapper
│   │   ├── CityEntityMapper.kt
│   │   ├── CityFullDataMapper.kt
│   │   ├── ForecastEntityMapper.kt
│   │   └── WeatherEntityMapper.kt
│   │
│   ├── preference
│   │   └── UserPreferenceDataStore.kt
│   │
│   └── repository
│       ├── WeatherRepositoryImpl.kt
│       └── UserPreferenceRepositoryImpl.kt
│
├── domain
│   ├── model
│   │   ├── City.kt
│   │   ├── CityWeatherForecast.kt
│   │   ├── Forecast.kt
│   │   └── Weather.kt
│   │
│   ├── repository
│   │   ├── UserPreferenceRepository.kt
│   │   └── WeatherRepository.kt
│   │
│   └── usecase
│       ├── DeleteCityUseCase.kt
│       ├── GetCurrentWeatherUseCase.kt
│       ├── GetForecastUseCase.kt
│       ├── GetLastInsertedIdUseCase.kt
│       ├── GetLastSelectedCityFullDataUseCase.kt
│       ├── GetLastSelectedCityIdUseCase.kt
│       ├── GetLastSelectedCityUseCase.kt
│       ├── GetSavedCitiesUseCase.kt
│       ├── SaveCityFullDataUseCase.kt
│       ├── SaveLastSelectedCityIdUseCase.kt
│       ├── SearchCitiesUseCase.kt
│       └── UpdateCityFullDataUseCase.kt
│
├── presentation
│   ├── ui
│   │   ├── activity
│   │   │   ├── MainActivity.kt
│   │   │   └── CitySearchActivity.kt
│   │   │
│   │   ├── adapter
│   │   │   ├── CityAdapter.kt
│   │   │   ├── ForecastAdapter.kt
│   │   │   └── SavedCityAdapter.kt
│   │   │
│   │   └── utils
│   │       └── WeatherIconMapper.kt
│   │       └── CityDiffUtil.kt
│   │
│   └── viewmodel
│       ├── WeatherViewModel.kt
│       ├── WeatherUiState.kt
│       ├── CitySearchViewModel.kt
│       └── CitySearchUiState.kt
│
└── di
    ├── App.kt
    ├── AppModule.kt
    ├── DispatchersModule.kt
    ├── DispatchersQualifiers.kt
    └── PreferenceModule.kt
```

---

## Build & Run

```bash
git clone https://github.com/JAVADESTEKI/WeatherApp-XML_Layouts_Clean_Architecture.git
```

Open the project in Android Studio and run:

```bash
Sync Gradle
Build Project
Run App
```

---

## Author

**Mohammad Javad Esteki**
