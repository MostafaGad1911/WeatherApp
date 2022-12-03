package mostafagad.projects.weatherapp.di

import dagger.Component
import mostafagad.projects.weatherapp.ui.activities.CityWeatherDetails
import mostafagad.projects.weatherapp.ui.activities.CityWeatherHistory
import mostafagad.projects.weatherapp.ui.activities.MainActivity
import javax.inject.Singleton

@Singleton
@Component(modules = [WeatherModule::class])
interface WeatherComponent {
    fun inject(mainActivity: MainActivity)
    fun inject(cityWeatherDetails: CityWeatherDetails)
    fun inject(cityWeatherHistory: CityWeatherHistory)
}