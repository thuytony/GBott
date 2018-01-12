package gcam.vn.gbot.manager.ext

import android.util.Log

/**
 * Created by thuythu on 12/01/2018.
 */
class LogUtil {




    companion object {

        private val isShowLog = true

        fun v(tag: String, message: String) {
            if (isShowLog) {
                Log.v(tag, message)
            }
        }

        fun i(tag: String, message: String) {
            if (isShowLog) {
                Log.i(tag, message)
            }
        }

        fun d(tag: String, message: String) {
            if (isShowLog) {
                Log.d(tag, message)
            }
        }

        fun e(tag: String, message: String) {
            if (isShowLog) {
                Log.e(tag, message)
            }
        }

        fun w(tag: String, message: String) {
            if (isShowLog) {
                Log.w(tag, message)
            }
        }
    }

}