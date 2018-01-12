package gcam.vn.gbot.util

import gcam.vn.gbot.application.GBotApp
import org.json.JSONObject

/**
 * Created by thuythu on 12/01/2018.
 */
open class Utils{

    companion object {
        var jsonObject: JSONObject? = JSONObject()
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
    }
}