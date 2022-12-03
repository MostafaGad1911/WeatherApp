package mostafagad.projects.weatherapp.data.remote.network

import io.reactivex.rxjava3.core.Observable
import mostafagad.projects.weatherapp.data.remote.network.models.CityData
import mostafagad.projects.weatherapp.data.remote.network.models.HistoryData
import mostafagad.projects.weatherapp.ui.activities.CityWeatherHistory
import retrofit2.Response
import retrofit2.http.*

interface Apis {

    @JvmSuppressWildcards
    @GET("weather")
    fun findCity(
        @QueryMap findMap: Map<String?, Any?>?,
    ): Observable<Response<CityData>>

    @JvmSuppressWildcards
    @GET("onecall/timemachine")
    fun getWeatherHistory(
        @QueryMap historyMap: Map<String?, Any?>?,
    ): Observable<Response<HistoryData>>

}