package ir.example1.weather.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ir.example1.weather.R
import ir.example1.weather.domain.model.City
import ir.example1.weather.presentation.ui.utils.CityDiffUtil

// ✅ استفاده از ListAdapter برای DiffUtil خودکار
class SavedCityAdapter(
    private val onSelect: (Long?) -> Unit,
    private val onDelete: (Long?) -> Unit
) : ListAdapter<City, SavedCityAdapter.ViewHolder>(CityDiffUtil) {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtName: TextView = view.findViewById(R.id.txtCityName)
        val btnDelete: ImageView = view.findViewById(R.id.btnDelete)

        fun bind(city: City) {
            txtName.text = "${city.name}, ${city.country}"

            itemView.setOnClickListener {
                onSelect(city.id)
            }

            btnDelete.setOnClickListener {
                onDelete(city.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_saved_city, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}