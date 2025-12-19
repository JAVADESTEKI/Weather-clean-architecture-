// presentation/ui/utils/WeatherIconMapper.kt
package ir.example1.weather.presentation.ui.utils

import ir.example1.weather.R

object WeatherIconMapper {

    fun getIconResource(iconCode: String): Int {
        return when (iconCode) {
            "01d" -> R.drawable.img_clear_sky_day
            "01n" -> R.drawable.img_clear_sky_night
            "02d", "02n", "03d", "03n", "04d", "04n" -> R.drawable.img_cloudy
            "09d", "09n", "10d", "10n" -> R.drawable.img_rainy
            "11d", "11n" -> R.drawable.img_stormy
            "13d", "13n" -> R.drawable.img_snowy
            "50d", "50n" -> R.drawable.img_misty
            else -> R.drawable.img_clear_sky_day
        }
    }
}