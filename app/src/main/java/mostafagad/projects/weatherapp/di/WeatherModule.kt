package mostafagad.projects.weatherapp.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.readystatesoftware.chuck.ChuckInterceptor
import dagger.Binds
import dagger.Module
import dagger.Provides
import mostafagad.projects.weatherapp.BuildConfig
import mostafagad.projects.weatherapp.WeatherApp
import mostafagad.projects.weatherapp.data.local.CitiesDB
import mostafagad.projects.weatherapp.data.local.CitiesDataSource
import mostafagad.projects.weatherapp.data.local.CitiesVM
import mostafagad.projects.weatherapp.data.remote.network.Apis
import mostafagad.projects.weatherapp.data.remote.network.CitiesRepo
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class WeatherModule {
    lateinit var retrofit: Retrofit
    lateinit var apiInterface: Apis


    @Provides
    fun getService(context: Context): Apis {
        apiInterface = getClient(context = context).create(Apis::class.java)
        return apiInterface
    }

    @Provides
    fun getContext(): Context {
        return WeatherApp.applicationContext()
    }

    @Provides
    fun getClient(context: Context): Retrofit {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BASIC
        logging.level = HttpLoggingInterceptor.Level.HEADERS
        logging.level = HttpLoggingInterceptor.Level.BODY


        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS)
            .addInterceptor(ChuckInterceptor(context))
            .addInterceptor(logging)
            .build()

        val gson: Gson = GsonBuilder()
            .setLenient()
            .create()

        retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addConverterFactory(ScalarsConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .client(client)
            .build()
        return retrofit
    }

    @Provides
    @Singleton
    fun provideRepo(api: Apis): CitiesRepo = CitiesRepo(apis = api)

    @Provides
    @Singleton
    fun provideRoom(context: Context): CitiesDB = Room.databaseBuilder(
        context.applicationContext,
        CitiesDB::class.java,
        CitiesDB.DB_NAME
    ).fallbackToDestructiveMigration()
        .allowMainThreadQueries().build()


    @Provides
    @Singleton
    fun provideDataSource(citiesDB: CitiesDB): CitiesDataSource =
        CitiesDataSource(citiesDao = citiesDB.getCitiesDao())

    @Provides
    @Singleton
    fun provideCitiesVM(citiesRepo: CitiesRepo, citiesDataSource: CitiesDataSource): CitiesVM =
        CitiesVM(citiesRepo = citiesRepo, citiesDataSource = citiesDataSource)

    @Provides
    fun provideSharedPrefs(ctx: Context): SharedPreferences = ctx.getSharedPreferences("app_data", Context.MODE_PRIVATE)

    @Provides
    fun provideSharedHelper(sharedPreferences: SharedPreferences):SharedHelper = SharedHelper(sharedPreferences)

}