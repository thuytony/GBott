package gcam.vn.gbot.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import gcam.vn.gbot.R
import gcam.vn.gbot.view.widget.BaseFragment

/**
 * Created by thuythu on 12/01/2018.
 */

class SettingFragment: BaseFragment(){
    private lateinit var root: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.fragment_setting, container, false)
        return root
    }

    override fun init() {
        super.init()
    }

    companion object {

        val TAG = SettingFragment::class.java.simpleName

        fun instance(): SettingFragment {
            val frag = SettingFragment()
            var bundle = Bundle()
            frag.arguments = bundle
            return frag
        }
    }
}