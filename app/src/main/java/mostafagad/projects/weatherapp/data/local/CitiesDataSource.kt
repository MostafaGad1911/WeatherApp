package mostafagad.projects.weatherapp.data.local

class CitiesDataSource(private val citiesDao: CitiesDao) {
    fun addCity(city: CityEntity) = citiesDao.addCity(city = city)
    fun getCities() = citiesDao.getCities()
    fun cityExits(city: String):Boolean = citiesDao.cityExit(cityName = city)
    fun deleteCity(city: CityEntity) = citiesDao.deleteCity(city_name  = city.name)

}