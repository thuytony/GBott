package gcam.vn.gbot.view.dialog

import android.app.Activity
import android.content.Context
import android.support.v7.app.AlertDialog
import android.view.View
import gcam.vn.gbot.R
import kotlinx.android.synthetic.main.dialog_book_restaurant.*
import org.jetbrains.annotations.NotNull

/**
 * Created by thuythu on 23/01/2018.
 */
class BookRestaurantDialog: AlertDialog{
    private lateinit var v: View
    constructor(@NotNull context: Context) : super(context){
        init(context)
    }
    fun init(context: Context) {
        val inflater = (context as Activity).layoutInflater
        v = inflater.inflate(R.layout.dialog_book_restaurant, null)
        window!!.attributes.windowAnimations = android.R.style.Theme_Light_NoTitleBar
        setView(v)
    }
    fun getView(): View{
        return v
    }
}