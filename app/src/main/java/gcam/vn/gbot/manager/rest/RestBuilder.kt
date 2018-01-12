package gcam.vn.gbot.manager.rest

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.schedulers.Schedulers
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

/**
 * Created by thuythu on 12/01/2018.
 */
class RestBuilder private constructor(domain: String){
    private val gBotApi: GBotApi

    init {

        val retrofit = Retrofit.Builder()
                .baseUrl(domain)
                .addConverterFactory(GsonConverterFactory.create(provideGson()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
                .client(provideOkhttpClient())
                .build()


        gBotApi = retrofit.create(GBotApi::class.java)

    }


    companion object {

        val UTF8 = Charset.forName("UTF-8")
        private var instance: RestBuilder? = null
        private var instanceRss: RestBuilder? = null
        val TIME_OUT = 30

        fun get(): RestBuilder {

            if (instance == null) {

                instance = RestBuilder(GBotApi.DOMAIN)
            }

            return instance as RestBuilder
        }



        fun api(): GBotApi {

            return get().gBotApi
        }

        fun provideGson(): Gson {

            return GsonBuilder().create()
        }
    }


    private fun provideOkhttpClient(): OkHttpClient {
        val gson = provideGson()
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val paramtersInterceptor = Interceptor {
            chain -> val original = chain.request()
            val originalHttpUrl = original.url()
            val url = originalHttpUrl.newBuilder()
                    .build()
            val request = original.newBuilder()
                    .url(url)
                    .build()
            val response = chain.proceed(request)
            handleResponse(response)
            response
        }
        return OkHttpClient.Builder()
                .connectTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
                .readTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(paramtersInterceptor)
                .build()
    }

    private fun handleResponse(response: Response) {

        if (!response.isSuccessful) {
            ResponseHandler.handlerError(response)
        }

    }
}