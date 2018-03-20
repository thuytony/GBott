package gcam.vn.gbot.module

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by thuythu on 12/01/2018.
 */
class Suggestion : Serializable {
    @SerializedName("restaurants")
    @Expose
    private var restaurants:List<Restaurant>? = null
    @SerializedName("restaurant")
    @Expose
    private var restaurant:Restaurant? = null
    @SerializedName("keyword")
    @Expose
    private var keyword:MutableList<KeyWord>? = null
    @SerializedName("images")
    @Expose
    private var images:List<String>? = null
    @SerializedName("action")
    @Expose
    private var action:MutableList<KeyWord>? = null
    @SerializedName("sql")
    @Expose
    private var sql:String? = null
    @SerializedName("search")
    @Expose
    private var search:MutableList<KeyWord>? = null
    @SerializedName("type")
    @Expose
    private var type:Int? = null

    constructor(restaurants: List<Restaurant>?, restaurant: Restaurant?, keyword: MutableList<KeyWord>?, images: List<String>?){
        this.restaurants = restaurants
        this.restaurant  = restaurant
        this.keyword     = keyword
        this.images      = images
    }

    fun setRestaurants(restaurants: List<Restaurant>){
        this.restaurants = restaurants
    }
    fun getRestaurants(): List<Restaurant>?{
        return restaurants
    }

    fun setRestaurant(restaurant: Restaurant){
        this.restaurant = restaurant
    }
    fun getRestaurant(): Restaurant?{
        return restaurant
    }

    fun setKeyword(keyword: MutableList<KeyWord>){
        this.keyword = keyword
    }
    fun getKeyword(): MutableList<KeyWord>?{
        return keyword
    }

    fun setImages(images: List<String>){
        this.images = images
    }
    fun getImages(): List<String>?{
        return images
    }

    fun setAction(action: MutableList<KeyWord>){
        this.action = action
    }
    fun getAction(): MutableList<KeyWord>?{
        return action
    }

    fun setSql(sql: String){
        this.sql = sql
    }
    fun getSql(): String?{
        return sql
    }

    fun setSearch(serach: MutableList<KeyWord>){
        this.search = search
    }
    fun getSearch(): MutableList<KeyWord>?{
        return search
    }

    fun setType(type: Int){
        this.type = type
    }
    fun getType(): Int?{
        return type
    }
}