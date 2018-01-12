package gcam.vn.gbot.view.activity

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.roughike.bottombar.OnTabSelectListener
import gcam.vn.gbot.BuildConfig
import gcam.vn.gbot.R
import gcam.vn.gbot.manager.ext.LogUtil
import gcam.vn.gbot.view.adapter.HomeViewpagerAdapter
import gcam.vn.gbot.view.widget.BaseActivity
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : BaseActivity(), OnTabSelectListener {

    private lateinit var homeVPAdapter     : HomeViewpagerAdapter
    private lateinit var context           : Context

    companion object {
        val TAG = HomeActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        context = this

        homeViewPager.offscreenPageLimit = HomeViewpagerAdapter.PAGE_NUMBER
        homeVPAdapter                    = HomeViewpagerAdapter(supportFragmentManager)
        homeViewPager.adapter            = homeVPAdapter
        bottomBar.setOnTabSelectListener(this)

    }

    override fun onTabSelected(tabId: Int) {
        when(tabId) {
            R.id.tabChat     -> homeViewPager.currentItem = HomeViewpagerAdapter.CHAT_INDEX
            R.id.tabRestaurant  -> homeViewPager.currentItem = HomeViewpagerAdapter.RESTAURANT_INDEX
            R.id.tabAbout -> homeViewPager.currentItem = HomeViewpagerAdapter.ABOUT_INDEX
            R.id.tabSetting  -> homeViewPager.currentItem = HomeViewpagerAdapter.SETTING_INDEX
        }
    }

}
