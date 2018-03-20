package gcam.vn.gbot.module

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by thuythu on 19/03/2018.
 */
class LoadMoreRestaurant{
    @SerializedName("name")
    @Expose
    private var name:String? = null
    @SerializedName("link")
    @Expose
    private var link:String? = null
    @SerializedName("address")
    @Expose
    private var address:String? = null
    @SerializedName("avatar")
    @Expose
    private var avatar:String? = null

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

    fun setAvatar(avatar: String){
        this.avatar = avatar
    }
    fun getAvatar(): String{
        return avatar?:"restaurants/1519973954_OFsxT.jpg"
    }
}