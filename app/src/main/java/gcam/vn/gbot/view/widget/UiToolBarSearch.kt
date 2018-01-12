package gcam.vn.gbot.view.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import gcam.vn.gbot.R

/**
 * Created by thuythu on 12/01/2018.
 */
class UiToolBarSearch : RelativeLayout, BaseView {

    private lateinit var v: View

    constructor(context: Context) : super(context) {
        init(null!!, -1)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, -1)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs, defStyleAttr)
    }

    override fun init(attrs: AttributeSet, defStyleAttr: Int) {

        v = LayoutInflater.from(context).inflate(R.layout.ui_toolbar_search,this,true)

    }

}