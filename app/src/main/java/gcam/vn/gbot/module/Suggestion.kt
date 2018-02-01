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
    private var keyword:List<KeyWord>? = null

    constructor(restaurants: List<Restaurant>?, restaurant: Restaurant?, keyword: List<KeyWord>?){
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

    fun setRestaurant(restaurant: Restaurant){
        this.restaurant = restaurant
    }
    fun getRestaurant(): Restaurant?{
        return restaurant
    }

    fun setKeyword(keyword: List<KeyWord>){
        this.keyword = keyword
    }
    fun getKeyword(): List<KeyWord>?{
        return keyword
    }
}