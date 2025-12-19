// presentation/ui/activity/CityListActivity.kt
package ir.example1.weather.presentation.ui.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ir.example1.weather.databinding.ActivityCityListBinding
import ir.example1.weather.presentation.ui.adapter.CityAdapter
import ir.example1.weather.presentation.viewmodel.CityViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CityListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCityListBinding
    private val viewModel: CityViewModel by viewModels()

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
    }

    private fun setupWindow() {
        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }
    }

    private fun setupRecyclerView() {
        binding.addingCityRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@CityListActivity)
            adapter = cityAdapter
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.cities.collectLatest { cities ->
                cityAdapter.submitList(cities)
            }
        }

        lifecycleScope.launch {
            viewModel.loading.collectLatest { isLoading ->
                binding.progressBar2.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }

        lifecycleScope.launch {
            viewModel.error.collectLatest { error ->
                error?.let {
                    Toast.makeText(this@CityListActivity, it, Toast.LENGTH_SHORT).show()
                    viewModel.clearError()
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

    private fun navigateToMainActivity(city: ir.example1.weather.domain.model.City) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("lat", city.lat)
            putExtra("lon", city.lon)
            putExtra("name", city.name)
        }
        startActivity(intent)
        finish()
    }
}