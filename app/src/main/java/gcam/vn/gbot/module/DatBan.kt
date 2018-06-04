package gcam.vn.gbot.module

import java.io.Serializable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



/**
 * Created by thuythu on 11/04/2018.
 */
class DatBan: Serializable{

    @SerializedName("@@people")
    @Expose
    private var people: Int? = null
    @SerializedName("@@children")
    @Expose
    private var children: Int? = null
    @SerializedName("@@hourofday")
    @Expose
    private var hourofday: String? = null
    @SerializedName("@@note")
    @Expose
    private var note: String? = null
    @SerializedName("@@restaurant")
    @Expose
    private var restaurant: String? = null
    @SerializedName("@@dayofmonth")
    @Expose
    private var dayofmonth: String? = null

    fun getPeople(): Int {
        return people ?: 0
    }

    fun setPeople(people: Int?) {
        this.people = people
    }

    fun getChildren(): Int {
        return children ?: 0
    }

    fun setChildren(children: Int?) {
        this.children = children
    }

    fun getHourofday(): String {
        return hourofday ?: "@@unk"
    }

    fun setHourofday(hourofday: String) {
        this.hourofday = hourofday
    }

    fun getNote(): String {
        return note ?: "@@unk"
    }

    fun setNote(note: String) {
        this.note = note
    }

    fun getRestaurant(): String {
        return restaurant ?: "@@unk"
    }

    fun setRestaurant(restaurant: String) {
        this.restaurant = restaurant
    }

    fun getDayofmonth(): String {
        return dayofmonth ?: "@@unk"
    }

    fun setDayofmonth(dayofmonth: String) {
        this.dayofmonth = dayofmonth
    }

}