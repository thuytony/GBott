package gcam.vn.gbot.view.widget

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.inputmethod.InputMethodManager
import gcam.vn.gbot.R
import gcam.vn.gbot.manager.ext.Constant
import gcam.vn.gbot.manager.ext.LogUtil
import gcam.vn.gbot.manager.ext.SimpleToast
import gcam.vn.gbot.service.TrackLocation
import gcam.vn.gbot.service.TrackLocationInterface
import gcam.vn.gbot.util.Utils

/**
 * Created by thuythu on 12/01/2018.
 */
open class BaseActivity : AppCompatActivity() {


    companion object {

        val TAG = BaseActivity::class.java.simpleName

    }


    fun hideKeyboard() {
        try {
            val view = currentFocus
            if (view != null) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun getLocation(){
        if(Utils.checkGPS(this)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                TrackLocation.getInstance().startGetLocation()
            }
        }else{
            Utils.connectGPS(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_CANCELED -> {
                LogUtil.d("LOCATION", "cancel")
                when(requestCode) {
                    Constant.RESULT_REQUEST_LOCATION -> {
                        SimpleToast.showInfo(this, getString(R.string.cannot_get_location))
                    }
                }
            }
            Activity.RESULT_OK -> {
                LogUtil.d("LOCATION", "ok")
                when(requestCode) {
                    Constant.RESULT_REQUEST_LOCATION -> {
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            TrackLocation.getInstance().startGetLocation()
                        }
                    }
                }
            }
        }
    }

}