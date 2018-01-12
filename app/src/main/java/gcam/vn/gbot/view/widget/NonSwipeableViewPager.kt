package gcam.vn.gbot.view.widget

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

/**
 * Created by thuythu on 12/01/2018.
 */
class NonSwipeableViewPager : ViewPager {

    private var enabled:Boolean? = false

    constructor(context: Context) : super(context) {
        this.enabled = false
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.enabled = false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return if (this.enabled!!) {
            super.onInterceptTouchEvent(ev)
        } else false

    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return if (this.enabled!!) {
            super.onTouchEvent(ev)
        } else false

    }

    fun setScrollEnabled(enabled: Boolean) {
        this.enabled = enabled
    }
}
