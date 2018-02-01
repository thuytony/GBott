package gcam.vn.gbot.service

/**
 * Created by thuythu on 31/01/2018.
 */
interface TrackLocationInterface {
    fun onLocationSuccess (location: String)
    fun onLocationFaile (faile: String)
}