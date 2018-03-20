package gcam.vn.gbot.view.activity

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.View
import com.roughike.bottombar.OnTabSelectListener
import gcam.vn.gbot.BuildConfig
import gcam.vn.gbot.R
import gcam.vn.gbot.manager.ext.LogUtil
import gcam.vn.gbot.util.Utils
import gcam.vn.gbot.view.adapter.HomeViewpagerAdapter
import gcam.vn.gbot.view.widget.BaseActivity
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.android.synthetic.main.activity_home.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.support.v4.view.ViewCompat.setTranslationX
import android.opengl.ETC1.getWidth
import android.os.Handler
import gcam.vn.gbot.view.widget.FontDialog
import android.widget.Toast
import gcam.vn.gbot.manager.event.Event
import gcam.vn.gbot.manager.event.EventDefine
import gcam.vn.gbot.manager.event.EventMessage
import gcam.vn.gbot.manager.ext.SimpleToast


class HomeActivity : BaseActivity(), OnTabSelectListener {

    private lateinit var homeVPAdapter     : HomeViewpagerAdapter
    private lateinit var context           : Context
    private lateinit var view              : View
    private var isExitApp                  : Boolean = false

    companion object {
        val TAG = HomeActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        context = this
        view = this.window.decorView.rootView

        homeViewPager.offscreenPageLimit = HomeViewpagerAdapter.PAGE_NUMBER
        homeVPAdapter                    = HomeViewpagerAdapter(supportFragmentManager)
        homeViewPager.adapter            = homeVPAdapter
        bottomBar.setOnTabSelectListener(this)

    }

    override fun onTabSelected(tabId: Int) {
        Utils.hideKeyboard(this, view)
        when(tabId) {
            R.id.tabChat     -> homeViewPager.currentItem = HomeViewpagerAdapter.CHAT_INDEX
            R.id.tabRestaurant  -> homeViewPager.currentItem = HomeViewpagerAdapter.RESTAURANT_INDEX
            R.id.tabAbout -> homeViewPager.currentItem = HomeViewpagerAdapter.ABOUT_INDEX
            R.id.tabSetting  -> homeViewPager.currentItem = HomeViewpagerAdapter.SETTING_INDEX
        }
    }

    override fun onBackPressed() {
        if (!getIsShowWebView()) {
            if (isExitApp) {
                super.onBackPressed()
                return
            }
            this.isExitApp = true
            SimpleToast.showInfo(this, "Ấn back thêm lần nữa để thoát")
            Handler().postDelayed(Runnable { isExitApp = false }, 2000)
        }else{
            Event.postEvent(EventMessage(EventDefine.DISSMISS_WEBVIEW, true))
        }
    }
}
