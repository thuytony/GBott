package gcam.vn.gbot.util

import android.content.Context
import gcam.vn.gbot.application.GBotApp
import org.json.JSONObject
import android.support.v4.content.ContextCompat.startActivity
import android.content.Intent
import android.net.Uri


/**
 * Created by thuythu on 12/01/2018.
 */
open class Utils{

    companion object {
        var jsonObject: JSONObject? = JSONObject()
        val intent = Intent()
        fun toJsonObj(key: String, value: String){
            jsonObject!!.put(key, value)
        }

        fun removeAllJsonObj(){
            jsonObject = null
            jsonObject = JSONObject()
        }

        fun getJsonObj(): JSONObject {
            return jsonObject!!
        }

        fun startWebView(context: Context, url: String){
            var uri = Uri.parse(url)
            intent.data = uri
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}