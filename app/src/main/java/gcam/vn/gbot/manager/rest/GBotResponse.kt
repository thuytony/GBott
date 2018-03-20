package gcam.vn.gbot.manager.rest

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by thuythu on 12/01/2018.
 */
class GBotResponse<T> {

    @SerializedName("code")
    @Expose
    var errorId: String? = null
    @SerializedName("data")
    @Expose
    var data: T? = null
    @SerializedName("msg")
    @Expose
    var message: String? = null
}