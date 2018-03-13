package gcam.vn.gbot.service

import android.location.Address
import android.location.Location
import com.google.android.gms.maps.model.LatLng
import gcam.vn.gbot.module.LocationToServer

/**
 * Created by thuythu on 31/01/2018.
 */
interface TrackLocationInterface {
    fun onLocationSuccess (locationToSer: LocationToServer)
    fun onLocationFaile (faile: String)
}