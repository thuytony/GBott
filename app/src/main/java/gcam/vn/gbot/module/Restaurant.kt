package gcam.vn.gbot.module

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by thuythu on 12/01/2018.
 */
class Restaurant{
    @SerializedName("name")
    @Expose
    private var name:String? = null
    @SerializedName("link")
    @Expose
    private var link:String? = null
    @SerializedName("address")
    @Expose
    private var address:String? = null

    constructor()

    constructor(name: String?, link: String?, address: String?){
        this.name = name
        this.link = link
        this.address = address
    }

    fun setName(name: String){
        this.name = name
    }
    fun getName(): String?{
        return name
    }

    fun setLink(link: String){
        this.link = link
    }
    fun getLink(): String?{
        return link
    }

    fun setAddress(keyword: String){
        this.address = address
    }
    fun getAddress(): String?{
        return address
    }
}