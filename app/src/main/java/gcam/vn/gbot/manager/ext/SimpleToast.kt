package gcam.vn.gbot.manager.ext

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.support.annotation.StringRes
import android.view.LayoutInflater
import android.widget.Toast
import gcam.vn.gbot.application.GBotApp
import gcam.vn.gbot.R
import android.view.View
import gcam.vn.gbot.view.widget.FontTextView


/**
 * Created by thuythu on 12/01/2018.
 */
class SimpleToast private constructor(context: Context) {

    private val handler: Handler = Handler(Looper.getMainLooper())
    private val context: Context = context.applicationContext
    private var layoutSuccess: View? = null
    private var toastSuccess: Toast? = null
    private var layoutInfo: View? = null
    private var toastInfo: Toast? = null

    private fun show(message: String, duration: Int) {
        handler.post { Toast.makeText(GBotApp.buildInstance(), message, duration).show() }
    }

    private fun show(message: Int, duration: Int) {
        handler.post { Toast.makeText(GBotApp.buildInstance(), message, duration).show() }
    }

    private fun showSuccess(activity: Context, message: String) {
        if(layoutSuccess == null){
            layoutSuccess = LayoutInflater.from(activity).inflate(R.layout.custom_toast_success, null)
        }
        if(toastSuccess == null){
            toastSuccess = Toast(GBotApp.buildInstance())
        }
        layoutSuccess!!.findViewById<FontTextView>(R.id.txtToast).setText(message)
        //toast.setGravity(Gravity.CENTER, 0, 0)
        toastSuccess!!.duration = Toast.LENGTH_SHORT
        toastSuccess!!.view = layoutSuccess
        handler.post { toastSuccess!!.show() }
    }

    private fun showInfo(activity: Context, message: String) {
        if(layoutInfo == null){
            layoutInfo = LayoutInflater.from(activity).inflate(R.layout.custom_toast_info, null)
        }
        if(toastInfo == null){
            toastInfo = Toast(GBotApp.buildInstance())
        }
        layoutInfo!!.findViewById<FontTextView>(R.id.txtToast).setText(message)
        //toast.setGravity(Gravity.CENTER, 0, 0)
        toastInfo!!.duration = Toast.LENGTH_SHORT
        toastInfo!!.view = layoutInfo
        handler.post { toastInfo!!.show() }
    }

    companion object {
        private var INSTANCE: SimpleToast? = null


        private fun get(): SimpleToast {
            if (INSTANCE == null) {
                INSTANCE = SimpleToast(GBotApp.buildInstance())
            }
            return INSTANCE!!
        }

        fun showShort(message: String) {
            get().show(message, Toast.LENGTH_SHORT)
        }

        fun showLong(message: String) {
            get().show(message, Toast.LENGTH_LONG)
        }

        fun showShort(@StringRes message: Int) {
            get().show(message, Toast.LENGTH_SHORT)
        }

        fun showLong(@StringRes message: Int) {
            get().show(message, Toast.LENGTH_LONG)
        }

        fun showSuccess(activity: Context, message: String){
            get().showSuccess(activity, message)
        }

        fun showInfo(activity: Context, message: String){
            get().showInfo(activity, message)
        }
    }
}