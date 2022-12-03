package mostafagad.projects.weatherapp.ui.activities

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.jakewharton.rxbinding.widget.RxTextView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mostafagad.projects.weatherapp.BuildConfig
import mostafagad.projects.weatherapp.R
import mostafagad.projects.weatherapp.data.local.CitiesDataSource
import mostafagad.projects.weatherapp.data.local.CitiesVM
import mostafagad.projects.weatherapp.data.local.CityEntity
import mostafagad.projects.weatherapp.di.DaggerWeatherComponent
import mostafagad.projects.weatherapp.di.SharedHelper
import mostafagad.projects.weatherapp.di.WeatherComponent
import mostafagad.projects.weatherapp.interfaces.CitiesController
import mostafagad.projects.weatherapp.ui.adapter.CitiesAdapter
import mostafagad.projects.weatherapp.utils.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity(), CitiesController, OnClickListener {

    private var citiesList: ArrayList<String> = ArrayList()

    private val citiesAdapter: CitiesAdapter by lazy {
        CitiesAdapter(cities = citiesList, this)
    }

    @Inject
    lateinit var citiesVM: CitiesVM


    @Inject
    lateinit var dataSource: CitiesDataSource


    @Inject
    lateinit var sharedHelper: SharedHelper

    private var searchTxt = ""
    private val weatherComponent: WeatherComponent by lazy {
        DaggerWeatherComponent.create()
    }

    private lateinit var citiesRV: RecyclerView
    private lateinit var itemProgressBar: View
    private lateinit var dialog: Dialog
    private lateinit var searchDialog: Dialog
    private lateinit var addCityTxt: TextView
    private lateinit var topVew: View

    private val city = CityEntity()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        initObjects()
        initCitiesRV()
        observeData()
    }

    private fun initObjects() {
        weatherComponent.inject(this)
        showLoading()
        citiesVM.getCities()

        citiesVM.allCities.observeNonNull(this) { cities_list ->
            lifecycleScope.launch {
                delay(1000)
                hideLoading()
                citiesList.clear()
                if (searchTxt.isEmpty())
                    citiesList.addAll(cities_list.map { it.name }.filter { it.isNotEmpty() })
                else
                    citiesList.addAll(cities_list.map { it.name }.filter { it == searchTxt })
                citiesAdapter.notifyItemRangeInserted(citiesList.size, cities_list.size)
                Log.i("ROOM_SIZE", "${cities_list.size}")
                if (cities_list.isEmpty()) {
                    for (city in fillData()) {
                        val cityEntity = CityEntity()
                        cityEntity.name = city
                        Log.i("ROOM_CITY", cityEntity.name)
                        citiesVM.addCity(cityEntity = cityEntity)
                        citiesVM.getCities()
                    }
                }
            }
        }


        dialog = Dialog(this, R.style.DefaultDialogTheme).initDialog(R.layout.add_city)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)

        searchDialog = Dialog(this, R.style.DefaultDialogTheme).initDialog(R.layout.search_dialog)
        searchDialog.setCancelable(true)
        searchDialog.setCanceledOnTouchOutside(true)

    }

    private fun initViews() {
        citiesRV = findViewById(R.id.citiesRV)
        itemProgressBar = findViewById(R.id.itemProgressBar)
        addCityTxt = findViewById(R.id.addCityTxt)
        topVew = findViewById(R.id.topVew)

        addCityTxt.setOnClickListener(this)
        topVew.setOnClickListener(this)
        searchTxt = ""
    }

    private fun fillData(): ArrayList<String> {
        val cities: ArrayList<String> = ArrayList()
        cities.add("London")
        cities.add("Paris")
        cities.add("Vienna")
        return cities
    }


    override fun onBackPressed() {
        if (searchTxt.isEmpty())
            super.onBackPressed()
        else{
            searchTxt = ""
            citiesList.clear()
            citiesAdapter.notifyItemRangeRemoved(0 , citiesList.size)
            citiesVM.getCities()
        }
    }

    private fun initCitiesRV() {
        citiesRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        lifecycleScope.launch {
            delay(1000)
            hideLoading()
            citiesRV.adapter = citiesAdapter
        }
    }

    private fun observeData() {
        citiesVM.error.observeNonNull(this) {
            it.toast()
            hideLoading()
        }
        citiesVM.findCityData.observeNonNull(this@MainActivity) {
            citiesVM.addCity(cityEntity = city)
            citiesVM.getCities()
        }
    }

    private fun hideLoading() = itemProgressBar.hide()

    private fun showLoading() = itemProgressBar.show()
    override fun removeCity(cityName: String) {
        showLoading()
        val cityEntity = CityEntity()
        cityEntity.name = cityName
        citiesVM.deleteCity(cityEntity = cityEntity)
        citiesVM.cityDeleted.observeNonNull(this) {
            lifecycleScope.launch {
                delay(1000)
                if (it == 1) {
                    hideLoading()
                    val cityIdx = citiesList.indexOf(cityName)
                    if (cityIdx != -1) {
                        citiesList.removeAt(cityIdx)
                        citiesAdapter.notifyItemRemoved(cityIdx)

                    }
                }
            }

        }
    }

    override fun getCityWeatherHistory(cityName: String) {
        val findMap: HashMap<String?, Any?> = HashMap()
        findMap["q"] = cityName
        findMap["appid"] = BuildConfig.API_KEY
        citiesVM.findCity(findMap = findMap)
        citiesVM.findCityData.observeNonNull(this) {
            sharedHelper.saveString(SharedHelper.NAME, cityName)
            sharedHelper.saveString(SharedHelper.NAME, cityName)
            sharedHelper.saveString(SharedHelper.NAME, cityName)
            val intent = Intent(this, CityWeatherHistory::class.java)
            startActivity(intent)

        }

    }

    override fun getCityDetails(cityName: String) {
        sharedHelper.saveString(SharedHelper.NAME, cityName)
        val intent = Intent(this, CityWeatherDetails::class.java)
        startActivity(intent)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.addCityTxt -> {
                dialog.show()
                val cityEdt: EditText = dialog.findViewById(R.id.cityEdt)
                val cityLyt: TextInputLayout = dialog.findViewById(R.id.cityLyt)
                val addImg: ImageView = dialog.findViewById(R.id.addImg)

                cityEdt.setOnFocusChangeListener { _, b ->
                    if (b) cityEdt.error = null
                }

                addImg.setOnClickListener {
                    if (cityEdt.text.toString().trim().isEmpty()) {
                        cityLyt.error = getString(R.string.add_city)
                    } else if (dataSource.cityExits(city = cityEdt.text.toString().trim())) {
                        cityEdt.error = getString(R.string.city_exits)
                    } else {
                        lifecycleScope.launch {
                            dialog.dismiss()
                            showLoading()

                            val findMap: HashMap<String?, Any?> = HashMap()
                            findMap["q"] = cityEdt.text.toString().trim()
                            findMap["appid"] = BuildConfig.API_KEY
                            city.name =
                                cityEdt.text.toString().trim().replaceFirstChar { it.uppercase() }

                            citiesVM.findCity(findMap = findMap)


                        }
                    }
                }
            }
            R.id.topVew -> {
                searchTxt = ""
                searchDialog.show()
                val searchCitiesEdt: EditText = searchDialog.findViewById(R.id.searchCitiesEdt)
                RxTextView.textChanges(searchCitiesEdt)
                    .debounce(3, TimeUnit.SECONDS)
                    .subscribe { textChanged: CharSequence? ->
                        lifecycleScope.launch {
                            searchTxt = textChanged.toString()
                            citiesList.clear()
                            citiesAdapter.notifyItemRangeRemoved(0, citiesList.size)
                            searchDialog.dismiss()
                            showLoading()
                            citiesVM.getCities()
                        }
                    }
            }
        }
    }
}