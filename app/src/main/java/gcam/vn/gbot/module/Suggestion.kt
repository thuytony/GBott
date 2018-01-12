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
    private var restaurant:Any? = null
    @SerializedName("keyword")
    @Expose
    private var keyword:Any? = null

    constructor(restaurants: List<Restaurant>?, restaurant: Any?, keyword: Any?){
        this.restaurants = restaurants
        this.restaurant = restaurant
        this.keyword = keyword
    }

    fun setRestaurants(restaurants: List<Restaurant>){
        this.restaurants = restaurants
    }
    fun getRestaurants(): List<Restaurant>?{
        return restaurants
    }

    fun setRestaurant(restaurant: Any){
        this.restaurant = restaurant
    }
    fun getRestaurant(): Any?{
        return restaurant
    }

    fun setKeyword(keyword: Any){
        this.keyword = keyword
    }
    fun getKeyword(): Any?{
        return keyword
    }
}