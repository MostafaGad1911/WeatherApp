package mostafagad.projects.weatherapp.di

import android.content.Context
import android.content.SharedPreferences
import mostafagad.projects.weatherapp.BuildConfig
import mostafagad.projects.weatherapp.data.remote.network.models.CityData
import java.util.*
import javax.inject.Inject

class SharedHelper @Inject constructor(private val sharedPreferences: SharedPreferences) {


    companion object{
        const val LAT = "LATITUDE"
        const val ID = "ID"
        const val LONG = "LONGITUDE"
        const val NAME = "NAME"
        const val TYPE = "TYPE"
        private const val APP_ID = "appid"
    }



     private fun getSharedPrefEditor(): SharedPreferences.Editor? {
        return sharedPreferences.edit()
    }

    fun saveString( key: String, value: String) {
        getSharedPrefEditor()?.putString(key, value)?.apply()
    }

    fun saveLong( key: String, value: Long) {
        getSharedPrefEditor()?.putLong(key, value)?.apply()
    }

    fun getLong( key: String): Long {
        return sharedPreferences.getLong(key, 0) ?: 0
    }

    fun saveFloat( key: String, value: Float) {
        getSharedPrefEditor()?.putFloat(key, value)?.apply()
    }

    fun getFloat( key: String): Float {
        return sharedPreferences.getFloat(key, 0f) ?: 0f
    }


    fun saveInt( key: String, value: Int) {
        getSharedPrefEditor()?.putInt(key, value)?.apply()
    }

    fun saveBoolean( key: String, value: Boolean) {
        getSharedPrefEditor()?.putBoolean(key, value)?.apply()
    }

    fun getString( key: String): String {
        return sharedPreferences.getString(key, "") ?: ""
    }

    private fun getBoolean( key: String): Boolean? {
        return sharedPreferences?.getBoolean(key, false)
    }

    fun getInt( key: String): Int? {
        return sharedPreferences?.getInt(key, -1)
    }



    fun clearUser() {
        getSharedPrefEditor()?.clear()?.apply()
    }

    fun getAppLanguage(newBase: Context): Locale {
        return Locale(getString("lang"))
    }

    fun saveCity(data: CityData) {
        data.apply {
            saveFloat( LAT, data.coord.lat)
            saveFloat( LONG, data.coord.lon)
            saveString( NAME, data.name)
            saveString( TYPE, "hour")
            saveString(APP_ID , BuildConfig.API_KEY)
        }
    }
}