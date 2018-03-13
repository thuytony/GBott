package gcam.vn.gbot.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import gcam.vn.gbot.application.GBotApp
import gcam.vn.gbot.manager.ext.Constant
import gcam.vn.gbot.manager.ext.LogUtil
import gcam.vn.gbot.manager.ext.SimpleToast
import gcam.vn.gbot.module.AddressFromRequest
import gcam.vn.gbot.module.LocationToServer
import gcam.vn.gbot.util.Utils
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*
import java.lang.reflect.Array.getDouble
import java.lang.reflect.InvocationTargetException
import java.net.HttpURLConnection


/**
 * Created by thuythu on 31/01/2018.
 */
class TrackLocation private constructor(context: Context): GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener{

    private var mGoogleApiClient: GoogleApiClient? = null
    private var mLocationRequest: LocationRequest? = null
    private val UPDATE_INTERVAL = (2 * 1000).toLong()  /* 10 secs */
    private val FASTEST_INTERVAL: Long = 2000 /* 2 sec */
    private var mLocation: Location? = null
    private var onLocationListener:TrackLocationInterface? = null
    private var geocoder: Geocoder? = null

    companion object {
        private var INSTANCE: TrackLocation? = null


        fun getInstance(): TrackLocation {
            if (INSTANCE == null) {
                INSTANCE = TrackLocation(GBotApp.buildInstance())
            }
            return INSTANCE!!
        }
    }

    init {
        mGoogleApiClient = GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
        geocoder = Geocoder(context, Locale.getDefault());
        LogUtil.d("LOCATION","uooo");
    }

    fun startGetLocation(){
        if (mGoogleApiClient != null) {
            mGoogleApiClient!!.connect()
        }
    }

    fun stopGetLocation(){
        if (mGoogleApiClient!!.isConnected()) {
            mGoogleApiClient!!.disconnect()
        }
    }

    @SuppressLint("MissingPermission")
    override fun onConnected(p0: Bundle?) {
        startLocationUpdates()

        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)

        if (mLocation == null) {
            startLocationUpdates()
        }
        if (mLocation != null) {

        } else {
            LogUtil.d("LOCATION", "Location not Detected, Không hỗ trợ, chưa bật GPS")
        }
    }

    override fun onConnectionSuspended(i: Int) {
        LogUtil.d("LOCATION", "Connection Suspended")
        mGoogleApiClient!!.connect()
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        LogUtil.d("LOCATION", "Connection failed. Error: " + connectionResult.getErrorCode())
        onLocationListener?.onLocationFaile("Connection failed. Error: " + connectionResult.getErrorCode())
    }

    override fun onLocationChanged(location: Location) {

        try {
            var arrLocation = geocoder!!.getFromLocation(location.latitude, location.longitude, 1)
            // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            if (arrLocation.size > 0) {
                val msg = "Updated Location: " +
                        java.lang.Double.toString(location.latitude) + "," +
                        java.lang.Double.toString(location.longitude) + "," +
                        arrLocation.toString() + " , " +
                        arrLocation.get(0).locality + ", " + arrLocation.get(0).subAdminArea + ", " + arrLocation.get(0).adminArea
                //LogUtil.d("LOCATION", Utils.convertObjectToJson(arrLocation))
                // You can now create a LatLng Object for use with maps
                val latLng = LatLng(location.latitude, location.longitude)
                var locationToSer: LocationToServer = LocationToServer("", location.latitude, location.longitude,
                        arrLocation.get(0).locality, arrLocation.get(0).subAdminArea, arrLocation.get(0).adminArea)
                onLocationListener?.onLocationSuccess(locationToSer)
                //LogUtil.d("LOCATION", Utils.convertObjectToJson(locationToSer))
            } else {
                //onLocationListener?.onLocationFaile("Không chuyển được vị trí text")
                LogUtil.d("LOCATION", "Không chuyển được vị trí text")
                getNameLocation("${location.latitude},${location.longitude}", location.latitude, location.longitude)
            }
        }catch (e: IOException){
            e.printStackTrace()
            LogUtil.d("LOCATION", "Exception location: ${e.message}")
            getNameLocation("${location.latitude},${location.longitude}", location.latitude, location.longitude)

        }
        stopGetLocation()
    }

    @SuppressLint("MissingPermission")
    protected fun startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL)
        // Request location updates
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this)
    }

    fun setOnLocationListener(onLocationListener:TrackLocationInterface){
        this.onLocationListener = onLocationListener
    }

    //khi ko lay dc ten vitri bang geocoder:
    fun getNameLocation(placesName: String, lt: Double, ln: Double){
        var queue: RequestQueue = Volley.newRequestQueue(GBotApp.buildInstance())
        var request: StringRequest = StringRequest(Request.Method.GET, "http://maps.google.com/maps/api/geocode/json?address=$placesName&ka&sensor=false",
                Response.Listener {
                    s ->
                    var jsonObject = JSONObject(s)
                    LogUtil.d("LOCATION", "json ${jsonObject.get("results")}")
                    try {
                        if (jsonObject.get("status").equals("OK")) {
                            var jsonAdd: JSONArray = JSONObject(s).getJSONArray("results").getJSONObject(0).getJSONArray("address_components") as JSONArray
                            var jsonObjLocality: JSONObject = jsonAdd.get(2) as JSONObject
                            var jsonObjSubArea: JSONObject = jsonAdd.get(4) as JSONObject
                            var jsonObjArea: JSONObject = jsonAdd.get(5) as JSONObject
                            var strLocality : String = jsonObjLocality.get("long_name") as String
                            var strSubArea : String = jsonObjSubArea.get("long_name") as String
                            var strArea : String = jsonObjArea.get("long_name") as String
                            var locationToSer: LocationToServer = LocationToServer("", lt, ln,
                                    strLocality, strSubArea, strArea)
                            onLocationListener?.onLocationSuccess(locationToSer)
                        }
                    }catch (io: InvocationTargetException){
                        LogUtil.d("LOCATION", "exception ${io.toString()}")
                        onLocationListener?.onLocationFaile("Lỗi convert Json")
                    }catch (ex: JSONException){
                        LogUtil.d("LOCATION", "exception ${ex.toString()}")
                        onLocationListener?.onLocationFaile("Lỗi convert Json")
                    }catch (ca: ClassCastException){
                        LogUtil.d("LOCATION", "exception ${ca.toString()}")
                        onLocationListener?.onLocationFaile("Lỗi convert Json")
                    }
                },
                Response.ErrorListener {
                    onLocationListener?.onLocationFaile("Không chuyển được vị trí text")
                })
        request.setRetryPolicy(DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
        queue.add(request)
    }
}