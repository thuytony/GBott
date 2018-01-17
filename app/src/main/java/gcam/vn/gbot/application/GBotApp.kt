package gcam.vn.gbot.application

import android.app.Application
import android.content.Context
import com.google.gson.Gson
import gcam.vn.gbot.manager.rest.RestBuilder
import org.json.JSONObject
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric



/**
 * Created by thuythu on 12/01/2018.
 */
class GBotApp : Application() {
    var context: Context? = null
        get() = context
    protected var gson: Gson? = null




    companion object {

        var instance: GBotApp? = null

        fun buildInstance(): GBotApp {
            return instance!!
        }

    }

    override fun onCreate() {

        super.onCreate()
        context = applicationContext
        instance = this
        gson = RestBuilder.provideGson()
        Fabric.with(this, Crashlytics())
    }


    fun gson(): Gson {
        return instance?.gson!!
    }

}