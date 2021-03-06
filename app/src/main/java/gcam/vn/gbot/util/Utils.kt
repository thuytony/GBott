package gcam.vn.gbot.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import org.json.JSONObject
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.telephony.TelephonyManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.location.LocationManager
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import gcam.vn.gbot.manager.ext.LogUtil
import gcam.vn.gbot.service.TrackLocation
import io.fabric.sdk.android.services.settings.IconRequest.build
import android.content.IntentSender
import android.os.Bundle
import android.text.Html
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.location.LocationServices.API
import com.stfalcon.frescoimageviewer.ImageViewer
import gcam.vn.gbot.application.GBotApp
import gcam.vn.gbot.manager.ext.Constant
import gcam.vn.gbot.module.CustomImage
import gcam.vn.gbot.module.ImageOverlayView
import java.lang.reflect.Type
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern.matches
import gcam.vn.gbot.R


/**
 * Created by thuythu on 12/01/2018.
 */
open class Utils{

    companion object {
        var jsonObject: JSONObject? = JSONObject()
        val intent = Intent()
        fun toJsonObj(key: String, value: String){
            jsonObject!!.put(key, value)
        }
        fun toJsonObj(key: String, value: Double){
            jsonObject!!.put(key, value)
        }

        fun removeAllJsonObj(){
            jsonObject = null
            jsonObject = JSONObject()
        }

        fun getJsonObj(): JSONObject {
            return jsonObject!!
        }

        fun startWebView(context: Context, url: String){
            var uri = Uri.parse(url)
            intent.data = uri
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }

        fun crossfade(mContentView: View, mLoadingView: View, mShortAnimationDuration: Long) {

            // Set the content view to 0% opacity but visible, so that it is visible
            // (but fully transparent) during the animation.
            mContentView.setAlpha(0f)
            mContentView.setVisibility(View.VISIBLE)

            // Animate the content view to 100% opacity, and clear any animation
            // listener set on the view.
            mContentView.animate()
                    .alpha(1f)
                    .setDuration(mShortAnimationDuration)
                    .setListener(null)

            // Animate the loading view to 0% opacity. After the animation ends,
            // set its visibility to GONE as an optimization step (it won't
            // participate in layout passes, etc.)
            mLoadingView.animate()
                    .alpha(0f)
                    .setDuration(mShortAnimationDuration)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            mLoadingView.setVisibility(View.GONE)
                        }
                    })

        }

        //an ban phim
        fun hideKeyboard(activity: Activity, view: View) {
            try {
                val view = view
                if (view != null) {
                    val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        fun convertObjectToJson(obj: Any): String{
            return GBotApp.buildInstance().gson().toJson(obj)
        }
        fun convertJsonToObject(json: String, type: Type): Any{
            return GBotApp.buildInstance().gson().fromJson(json, type)
        }

        //get device id
        @SuppressLint("MissingPermission")
        fun getDeviceId(context: Context): String{
            var telephonyManager: TelephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                telephonyManager.imei
            } else {
                telephonyManager.deviceId
            }
        }

        fun checkGPS(activity: Activity): Boolean{
            val lm = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            try {
                if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    return true
                }
            } catch (ex: Exception) {
                LogUtil.e("LOCATION", "error: "+ex.toString())
            }
            return false
        }

        fun connectGPS(activity: Activity){
            var mGoogleApiClient: GoogleApiClient
            mGoogleApiClient = GoogleApiClient.Builder(activity.applicationContext)
                    .addConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks{
                        override fun onConnected(p0: Bundle?) {

                        }

                        override fun onConnectionSuspended(p0: Int) {

                        }
                    })
                    .addOnConnectionFailedListener(object : GoogleApiClient.OnConnectionFailedListener{
                        override fun onConnectionFailed(p0: ConnectionResult) {

                        }
                    })
                    .addApi(LocationServices.API)
                    .build()
            mGoogleApiClient.connect()
            var mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(2000)
                    .setFastestInterval(2000)
            var builder = LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest)
                builder.setAlwaysShow(true)
            var result: PendingResult<LocationSettingsResult> = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build())
            result.setResultCallback(object : ResultCallback<LocationSettingsResult> {
                override fun onResult(p0: LocationSettingsResult) {
                    var status: Status = p0.getStatus()
                    var state: LocationSettingsStates = p0.getLocationSettingsStates()
                    when(status.statusCode){
                        LocationSettingsStatusCodes.SUCCESS -> {

                        }
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                            try {
                                status.startResolutionForResult(activity, Constant.RESULT_REQUEST_LOCATION)
                            } catch (e: IntentSender.SendIntentException) {

                            }
                        }
                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {

                        }
                    }
                }
            })
        }

        //set view image menu restaurant
        fun showMenuViewer(context: Context, overlayView: ImageOverlayView, listImage: MutableList<CustomImage>,
                           hierarchyBuilder: GenericDraweeHierarchyBuilder, startPos: Int,imageChangeListener: ImageViewer.OnImageChangeListener,
                           onDismissListener: ImageViewer.OnDismissListener): ImageViewer{
            var color = 0
            if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.M){
                color = context.resources.getColor(android.R.color.white)
            }else{
                context.resources.getColor(android.R.color.white, null)
            }
            return ImageViewer.Builder(context, listImage)
                    .setStartPosition(startPos)
                    .hideStatusBar(false)
                    .allowZooming(true)
                    .allowSwipeToDismiss(true)
                    .setBackgroundColor(color)
                    .setImageChangeListener(imageChangeListener)
                    .setOnDismissListener(onDismissListener)
                    .setOverlayView(overlayView)
                    .setContainerPaddingPx(0, 0, 0, 20)
                    .setCustomImageRequestBuilder(ImageViewer.createImageRequestBuilder())
                    .setCustomDraweeHierarchyBuilder(hierarchyBuilder)
                    .setFormatter { customImage -> (customImage as CustomImage).getUrl() }
                    .build()
        }

        //check sdt
        fun checkIsPhone(phone: String): Boolean{
            var regex = "(841|01|1)+?\\d{9}".toRegex()
            var regexx = "(849|09|9)+?\\d{8}".toRegex()
            if(regex.matches(phone)){
                return true
            }else if(regexx.matches(phone)){
                return true
            }
            return false
        }

        //check time order restaurant
        fun checkValiTime(time: String): Boolean{
            val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm")
            try {
                val date = dateFormat.parse(time)
                val now = Calendar.getInstance().timeInMillis+1800000
                if(date.time > now){
                    return true
                }else{
                    return false
                }
            } catch (e: ParseException) {
                return false
            }
            return false
        }

        //load image
        fun loadImageByGlide(itemView: View, imageUrl: String, imageView: ImageView){
            var myOb: RequestOptions = RequestOptions().error(R.drawable.img_not_found)
            Glide.with(itemView).load(imageUrl).apply(myOb).into(imageView)
        }

        //load text chi tiet nha hang dang html
        fun loadHtml(text: TextView, str: String, html: String){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                text.setText(str+Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT))
            }else{
                text.setText(str+Html.fromHtml(html))
            }
        }
    }
}