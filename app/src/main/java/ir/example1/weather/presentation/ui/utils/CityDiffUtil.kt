package ir.example1.weather.presentation.ui.utils

import androidx.recyclerview.widget.DiffUtil
import ir.example1.weather.domain.model.City

object CityDiffUtil : DiffUtil.ItemCallback<City>() {
    override fun areItemsTheSame(oldItem: City, newItem: City): Boolean {
        return oldItem.name == newItem.name &&
                oldItem.lat == newItem.lat &&
                oldItem.lon == newItem.lon
    }

    override fun areContentsTheSame(oldItem: City, newItem: City): Boolean {
        return oldItem == newItem
    }
}