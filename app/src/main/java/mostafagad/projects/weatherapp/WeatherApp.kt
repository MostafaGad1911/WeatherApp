package mostafagad.projects.weatherapp

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.util.Log


// Lifecycle callback https://medium.com/@mobile_develop/android-application-development-detecting-when-your-app-enters-the-background-or-foreground-bbced47ad8a5
class WeatherApp : Application(), Application.ActivityLifecycleCallbacks {

    companion object {
        lateinit var appContext: Context
        lateinit var currentActivity: Activity


        fun applicationContext(): Context {
            return appContext
        }

        fun currentActivity(): Activity {
            return currentActivity
        }

        fun getResources(): Resources? {
            return instance!!.resources
        }


        /**
         * The current instance.
         */
        @get:Synchronized
        var instance: WeatherApp? = null
            private set

    }


    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(this)
        appContext = this.applicationContext
        instance = this
    }





    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        Log.i("ActivityLifeCycle", "onActivityCreated")
        currentActivity = activity

    }

    override fun onActivityStarted(activity: Activity) {
        Log.i("ActivityLifeCycle", "onActivityStarted")
        currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        Log.i("ActivityLifeCycle", "onActivityResumed")
    }

    override fun onActivityPaused(activity: Activity) {
        Log.i("ActivityLifeCycle", "onActivityPaused")
    }

    override fun onActivityStopped(activity: Activity) {
        Log.i("ActivityLifeCycle", "onActivityStopped")

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        Log.i("ActivityLifeCycle", "onActivitySaveInstanceState")
    }

    override fun onActivityDestroyed(activity: Activity) {
        Log.i("ActivityLifeCycle", "onActivityDestroyed")
    }


}