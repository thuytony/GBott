package gcam.vn.gbot.view.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import gcam.vn.gbot.R
import kotlinx.android.synthetic.main.ui_toolbar_back.view.*

/**
 * Created by thuythu on 12/01/2018.
 */
class UiToolBarBack : RelativeLayout, BaseView {

    private lateinit var v: View
    private lateinit var base: BaseActivity

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

        v = LayoutInflater.from(context).inflate(R.layout.ui_toolbar_back,this,true)
        base = context as BaseActivity

        val t = context.obtainStyledAttributes(attrs,
                R.styleable.ToolbarStyleable)
        try {
            val title = t.getString(R.styleable.ToolbarStyleable_title)
            pageTitle.text = title
        } finally {
            t.recycle()
        }


        v.icBack.setOnClickListener { base.supportFragmentManager.popBackStackImmediate()}
    }

    fun setTitle(title: String) {
        pageTitle.text = title
    }

}