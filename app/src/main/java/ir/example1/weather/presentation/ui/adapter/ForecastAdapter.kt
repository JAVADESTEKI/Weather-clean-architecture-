// presentation/ui/adapter/ForecastAdapter.kt
package ir.example1.weather.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ir.example1.weather.databinding.ForecastViewholderBinding
import ir.example1.weather.domain.model.Forecast
import ir.example1.weather.presentation.ui.utils.WeatherIconMapper
import java.util.Calendar

class ForecastAdapter : ListAdapter<Forecast, ForecastAdapter.ViewHolder>(ForecastDiffCallback()) {

    inner class ViewHolder(
        private val binding: ForecastViewholderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(forecast: Forecast) {
            binding.apply {
                val calendar = Calendar.getInstance().apply {
                    timeInMillis = forecast.dateTime
                }

                val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                val dayName = when (dayOfWeek) {
                    Calendar.SUNDAY -> "Sun"
                    Calendar.MONDAY -> "Mon"
                    Calendar.TUESDAY -> "Tue"
                    Calendar.WEDNESDAY -> "Wed"
                    Calendar.THURSDAY -> "Thu"
                    Calendar.FRIDAY -> "Fri"
                    Calendar.SATURDAY -> "Sat"
                    else -> "-"
                }
                nameDayTxt.text = dayName

                val hour = calendar.get(Calendar.HOUR)
                val amPm = if (calendar.get(Calendar.HOUR_OF_DAY) < 12) "am" else "pm"
                hourTxt.text = "$hour$amPm"

                tempTxt.text = "${forecast.temperature.toInt()}°"

                Glide.with(root.context)
                    .load(WeatherIconMapper.getIconResource(forecast.icon))
                    .into(pic)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ForecastViewholderBinding.inflate(
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

class ForecastDiffCallback : DiffUtil.ItemCallback<Forecast>() {
    override fun areItemsTheSame(oldItem: Forecast, newItem: Forecast): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Forecast, newItem: Forecast): Boolean {
        return oldItem == newItem
    }
}