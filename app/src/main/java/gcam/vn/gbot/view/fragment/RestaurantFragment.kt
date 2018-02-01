package gcam.vn.gbot.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import gcam.vn.gbot.R
import gcam.vn.gbot.view.widget.BaseFragment
import kotlinx.android.synthetic.main.fragment_restaurant.*

/**
 * Created by thuythu on 12/01/2018.
 */
class RestaurantFragment: BaseFragment(){
    private lateinit var root: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.fragment_restaurant, container, false)
        return root
    }

    override fun init() {
        super.init()

        btnChange.setOnClickListener { txtRestaurant.setText("Change text") }
    }

    companion object {

        val TAG = RestaurantFragment::class.java.simpleName

        fun instance(): RestaurantFragment {
            val frag = RestaurantFragment()
            var bundle = Bundle()
            frag.arguments = bundle
            return frag
        }
    }
}