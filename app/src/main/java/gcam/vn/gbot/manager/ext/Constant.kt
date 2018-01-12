package gcam.vn.gbot.manager.ext

import com.google.gson.reflect.TypeToken
import gcam.vn.gbot.module.ChatFromServer

/**
 * Created by thuythu on 12/01/2018.
 */
class Constant{
    companion object {
        val chatFromServerType = object : TypeToken<ChatFromServer>() {}.type
    }
}