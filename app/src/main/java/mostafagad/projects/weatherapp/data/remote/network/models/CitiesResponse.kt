package mostafagad.projects.weatherapp.data.remote.network.models


data class CityData(val id:Long, val name:String, val coord: CityCoordinate, val main: TempData, val dt:Long, val wind: WindData, val sys: SysData, val clouds: CloudData, val weather: ArrayList<WeatherData>)

data class CityCoordinate(val lat:Float , val lon:Float)

data class TempData(
    val feels_like: Double,
    val humidity: Int,
    val pressure: Int,
    val temp: Double,
    val temp_max: Double,
    val temp_min: Double
)

data class WindData(
    val speed:Double ,
    val deg:Int
)

data class SysData(
    val country:String
)

data class CloudData(
    val all:Int
)

data class WeatherData(
    val id:Int ,
    val main:String ,
    val description: String ,
    val icon:String
)