package mostafagad.projects.weatherapp.data.remote.network.models

data class HistoryData(val lat:Double , val long:Double , val timezone:String , val timezone_offset:Long , val current:CurrentWeather)

data class CurrentWeather(val dt:Long , val weather: ArrayList<WeatherData> , val temp:Double)

