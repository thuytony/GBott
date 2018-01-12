package gcam.vn.gbot.view.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import gcam.vn.gbot.view.fragment.AboutFragment
import gcam.vn.gbot.view.fragment.ChatFragment
import gcam.vn.gbot.view.fragment.RestaurantFragment
import gcam.vn.gbot.view.fragment.SettingFragment

/**
 * Created by thuythu on 12/01/2018.
 */
class HomeViewpagerAdapter(fm: FragmentManager?): FragmentStatePagerAdapter(fm){

    companion object {
        val PAGE_NUMBER = 4
        val CHAT_INDEX = 0
        val RESTAURANT_INDEX = 1
        val ABOUT_INDEX = 2
        val SETTING_INDEX = 3
    }

    init {

    }

    override fun getItem(position: Int): Fragment {
        var fr: Fragment? = null

        when(position){
            CHAT_INDEX -> fr = ChatFragment.instance()
            RESTAURANT_INDEX -> fr = RestaurantFragment.instance()
            ABOUT_INDEX -> fr = AboutFragment.instance()
            SETTING_INDEX -> fr = SettingFragment.instance()
        }
        return fr!!
    }

    override fun getCount(): Int {
        return PAGE_NUMBER
    }
}