package mostafagad.projects.weatherapp.data.remote.network

import io.reactivex.rxjava3.core.Observable
import mostafagad.projects.weatherapp.data.remote.network.models.CityData
import mostafagad.projects.weatherapp.data.remote.network.models.HistoryData
import retrofit2.Response

class CitiesRepo(private val apis: Apis) {


   fun findCity(findMap: HashMap<String?, Any?>): Observable<Response<CityData>> {
       return apis.findCity(findMap = findMap)
   }

    fun getWeatherHistory(historyMap: HashMap<String?, Any?>): Observable<Response<HistoryData>> {
        return apis.getWeatherHistory(historyMap = historyMap)
    }
}