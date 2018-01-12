package gcam.vn.gbot

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import gcam.vn.gbot.application.GBotApp
import gcam.vn.gbot.manager.ext.LogUtil
import gcam.vn.gbot.manager.ext.SimpleToast
import gcam.vn.gbot.view.activity.HomeActivity
import io.socket.client.Socket
import io.socket.emitter.Emitter

class SplashActivity : AppCompatActivity() {

    private lateinit var mSocket : Socket

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        var intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
