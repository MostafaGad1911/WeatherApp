package mostafagad.projects.weatherapp.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "cities")
class CityEntity{
    @ColumnInfo
    @PrimaryKey(autoGenerate = true)
    var id:Int = 0

    @ColumnInfo
    var name: String = ""
}