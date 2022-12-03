package mostafagad.projects.weatherapp.utils

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.NetworkOnMainThreadException
import android.system.ErrnoException
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.exceptions.UndeliverableException
import io.reactivex.rxjava3.observers.DisposableObserver
import mostafagad.projects.weatherapp.R
import mostafagad.projects.weatherapp.WeatherApp
import okhttp3.ResponseBody
import okhttp3.internal.http2.ConnectionShutdownException
import org.json.JSONObject
import retrofit2.Response
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.*

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

fun Dialog.initDialog(lyt: Int): Dialog {
    this.requestWindowFeature(Window.FEATURE_NO_TITLE)
    this.window?.setBackgroundDrawable(
        ColorDrawable(Color.TRANSPARENT)
    )
    this.setCancelable(true)
    this.setCanceledOnTouchOutside(true)
    this.setContentView(lyt)
    this.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    this.window?.setLayout(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )


    return this
}

fun <T : Any> Observable<Response<T>>.callApi(
    onSuccess: (Response<T>) -> Unit,
    onValidationFailed: (String) -> Unit,
    onFailed: (String) -> Unit,
) {
    this.distinctUntilChanged()
        .subscribeOn(io.reactivex.rxjava3.schedulers.Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(object : DisposableObserver<Response<T>>() {


            override fun onError(e: Throwable) {
                val msg = e.getMessage()
                onFailed(msg)
            }


            override fun onComplete() {
            }

            override fun onNext(response: Response<T>) {
                when (response.code()) {
                    200 -> {
                        onSuccess(response)
                    }


                    500, 503, 302, 504 -> {
                        onFailed(
                            WeatherApp.getResources()?.getString(R.string.server_error)
                                .toString()
                        )
                    }

                    422, 502, 401, 400, 404, 403 -> {
                        val msg = response.errorBody()?.getErrorMessage()
                        onValidationFailed(msg.toString())
                    }
                }

            }


        })
}

fun ResponseBody.getErrorMessage(): String {
    val errorJsonString = this.string()
    val msg: String = try {
        val json = JSONObject(errorJsonString)
        json.getString("message")
    } catch (ex: Exception) {
        errorJsonString
    }
    return msg
}

fun Throwable.getMessage(): String {
    val ctx = WeatherApp.currentActivity()
    val msg: String = when (this) {
        is SocketTimeoutException -> {
             ctx.getString(R.string.timeout)
        }
        is UnknownHostException -> {
             ctx.getString(R.string.internet_connection)
        }
        is NetworkOnMainThreadException -> {
             ctx.getString(R.string.internet_connection)
        }
        is ConnectException -> {
             ctx.getString(R.string.internet_connection)
        }
        is ConnectionShutdownException -> {
             ctx.getString(R.string.connection_shutdown)
        }
        is ErrnoException -> {
             ctx.getString(R.string.connection_shutdown)
        }
        is IOException -> {
             ctx.getString(R.string.server_unreach)
        }
        is UndeliverableException -> {
             ctx.getString(R.string.server_unreach)
        }
        is IllegalStateException -> {
             "${this.message}"
        }
        else -> {
             "${this.message}"
        }
    }
    return msg
}


fun String.toast() = Toast.makeText(WeatherApp.applicationContext() , this , Toast.LENGTH_LONG).show()

fun getCalculatedDate(dateFormat: String?, days: Int): String? {
    val cal: Calendar = Calendar.getInstance()
    val s = SimpleDateFormat(dateFormat ,  Locale("en"))
    cal.add(Calendar.DAY_OF_YEAR, days)
    return s.format(Date(cal.timeInMillis))
}

class SelfCleaningLiveData<T> : MutableLiveData<T>(){
    override fun onInactive() {
        super.onInactive()
        value = null
    }
}

fun <T> LiveData<T>.observeNonNull(owner: LifecycleOwner, observer: (t: T) -> Unit) {
    this.observe(owner) {
        it?.let(observer)
    }
}