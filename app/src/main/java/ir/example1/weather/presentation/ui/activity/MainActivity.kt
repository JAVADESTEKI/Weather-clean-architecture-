package ir.example1.weather.presentation.ui.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import ir.example1.weather.R
import ir.example1.weather.databinding.ActivityMainBinding
import ir.example1.weather.presentation.ui.adapter.ForecastAdapter
import ir.example1.weather.presentation.ui.utils.WeatherIconMapper
import ir.example1.weather.presentation.viewmodel.WeatherViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: WeatherViewModel by viewModels()
    private val forecastAdapter = ForecastAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindow()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        decideInitialLoad()
    }

    private fun setupWindow() {
        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }
    }

    private fun setupRecyclerView() {
        binding.forcastRecyclerView.apply {
            layoutManager = LinearLayoutManager(
                this@MainActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = forecastAdapter
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.currentWeather.collectLatest { weather ->
                weather?.let { updateCurrentWeatherUI(it) }
            }
        }

        lifecycleScope.launch {
            viewModel.forecast.collectLatest { forecastList ->
                updateForecastUI(forecastList)
            }
        }

        lifecycleScope.launch {
            viewModel.loading.collectLatest { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }

        lifecycleScope.launch {
            viewModel.error.collectLatest { error ->
                error?.let {
                    Toast.makeText(this@MainActivity, it, Toast.LENGTH_SHORT).show()
                    viewModel.clearError()
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.addCity.setOnClickListener {
            startActivity(Intent(this, CityListActivity::class.java))
        }

        binding.refreshCurrunt.setOnClickListener {
            val cw = viewModel.currentWeather.value
            if (cw != null) {
                viewModel.loadWeatherData(
                    lat = cw.coord.lat,
                    lon = cw.coord.lon,
                    name = cw.cityName,
                    forceRefresh = true
                )
            } else {
                // اگر کش خالی است یا هنوز چیزی لود نشده، از آخرین شهر ذخیره‌شده شروع کن
                viewModel.loadInitialWeather()
            }
        }
    }

    // تصمیم‌گیری برای لود اولیه: اگر از CityList آمدیم فورس رفرش، در غیر این‌صورت از کشِ آخرین شهر
    private fun decideInitialLoad() {
        val hasExtras = intent.hasExtra("lat") && intent.hasExtra("lon") && intent.hasExtra("name")
        if (hasExtras) {
            val lat = intent.getDoubleExtra("lat", 51.50)
            val lon = intent.getDoubleExtra("lon", -0.12)
            val name = intent.getStringExtra("name") ?: "London"
            viewModel.loadWeatherData(lat, lon, name, forceRefresh = true)
        } else {
            viewModel.loadInitialWeather()
        }
    }

    private fun updateForecastUI(forecast: List<ir.example1.weather.domain.model.Forecast>) {
        forecastAdapter.submitList(forecast)
    }

    private fun updateCurrentWeatherUI(weather: ir.example1.weather.domain.model.Weather) {
        binding.apply {
            txtCity.text = weather.cityName
            txtStatus.text = weather.condition
            txtWindNum.text = "${weather.windSpeed.toInt()} Km/h"
            txtHumidityNum.text = "${weather.humidity}%"
            txtTodayDegree.text = "${weather.temperature.toInt()}°"
            txtMaxDegree.text = "H: ${weather.maxTemp.toInt()}°"
            txtMinDegree.text = "L: ${weather.minTemp.toInt()}°"

            Glide.with(root.context)
                .load(WeatherIconMapper.getIconResource(weather.icon))
                .into(imgTodayCondition)

            updateDescriptionCard(weather)
        }
    }

    private fun updateDescriptionCard(weather: ir.example1.weather.domain.model.Weather) {
        val iconRes: Int
        val description: String
        val statusValue: String

        when (weather.condition) {
            "Rain", "Drizzle", "Thunderstorm" -> {
                description = "Last 1h rain"
                statusValue = "${weather.rain} mm/h"
                iconRes = R.drawable.img_rainy
            }
            "Clouds" -> {
                description = "Cloudiness"
                statusValue = "${weather.clouds} %"
                iconRes = R.drawable.img_cloudy
            }
            "Snow" -> {
                description = "Status"
                statusValue = weather.description
                iconRes = R.drawable.img_snowy
            }
            "Mist", "Fog", "Haze" -> {
                description = "Visibility"
                statusValue = "${weather.visibility ?: 0} m"
                iconRes = R.drawable.img_visibility
            }
            else -> {
                description = "Pressure"
                statusValue = "${weather.pressure} hPa"
                iconRes = R.drawable.img_pressure
            }
        }

        binding.apply {
            txtDescription.text = description
            txtDescriptionStatus.text = statusValue
            Glide.with(root.context)
                .load(iconRes)
                .into(imgDescription)
        }
    }
}
