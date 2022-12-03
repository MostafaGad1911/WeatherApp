package mostafagad.projects.weatherapp.interfaces

interface CitiesController {
    fun removeCity(cityName: String)
    fun getCityWeatherHistory(cityName:String)
    fun getCityDetails(cityName: String)
}