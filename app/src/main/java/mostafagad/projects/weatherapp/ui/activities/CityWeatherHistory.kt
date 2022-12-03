package mostafagad.projects.weatherapp.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.*
import mostafagad.projects.weatherapp.BuildConfig
import mostafagad.projects.weatherapp.R
import mostafagad.projects.weatherapp.data.local.CitiesVM
import mostafagad.projects.weatherapp.data.remote.network.models.HistoryData
import mostafagad.projects.weatherapp.di.DaggerWeatherComponent
import mostafagad.projects.weatherapp.di.SharedHelper
import mostafagad.projects.weatherapp.di.SharedHelper.Companion.LAT
import mostafagad.projects.weatherapp.di.SharedHelper.Companion.LONG
import mostafagad.projects.weatherapp.di.WeatherComponent
import mostafagad.projects.weatherapp.ui.adapter.CitiesHistoryAdapter
import mostafagad.projects.weatherapp.utils.observeNonNull
import mostafagad.projects.weatherapp.utils.toast
import rx.android.schedulers.AndroidSchedulers
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.collections.ArrayList

class CityWeatherHistory : AppCompatActivity(), OnClickListener {

    private lateinit var weatherHistoryBackImg: ImageView
    private lateinit var cityHistoricalTitleTxt: TextView
    private lateinit var weatherHistoryRV:RecyclerView

    private val historyList: ArrayList<HistoryData> = ArrayList()

    private val historyAdapter: CitiesHistoryAdapter by lazy {
        CitiesHistoryAdapter(historiesList = historyList)
    }

    @Inject
    lateinit var citiesVM: CitiesVM

    @Inject
    lateinit var sharedHelper: SharedHelper

    private val cityName:String by lazy {
        sharedHelper.getString(SharedHelper.NAME)
    }

    private val weatherComponent: WeatherComponent by lazy {
        DaggerWeatherComponent.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city_weather_history)
        initObjects()
        initViews()
        getLast5Days()
        getDaysWeathers()
        observeHistories()
    }

    private fun observeHistories() {
        citiesVM.historyData.observeNonNull(this@CityWeatherHistory) {historyData ->
            historyList.add(historyData)
            historyAdapter.notifyItemInserted(historyList.size.plus(1))
        }
        citiesVM.error.observeNonNull(this@CityWeatherHistory){
            it.toast()
        }
    }

    private fun getDaysWeathers() {
        val historyMap: HashMap<String?, Any?> = HashMap()
        historyMap["exclude"] = "current,hourly"
        historyMap["appid"] = BuildConfig.API_KEY
        historyMap["lon"] = sharedHelper.getLong(LONG)
        historyMap["lat"] = sharedHelper.getLong(LAT)
        historyMap["only_current"] = true

        getLast5Days().forEachIndexed { index, value ->
            Observable.just(value)
                .subscribeOn(Schedulers.io())
                .observeOn(io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread())
                .flatMap { item: Long ->
                    historyMap["dt"] = item
                    citiesVM.getWeatherHistory(historyMap = historyMap)
                    Observable.just(item).delay(1, TimeUnit.MILLISECONDS)
                }
                .subscribe { x: Any -> print("$x ") }
        }


    }

    private fun initObjects() {
        weatherComponent.inject(this)
    }

    private fun initViews() {
        weatherHistoryBackImg = findViewById(R.id.weatherHistoryBackImg)
        cityHistoricalTitleTxt = findViewById(R.id.cityHistoricalTitleTxt)
        weatherHistoryRV = findViewById(R.id.weatherHistoryRV)
        weatherHistoryRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        weatherHistoryRV.adapter = historyAdapter
        cityHistoricalTitleTxt.text = getString(R.string.city_historical , cityName)
        weatherHistoryBackImg.setOnClickListener(this)
    }


    private fun getLast5Days(): ArrayList<Long> {
        val last5days: ArrayList<Long> = ArrayList()
        for (i in 0..4) {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("en"))
            val l = LocalDate.parse(dateFormat.format(calendar.time),
                DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            val unix = l.atStartOfDay(ZoneId.systemDefault()).toInstant().epochSecond

            Log.i("TEST_DATE", "${dateFormat.format(calendar.time)} $unix")
            if (last5days.size < 5)
                last5days.add(unix)
        }
        return last5days
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.weatherHistoryBackImg -> {
                finish()
            }
        }
    }
}