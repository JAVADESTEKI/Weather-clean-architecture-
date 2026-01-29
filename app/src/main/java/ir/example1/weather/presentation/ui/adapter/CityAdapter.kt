// presentation/ui/adapter/CityAdapter.kt
package ir.example1.weather.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ir.example1.weather.databinding.CityViewholderBinding
import ir.example1.weather.domain.model.City
import ir.example1.weather.presentation.ui.utils.CityDiffUtil

class CityAdapter(
    private val onCityClicked: (City) -> Unit
) : ListAdapter<City, CityAdapter.ViewHolder>(CityDiffUtil) {

    inner class ViewHolder(
        private val binding: CityViewholderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(city: City) {
            binding.apply {
                txtCityFinds.text = city.name
                txtContryOfcityFinds.text = city.country

                root.setOnClickListener {
                    onCityClicked(city)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CityViewholderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}