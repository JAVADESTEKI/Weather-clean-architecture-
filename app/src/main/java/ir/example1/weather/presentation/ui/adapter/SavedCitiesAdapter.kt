package ir.example1.weather.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.example1.weather.databinding.ItemSavedCityBinding
import ir.example1.weather.domain.model.City

class SavedCitiesAdapter(
    private val onSelect: (City) -> Unit,
    private val onDelete: (City) -> Unit
) : RecyclerView.Adapter<SavedCitiesAdapter.VH>() {

    private val items = mutableListOf<City>()

    fun submitList(list: List<City>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    inner class VH(private val binding: ItemSavedCityBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: City) {
            binding.txtCityName.text = item.name
            binding.txtCountry.text = item.country
            binding.root.setOnClickListener { onSelect(item) }
            binding.btnDelete.setOnClickListener { onDelete(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemSavedCityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])

    override fun getItemCount(): Int = items.size
}