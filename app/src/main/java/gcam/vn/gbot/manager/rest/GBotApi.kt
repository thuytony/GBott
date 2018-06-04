package gcam.vn.gbot.manager.rest

import gcam.vn.gbot.BuildConfig
import gcam.vn.gbot.module.LoadMoreRestaurant
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import rx.Observable

/**
 * Created by thuythu on 12/01/2018.
 */
interface GBotApi {
    companion object {

        val DOMAIN = BuildConfig.DOMAIN

    }

    //load more restaurant follow page
    @FormUrlEncoded
    @POST(RestEndPoint.POST_PAGE_RESTAURANT)
    fun postPageRestaurant(@Field("sql") sql: String, @Field("p") p: String): Observable<GBotResponse<MutableList<LoadMoreRestaurant>>>

    //get time restaurant
    @FormUrlEncoded
    @POST(RestEndPoint.GET_TIME_RESTAURANT)
    fun getTimeRestaurant(@Field("res") res: String, @Field("day") day: String): Observable<GBotResponse<Array<String>>>

    //order restaurant
    @FormUrlEncoded
    @POST(RestEndPoint.ORDER_RESTAURANT)
    fun orderRestaurant(@Field("day") day: String, @Field("hour") hour: String, @Field("people") people: String, @Field("children") children: String, @Field("name") name: String, @Field("phone") phone: String, @Field("note") note: String, @Field("res") res: String, @Field("id") id: String): Observable<GBotResponse<String>>

    //complain restaurant
    @FormUrlEncoded
    @POST(RestEndPoint.KHIEU_NAI_RESTAURANT)
    fun postKNHT(@Field("name") name: String, @Field("phone") phone: String, @Field("content") content: String, @Field("id") id: String, @Field("type") type: Int): Observable<GBotResponse<String>>
}