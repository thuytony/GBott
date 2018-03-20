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

    @FormUrlEncoded
    @POST(RestEndPoint.POST_PAGE_RESTAURANT)
    fun postPageRestaurant(@Field("sql") sql: String, @Field("p") p: String): Observable<GBotResponse<MutableList<LoadMoreRestaurant>>>
}