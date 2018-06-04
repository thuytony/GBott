package gcam.vn.gbot.module

import java.io.Serializable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



/**
 * Created by thuythu on 11/04/2018.
 */
class ThongTinCaNhan: Serializable {

    @SerializedName("@@lng")
    @Expose
    private var lng: Double? = null
    @SerializedName("@@district")
    @Expose
    private var district: String? = null
    @SerializedName("@@lat")
    @Expose
    private var lat: Double? = null
    @SerializedName("@@username")
    @Expose
    private var username: String? = null
    @SerializedName("@@province")
    @Expose
    private var province: String? = null
    @SerializedName("@@phone")
    @Expose
    private var phone: String? = null

    constructor()

    fun getLng(): Double {
        return lng ?: 0.0
    }

    fun setLng(lng: Double) {
        this.lng = lng
    }

    fun getDistrict(): String {
        return district ?: "@@unk"
    }

    fun setDistrict(district: String) {
        this.district = district
    }

    fun getLat(): Double {
        return lat ?: 0.0
    }

    fun setLat(lat: Double) {
        this.lat = lat
    }

    fun getUsername(): String {
        return username ?: "@@unk"
    }

    fun setUsername(username: String) {
        this.username = username
    }

    fun getProvince(): String {
        return province ?: "@@unk"
    }

    fun setProvince(province: String) {
        this.province = province
    }

    fun getPhone(): String {
        return phone ?: "@@unk"
    }

    fun setPhone(phone: String) {
        this.phone = phone
    }

}