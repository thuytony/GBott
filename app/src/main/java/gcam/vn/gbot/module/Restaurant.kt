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
    @SerializedName("avatar")
    @Expose
    private var avatar:String? = null
    @SerializedName("price_min")
    @Expose
    private var priceMin:Float? = null
    @SerializedName("price_max")
    @Expose
    private var priceMax:Float? = null
    @SerializedName("menu")
    @Expose
    private var menu:Any? = null
    @SerializedName("s_menu")
    @Expose
    private var sMenu:Any? = null
    @SerializedName("open_time")
    @Expose
    private var openTime:Any? = null
    @SerializedName("chuyenmon")
    @Expose
    private var chuyenMon:String? = null
    @SerializedName("ndtaitro")
    @Expose
    private var ndtaitro:String? = null
    @SerializedName("status")
    @Expose
    private var status:Int? = null
    @SerializedName("code")
    @Expose
    private var code:String? = null

    //list string images cua images
    private var images: String ? = null

    constructor()

    //khi la nhieu nha hang
    constructor(name: String?, link: String?, address: String?, avatar: String?, priceMin:Float?, priceMax:Float?, chuyenMon:String?, ndtaitro:String?, status: Int?, code: String?){
        this.name = name
        this.link = link
        this.address = address
        this.avatar = avatar
        this.priceMin = priceMin
        this.priceMax = priceMax
        this.chuyenMon = chuyenMon
        this.ndtaitro = ndtaitro
        this.status =status
        this.code = code
    }

    constructor(name: String?, link: String?, address: String?, avatar: String?){
        this.name = name
        this.link = link
        this.address = address
        this.avatar = avatar
    }

    //add du lieu cho image
    constructor(name: String?, link: String?, address: String?, images: String?, avatar: String?){
        this.name = name
        this.link = link
        this.address = address
        this.images = images
        this.avatar = avatar
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

    fun setImages(images: String){
        this.images = images
    }
    fun getImages(): String?{
        return images
    }

    fun setAvatar(avatar: String){
        this.avatar = avatar
    }
    fun getAvatar(): String{
        return avatar?:"restaurants/1519973954_OFsxT.jpg"
    }

    fun setPriceMin(priceMin: Float){
        this.priceMin = priceMin
    }
    fun getPriceMin(): Float{
        return priceMin ?: 0f
    }

    fun setPriceMax(priceMax: Float){
        this.priceMax = priceMax
    }
    fun getPriceMax(): Float{
        return priceMax ?: 0f
    }

    fun setMenu(menu: Any){
        this.menu = menu
    }
    fun getMenu(): Any?{
        return menu
    }

    fun setSMenu(sMenu: Any){
        this.sMenu = sMenu
    }
    fun getSMenu(): Any?{
        return sMenu
    }

    fun setOpenTime(openTime: Any){
        this.openTime = openTime
    }
    fun getOpenTime(): Any?{
        return openTime
    }

    fun setChuyenMon(chuyenMon: String){
        this.chuyenMon = chuyenMon
    }
    fun getChuyenMon(): String?{
        return chuyenMon
    }

    fun setNdtaitro(ndtaitro: String){
        this.ndtaitro = ndtaitro
    }
    fun getNdtaitro(): String?{
        return ndtaitro
    }

    fun setStatus(status: Int){
        this.status = status
    }
    fun getStatus(): Int?{
        return status
    }

    fun setCode(code: String){
        this.code = code
    }
    fun getCode(): String?{
        return code
    }
}