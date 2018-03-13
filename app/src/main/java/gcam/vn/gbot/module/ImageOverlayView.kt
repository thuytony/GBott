package gcam.vn.gbot.module

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import gcam.vn.gbot.R
import kotlinx.android.synthetic.main.view_image_overlay.view.*

/**
 * Created by thuythu on 08/02/2018.
 */
class ImageOverlayView: RelativeLayout {

    private var sharingText: String? = null

    constructor(context: Context): super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    fun setDescription(description: String) {
        tvDescription!!.text = description
    }

    fun setShareText(text: String) {
        this.sharingText = text
    }

    private fun sendShareIntent() {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, sharingText)
        sendIntent.type = "text/plain"
        context.startActivity(sendIntent)
    }

    private fun init() {
        var v = View.inflate(context, R.layout.view_image_overlay, this)
        btnShare.setOnClickListener({
            sendShareIntent()
        })
    }
}