package gcam.vn.gbot.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import gcam.vn.gbot.R
import gcam.vn.gbot.application.GBotApp
import gcam.vn.gbot.manager.ext.LogUtil
import gcam.vn.gbot.manager.ext.SimpleToast
import gcam.vn.gbot.util.Utils
import gcam.vn.gbot.view.widget.BaseActivity
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*





class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnLogin.setOnClickListener { v ->
            login("", "")
        }

        edtPass.setOnEditorActionListener(){v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE){
                login("", "")
                true
            } else {
                false
            }
        }
    }


    fun login(user: String, pass: String){
        var view: View = this.window.decorView.rootView
        Utils.hideKeyboard(this, view)
        var intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(R.anim.mainfadein, R.anim.splashfadeout)
    }

}
