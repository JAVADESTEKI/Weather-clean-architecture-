package ir.example1.weather.presentation.ui.adapter

import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ir.example1.weather.R
import ir.example1.weather.data.local.entity.CityEntity
import ir.example1.weather.domain.model.City

class SavedCityAdapter(
    private val cities: List<City>,
    private val onSelect: (Long?) -> Unit,
    private val onDelete: (Long?) -> Unit
) : RecyclerView.Adapter<SavedCityAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtName: TextView = view.findViewById(R.id.txtCityName)
        val btnDelete: ImageView = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_saved_city, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val city = cities[position]

        holder.txtName.text = "${city.name}, ${city.country}"

        holder.itemView.setOnClickListener {
            onSelect(city.id)
        }

        holder.btnDelete.setOnClickListener {
            onDelete(city.id)
        }
    }

    override fun getItemCount() = cities.size
}
