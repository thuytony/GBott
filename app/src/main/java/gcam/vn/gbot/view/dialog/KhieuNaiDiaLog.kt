package gcam.vn.gbot.view.dialog

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import gcam.vn.gbot.R
import gcam.vn.gbot.manager.event.Event
import gcam.vn.gbot.manager.event.EventDefine
import gcam.vn.gbot.manager.event.EventMessage
import gcam.vn.gbot.manager.ext.LogUtil
import gcam.vn.gbot.manager.ext.RxUtil
import gcam.vn.gbot.manager.ext.SimpleToast
import gcam.vn.gbot.manager.rest.RestBuilder
import gcam.vn.gbot.util.Utils
import gcam.vn.gbot.view.widget.BaseActivity
import gcam.vn.gbot.view.widget.FontButton
import gcam.vn.gbot.view.widget.FontEditText
import gcam.vn.gbot.view.widget.FontTextView
import org.jetbrains.annotations.NotNull
import java.text.SimpleDateFormat

/**
 * Created by thuythu on 11/04/2018.
 */
class KhieuNaiDiaLog: AlertDialog {

    private lateinit var v: View
    private lateinit var edtNameKhieuNai: FontEditText
    private lateinit var edtPhoneKhieuNai: FontEditText
    private lateinit var edtNoteKhieuNai: FontEditText
    private lateinit var imgKNClose: ImageView
    private lateinit var txtKNRestaurant: FontTextView
    private lateinit var btnSubmitKhieuNai: FontButton
    private var type = 1
    private var deviceId = ""

    constructor(@NotNull context: Context) : super(context){
        init(context)
    }
    fun init(context: Context) {
        val inflater = (context as Activity).layoutInflater
        v = inflater.inflate(R.layout.dialog_khieu_nai, null)
        window!!.attributes.windowAnimations = android.R.style.Theme_Light_NoTitleBar

        edtNameKhieuNai = v.findViewById(R.id.edtNameKhieuNai)
        edtPhoneKhieuNai = v.findViewById(R.id.edtPhoneKhieuNai)
        edtNoteKhieuNai = v.findViewById(R.id.edtNoteKhieuNai)
        imgKNClose = v.findViewById(R.id.imgKNClose)
        txtKNRestaurant = v.findViewById(R.id.txtKNRestaurant)
        btnSubmitKhieuNai = v.findViewById(R.id.btnSubmitKhieuNai)

        imgKNClose.setOnClickListener { dismiss() }

        btnSubmitKhieuNai.setOnClickListener {
            var alertDialogBuilder = AlertDialog.Builder(context!!)
            // khởi tạo dialog
            alertDialogBuilder.setMessage("Bạn có chắc chắn muốn gửi đơn?")
            // thiết lập nội dung cho dialog
            alertDialogBuilder.setPositiveButton("Có", object : DialogInterface.OnClickListener {
                override fun onClick(arg0: DialogInterface, arg1: Int) {
                    if(validateData()){
                        postKNHT(edtNameKhieuNai.text.toString(), edtPhoneKhieuNai.text.toString(), edtNoteKhieuNai.text.toString(), deviceId, type)
                    }
                }
            })

            alertDialogBuilder.setNegativeButton("Không", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface, which: Int) {
                    // button "no" ẩn dialog đi
                    dialog.dismiss()
                }
            })

            alertDialogBuilder.create().show()
        }

        edtNoteKhieuNai.setOnEditorActionListener(object: TextView.OnEditorActionListener{
            override fun onEditorAction(view: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)

                    var alertDialogBuilder = AlertDialog.Builder(context!!)
                    // khởi tạo dialog
                    alertDialogBuilder.setMessage("Bạn có chắc chắn muốn gửi đơn?")
                    // thiết lập nội dung cho dialog
                    alertDialogBuilder.setPositiveButton("Có", object : DialogInterface.OnClickListener {
                        override fun onClick(arg0: DialogInterface, arg1: Int) {
                            if(validateData()){
                                postKNHT(edtNameKhieuNai.text.toString(), edtPhoneKhieuNai.text.toString(), edtNoteKhieuNai.text.toString(), deviceId, type)
                            }
                        }
                    })

                    alertDialogBuilder.setNegativeButton("Không", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface, which: Int) {
                            // button "no" ẩn dialog đi
                            dialog.dismiss()
                        }
                    })

                    alertDialogBuilder.create().show()

                    return true
                }
                // Return true if you have consumed the action, else false.
                return false
            }

        })

        setView(v)
    }
    fun getView(): View {
        return v
    }

    fun setDataName(name: String){
        edtNameKhieuNai.setText(name)
    }
    fun setDataPhone(phone: String){
        edtPhoneKhieuNai.setText(phone)
    }
    fun setDataNote(note: String){
        edtNoteKhieuNai.setText(note)
    }
    fun setDataResName(resName: String){
        txtKNRestaurant.setText(resName)
    }

    fun getDataName(): String{
        return edtNameKhieuNai.text.toString()
    }
    fun getDataPhone(): String{
        return edtPhoneKhieuNai.text.toString()
    }
    fun getDataNote(): String{
        return edtNoteKhieuNai.text.toString()
    }
    fun getDataResName(): String{
        return txtKNRestaurant.text.toString()
    }

    fun setType(type: Int){
        this.type = type
        if(type == 1){
            txtKNRestaurant.setText("Hợp tác")
        }else if(type == 2){
            txtKNRestaurant.setText("Khiếu nại")
        }
    }
    fun getType(): Int{
        return type
    }
    fun setDeviceId(deviceId: String){
        this.deviceId = deviceId
    }

    fun validateData(): Boolean{
        if(edtNameKhieuNai.text.toString().isEmpty()){
            SimpleToast.showInfo(context, "Bạn chưa nhập tên.")
            return false
        }
        if(edtPhoneKhieuNai.text.toString().isEmpty()){
            SimpleToast.showInfo(context, "Bạn chưa nhập số điện thoại.")
            return false
        }else{
            if(!Utils.checkIsPhone(edtPhoneKhieuNai.text.toString())){
                SimpleToast.showInfo(context, "Số điện thoại không đúng định dạng.")
                return false
            }
        }
        if(edtNoteKhieuNai.text.toString().isEmpty()){
            SimpleToast.showInfo(context, "Bạn chưa nhập nội dung.")
            return false
        }
        return true
    }

    fun postKNHT(name: String, phone: String, content: String, id: String, type: Int){
        RxUtil.applyHandlerStartFinish(RestBuilder.api().postKNHT(name, phone, content, id, type),
                Runnable {  },
                Runnable {  })
                .compose(RxUtil.applyMain())
                .subscribe(
                        {
                            response ->
                            run {
                                dismiss()
                                response.data.let {
                                    LogUtil.d("KHIEU_NAI_RESTAURANT", response.data.toString())
                                    if(response.data!=null){
                                        Event.postEvent(EventMessage(EventDefine.DONE_POST_KNHT, response.data.toString()))
                                    }
                                }
                            }
                        },
                        {t ->
                            dismiss()
                            LogUtil.d("KHIEU_NAI_RESTAURANT", t.message!!)
                            SimpleToast.showShort(t.message!!)
                        }
                )
    }

}