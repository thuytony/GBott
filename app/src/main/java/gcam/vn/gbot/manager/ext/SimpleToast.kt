package gcam.vn.gbot.manager.ext

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.support.annotation.StringRes
import android.widget.Toast
import gcam.vn.gbot.application.GBotApp

/**
 * Created by thuythu on 12/01/2018.
 */
class SimpleToast private constructor(context: Context) {

    private val handler: Handler = Handler(Looper.getMainLooper())
    private val context: Context = context.applicationContext

    private fun show(message: String, duration: Int) {
        handler.post { Toast.makeText(GBotApp.buildInstance(), message, duration).show() }
    }

    private fun show(message: Int, duration: Int) {
        handler.post { Toast.makeText(GBotApp.buildInstance(), message, duration).show() }
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
    }
}