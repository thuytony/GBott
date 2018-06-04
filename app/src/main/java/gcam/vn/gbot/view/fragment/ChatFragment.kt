package gcam.vn.gbot.view.fragment

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextWatcher
import gcam.vn.gbot.BuildConfig
import gcam.vn.gbot.R
import gcam.vn.gbot.application.GBotApp
import gcam.vn.gbot.manager.event.EventDefine
import gcam.vn.gbot.manager.event.EventMessage
import gcam.vn.gbot.view.adapter.ChatAdapter
import gcam.vn.gbot.view.widget.BaseFragment
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.android.synthetic.main.fragment_chat.*
import gcam.vn.gbot.util.Utils
import io.socket.emitter.Emitter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import android.text.Editable
import android.text.Selection
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import com.afollestad.materialdialogs.MaterialDialog
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.stfalcon.frescoimageviewer.ImageViewer
import gcam.vn.gbot.manager.ext.*
import gcam.vn.gbot.manager.rest.RestBuilder
import gcam.vn.gbot.module.*
import gcam.vn.gbot.service.TrackLocation
import gcam.vn.gbot.service.TrackLocationInterface
import gcam.vn.gbot.view.adapter.SectionActionAdapter
import gcam.vn.gbot.view.adapter.SectionRestaurantsAdapter
import gcam.vn.gbot.view.dialog.BookRestaurantDialog
import gcam.vn.gbot.view.dialog.KhieuNaiDiaLog
import gcam.vn.gbot.view.widget.FontButton
import gcam.vn.gbot.view.widget.FontEditText
import gcam.vn.gbot.view.widget.FontTextView
import kotlinx.android.synthetic.main.dialog_book_restaurant.*
import retrofit2.http.Field
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by thuythu on 12/01/2018.
 */
class ChatFragment: BaseFragment(), TrackLocationInterface {

    private lateinit var root: View
    private lateinit var allSampleData: MutableList<ChatDataModel>
    private lateinit var mSocket : Socket
    private lateinit var restaurant: MutableList<Restaurant>
    private lateinit var chatAdapter: ChatAdapter
    private var resFromClick: Restaurant = Restaurant()
    private lateinit var prefernce: PreferenceUtil
    private lateinit var dialogPickSuggesstion: MaterialDialog
    private var postWaitBot = 0
    private var resFromClickAction: KeyWord = KeyWord()

    //static web view show detail restaurant
    private lateinit var mPopupWebView: PopupWindow
    private lateinit var mWebViewNew: WebView
    private lateinit var mToolBarPopUp: ImageView
    private lateinit var mImgBackPopUp: ImageView
    private lateinit var mPageTitle: FontTextView
    private lateinit var progressBar: ProgressBar

    private lateinit var chatFromServerDefault: ChatFromServer
    private var dmmDefault = ChatDataModel()

    //latlong
    private var strLatLn: String = ""

    //dat ban = form
    private lateinit var dialogBookRestaurant: BookRestaurantDialog
    private lateinit var spinnerTime: Spinner
    private lateinit var itemBookTime: Array<String>
    private lateinit var cal: Calendar
    private lateinit var txtDate: FontTextView
    private lateinit var dateNext: LinearLayout
    private lateinit var datePre: LinearLayout
    private lateinit var txtAdult: FontTextView
    private lateinit var adultNext: LinearLayout
    private lateinit var adultPre: LinearLayout
    private lateinit var txtYoung: FontTextView
    private lateinit var youngNext: LinearLayout
    private lateinit var youngPre: LinearLayout
    private lateinit var timeNext: LinearLayout
    private lateinit var timePre: LinearLayout
    private lateinit var edtName: FontEditText
    private lateinit var edtPhone: FontEditText
    private lateinit var edtNote: FontEditText
    private lateinit var btnBookDatBan: FontButton
    private lateinit var txtBookRestaurant: FontTextView
    private lateinit var txtErrorPhone: FontTextView
    //object luu du lieu object User moi nhat
    private var objectUserRecent: ObjectUser = ObjectUser()

    //dialog khieu nai
    private lateinit var dialogKhieuNai: KhieuNaiDiaLog

    //check phone
    private var timer = Timer()
    private val DELAY: Long = 3000

    companion object {

        val TAG = ChatFragment::class.java.simpleName

        fun instance(): ChatFragment {
            val frag = ChatFragment()
            var bundle = Bundle()
            frag.arguments = bundle
            return frag
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.fragment_chat, container, false)
        return root
    }

    override fun init() {
        super.init()

        prefernce = PreferenceUtil.mInstance(context!!)
        Log.d("DEVICE_ID", prefernce.getDeviceId())
        listenSocket()
        setIconSend()
        creatDialogSuggest()

        allSampleData = arrayListOf()
        restaurant = arrayListOf()

        rcChat.setHasFixedSize(true)
        chatAdapter = ChatAdapter(context!!, allSampleData)
        //
        //chatAdapter.setHasStableIds(true)
        //
        rcChat.layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.VERTICAL, false)
        rcChat.adapter = chatAdapter

        imgSend.setOnClickListener { v->

            /**
            //set socket kết nối
            mSocket.let {
                if(!mSocket.connected()){
                    SimpleToast.showInfo(base, "Chưa thể gửi tin nhắn vào lúc này.")
                }else{
                    //remove suggess
                    removeSuggesstion()
                    removeWaitBot()
                    //

                    clientSendMessage()

                    //create chat
                    createWaitBot()
                    //
                }
            }
            //
            **/

            //remove suggess
            //removeSuggesstion()
            removeWaitBot()
            //

            //create new client message
            clientSendMessage()

            //create chat
            createWaitBot()
            //

         }

        mSocket.on("server-botchat", onBotChat)
        mSocket.on("server-hello", onServerHello)

        //event on scroll bottom
        setOnCrollBottom()

        //get location
        base.getLocation()
        TrackLocation.getInstance().setOnLocationListener(this)

        //create popup web view
        createPopUp()

        //dat ban = form
        setFormBookRestaurant()

        //khieu nai, hop tac = form
        dialogKhieuNai = KhieuNaiDiaLog(context!!)
        dialogKhieuNai.setDeviceId(prefernce.getDeviceId())
    }

    override fun onDestroy() {
        super.onDestroy()
        /*if(mSocket.connected()){
            mSocket.disconnect()
        }*/
    }

    override fun onStop() {
        super.onStop()
        mSocket.let {
            if(mSocket.connected()){
                mSocket.disconnect()
            }
        }
    }

    //create new client message
    fun clientSendMessage(){
        Utils.toJsonObj("msg", edtChat.text.toString())
        //Utils.toJsonObj("type", "1")
        Utils.toJsonObj("id", prefernce.getDeviceId())
        Log.d("DEVICE_ID", Utils.getJsonObj().toString())
        mSocket.emit("client-send-msg", Utils.getJsonObj())
        Utils.removeAllJsonObj()

        var dm = ChatDataModel()
        dm.setHeaderTitle("My Chat - Hôm nay 18:00")
        dm.setMessageBot(edtChat.text.toString())
        dm.setType(ChatAdapter.TYPE_MY)
        restaurant = arrayListOf()
        restaurant.add(Restaurant(edtChat.text.toString(), null, null, null))
        dm.setAllItemsInSection(restaurant)
        LogUtil.d("size", allSampleData.size.toString())
        allSampleData.add(dm)
        LogUtil.d("size sau", allSampleData.size.toString())
        chatAdapter.replaceChatData(allSampleData)
        base.runOnUiThread {
            chatAdapter.notifyDataSetChanged()
            try {
                rcChat.smoothScrollToPosition(allSampleData.size)
            }catch (e: NullPointerException){
                LogUtil.e("ERROR", "error smooth recycler view")
            }
        }
        edtChat.setText("")
    }

    fun listenSocket(){
        mSocket = IO.socket(BuildConfig.DOMAIN_SOCKET)
        if(!mSocket.connected()) {
            try {
                mSocket.connect()

                Utils.toJsonObj("id", prefernce.getDeviceId())
                mSocket.emit("client-connect", Utils.getJsonObj())
                Utils.removeAllJsonObj()
            } catch (e: Exception) {
                e.printStackTrace()
                LogUtil.d("MAIN_SERVICE", e.printStackTrace().toString())
            }
        }
    }
    /*private val onBotChat = Emitter.Listener {
        args ->
        val message = args[0].toString()
        LogUtil.d("MAIN_SERVICE", "khi co su kien moi login: " + message)

        //remove wait bot
        removeWaitBot()
        //

        var chatFromServer: ChatFromServer = GBotApp.buildInstance().gson().fromJson(message, Constant.chatFromServerType)

        var dmm = ChatDataModel()
        //restaurant.removeAll(restaurant)
        restaurant = arrayListOf()
        if(chatFromServer.getSuggestion()!!.getRestaurants()!!.size>0){
            var size: Int = chatFromServer.getSuggestion()!!.getRestaurants()!!.size
            LogUtil.d("size", "size tra ve: "+size.toString())
            if(size==1){
                //khi restaurants co 1 item
                dmm.setHeaderTitle("Bot Chat")
                dmm.setMessageBot(chatFromServer.getAnswer()!!)
                dmm.setType(ChatAdapter.TYPE_RESTAURANT)
                restaurant.add(Restaurant(chatFromServer.getSuggestion()!!.getRestaurants()!!.get(0).getName(), chatFromServer.getSuggestion()!!.getRestaurants()!!.get(0).getLink(), chatFromServer.getSuggestion()!!.getRestaurants()!!.get(0).getAddress()))
                LogUtil.d("size", allSampleData.size.toString())
                //dmm.setAllItemsInSection(restaurant)
                //allSampleData.add(dmm)
                LogUtil.d("size sau", allSampleData.size.toString())
            }else {
                for (i in 0..size - 1) {
                    dmm.setHeaderTitle("Bot Chat")
                    dmm.setMessageBot(chatFromServer.getAnswer()!!)
                    dmm.setType(ChatAdapter.TYPE_RESTAURANTS)
                    restaurant.add(Restaurant(chatFromServer.getSuggestion()!!.getRestaurants()!!.get(i).getName(), chatFromServer.getSuggestion()!!.getRestaurants()!!.get(i).getLink(), chatFromServer.getSuggestion()!!.getRestaurants()!!.get(i).getAddress()))
                    LogUtil.d("size", allSampleData.size.toString())
                    //dmm.setAllItemsInSection(restaurant)
                    //allSampleData.add(dmm)
                    LogUtil.d("size sau", allSampleData.size.toString())
                }
            }
        }else if((chatFromServer.getSuggestion()!!.getRestaurant()!!.getName())!=null){
            LogUtil.d("size", "Khi restaurant: ")
            dmm.setHeaderTitle("Bot Chat")
            dmm.setMessageBot(chatFromServer.getAnswer()!!)
            dmm.setType(ChatAdapter.TYPE_RESTAURANTS)
            restaurant.add(Restaurant(chatFromServer.getSuggestion()!!.getRestaurant()!!.getName(), chatFromServer.getSuggestion()!!.getRestaurant()!!.getLink(), chatFromServer.getSuggestion()!!.getRestaurant()!!.getAddress()))
            LogUtil.d("size", allSampleData.size.toString())
            //dmm.setAllItemsInSection(restaurant)
            //allSampleData.add(dmm)
            LogUtil.d("size sau", allSampleData.size.toString())
        }else if(chatFromServer.getSuggestion()!!.getKeyword()!!.size>0){
            var size: Int = chatFromServer.getSuggestion()!!.getKeyword()!!.size
            LogUtil.d("size", "size tra ve: "+size.toString())
            for(i in 0..size-1){
                dmm.setHeaderTitle("Bot Chat")
                dmm.setMessageBot(chatFromServer.getAnswer()!!)
                dmm.setType(ChatAdapter.TYPE_IMAGES)
                //neu type = 1: text
                /*if(chatFromServer.getSuggestion()!!.getKeyword()!!.get(i).getType()!!.equals("1")){
                    //gan content 0 cho name, key cho link, type cho address, content cho content
                    restaurant.add(Restaurant(chatFromServer.getSuggestion()!!.getKeyword()!!.get(i).getContent()!!.get(0), chatFromServer.getSuggestion()!!.getKeyword()!!.get(i).getKey()!!, chatFromServer.getSuggestion()!!.getKeyword()!!.get(i).getType()!!, chatFromServer.getSuggestion()!!.getKeyword()!!.get(i).getContent()!!))
                }else{

                }*/
                //gan content 0 cho name, key cho link, type cho address, content cho content
                restaurant.add(Restaurant(chatFromServer.getSuggestion()!!.getKeyword()!!.get(i).getContent()!!.get(0), chatFromServer.getSuggestion()!!.getKeyword()!!.get(i).getKey()!!, chatFromServer.getSuggestion()!!.getKeyword()!!.get(i).getType()!!, chatFromServer.getSuggestion()!!.getKeyword()!!.get(i).getContent()!!))
                LogUtil.d("size", allSampleData.size.toString())
                //dmm.setAllItemsInSection(restaurant)
                //allSampleData.add(dmm)
                LogUtil.d("size sau", allSampleData.size.toString())
            }
        }else{
            LogUtil.d("size", "size tra ve: "+chatFromServer.getSuggestion()!!.getRestaurants()!!.size)
            dmm.setHeaderTitle("Bot Chat")
            dmm.setMessageBot(chatFromServer.getAnswer()!!)
            dmm.setType(ChatAdapter.TYPE_FRIEND_TEXT)
            restaurant.add(Restaurant(chatFromServer.getAnswer(), null, null))
            //dmm.setAllItemsInSection(restaurant)
            LogUtil.d("size", allSampleData.size.toString())
            //allSampleData.add(dmm)
            LogUtil.d("size sau", allSampleData.size.toString())
        }
        dmm.setAllItemsInSection(restaurant)
        allSampleData.add(dmm)
        chatAdapter.replaceChatData(allSampleData)
        base.runOnUiThread {
            chatAdapter.notifyDataSetChanged()
            rcChat.smoothScrollToPosition(allSampleData.size)
        }
    }*/

    private val onBotChat = Emitter.Listener {
        args ->
        var message = args[0].toString()
        LogUtil.d("MAIN_SERVICE", "khi co su kien moi login: " + message)

        //remove wait bot
        removeWaitBot()
        //

        var chatFromServer: ChatFromServer = GBotApp.buildInstance().gson().fromJson(message, Constant.chatFromServerType)

        //gan data object user gan nhat
        objectUserRecent = chatFromServer.getObjectUser() ?: ObjectUser()

        var dmm = ChatDataModel()
        restaurant = arrayListOf()
        chatFromServer.setType(chatFromServer.getSuggestion()?.getType()?:ChatAdapter.TYPE_FRIEND_TEXT)
        if(chatFromServer.getType() == ChatAdapter.TYPE_RESTAURANTS){
            var size: Int = chatFromServer.getSuggestion()!!.getRestaurants()!!.size
            if(size==1){
                //khi restaurants co 1 item
                addRestaurantsIfOne(chatFromServer, dmm)
            }else {
                var size: Int = chatFromServer.getSuggestion()!!.getRestaurants()!!.size
                //khoi tao du lieu neu null
                if(size <= 0){
                    addFriendChatText(chatFromServerDefault, dmmDefault)
                }else{
                    addRestaurants(chatFromServer, dmm)
                }
            }
        }else if(chatFromServer.getType() == ChatAdapter.TYPE_RESTAURANT){
            //khoi tao du lieu neu null
            if(chatFromServer.getSuggestion()!!.getRestaurant()==null){
                addFriendChatText(chatFromServerDefault, dmmDefault)
            }else{
                addOneRestaurant(chatFromServer, dmm)
            }
        }else if(chatFromServer.getType() == ChatAdapter.TYPE_IMAGES){
            var sizeImage: Int = chatFromServer.getSuggestion()!!.getImages()!!.size
            if(sizeImage==1){
                //khi image co 1 item
                addImageIfOne(chatFromServer, dmm)
            }else {
                var size: Int = chatFromServer.getSuggestion()!!.getImages()!!.size
                //khoi tao du lieu neu null
                if(size<=0){
                    addFriendChatText(chatFromServerDefault, dmmDefault)
                }else{
                    addImages(chatFromServer, dmm)
                }
            }
        }else{
            addFriendChatText(chatFromServer, dmm)
        }
    }

    //add one item restaurant
    fun addOneRestaurant(chatFromServer: ChatFromServer, dmm: ChatDataModel){
        dmm.setHeaderTitle("Bot Chat - Hôm nay 18:00")
        dmm.setMessageBot(chatFromServer.getAnswer()!!)
        dmm.setContentKeyWord(chatFromServer.getSuggestion()?.getKeyword())
        dmm.setListAction(chatFromServer.getSuggestion()?.getAction()?: arrayListOf())
        dmm.setTagView(chatFromServer.getSuggestion()?.getSearch()?: arrayListOf())
        dmm.setType(ChatAdapter.TYPE_RESTAURANT)
        restaurant.add(Restaurant(chatFromServer.getSuggestion()!!.getRestaurant()!!.getName(), chatFromServer.getSuggestion()!!.getRestaurant()!!.getLink(), chatFromServer.getSuggestion()!!.getRestaurant()!!.getAddress(), chatFromServer.getSuggestion()!!.getRestaurant()!!.getAvatar(),
                chatFromServer.getSuggestion()!!.getRestaurant()!!.getPriceMin(), chatFromServer.getSuggestion()!!.getRestaurant()!!.getPriceMax(), chatFromServer.getSuggestion()!!.getRestaurant()!!.getChuyenMon(),
                chatFromServer.getSuggestion()!!.getRestaurant()!!.getNdtaitro(), chatFromServer.getSuggestion()!!.getRestaurant()!!.getStatus(), chatFromServer.getSuggestion()!!.getRestaurant()!!.getCode()))

        dmm.setAllItemsInSection(restaurant)
        allSampleData.add(dmm)
        chatAdapter.replaceChatData(allSampleData)
        base.runOnUiThread {
            chatAdapter.notifyDataSetChanged()
            try {
                rcChat.smoothScrollToPosition(allSampleData.size)
            }catch (e: NullPointerException){
                LogUtil.e("ERROR", "error smooth recycler view")
            }
        }
    }

    //if restaurants have one item, add as restautant
    fun addRestaurantsIfOne(chatFromServer: ChatFromServer, dmm: ChatDataModel){
        dmm.setHeaderTitle("Bot Chat - Hôm nay 18:00")
        dmm.setMessageBot(chatFromServer.getAnswer()!!)
        dmm.setContentKeyWord(chatFromServer.getSuggestion()?.getKeyword())
        dmm.setTagView(chatFromServer.getSuggestion()?.getSearch()?: arrayListOf())
        //khi list nha hang tra ve 1 nha hang -> tao list action rong, sua lai load du lieu tra ve
        dmm.setListAction(chatFromServer.getSuggestion()?.getAction()?: arrayListOf())
        //
        dmm.setType(ChatAdapter.TYPE_RESTAURANT)
        restaurant.add(Restaurant(chatFromServer.getSuggestion()!!.getRestaurants()!!.get(0).getName(), chatFromServer.getSuggestion()!!.getRestaurants()!!.get(0).getLink(), chatFromServer.getSuggestion()!!.getRestaurants()!!.get(0).getAddress(), chatFromServer.getSuggestion()!!.getRestaurants()!!.get(0).getAvatar(),
                chatFromServer.getSuggestion()!!.getRestaurants()!!.get(0).getPriceMin(), chatFromServer.getSuggestion()!!.getRestaurants()!!.get(0).getPriceMax(), chatFromServer.getSuggestion()!!.getRestaurants()!!.get(0).getChuyenMon(),
                chatFromServer.getSuggestion()!!.getRestaurants()!!.get(0).getNdtaitro(), chatFromServer.getSuggestion()!!.getRestaurants()!!.get(0).getStatus(), chatFromServer.getSuggestion()!!.getRestaurants()!!.get(0).getCode()))

        dmm.setAllItemsInSection(restaurant)
        allSampleData.add(dmm)
        chatAdapter.replaceChatData(allSampleData)
        base.runOnUiThread {
            chatAdapter.notifyDataSetChanged()
            try {
                rcChat.smoothScrollToPosition(allSampleData.size)
            }catch (e: NullPointerException){
                LogUtil.e("ERROR", "error smooth recycler view")
            }
        }
    }

    //add restaurants (nhieu nha hang)
    fun addRestaurants(chatFromServer: ChatFromServer, dmm: ChatDataModel){
        var size: Int = chatFromServer.getSuggestion()!!.getRestaurants()!!.size
        for (i in 0..size - 1) {
            dmm.setHeaderTitle("Bot Chat - Hôm nay 18:00")
            dmm.setMessageBot(chatFromServer.getAnswer()!!)
            dmm.setContentKeyWord(chatFromServer.getSuggestion()?.getKeyword())
            dmm.setTagView(chatFromServer.getSuggestion()?.getSearch()?: arrayListOf())
            //add sql for load more
            dmm.setSql(chatFromServer.getSuggestion()?.getSql()?:"")
            dmm.setType(ChatAdapter.TYPE_RESTAURANTS)
            restaurant.add(Restaurant(chatFromServer.getSuggestion()!!.getRestaurants()!!.get(i).getName(), chatFromServer.getSuggestion()!!.getRestaurants()!!.get(i).getLink(), chatFromServer.getSuggestion()!!.getRestaurants()!!.get(i).getAddress(), chatFromServer.getSuggestion()!!.getRestaurants()!!.get(i).getAvatar(),
                    chatFromServer.getSuggestion()!!.getRestaurants()!!.get(i).getPriceMin(), chatFromServer.getSuggestion()!!.getRestaurants()!!.get(i).getPriceMax(), chatFromServer.getSuggestion()!!.getRestaurants()!!.get(i).getChuyenMon(),
                    chatFromServer.getSuggestion()!!.getRestaurants()!!.get(i).getNdtaitro(), chatFromServer.getSuggestion()!!.getRestaurants()!!.get(i).getStatus(), chatFromServer.getSuggestion()!!.getRestaurants()!!.get(i).getCode()))
        }

        dmm.setAllItemsInSection(restaurant)
        allSampleData.add(dmm)
        chatAdapter.replaceChatData(allSampleData)
        base.runOnUiThread {
            chatAdapter.notifyDataSetChanged()
            try {
                rcChat.smoothScrollToPosition(allSampleData.size)
            }catch (e: NullPointerException){
                LogUtil.e("ERROR", "error smooth recycler view")
            }
        }
    }

    //add images (nhieu anh)
    fun addImages(chatFromServer: ChatFromServer, dmm: ChatDataModel){
        var size: Int = chatFromServer.getSuggestion()!!.getImages()!!.size
        for(i in 0..size-1){
            dmm.setHeaderTitle("Bot Chat - Hôm nay 18:00")
            dmm.setMessageBot(chatFromServer.getAnswer()!!)
            dmm.setContentKeyWord(chatFromServer.getSuggestion()?.getKeyword())
            dmm.setTagView(chatFromServer.getSuggestion()?.getSearch()?: arrayListOf())
            dmm.setType(ChatAdapter.TYPE_IMAGES)
            //gan content 0 cho name, key cho link, type cho address, content cho content
            restaurant.add(Restaurant(null, null, null, chatFromServer.getSuggestion()?.getImages()?.get(i), null))
        }

        dmm.setAllItemsInSection(restaurant)
        allSampleData.add(dmm)
        chatAdapter.replaceChatData(allSampleData)
        base.runOnUiThread {
            chatAdapter.notifyDataSetChanged()
            try {
                rcChat.smoothScrollToPosition(allSampleData.size)
            }catch (e: NullPointerException){
                LogUtil.e("ERROR", "error smooth recycler view")
            }
        }
    }

    //add one item image
    fun addImageIfOne(chatFromServer: ChatFromServer, dmm: ChatDataModel){
        var size: Int = chatFromServer.getSuggestion()!!.getImages()!!.size
        dmm.setHeaderTitle("Bot Chat - Hôm nay 18:00")
        dmm.setMessageBot(chatFromServer.getAnswer()!!)
        dmm.setContentKeyWord(chatFromServer.getSuggestion()?.getKeyword())
        dmm.setTagView(chatFromServer.getSuggestion()?.getSearch()?: arrayListOf())
        dmm.setType(ChatAdapter.TYPE_ONE_IMAGE)
        restaurant.add(Restaurant(null, null, null, chatFromServer.getSuggestion()?.getImages()?.get(0), null))

        dmm.setAllItemsInSection(restaurant)
        allSampleData.add(dmm)
        chatAdapter.replaceChatData(allSampleData)
        base.runOnUiThread {
            chatAdapter.notifyDataSetChanged()
            try {
                rcChat.smoothScrollToPosition(allSampleData.size)
            }catch (e: NullPointerException){
                LogUtil.e("ERROR", "error smooth recycler view")
            }
        }
    }

    //add text from bot response
    fun addFriendChatText(chatFromServer: ChatFromServer, dmm: ChatDataModel){
        LogUtil.d("size", "size tra ve: "+chatFromServer.getSuggestion()!!.getRestaurants()!!.size)
        dmm.setHeaderTitle("Bot Chat - Hôm nay 18:00")
        dmm.setMessageBot(chatFromServer.getAnswer()!!)
        dmm.setContentKeyWord(chatFromServer.getSuggestion()?.getKeyword())
        dmm.setTagView(chatFromServer.getSuggestion()?.getSearch()?: arrayListOf())
        dmm.setType(ChatAdapter.TYPE_FRIEND_TEXT)
        restaurant.add(Restaurant(chatFromServer.getAnswer(), null, null, null))

        dmm.setAllItemsInSection(restaurant)
        allSampleData.add(dmm)
        chatAdapter.replaceChatData(allSampleData)
        base.runOnUiThread {
            chatAdapter.notifyDataSetChanged()
            try {
                rcChat.smoothScrollToPosition(allSampleData.size)
            }catch (e: NullPointerException){
                LogUtil.e("ERROR", "error smooth recycler view")
            }
        }
    }

    //when server hello first
    private val onServerHello = Emitter.Listener {
        args ->
        val message = args[1].toString()
        LogUtil.d("MAIN_SERVICE", "khi co su kien moi login: " + message)

        //remove wait bot
        removeWaitBot()
        //

        var chatFromServer: ChatFromServer = GBotApp.buildInstance().gson().fromJson(message, Constant.chatFromServerType)

        var dmm = ChatDataModel()
        restaurant = arrayListOf()
        chatFromServer.setType(chatFromServer.getSuggestion()?.getType()?:ChatAdapter.TYPE_FRIEND_TEXT)
        if(chatFromServer.getType() == ChatAdapter.TYPE_RESTAURANTS){
            var size: Int = chatFromServer.getSuggestion()!!.getRestaurants()!!.size
            if(size==1){
                //khi restaurants co 1 item
                addRestaurantsIfOne(chatFromServer, dmm)
            }else {
                var size: Int = chatFromServer.getSuggestion()!!.getRestaurants()!!.size
                //khoi tao du lieu neu null
                if(size<=0){
                    addFriendChatText(chatFromServerDefault, dmmDefault)
                }else{
                    addRestaurants(chatFromServer, dmm)
                }
            }
        }else if(chatFromServer.getType() == ChatAdapter.TYPE_RESTAURANT){
            //khoi tao du lieu neu null
            if(chatFromServer.getSuggestion()!!.getRestaurant()==null){
                addFriendChatText(chatFromServerDefault, dmmDefault)
            }else{
                addOneRestaurant(chatFromServer, dmm)
            }
        }else if(chatFromServer.getType() == ChatAdapter.TYPE_IMAGES){
            var sizeImage: Int = chatFromServer.getSuggestion()!!.getImages()!!.size
            if(sizeImage==1){
                //khi image co 1 item
                addImageIfOne(chatFromServer, dmm)
            }else {
                var size: Int = chatFromServer.getSuggestion()!!.getImages()!!.size
                //khoi tao du lieu neu null
                if(size<=0){
                    addFriendChatText(chatFromServerDefault, dmmDefault)
                }else{
                    addImages(chatFromServer, dmm)
                }
            }
        }else{
            addFriendChatText(chatFromServer, dmm)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: EventMessage) {

        // an ban phim khi an bat ky nut nao
        base.hideKeyboard()
        //

        when (event.key) {
            //click suggestion
            EventDefine.CLICK_SUGGESTION -> {
                var keyWord = event.values as KeyWord

                //gan content 0 cho name, key cho link, type cho address, content cho content
                //if 5, 6 show dialog
                if(keyWord.getType().equals("5")){
                    dialogKhieuNai.setType(2)
                    dialogKhieuNai.show()
                }else if(keyWord.getType().equals("6")){
                    dialogKhieuNai.setType(1)
                    dialogKhieuNai.show()
                }else{
                    sendServerSuggestion(keyWord.getContent()?.get(0) ?: "", keyWord.getType()?:"1")
                }
            }
            //click view restaurant -> send name restaurant to server
            EventDefine.CLICK_FRIEND_ITEM_VIEW -> {
                resFromClick = event.values as Restaurant
                sendServerActionText(resFromClick.getName()?:"")
            }
            //click button book restaurant -> show dialog book restaurant
            EventDefine.CLICK_FRIEND_ITEM_BUTTON -> {
                resFromClick = event.values as Restaurant
                showDialogBookRestaurant(resFromClick.getName().toString())

            }
            //click button detail restaurant -> show web view
            EventDefine.CLICK_FRIEND_ITEM_DETAILS_RES -> {
                resFromClick = event.values as Restaurant

                mWebViewNew.loadUrl(resFromClick.getLink()?:"http://google.com.vn")
                mPageTitle.setText(resFromClick.getName())
                mPopupWebView.showAtLocation(root, Gravity.CENTER, 0, 0)
                base.setIsShowWebView(true)
            }
            //click view menu from restaurant -> show list image for menu
            EventDefine.CLICK_FRIEND_ITEM_MENU_RES -> {
                var list: MutableList<CustomImage> = arrayListOf()
                for(i in 0..5){
                    list.add(CustomImage("https://mtdata.ru/u30/photoA765/20003481040-0/original.jpg", "Meo xinh"))
                }
                setShowMenu(list)
            }
            //click item friend only text
            EventDefine.CLICK_FRIEND_TEXT -> {
                LogUtil.d("CLICK_ITEM_CHAT", event.values.toString())
            }
            //click view when only one restaurant
            EventDefine.CLICK_FRIEND_ITEM_ONE_VIEW -> {
                resFromClick = event.values as Restaurant
                //Utils.startWebView(context!!, resFromClick.getLink()!!)
                /*mWebViewNew.loadUrl(resFromClick.getLink()?:"http://google.com.vn")
                mPageTitle.setText(resFromClick.getName())
                mPopupWebView.showAtLocation(root, Gravity.CENTER, 0, 0)*/

                //edtChat.setText("${edtChat.text}${ resFromClick.getName()}")
                //edtChat.setSelection(edtChat.length())
            }
            //click item restaurant include list action
            EventDefine.CLICK_FRIEND_ITEM_ONE_ACTION -> {
                resFromClickAction = event.values as KeyWord
                //show map
                if(resFromClickAction.getType()!!.equals(SectionActionAdapter.TYPE_BAN_DO)){
                    resFromClickAction.getContent().let {
                        if(resFromClickAction.getContent()!!.size>0){
                            var intent = Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://maps.google.com/maps?saddr=${strLatLn}&daddr=${resFromClickAction.getContent()!!.get(0)}"))
                            startActivity(intent)
                        }
                    }
                }else if(resFromClickAction.getType()!!.equals(SectionActionAdapter.TYPE_CHI_TIET)){
                    //show webview
                    resFromClickAction.getContent().let {
                        if(resFromClickAction.getContent()!!.size>0){
                            mWebViewNew.loadUrl(resFromClickAction.getContent()!!.get(0))
                            //mPageTitle.setText(resFromClick.getName())
                            mPageTitle.setText(resFromClickAction.getTitle())
                            mPopupWebView.showAtLocation(root, Gravity.CENTER, 0, 0)
                            base.setIsShowWebView(true)
                        }
                    }
                }else{
                    //book restaurant
                    resFromClickAction.getContent().let {
                        if(resFromClickAction.getContent()!!.size>0) {
                            showDialogBookRestaurant(resFromClickAction.getTitle())
                        }
                    }
                }
            }
            //click image from menu -> show list menu image
            EventDefine.CLICK_FRIEND_ITEM_IMAGE -> {
                var list:MutableList<CustomImage> = event.values as MutableList<CustomImage>
                if(list.size > 0) {
                    setShowMenu(list)
                }
            }
            //dismiss web view when touch back
            EventDefine.DISSMISS_WEBVIEW -> {
                mPopupWebView.dismiss()
                base.setIsShowWebView(false)
            }
            //click delete tag search
            EventDefine.CLICK_DELETE_TAG_SEARCH -> {
                var deleteTagSearch: ModuleDeleteTagSearch = event.values as ModuleDeleteTagSearch
                if(allSampleData.get(deleteTagSearch.positionOfListChat).getType()!=ChatAdapter.TYPE_WAIT_BOT && allSampleData.get(deleteTagSearch.positionOfListChat).getType()!=ChatAdapter.TYPE_WAIT_BOT){
                    allSampleData.get(deleteTagSearch.positionOfListChat).getTagView().let {
                        if(allSampleData.get(deleteTagSearch.positionOfListChat).getTagView().size>deleteTagSearch.positionClick){
                            allSampleData.get(deleteTagSearch.positionOfListChat).getTagView().removeAt(deleteTagSearch.positionClick)
                            //chatAdapter.notifyItemChanged(deleteTagSearch.positionOfListChat)
                            LogUtil.d("CLICK_ITEM_CHAT", event.values.toString())
                            var keyWord = deleteTagSearch.keyWord
                            if(keyWord.getType().equals("1")){
                                sendServerDeleteTag(keyWord.getContent()?.get(0) ?: "")
                            }else{

                            }
                        }
                    }
                }
            }
            //when post khieu nai, hop tac done -> create new message to show
            EventDefine.DONE_POST_KNHT -> {
                createKNHTSuccess(dialogKhieuNai.getDataName(), dialogKhieuNai.getDataPhone(), dialogKhieuNai.getDataNote(), event.values.toString())
            }
        }
    }

    /*TrackLocationInterface*/
    override fun onLocationSuccess(locationToSer: LocationToServer) {
        locationToSer.id = prefernce.getDeviceId()
        LogUtil.d("LOCATION", Utils.convertObjectToJson(locationToSer))

        //lay lat long
        strLatLn = "${locationToSer.lat},${locationToSer.lng}"

        //convert obj to json obj
        Utils.toJsonObj("id", prefernce.getDeviceId())
        Utils.toJsonObj("lat", locationToSer.lat)
        Utils.toJsonObj("lng", locationToSer.lng)
        Utils.toJsonObj("locality", locationToSer.locality)
        Utils.toJsonObj("sub_area", locationToSer.sub_area)
        Utils.toJsonObj("area", locationToSer.area)
        Log.d("SEND_LOCATION", Utils.getJsonObj().toString())
        mSocket.emit("client-send-location", Utils.getJsonObj())
        Utils.removeAllJsonObj()
    }

    override fun onLocationFaile(faile: String) {
        LogUtil.d("LOCATION", faile)
    }

    //set list image menu restaurant
    fun setShowMenu(listImage: MutableList<CustomImage>){
        var hierarchyBuilder = GenericDraweeHierarchyBuilder.newInstance(resources)
                .setFailureImage(R.drawable.ic_thinking)   //anh loi
                .setProgressBarImage(R.drawable.ic_avatar_default)  //dang load
                .setPlaceholderImage(R.drawable.ic_thinking)   //
        var overlayView = ImageOverlayView(root.context)
        var imageChangeListener = object: ImageViewer.OnImageChangeListener{
            override fun onImageChange(position: Int) {
                LogUtil.d("IMAGE_SHOW", position.toString())
                overlayView.setShareText(listImage.get(position).getUrl())
                overlayView.setDescription(listImage.get(position).getDescription())
            }
        }

        var onDismissListener = object: ImageViewer.OnDismissListener{
            override fun onDismiss() {
                LogUtil.d("IMAGE_SHOW", "on Dismiss")
            }

        }
        var builder = Utils.showMenuViewer(root.context, overlayView, listImage, hierarchyBuilder, 0,
                imageChangeListener, onDismissListener)
        builder.show()
    }

    //set status icon send
    fun setIconSend(){
        imgSend.isEnabled = false
        edtChat.addTextChangedListener(object: TextWatcher{
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if(p0!!.length > 0){
                    imgSend.setImageResource(R.drawable.ic_send)
                    imgSend.isEnabled = true
                    if(p0!!.length > 200){
                        p0!!.delete(p0!!.length-1, p0!!.length)
                    }
                }else{
                    imgSend.setImageResource(R.drawable.ic_not_send)
                    imgSend.isEnabled = false
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })
    }

    fun showDialog(keyWord: KeyWord){
        dialogPickSuggesstion.setTitle("Chọn "+keyWord.getKey())
        dialogPickSuggesstion.setItems(keyWord.getContent()?.toTypedArray()?.joinToString())
        dialogPickSuggesstion.show()
    }

    fun creatDialogSuggest(){
        dialogPickSuggesstion = MaterialDialog.Builder(context!!)
                .title("Chọn")
                .items(R.array.pick_suggestion)
                .typeface("DroidSerif-Regular.ttf", "DroidSerif-Regular.ttf")
                .itemsCallback { dialog, itemView, position, text -> sendServerSuggestion(dialogPickSuggesstion.items!!.get(position).toString(), "") }
                .build()
    }

    //hide keyboard when scroll bottom recycler view
    fun setOnCrollBottom(){
        rcChat.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                //LogUtil.d("SCROLL", "onScroll: "+dx.toString()+" "+dy.toString())
                if(dy < 0){
                    //var view: View = base.window.decorView.rootView
                    Utils.hideKeyboard(base, root)
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                //LogUtil.d("SCROLL", "newState: "+newState.toString())
            }
        })
    }

    //send server when click suggestion
    fun sendServerSuggestion(message: String, type: String){

        //remove wait bot
        removeWaitBot()
        //

        var dm = ChatDataModel()
        dm.setHeaderTitle("My Chat - Hôm nay 18:00")
        dm.setMessageBot(message)
        dm.setType(ChatAdapter.TYPE_MY)
        //restaurant.removeAll(restaurant)
        restaurant = arrayListOf()
        restaurant.add(Restaurant(message, null, null, null))
        dm.setAllItemsInSection(restaurant)
        LogUtil.d("size", allSampleData.size.toString())
        allSampleData.add(dm)
        LogUtil.d("size sau", allSampleData.size.toString())
        chatAdapter.replaceChatData(allSampleData)
        base.runOnUiThread {
            chatAdapter.notifyDataSetChanged()
            try {
                rcChat.smoothScrollToPosition(allSampleData.size)
            }catch (e: NullPointerException){
                LogUtil.e("ERROR", "error smooth recycler view")
            }
        }

        Utils.toJsonObj("msg", message)
        Utils.toJsonObj("type", type)
        Utils.toJsonObj("id", prefernce.getDeviceId())
        mSocket.emit("client-send-msg", Utils.getJsonObj())
        Utils.removeAllJsonObj()

        //create chat
        createWaitBot()
        //
    }

    fun sendServerDatBan(message: String){

        //remove wait bot
        removeWaitBot()
        //

        var dm = ChatDataModel()
        dm.setHeaderTitle("My Chat - Hôm nay 18:00")
        dm.setMessageBot("Đặt bàn "+message)
        dm.setType(ChatAdapter.TYPE_MY)
        //restaurant.removeAll(restaurant)
        restaurant = arrayListOf()
        restaurant.add(Restaurant("Đặt bàn "+message, null, null, null))
        dm.setAllItemsInSection(restaurant)
        LogUtil.d("size", allSampleData.size.toString())
        allSampleData.add(dm)
        LogUtil.d("size sau", allSampleData.size.toString())
        chatAdapter.replaceChatData(allSampleData)
        base.runOnUiThread {
            chatAdapter.notifyDataSetChanged()
            try {
                rcChat.smoothScrollToPosition(allSampleData.size)
            }catch (e: NullPointerException){
                LogUtil.e("ERROR", "error smooth recycler view")
            }
        }

        Utils.toJsonObj("msg", "Đặt bàn "+message)
        //Utils.toJsonObj("type", "1")
        Utils.toJsonObj("id", prefernce.getDeviceId())
        mSocket.emit("client-send-msg", Utils.getJsonObj())
        Utils.removeAllJsonObj()

        //create chat
        createWaitBot()
        //
    }

    //click item restaurant -> send server only text name restaurant
    fun sendServerActionText(message: String){

        //remove wait bot
        removeWaitBot()
        //

        var dm = ChatDataModel()
        dm.setHeaderTitle("My Chat - Hôm nay 18:00")
        dm.setMessageBot(message)
        dm.setType(ChatAdapter.TYPE_MY)
        //restaurant.removeAll(restaurant)
        restaurant = arrayListOf()
        restaurant.add(Restaurant(message, null, null, null))
        dm.setAllItemsInSection(restaurant)
        LogUtil.d("size", allSampleData.size.toString())
        allSampleData.add(dm)
        LogUtil.d("size sau", allSampleData.size.toString())
        chatAdapter.replaceChatData(allSampleData)
        base.runOnUiThread {
            chatAdapter.notifyDataSetChanged()
            try {
                rcChat.smoothScrollToPosition(allSampleData.size)
            }catch (e: NullPointerException){
                LogUtil.e("ERROR", "error smooth recycler view")
            }
        }

        Utils.toJsonObj("msg", message)
        Utils.toJsonObj("type", "1")
        Utils.toJsonObj("id", prefernce.getDeviceId())
        mSocket.emit("client-send-msg", Utils.getJsonObj())
        Utils.removeAllJsonObj()

        //create chat
        createWaitBot()
        //
    }

    //click deteletag -> send server delete it
    fun sendServerDeleteTag(message: String){

        //remove wait bot
        removeWaitBot()
        //

        var dm = ChatDataModel()
        dm.setHeaderTitle("My Chat - Hôm nay 18:00")
        dm.setMessageBot(message)
        dm.setType(ChatAdapter.TYPE_MY)
        //restaurant.removeAll(restaurant)
        restaurant = arrayListOf()
        restaurant.add(Restaurant(message, null, null, null))
        dm.setAllItemsInSection(restaurant)
        LogUtil.d("size", allSampleData.size.toString())
        allSampleData.add(dm)
        LogUtil.d("size sau", allSampleData.size.toString())
        chatAdapter.replaceChatData(allSampleData)
        base.runOnUiThread {
            chatAdapter.notifyDataSetChanged()
            try {
                rcChat.smoothScrollToPosition(allSampleData.size)
            }catch (e: NullPointerException){
                LogUtil.e("ERROR", "error smooth recycler view")
            }
        }

        Utils.toJsonObj("msg", message)
        //Utils.toJsonObj("type", "1")
        Utils.toJsonObj("id", prefernce.getDeviceId())
        mSocket.emit("client-send-msg", Utils.getJsonObj())
        Utils.removeAllJsonObj()

        //create chat
        createWaitBot()
        //
    }

    //remove wait bot when new message from client or server
    fun removeWaitBot(){
        if(allSampleData!!.size > 0){
            postWaitBot = allSampleData!!.size-1
            if(allSampleData!!.get(postWaitBot).getType() == ChatAdapter.TYPE_WAIT_BOT){
                allSampleData.removeAt(postWaitBot)
            }
        }
    }

    //create view wait bot
    fun createWaitBot(){
        var dm = ChatDataModel()
        dm.setHeaderTitle("Wait Bot Chat")
        dm.setMessageBot("wait bot chat")
        dm.setType(ChatAdapter.TYPE_WAIT_BOT)
        restaurant = arrayListOf()
        restaurant.add(Restaurant("wait", null, null, null))
        dm.setAllItemsInSection(restaurant)
        LogUtil.d("size", allSampleData.size.toString())
        allSampleData.add(dm)
        LogUtil.d("size sau", allSampleData.size.toString())
        chatAdapter.replaceChatData(allSampleData)
        base.runOnUiThread {
            chatAdapter.notifyDataSetChanged()
            try {
                rcChat.smoothScrollToPosition(allSampleData.size)
            }catch (e: NullPointerException){
                LogUtil.e("ERROR", "error smooth recycler view")
            }
        }
    }

    //when order/book restaurant success, create view for show
    fun createOrderSuccess(day: String, hour: String, people: String, children: String, name: String, phone: String, note: String, res: String, idOrder: String){
        var dm = ChatDataModel()
        dm.setHeaderTitle("Bot Chat - Hôm nay 18:00")
        dm.setMessageBot("Anh/chị ${name} đã đặt bàn thành công tới nhà hàng ${res}. Mã đặt bàn của bạn là: ${idOrder}" +
                "\nChi tiết: Ngày đến: ${day}, giờ đến: ${hour}, " +
                "số người lớn: ${people}, số trẻ em: ${children}, số điện thoại: ${phone}, ghi chú: ${note}")
        dm.setType(ChatAdapter.TYPE_FRIEND_TEXT)

        //tao key word goi y
        var listKeyWord: MutableList<KeyWord>
        var strKeyWord = "[{\"content\":[\"Tôi muốn đặt bàn\"],\"type\":\"1\",\"key\":\"đặt bàn\"},{\"content\":[\"Tôi muốn tư vấn nhà hàng\"],\"type\":\"1\",\"key\":\"Tìm nhà hàng\"},{\"content\":[\"Tôi cần hỗ trợ\"],\"type\":\"3\",\"key\":\"cần hỗ trợ\"},{\"content\":[\"Tôi muốn khiếu nại\"],\"type\":\"5\",\"key\":\"Khiếu nại\"},{\"content\":[\"Tôi muốn hợp tác với Pasgo\"],\"type\":\"6\",\"key\":\"Hợp tác\"}]"
        listKeyWord = Utils.convertJsonToObject(strKeyWord, Constant.multiKeyWordType) as MutableList<KeyWord>
        if(listKeyWord == null){
            listKeyWord = arrayListOf()
        }
        dm.setContentKeyWord(listKeyWord)
        //end tao key word

        restaurant = arrayListOf()
        restaurant.add(Restaurant("wait", null, null, null))
        dm.setAllItemsInSection(restaurant)
        LogUtil.d("size", allSampleData.size.toString())
        allSampleData.add(dm)
        LogUtil.d("size sau", allSampleData.size.toString())
        chatAdapter.replaceChatData(allSampleData)
        base.runOnUiThread {
            chatAdapter.notifyDataSetChanged()
            try {
                rcChat.smoothScrollToPosition(allSampleData.size)
            }catch (e: NullPointerException){
                LogUtil.e("ERROR", "error smooth recycler view")
            }
        }
    }

    //when khieu nai, hop tac success -> create view to show
    fun createKNHTSuccess(name: String, phone: String, content: String, mss: String){
        var mggg = mss
        if(dialogKhieuNai.getType() == 1){
            mggg = "Anh/chị ${name}, số điện thoại: ${phone}\nNội dung: ${content}\n"+mss
        }else if(dialogKhieuNai.getType() == 2){
            mggg = "Anh/chị ${name}, số điện thoại: ${phone}\nNội dung: ${content}\n"+mss
        }

        var dm = ChatDataModel()
        dm.setHeaderTitle("Bot Chat - Hôm nay 18:00")
        dm.setMessageBot(mggg)
        dm.setType(ChatAdapter.TYPE_FRIEND_TEXT)

        //tao key word goi y
        var listKeyWord: MutableList<KeyWord>
        var strKeyWord = "[{\"content\":[\"Tôi muốn đặt bàn\"],\"type\":\"1\",\"key\":\"đặt bàn\"},{\"content\":[\"Tôi muốn tư vấn nhà hàng\"],\"type\":\"1\",\"key\":\"Tìm nhà hàng\"},{\"content\":[\"Tôi cần hỗ trợ\"],\"type\":\"3\",\"key\":\"cần hỗ trợ\"},{\"content\":[\"Tôi muốn khiếu nại\"],\"type\":\"5\",\"key\":\"Khiếu nại\"},{\"content\":[\"Tôi muốn hợp tác với Pasgo\"],\"type\":\"6\",\"key\":\"Hợp tác\"}]"
        listKeyWord = Utils.convertJsonToObject(strKeyWord, Constant.multiKeyWordType) as MutableList<KeyWord>
        if(listKeyWord == null){
            listKeyWord = arrayListOf()
        }
        dm.setContentKeyWord(listKeyWord)
        //end tao key word

        restaurant = arrayListOf()
        restaurant.add(Restaurant("wait", null, null, null))
        dm.setAllItemsInSection(restaurant)
        LogUtil.d("size", allSampleData.size.toString())
        allSampleData.add(dm)
        LogUtil.d("size sau", allSampleData.size.toString())
        chatAdapter.replaceChatData(allSampleData)
        base.runOnUiThread {
            chatAdapter.notifyDataSetChanged()
            try {
                rcChat.smoothScrollToPosition(allSampleData.size)
            }catch (e: NullPointerException){
                LogUtil.e("ERROR", "error smooth recycler view")
            }
        }
    }

    //create popup to show webview
    fun createPopUp(){
        val inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        // Inflate the custom layout/view
        val customView = inflater.inflate(R.layout.view_popup_webview, null)
        mPopupWebView = PopupWindow(
                customView,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
        )
        if(Build.VERSION.SDK_INT >= 21){
            mPopupWebView.setElevation(5.0f);
        }

        mWebViewNew = mPopupWebView.contentView.findViewById<WebView>(R.id.webViewNew)
        mToolBarPopUp = mPopupWebView.contentView.findViewById<ImageView>(R.id.icBack)
        mPageTitle = mPopupWebView.contentView.findViewById<FontTextView>(R.id.pageTitle)
        mImgBackPopUp = mToolBarPopUp.findViewById<ImageView>(R.id.icBack)
        progressBar = mPopupWebView.contentView.findViewById<ProgressBar>(R.id.progressBar)
        mImgBackPopUp.setOnClickListener {
            mPopupWebView.dismiss()
            base.setIsShowWebView(false)
        }
        mWebViewNew.getSettings().setJavaScriptEnabled(true)
        mWebViewNew.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressBar.visibility = View.VISIBLE
            }
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                progressBar.visibility = View.GONE
            }
        }
        //dissable click webview
        var startClickTime: Long = 0
        mWebViewNew.setOnTouchListener(object: View.OnTouchListener{
            override fun onTouch(p0: View?, event: MotionEvent?): Boolean {
                if (event?.getAction() == MotionEvent.ACTION_DOWN) {
                    startClickTime = System.currentTimeMillis()
                }else if (event?.getAction() == MotionEvent.ACTION_UP){
                    if (System.currentTimeMillis() - startClickTime < ViewConfiguration.getTapTimeout()) {

                        // Touch was a simple tap.
                        return true

                    } else {
                        // Touch was a not a simple tap. Do whatever.
                    }
                }
                return false
            }
        })
    }

    //set form book restaurant to view
    fun setFormBookRestaurant(){
        dialogBookRestaurant = BookRestaurantDialog(context!!)
        txtDate = dialogBookRestaurant.getView().findViewById(R.id.txtDate)
        dateNext = dialogBookRestaurant.getView().findViewById(R.id.dateNext)
        datePre = dialogBookRestaurant.getView().findViewById(R.id.datePre)
        txtAdult = dialogBookRestaurant.getView().findViewById(R.id.txtAdult)
        adultNext = dialogBookRestaurant.getView().findViewById(R.id.adultNext)
        adultPre = dialogBookRestaurant.getView().findViewById(R.id.adultPre)
        txtYoung = dialogBookRestaurant.getView().findViewById(R.id.txtYoung)
        youngNext = dialogBookRestaurant.getView().findViewById(R.id.youngNext)
        youngPre = dialogBookRestaurant.getView().findViewById(R.id.youngPre)
        timeNext = dialogBookRestaurant.getView().findViewById(R.id.timeNext)
        timePre = dialogBookRestaurant.getView().findViewById(R.id.timePre)
        edtName = dialogBookRestaurant.getView().findViewById(R.id.edtName)
        edtPhone = dialogBookRestaurant.getView().findViewById(R.id.edtPhone)
        edtNote =  dialogBookRestaurant.getView().findViewById(R.id.edtNote)
        btnBookDatBan = dialogBookRestaurant.getView().findViewById(R.id.btnBookDatBan)
        var imgBookClose: ImageView = dialogBookRestaurant.getView().findViewById(R.id.imgBookClose)
        txtBookRestaurant = dialogBookRestaurant.getView().findViewById(R.id.txtBookRestaurant)
        txtErrorPhone = dialogBookRestaurant.getView().findViewById(R.id.txtErrorPhone)
        //Set ngày giờ hiện tại khi mới chạy lần đầu
        cal= Calendar.getInstance()
        var calNow = Calendar.getInstance()
        //Định dạng kiểu ngày / tháng /năm
        var dft = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        var strDate: String = dft.format(cal.getTime())
        //hiển thị lên giao diện
        txtDate.setText(strDate);
        txtDate.setOnClickListener {
            var callback: DatePickerDialog.OnDateSetListener = object: DatePickerDialog.OnDateSetListener{
                override fun onDateSet(datePicker: DatePicker?, year: Int, month: Int, day: Int) {
                    // Set text cho textView
                    txtDate.setText("${day}-${(month +1)}-${year}")
                    //Lưu vết lại ngày mới cập nhật
                    cal.set(year, month, day)
                }
            }
            var s = txtDate.text.toString() + ""
            //Lấy ra chuỗi của textView Date
            var strArrtmp = s.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            var ngay = Integer.parseInt(strArrtmp[0])
            var thang = Integer.parseInt(strArrtmp[1]) - 1
            var nam = Integer.parseInt(strArrtmp[2])
            //Hiển thị ra Dialog
            var pic = DatePickerDialog(
                    context,
                    callback, nam, thang, ngay)
            pic.setTitle("Chọn ngày đến")
            pic.datePicker.setMinDate(System.currentTimeMillis())
            pic.show()
        }
        dateNext.setOnClickListener {
            cal.add(Calendar.DATE, 1)
            txtDate.setText(dft.format(cal.getTime()))
        }
        datePre.setOnClickListener {
            if(cal.time.equals(calNow.time)){
                SimpleToast.showInfo(context!!, "Bạn không được chọn ngày nhỏ hơn ngày hiện tại.")
            }else{
                cal.add(Calendar.DATE, -1)
                txtDate.setText(dft.format(cal.getTime()))
            }
        }

        //set adult
        adultNext.setOnClickListener {
            var numberAdult = txtAdult.text.toString().toInt()
            txtAdult.setText((numberAdult+1).toString())
        }
        adultPre.setOnClickListener {
            var numberAdult = txtAdult.text.toString().toInt()
            if(numberAdult > 0){
                txtAdult.setText((numberAdult-1).toString())
            }
        }
        //set Young
        youngNext.setOnClickListener {
            var numberYoung = txtYoung.text.toString().toInt()
            txtYoung.setText((numberYoung+1).toString())
        }
        youngPre.setOnClickListener {
            var numberYoung = txtYoung.text.toString().toInt()
            if(numberYoung > 0){
                txtYoung.setText((numberYoung-1).toString())
            }
        }

        //set time
        timeNext.setOnClickListener {
            if(spinnerTime.selectedItemPosition < itemBookTime.size-1){
                spinnerTime.setSelection(spinnerTime.selectedItemPosition+1)
            }
        }
        timePre.setOnClickListener {
            if(spinnerTime.selectedItemPosition > 0){
                spinnerTime.setSelection(spinnerTime.selectedItemPosition-1)
            }
        }

        //check phone:
        edtPhone.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                if (edtPhone.length() > 0) {
                    timer.cancel()
                    timer = Timer()
                    timer.schedule(object : TimerTask() {
                        override fun run() {
                            if(!Utils.checkIsPhone(edtPhone.text.toString())){
                                base.runOnUiThread({
                                    txtErrorPhone.visibility = View.VISIBLE
                                })
                            }else{
                                base.runOnUiThread({
                                    txtErrorPhone.visibility = View.GONE
                                })
                            }
                        }
                    }, DELAY
                    )
                }else{
                    base.runOnUiThread({
                        txtErrorPhone.visibility = View.GONE
                    })
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })

        //dismiss dialog book restaurant when click button close
        imgBookClose.setOnClickListener {
            dialogBookRestaurant.dismiss()
        }
    }

    //
    fun showDialogBookRestaurant(resName: String){
        spinnerTime = dialogBookRestaurant.getView().findViewById(R.id.spinnerTime)
        itemBookTime = arrayOf("12h", "13h", "9h", "9h30", "15h")
        var adapterBookTime = ArrayAdapter<String>(context, R.layout.hint_item, itemBookTime)
        adapterBookTime.setDropDownViewResource(R.layout.dropdown_hint_item)
        spinnerTime.adapter = adapterBookTime

        //check
        if(!objectUserRecent.getDatban().getDayofmonth().equals("@@unk")){
            var strArrtmp = objectUserRecent.getDatban().getDayofmonth().split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            var ngay = Integer.parseInt(strArrtmp[0])
            var thang = Integer.parseInt(strArrtmp[1]) - 1
            var nam = Integer.parseInt(strArrtmp[2])
            cal.set(nam, thang+1, ngay)
            txtDate.setText("${ngay}-${(thang +1)}-${nam}")
        }
        var dftToSer = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        var strDateToSer: String = dftToSer.format(cal.getTime())
        getTimeRestaurant(resName, strDateToSer)

        btnBookDatBan.setOnClickListener {
            base.hideKeyboard()

            //check dat ban
            var strTime = ""
            if(itemBookTime.size > 0) {
                strTime = spinnerTime.selectedItem.toString()
            }
            if(edtPhone.text.toString().isEmpty()){
                SimpleToast.showInfo(context!!, "Số điện thoại không được để trống.")
            }else if(!Utils.checkIsPhone(edtPhone.text.toString())){
                SimpleToast.showInfo(context!!, "Số điện thoại không đúng định dạng.")
            }else if(strTime.isEmpty()){
                SimpleToast.showInfo(context!!, "Giờ đến không hợp lệ.")
            }else if(!Utils.checkValiTime("${txtDate.text} ${strTime}")){
                SimpleToast.showInfo(context!!, "Thời gian đến không hợp lệ.")
            } else{
                var alertDialogBuilder = AlertDialog.Builder(context!!)
                // khởi tạo dialog
                alertDialogBuilder.setMessage("Bạn có chắc chắn muốn đặt bàn không?")
                // thiết lập nội dung cho dialog
                alertDialogBuilder.setPositiveButton("Có", object : DialogInterface.OnClickListener {
                    override fun onClick(arg0: DialogInterface, arg1: Int) {
                        // button "Có" thoát khỏi ứng dụng
                        //dat ban
                        val inputFormatter = SimpleDateFormat("dd-MM-yyyy")
                        val dateUser = inputFormatter.parse(txtDate.text.toString())
                        orderRestaurant(dftToSer.format(dateUser), "${strTime}:00",
                                txtAdult.text.toString(), txtYoung.text.toString(),
                                edtName.text.toString(), edtPhone.text.toString(), edtNote.text.toString(), resName,
                                prefernce.getDeviceId())
                    }
                })

                alertDialogBuilder.setNegativeButton("Không", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, which: Int) {
                        dialog.dismiss()
                        // button "no" ẩn dialog đi
                    }
                })
                alertDialogBuilder.create().show()
            }
            //end check dat ban
        }

        edtNote.setOnEditorActionListener(object: TextView.OnEditorActionListener{
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    base.hideKeyboard()
                    var alertDialogBuilder = AlertDialog.Builder(context!!)
                    // khởi tạo dialog
                    alertDialogBuilder.setMessage("Bạn có chắc chắn muốn đặt bàn không?")
                    // thiết lập nội dung cho dialog
                    alertDialogBuilder.setPositiveButton("Có", object : DialogInterface.OnClickListener {
                        override fun onClick(arg0: DialogInterface, arg1: Int) {
                            var strTime = ""
                            if(itemBookTime.size > 0) {
                                strTime = spinnerTime.selectedItem.toString()
                            }
                            if(edtPhone.text.toString().isEmpty()){
                                SimpleToast.showInfo(context!!, "Số điện thoại không được để trống.")
                            }else if(!Utils.checkIsPhone(edtPhone.text.toString())){
                                SimpleToast.showInfo(context!!, "Số điện thoại không đúng định dạng.")
                            }else if(strTime.isEmpty()){
                                SimpleToast.showInfo(context!!, "Giờ đến không hợp lệ.")
                            }else if(!Utils.checkValiTime("${txtDate.text} ${strTime}")){
                                SimpleToast.showInfo(context!!, "Thời gian đến không hợp lệ.")
                            } else{
                                val inputFormatter = SimpleDateFormat("dd-MM-yyyy")
                                val dateUser = inputFormatter.parse(txtDate.text.toString())
                                orderRestaurant(dftToSer.format(dateUser), "${strTime}:00",
                                        txtAdult.text.toString(), txtYoung.text.toString(),
                                        edtName.text.toString(), edtPhone.text.toString(), edtNote.text.toString(), resName,
                                        prefernce.getDeviceId())
                            }
                            // button "Có" thoát khỏi ứng dụng
                        }
                    })

                    alertDialogBuilder.setNegativeButton("Không", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface, which: Int) {
                            dialog.dismiss()
                            // button "no" ẩn dialog đi
                        }
                    })
                    alertDialogBuilder.create().show()
                    return true
                }
                // Return true if you have consumed the action, else false.
                return false
            }

        })

        //check ng lon, tre em, ten, sdt, ghi chu
        if(objectUserRecent.getDatban().getPeople() != 0){
            txtAdult.setText(objectUserRecent.getDatban().getPeople().toString())
        }
        if(objectUserRecent.getDatban().getChildren() != 0){
            txtYoung.setText(objectUserRecent.getDatban().getChildren().toString())
        }
        if(!objectUserRecent.getThongtincanhan().getUsername().equals("@@unk")){
            edtName.setText(objectUserRecent.getThongtincanhan().getUsername())
        }
        if(!objectUserRecent.getThongtincanhan().getPhone().equals("@@unk")){
            edtPhone.setText(objectUserRecent.getThongtincanhan().getPhone())
        }
        if(!objectUserRecent.getDatban().getNote().equals("@@unk")){
            edtNote.setText(objectUserRecent.getDatban().getNote())
        }
        txtBookRestaurant.setText(resName)

        dialogBookRestaurant.show()
    }

    //request get time for restaurant
    fun getTimeRestaurant(res: String, day: String){
        RxUtil.applyHandlerStartFinish(RestBuilder.api().getTimeRestaurant(res, day),
                Runnable {  },
                Runnable {  })
                .compose(RxUtil.applyMain())
                .subscribe(
                        {
                            response ->
                            run {
                                itemBookTime = arrayOf()
                                response.data.let {
                                    LogUtil.d("GET_TIME_RESTAURANT", response.data.toString())
                                    if(response.data!=null){
                                        itemBookTime = response.data!!
                                        var adapterBookTime = ArrayAdapter<String>(context, R.layout.hint_item, itemBookTime)
                                        adapterBookTime.setDropDownViewResource(R.layout.dropdown_hint_item)
                                        spinnerTime.adapter = adapterBookTime
                                        if(!objectUserRecent.getDatban().getHourofday().equals("@@unk")){
                                            var strTime: String = objectUserRecent.getDatban().getHourofday().substring(0, (objectUserRecent.getDatban().getHourofday().length-3))
                                            var inde = 0
                                            for(i in itemBookTime){
                                                if(i.equals(strTime)){
                                                    spinnerTime.setSelection(inde)
                                                }
                                                inde++
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        {t ->
                            LogUtil.d("GET_TIME_RESTAURANT", t.message!!)
                            SimpleToast.showShort(t.message!!)
                        }
                )
    }

    //request order restaurant
    fun orderRestaurant(day: String, hour: String, people: String, children: String, name: String, phone: String, note: String, res: String, id: String){
        RxUtil.applyHandlerStartFinish(RestBuilder.api().orderRestaurant(day, hour, people, children, name, phone, note, res, id),
                Runnable {  },
                Runnable {  })
                .compose(RxUtil.applyMain())
                .subscribe(
                        {
                            response ->
                            run {
                                itemBookTime = arrayOf()
                                response.data.let {
                                    LogUtil.d("ORDER_RESTAURANT", response.data.toString())
                                    if(response.data!=null){
                                        createOrderSuccess(day, hour, people, children, name, phone, note, res, response.data!!)
                                        dialogBookRestaurant.dismiss()
                                    }
                                }
                            }
                        },
                        {t ->
                            LogUtil.d("ORDER_RESTAURANT", t.message!!)
                            SimpleToast.showShort(t.message!!)
                        }
                )
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }
}