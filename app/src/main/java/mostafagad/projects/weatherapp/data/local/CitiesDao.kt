package mostafagad.projects.weatherapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.rxjava3.core.Flowable

@Dao
interface CitiesDao {
    @Insert
    fun addCity(city:CityEntity)

    @Query("SELECT * FROM cities")
    fun getCities():List<CityEntity>

    @Query("SELECT * FROM cities WHERE name LIKE '%' || :city_name || '%'")
    fun searchCities(city_name: String?): List<CityEntity>

    @Query("DELETE FROM cities WHERE name LIKE '%' || :city_name || '%'")
    fun deleteCity(city_name: String?):Int

    @Query("SELECT EXISTS ( SELECT * FROM cities WHERE name = :cityName )")
    fun cityExit(cityName: String?):Boolean

}