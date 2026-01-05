package ir.example1.weather.presentation.ui.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import ir.example1.weather.R
import ir.example1.weather.databinding.ActivityMainBinding
import ir.example1.weather.domain.model.City

import ir.example1.weather.presentation.ui.adapter.ForecastAdapter
import ir.example1.weather.presentation.ui.adapter.SavedCityAdapter
import ir.example1.weather.presentation.ui.utils.WeatherIconMapper
import ir.example1.weather.presentation.viewmodel.WeatherViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
            viewModel.loadSavedCities()
        }
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
            finish()
        }

        binding.refreshCurrunt.setOnClickListener {
            viewModel.refreshWeather()
        }

        binding.moreCity.setOnClickListener {
            showSavedCitiesPopup()
        }

    }

    // تصمیم‌گیری برای لود اولیه: اگر از CityList آمدیم فورس رفرش، در غیر این‌صورت از کشِ آخرین شهر
    private fun decideInitialLoad() {
        val hasExtras = intent.hasExtra("id")
        if (hasExtras) {
            val id =intent.getLongExtra("id", 0L)
            val name =intent.getStringExtra("name")
            val country =intent.getStringExtra("country")
            val lat =intent.getDoubleExtra("lat",0.0)
            val lon =intent.getDoubleExtra("lon",0.0)
            val selectetAt =intent.getLongExtra("selectetAt",0L)
            val localName =intent.getStringExtra("localName")

            val city= City(id,name!!,country!!,lat,lon,selectetAt,localName)
            viewModel.saveSelectedCity(city)
            viewModel.loadInitialWeather()

        }
        else {
            viewModel.loadInitialWeather()
        }
    }

    private fun updateForecastUI(forecast: List<ir.example1.weather.domain.model.Forecast>) {
        forecastAdapter.submitList(forecast)
    }

    private fun updateCurrentWeatherUI(weather: ir.example1.weather.domain.model.Weather) {
        val date = Date(weather.timestamp)
        val formatter = SimpleDateFormat("yyyy/MM/dd  HH:mm", Locale.getDefault())

        binding.apply {
            txtCity.text = weather.cityName
            txtStatus.text = weather.condition
            txtTodayDate.text= "Last update: ${formatter.format(date)}"
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
    private fun showSavedCitiesPopup() {
        val view = layoutInflater.inflate(R.layout.popup_saved_cities, null)

        val popupWindow = PopupWindow(
            view,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        popupWindow.elevation = 20f
        popupWindow.isOutsideTouchable = true

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerSavedCities)
        recycler.layoutManager = LinearLayoutManager(this)


        lifecycleScope.launch {
            viewModel.loadSavedCities()
            viewModel.savedCities.collectLatest { cities ->
                recycler.adapter = SavedCityAdapter(
                    cities = cities.map { it }, // یا مستقیم Domain استفاده کن
                    onSelect = { id ->
                        viewModel.selectCity(id)
                        popupWindow.dismiss()
                    },
                    onDelete = { id ->
                        viewModel.deleteCity(id)
                    }
                )
            }
        }

        popupWindow.showAsDropDown(binding.moreCity, -100, 10)
    }
}
