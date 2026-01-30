package com.android.prakiraancuaca.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.prakiraancuaca.R
import com.android.prakiraancuaca.model.Cuaca
import java.text.SimpleDateFormat
import java.util.Locale

class ForecastAdapter(
    private val list: List<Cuaca>
) : RecyclerView.Adapter<ForecastAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val time: TextView = view.findViewById(R.id.tvTime)
        val temp: TextView = view.findViewById(R.id.tvTemp)
        val desc: TextView = view.findViewById(R.id.tvDesc)
        val imgWeather: ImageView = itemView.findViewById(R.id.imgWeather)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_forecast, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val date = inputFormat.parse(item.local_datetime)
            holder.time.text =
                if (date != null) outputFormat.format(date) else item.local_datetime
        } catch (e: Exception) {
            holder.time.text = item.local_datetime.substringAfter(" ").substringBeforeLast(":")
        }

        holder.temp.text = "${item.t}°C"
        holder.desc.text = item.weather_desc

        holder.imgWeather.setImageResource(
            getWeatherIcon(item.weather_desc)
        )
    }
}
