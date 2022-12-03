package mostafagad.projects.weatherapp.data.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mostafagad.projects.weatherapp.data.remote.network.CitiesRepo
import mostafagad.projects.weatherapp.data.remote.network.models.CityData
import mostafagad.projects.weatherapp.data.remote.network.models.HistoryData
import mostafagad.projects.weatherapp.utils.SelfCleaningLiveData
import mostafagad.projects.weatherapp.utils.callApi

class CitiesVM(private val citiesDataSource: CitiesDataSource , private val citiesRepo: CitiesRepo) : ViewModel() {

    private val _allCities = SelfCleaningLiveData<List<CityEntity>>()
    val allCities: LiveData<List<CityEntity>> get() = _allCities

    private val _cityDeleted = SelfCleaningLiveData<Int>()
    val cityDeleted: LiveData<Int> get() = _cityDeleted

    private val _findCityData = SelfCleaningLiveData<CityData>()
    val findCityData: LiveData<CityData> get() = _findCityData

    private val _historyData = SelfCleaningLiveData<HistoryData>()
    val historyData: LiveData<HistoryData> get() = _historyData

    private val _error = SelfCleaningLiveData<String>()
    val error: LiveData<String> get() = _error

    fun addCity(cityEntity: CityEntity) = viewModelScope.launch {
        if (!citiesDataSource.cityExits(city = cityEntity.name))
            citiesDataSource.addCity(city = cityEntity)
    }

    fun getCities() = viewModelScope.launch {
        _allCities.value = citiesDataSource.getCities()
    }


    fun deleteCity(cityEntity: CityEntity) = viewModelScope.launch {
        _cityDeleted.value = citiesDataSource.deleteCity(city = cityEntity)

    }

    fun findCity(findMap: HashMap<String?, Any?>){
        citiesRepo.findCity(findMap = findMap)
            .callApi(onFailed = {
                _error.value = it
            } , onValidationFailed = {
                _error.value = it
            } , onSuccess = {
                _findCityData.value = it.body()
            })
    }

    fun getWeatherHistory(historyMap: HashMap<String?, Any?>){
        citiesRepo.getWeatherHistory(historyMap = historyMap)
            .callApi(onFailed = {
                _error.value = it
            } , onValidationFailed = {
                _error.value = it
            } , onSuccess = {
                _historyData.value = it.body()
            })
    }
}