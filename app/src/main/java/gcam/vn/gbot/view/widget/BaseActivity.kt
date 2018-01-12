package gcam.vn.gbot.view.widget

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.view.inputmethod.InputMethodManager

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


}