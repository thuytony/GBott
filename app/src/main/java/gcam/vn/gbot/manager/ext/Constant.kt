package gcam.vn.gbot.manager.ext

import com.google.gson.reflect.TypeToken
import gcam.vn.gbot.module.AddressFromRequest
import gcam.vn.gbot.module.ChatFromServer

/**
 * Created by thuythu on 12/01/2018.
 */
class Constant{
    companion object {
        //activity for result
        val RESULT_REQUEST_LOCATION = 3000

        //type object
        val chatFromServerType = object : TypeToken<ChatFromServer>() {}.type
        val addFromRequestType = object : TypeToken<AddressFromRequest>() {}.type
    }
}