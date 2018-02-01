package gcam.vn.gbot.util

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager


/**
 * Created by thuythu on 12/01/2018.
 */
class ActivityUtils{
    companion object {
        fun replaceFragmentToActivity(fragmentManager: FragmentManager,
                                      fragment: Fragment, frameId: Int, onBackstack: Boolean, tag: String?) {

            val transaction = fragmentManager.beginTransaction()
            transaction.replace(frameId, fragment, tag)
            if (onBackstack) {
                transaction.addToBackStack(null)
            }
            transaction.commit()


        }

        fun addFragmentToActivity(fragmentManager: FragmentManager,
                                  fragment: Fragment, frameId: Int, onBackstack: Boolean, tag: String?) {

            val currentFr = fragmentManager.findFragmentById(frameId)
            val transaction = fragmentManager.beginTransaction()
            transaction.add(frameId, fragment, tag)
            transaction.hide(currentFr)
            if (onBackstack) {
                transaction.addToBackStack(tag)
            }
            transaction.commit()
        }

        fun removeBackStack(fragmentManager: FragmentManager, tag: String) {
            val removeFr = fragmentManager.findFragmentByTag(tag)
            fragmentManager.popBackStack()
        }


    }

}