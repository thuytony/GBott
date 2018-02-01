package gcam.vn.gbot.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import gcam.vn.gbot.application.GBotApp
import gcam.vn.gbot.manager.ext.LogUtil
import gcam.vn.gbot.manager.ext.SimpleToast
import java.util.*

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

        var arrLocation = geocoder!!.getFromLocation(location.latitude, location.longitude, 1)
        // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        val msg = "Updated Location: " +
                java.lang.Double.toString(location.latitude) + "," +
                java.lang.Double.toString(location.longitude) + "," +
                arrLocation.toString() + " , "+
                arrLocation.get(0).locality+", "+arrLocation.get(0).subAdminArea+", "+arrLocation.get(0).adminArea
        //LogUtil.d("LOCATION", msg)
        // You can now create a LatLng Object for use with maps
        val latLng = LatLng(location.latitude, location.longitude)
        onLocationListener?.onLocationSuccess(msg)
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
}