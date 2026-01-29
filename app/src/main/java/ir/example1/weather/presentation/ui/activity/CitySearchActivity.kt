package ir.example1.weather.presentation.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import ir.example1.weather.databinding.ActivityCityListBinding
import ir.example1.weather.domain.model.City
import ir.example1.weather.presentation.ui.adapter.CityAdapter
import ir.example1.weather.presentation.viewmodel.CitySearchViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class CitySearchActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var settingsClient: SettingsClient

    private val REQUEST_CHECK_SETTINGS = 1001

    private lateinit var binding: ActivityCityListBinding
    private val viewModel: CitySearchViewModel by viewModels()

    private val cityAdapter by lazy {
        CityAdapter { city ->
            navigateToMainActivity(city)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCityListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindow()
        setupRecyclerView()
        setupObservers()
        setupSearch()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        settingsClient = LocationServices.getSettingsClient(this)

        binding.btnUseGps.setOnClickListener {
            getLocationAndAddCity()
        }
    }

    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (granted) {
                getLocationAndAddCity()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    private fun getLocationAndAddCity() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
            return //from getLocationAndAddCity
        }

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            1000
        ).build()

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)

        // چک می‌کنیم GPS روشن هست یا نه
        settingsClient.checkLocationSettings(builder.build())
            .addOnSuccessListener {
                // GPS روشن است → لوکیشن واقعی بگیر
                requestCurrentLocation()
            }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        // دیالوگ سیستم برای روشن کردن GPS
                        exception.startResolutionForResult(
                            this,
                            REQUEST_CHECK_SETTINGS
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(this, "Cannot open GPS settings", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Location service not available", Toast.LENGTH_SHORT).show()
                }
            }
    }

    @SuppressLint("MissingPermission")
    private fun requestCurrentLocation() {
        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            null
        ).addOnSuccessListener { location ->
            if (location != null) {

                handleLocation(location.latitude, location.longitude)
            } else {
                Toast.makeText(this, "Couldn't get current location", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            it.printStackTrace()
            Toast.makeText(this, "Failed to get location", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                requestCurrentLocation()
            } else {
                Toast.makeText(
                    this,
                    "GPS is required to get your location",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun handleLocation(lat: Double, lon: Double) {
        viewModel.searchCitiesLatLon(lat, lon)
    }



    private fun setupWindow() {
        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }
    }

    private fun setupRecyclerView() {
        binding.addingCityRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@CitySearchActivity)
            adapter = cityAdapter
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    cityAdapter.submitList(state.cities)

                    binding.progressBar2.visibility =
                        if (state.isLoading) View.VISIBLE else View.GONE
                }
            }
        }
    }

    private fun setupSearch() {
        binding.edtCityAdd.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString()?.trim() ?: ""
                viewModel.searchCities(query)
            }
        })
    }

    private fun navigateToMainActivity(city: City) {
        lifecycleScope.launch {
            val intent = Intent(this@CitySearchActivity, MainActivity::class.java).apply {
                putExtra("id", city.id)
                putExtra("name", city.name)
                putExtra("country", city.country)
                putExtra("lat", city.lat)
                putExtra("lon", city.lon)
                putExtra("selectedAt", city.selectetAt)
                putExtra("localName", city.localName)
            }
            startActivity(intent)
            finish()
        }
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
