package mostafagad.projects.weatherapp.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import mostafagad.projects.weatherapp.R
import mostafagad.projects.weatherapp.data.remote.network.models.HistoryData
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class CitiesHistoryAdapter(
    private var historiesList: ArrayList<HistoryData>
    ) :
    RecyclerView.Adapter<CitiesHistoryAdapter.CityHolder>() {


    private lateinit var scaleAnim: Animation
    private var mLastPosition = -1


    class CityHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cloudImg: ImageView = itemView.findViewById(R.id.cloudImg)
        var dateTxt: TextView = itemView.findViewById(R.id.dateTxt)
        var cloudTxt: TextView = itemView.findViewById(R.id.cloudTxt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityHolder {
        return CityHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.history_row,
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: CityHolder, position: Int) {
        startAnimation(vew = holder.itemView, position = position)
        val history = historiesList[position]
        holder.dateTxt.text = getDateTime(history.current.dt)
        holder.cloudTxt.text = "${history.current.weather[0].description} ${holder.itemView.context.getString(R.string.temp ,  history.current.temp.kelvinToCel().roundToInt().toString())}"
        history.current.weather[0].icon.loadImg(img = holder.cloudImg , ctx = holder.itemView.context)

    }

    private fun getDateTime(s: Long): String? {
        return try {
            val sdf = SimpleDateFormat("MM/dd/yyyy" , Locale("en"))
            val netDate = Date(s * 1000)
            sdf.format(netDate)
        } catch (e: Exception) {
            e.toString()
        }
    }

    private fun String.loadImg(img: ImageView , ctx:Context) {
        val icon = "https://openweathermap.org/img/w/$this.png"
        Log.i("MY_ICON" , icon)
        Glide.with(ctx).load(icon).placeholder(R.mipmap.ic_launcher)
            .into(img)
    }

    private fun Double.kelvinToCel():Double = this - 273.15

    override fun getItemCount(): Int {
        return historiesList.size
    }

    private fun startAnimation(vew: View, position: Int) {
        if (position > mLastPosition) {
            scaleAnim = AnimationUtils.loadAnimation(vew.context, R.anim.fall_down)
            vew.startAnimation(scaleAnim)
            mLastPosition = position
        }
    }
}