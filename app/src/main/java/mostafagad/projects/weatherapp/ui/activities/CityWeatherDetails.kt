package mostafagad.projects.weatherapp.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import mostafagad.projects.weatherapp.BuildConfig
import mostafagad.projects.weatherapp.R
import mostafagad.projects.weatherapp.data.local.CitiesVM
import mostafagad.projects.weatherapp.di.DaggerWeatherComponent
import mostafagad.projects.weatherapp.di.SharedHelper
import mostafagad.projects.weatherapp.di.WeatherComponent
import mostafagad.projects.weatherapp.utils.hide
import mostafagad.projects.weatherapp.utils.observeNonNull
import mostafagad.projects.weatherapp.utils.toast
import javax.inject.Inject
import kotlin.math.roundToInt

class CityWeatherDetails : AppCompatActivity() , OnClickListener{

    @Inject
    lateinit var citiesVM: CitiesVM

    @Inject
    lateinit var sharedHelper: SharedHelper

    private lateinit var itemProgressBar: View
    private lateinit var weatherDetailsBackImg:ImageView
    private lateinit var cityWeatherImg:ImageView
    private lateinit var cityNameTxt:TextView
    private lateinit var descriptionValueTxt:TextView
    private lateinit var temperatureValueTxt:TextView
    private lateinit var humidityValueTxt:TextView
    private lateinit var winSpeedValueTxt:TextView

    private val cityName: String by lazy {
        sharedHelper.getString(SharedHelper.NAME)
    }


    private val weatherComponent: WeatherComponent by lazy {
        DaggerWeatherComponent.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city_weather_details)
        initObjects()
        initViews()
        loadCity()
        observeData()
    }

    private fun loadCity() {
        val findMap:HashMap<String? , Any?> = HashMap()
        findMap["q"] =  cityName
        findMap["appid"] = BuildConfig.API_KEY
        citiesVM.findCity(findMap = findMap)
    }

    private fun hideLoading() = itemProgressBar.hide()

    private fun observeData(){
        citiesVM.error.observeNonNull(this) {
            it.toast()
            hideLoading()
        }
        citiesVM.findCityData.observeNonNull(this@CityWeatherDetails) {
            hideLoading()
            cityNameTxt.text = it.name
            it.weather[0].icon.loadImg(img = cityWeatherImg)
            descriptionValueTxt.text = it.weather[0].description
            temperatureValueTxt.text = getString(R.string.temp , it.main.temp.kelvinToCel().roundToInt().toString())
            humidityValueTxt.text = "${it.main.humidity} %"
            winSpeedValueTxt.text = getString(R.string.speed , it.wind.speed.toString())


        }
    }

    private fun initViews() {
        itemProgressBar = findViewById(R.id.itemProgressBar)
        weatherDetailsBackImg = findViewById(R.id.weatherDetailsBackImg)
        cityWeatherImg = findViewById(R.id.cityWeatherImg)
        cityNameTxt = findViewById(R.id.cityNameTxt)
        descriptionValueTxt = findViewById(R.id.descriptionValueTxt)
        temperatureValueTxt = findViewById(R.id.temperatureValueTxt)
        humidityValueTxt = findViewById(R.id.humidityValueTxt)
        winSpeedValueTxt = findViewById(R.id.winSpeedValueTxt)

        weatherDetailsBackImg.setOnClickListener(this)
    }

    private fun initObjects() {
        weatherComponent.inject(this)
    }


    private fun String.loadImg(img: ImageView) {
        val icon = "https://openweathermap.org/img/w/$this.png"
        Log.i("MY_ICON" , icon)
        Glide.with(this@CityWeatherDetails).load(icon).placeholder(R.mipmap.ic_launcher)
            .into(img)
    }

    private fun Double.kelvinToCel():Double = this - 273.15


    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.weatherDetailsBackImg -> {
                finish()
            }
        }
    }


}