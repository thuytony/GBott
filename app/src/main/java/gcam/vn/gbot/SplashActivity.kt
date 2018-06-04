package gcam.vn.gbot

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import gcam.vn.gbot.manager.ext.LogUtil
import gcam.vn.gbot.manager.ext.PreferenceUtil
import gcam.vn.gbot.manager.ext.SimpleToast
import gcam.vn.gbot.service.TrackLocation
import gcam.vn.gbot.service.TrackLocationInterface
import gcam.vn.gbot.util.Utils
import gcam.vn.gbot.view.activity.LoginActivity
import android.app.Activity
import android.os.Build
import gcam.vn.gbot.manager.ext.Constant


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        checkPermission()
    }

    fun startLogin(){
        var intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(R.anim.mainfadein, R.anim.splashfadeout)
    }

    fun setIdDevice(){
        var preference = PreferenceUtil.mInstance(this)
        if(preference.getDeviceId().isEmpty()){
            preference.setDeviceId(Utils.getDeviceId(this))
            LogUtil.d("DEVICE_ID", preference.getDeviceId())
        }
    }

    fun checkPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE), Constant.REQUEST_READ_PHONE_STATE)
            }else{
                setIdDevice()

                var handle: Handler = Handler()
                var runnable: Runnable = Runnable {
                    startLogin()
                }
                handle.postDelayed(runnable, 2000)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            Constant.REQUEST_READ_PHONE_STATE ->
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setIdDevice()

                    var handle: Handler = Handler()
                    var runnable: Runnable = Runnable {
                        startLogin()
                    }
                    handle.postDelayed(runnable, 2000)
                }else{
                    SimpleToast.showInfo(this, "Bạn cần cho phép quyền truy cập điện thoại để khởi động ứng dụng.")
                    checkPermission()
                }

            else -> {
                SimpleToast.showInfo(this, "Bạn cần cho phép quyền truy cập điện thoại để khởi động ứng dụng.")
            }
        }
    }
}
