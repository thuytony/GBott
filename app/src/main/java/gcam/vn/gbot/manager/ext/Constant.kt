package gcam.vn.gbot.manager.ext

import com.google.gson.reflect.TypeToken
import gcam.vn.gbot.module.AddressFromRequest
import gcam.vn.gbot.module.ChatFromServer
import gcam.vn.gbot.module.KeyWord

/**
 * Created by thuythu on 12/01/2018.
 */
class Constant{
    companion object {
        //activity for result
        val RESULT_REQUEST_LOCATION = 3000
        val REQUEST_READ_PHONE_STATE = 3001

        //type object
        val chatFromServerType = object : TypeToken<ChatFromServer>() {}.type
        val addFromRequestType = object : TypeToken<AddressFromRequest>() {}.type
        val multiKeyWordType = object : TypeToken<MutableList<KeyWord>>() {}.type
    }
}