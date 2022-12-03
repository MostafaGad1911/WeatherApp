package mostafagad.projects.weatherapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [CityEntity::class] , version = 1)
abstract class CitiesDB : RoomDatabase() {

    abstract fun getCitiesDao():CitiesDao

    companion object{
        const val DB_NAME = "Cities-DataBase.db"
        @Volatile
        private var instance: CitiesDB? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }



        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            CitiesDB::class.java,
            DB_NAME
        ).fallbackToDestructiveMigration().build()
    }

}