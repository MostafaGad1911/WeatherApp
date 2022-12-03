package mostafagad.projects.weatherapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import mostafagad.projects.weatherapp.R
import mostafagad.projects.weatherapp.interfaces.CitiesController

class CitiesAdapter(
    private var cities: ArrayList<String>,
    citiesController: CitiesController
) :
    RecyclerView.Adapter<CitiesAdapter.CityHolder>() {

    private var _citiesController: CitiesController

    init {
        _citiesController = citiesController
    }

    private lateinit var scaleAnim: Animation
    private var mLastPosition = -1


    class CityHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var locationImg:ImageView = itemView.findViewById(R.id.locationImg)
        var cityTxt:TextView = itemView.findViewById(R.id.cityTxt)
        var infoImg:ImageView = itemView.findViewById(R.id.infoImg)
        var removeImg:ImageView = itemView.findViewById(R.id.removeImg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityHolder {
        return CityHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.city_row,
                parent,
                false
            )
        )
    }

  
    override fun onBindViewHolder(holder: CityHolder, position: Int) {
        startAnimation(vew = holder.itemView , position = position)
        val city = cities[position]
        holder.cityTxt.text = city

        holder.infoImg.setOnClickListener { _citiesController.getCityDetails(cityName = city) }
        holder.removeImg.setOnClickListener { _citiesController.removeCity(cityName = city) }
        holder.locationImg.setOnClickListener { _citiesController.getCityWeatherHistory(cityName = city) }
    }


    override fun getItemCount(): Int {
        return cities.size
    }

    private fun startAnimation(vew:View, position:Int){
        if (position > mLastPosition) {
            scaleAnim = AnimationUtils.loadAnimation(vew.context , R.anim.fall_down)
            vew.startAnimation(scaleAnim)
            mLastPosition = position
        }
    }
}