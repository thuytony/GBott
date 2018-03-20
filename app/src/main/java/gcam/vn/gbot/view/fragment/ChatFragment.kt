package gcam.vn.gbot.view.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextWatcher
import gcam.vn.gbot.BuildConfig
import gcam.vn.gbot.R
import gcam.vn.gbot.application.GBotApp
import gcam.vn.gbot.manager.event.EventDefine
import gcam.vn.gbot.manager.event.EventMessage
import gcam.vn.gbot.manager.ext.LogUtil
import gcam.vn.gbot.view.adapter.ChatAdapter
import gcam.vn.gbot.view.widget.BaseFragment
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.android.synthetic.main.fragment_chat.*
import gcam.vn.gbot.manager.ext.Constant
import gcam.vn.gbot.util.Utils
import io.socket.emitter.Emitter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import android.text.Editable
import android.text.Selection
import android.util.Log
import android.view.*
import android.webkit.WebView
import android.widget.ImageView
import android.widget.PopupWindow
import com.afollestad.materialdialogs.MaterialDialog
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.stfalcon.frescoimageviewer.ImageViewer
import gcam.vn.gbot.manager.ext.PreferenceUtil
import gcam.vn.gbot.manager.ext.SimpleToast
import gcam.vn.gbot.module.*
import gcam.vn.gbot.service.TrackLocation
import gcam.vn.gbot.service.TrackLocationInterface
import gcam.vn.gbot.view.adapter.SectionActionAdapter
import gcam.vn.gbot.view.widget.FontTextView


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
    //static web view
    private lateinit var mPopupWebView: PopupWindow
    private lateinit var mWebViewNew: WebView
    private lateinit var mToolBarPopUp: ImageView
    private lateinit var mImgBackPopUp: ImageView
    private lateinit var mPageTitle: FontTextView
    private lateinit var chatFromServerDefault: ChatFromServer
    private var dmmDefault = ChatDataModel()

    //latlong
    private var strLatLn: String = ""

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

        //them
        var testViewOneRes = "{\"objectUser\":{\"thongtinnhahang\":{\"@@restaurant\":\"Món Huế - Trần Duy Hưng\"},\"sid\":\"fd2070ddca4d4aac8bf8b84f23b7bb9d\",\"current_topic\":\"datban\",\"pre_topic\":\"datban\",\"note_text\":\"\",\"tuvannhahang\":{\"@@res_type\":[],\"@@price\":[],\"@@fit\":[],\"@@province\":[],\"@@district\":[]},\"datban\":{\"@@people\":0,\"@@dayofmonth\":\"15-03-2018\",\"@@children\":0,\"@@restaurant\":\"Món Huế - Trần Duy Hưng\",\"@@hourofday\":\"07:00:00\",\"@@note\":\"@@unk\"},\"current_block_command\":{\"thongtindatban\":\"order\",\"thoigian\":\"order\",\"number\":\"number_to_people\",\"nhahang\":\"order\",\"children\":\"order\",\"people\":\"order\"},\"id\":\"358079050710906\",\"thongtincanhan\":{\"@@province\":\"Hà Nội\",\"@@username\":\"T\",\"@@lng\":105.8594583,\"@@phone\":\"0989892009\",\"@@lat\":20.9867161,\"@@district\":\"Trần Duy Hưng\"}},\"answer\":\"anh\\/chị đi bao nhiêu người ạ?\",\"suggestion\":{\"action\":[{\"key\":\"Chi tiết \",\"content\":[\"https:\\/\\/pasgo.vn\\/nha-hang\\/nha-hang-mon-hue-tran-duy-hung-1308\"],\"type\":\"2\"},{\"key\":\"Bản đồ \",\"content\":[\"21.011543900,105.800420000\"],\"type\":\"3\"}],\"restaurant\":{\"link\":\"https:\\/\\/pasgo.vn\\/nha-hang\\/nha-hang-mon-hue-tran-duy-hung-1308\",\"salesoff\":10,\"menu\":[\"menus\\/Pau6hU71iWH_1519966401.jpg\",\"menus\\/ch8ngi5ZtOAB_1519966401.jpg\",\"menus\\/9kZgLJ1pQr2A_1519966401.jpg\",\"menus\\/y4AWwIns2hxk_1519966401.jpg\",\"menus\\/rjDSK476Bm3N_1519966401.jpg\",\"menus\\/OLu0S4gVEPbi_1519966401.jpg\",\"menus\\/2HK6tCpSZuwA_1519966411.jpg\",\"menus\\/gAwSeChLmDXK_1519966411.jpg\",\"menus\\/XCbASrgl7TQk_1519966411.jpg\",\"menus\\/4Sc3oF2eYKdX_1519966411.jpg\",\"menus\\/XOL1_SH4KIgz_1519966411.jpg\",\"menus\\/U94elDIhznx0_1519966411.jpg\"],\"price_min\":100000,\"price_max\":200000,\"open_time\":\"6:30:00-21:30:00\",\"lng\":\"105.800420000\",\"avatar\":\"restaurants\\/1519966269_nQT7q.jpg\",\"address\":\"67 Trần Duy Hưng, Q. Cầu Giấy\",\"events\":\"I. Được tư vấn, đặt bàn và giữ chỗ miễn phíII.Tặng kèm ưu đãi lên tới 10%\",\"name\":\"Món Huế - Trần Duy Hưng\",\"s_menu\":\"Bún bò Huế; Nem công; Chả phụng; Nem nướng cuốn bánh tráng; Chạo tôm; Hến trộn xúc bánh đa\",\"lat\":\"21.011543900\"},\"restaurants\":[],\"type\":2,\"images\":[],\"keyword\":[{\"key\":\"1 người\",\"content\":[\"1 người \"],\"type\":\"1\"},{\"key\":\"2 người\",\"content\":[\"2 người \"],\"type\":\"1\"},{\"key\":\"3 người\",\"content\":[\"3 người \"],\"type\":\"1\"},{\"key\":\"4 người\",\"content\":[\"4 người \"],\"type\":\"1\"},{\"key\":\"5 người\",\"content\":[\"5 người \"],\"type\":\"1\"},{\"key\":\"6 người\",\"content\":[\"6 người \"],\"type\":\"1\"},{\"key\":\"7 người\",\"content\":[\"7 người \"],\"type\":\"1\"},{\"key\":\"8 người\",\"content\":[\"8 người \"],\"type\":\"1\"},{\"key\":\"9 người\",\"content\":[\"9 người \"],\"type\":\"1\"},{\"key\":\"10 người\",\"content\":[\"10 người \"],\"type\":\"1\"},{\"key\":\"Đổi thông tin đặt bàn\",\"content\":[\"Tôi muốn thay đổi thông tin đặt bàn\"],\"type\":\"1\"},{\"key\":\"cần hỗ trợ\",\"content\":[\"Tôi cần hỗ trợ\"],\"type\":\"1\"}]}}"
        //var chatFromServer: ChatFromServer = GBotApp.buildInstance().gson().fromJson(testViewOneRes, Constant.chatFromServerType)
        //var dmm = ChatDataModel()
        //addOneRestaurant(chatFromServer, dmm)
        var strDefault = "{\"suggestion\":{\"action\":[],\"keyword\":[{\"type\":\"1\",\"content\":[\"Tôi muốn đặt bàn nhà hàng\"],\"key\":\"đặt bàn\"},{\"type\":\"1\",\"content\":[\"Tôi muốn tư vấn nhà hàng\"],\"key\":\"Tìm nhà hàng\"},{\"type\":\"1\",\"content\":[\"Tôi cần hỗ trợ\"],\"key\":\"cần hỗ trợ\"}],\"restaurant\":{},\"type\":4,\"restaurants\":[],\"images\":[]},\"answer\":\"Không có kết quả phù hợp\",\"objectUser\":{\"pre_topic\":\"tuvannhahang\",\"note_text\":\"\",\"tuvannhahang\":{\"@@province\":[],\"@@district\":[],\"@@res_type\":[],\"@@fit\":[],\"@@price\":[]},\"id\":\"866527020338542\",\"thongtincanhan\":{\"@@lng\":105.8594595,\"@@phone\":\"@@unk\",\"@@province\":\"Hà Nội\",\"@@district\":\"Trần Duy Hưng\",\"@@username\":\"@@unk\",\"@@lat\":20.9867199},\"sid\":\"71d47b9a82d145809cc37ad6d296b998\",\"datban\":{\"@@note\":\"@@unk\",\"@@dayofmonth\":\"@@unk\",\"@@hourofday\":\"@@unk\",\"@@children\":0,\"@@people\":0,\"@@restaurant\":\"Món Huế - Trần Duy Hưng\"},\"current_topic\":\"tuvannhahang\",\"thongtinnhahang\":{\"@@restaurant\":\"King BBQ - Đào Tấn\"},\"current_block_command\":{}}}"
        chatFromServerDefault = GBotApp.buildInstance().gson().fromJson(strDefault, Constant.chatFromServerType)
        //
    }

    override fun onDestroy() {
        super.onDestroy()
        if(mSocket.connected()){
            mSocket.disconnect()
        }
    }

    fun clientSendMessage(){
        Utils.toJsonObj("msg", edtChat.text.toString())
        Utils.toJsonObj("type", "1")
        Utils.toJsonObj("id", prefernce.getDeviceId())
        Log.d("DEVICE_ID", Utils.getJsonObj().toString())
        mSocket.emit("client-send-msg", Utils.getJsonObj())
        Utils.removeAllJsonObj()

        var dm = ChatDataModel()
        dm.setHeaderTitle("My Chat - Hôm nay 18:00")
        dm.setMessageBot(edtChat.text.toString())
        dm.setType(ChatAdapter.TYPE_MY)
        //restaurant.removeAll(restaurant)
        restaurant = arrayListOf()
        restaurant.add(Restaurant(edtChat.text.toString(), null, null, null))
        dm.setAllItemsInSection(restaurant)
        LogUtil.d("size", allSampleData.size.toString())
        allSampleData.add(dm)
        LogUtil.d("size sau", allSampleData.size.toString())
        chatAdapter.replaceChatData(allSampleData)
        base.runOnUiThread {
            chatAdapter.notifyDataSetChanged()
            rcChat.smoothScrollToPosition(allSampleData.size)
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
        val message = args[0].toString()
        LogUtil.d("MAIN_SERVICE", "khi co su kien moi login: " + message)

        //remove wait bot
        removeWaitBot()
        //

        var chatFromServer: ChatFromServer = GBotApp.buildInstance().gson().fromJson(message, Constant.chatFromServerType)

        var dmm = ChatDataModel()
        //restaurant.removeAll(restaurant)
        restaurant = arrayListOf()
        //chatFromServer.setType(chatFromServer.getSuggestion()!!.getType()!!)
        chatFromServer.setType(chatFromServer.getSuggestion()?.getType()?:ChatAdapter.TYPE_FRIEND_TEXT)
        if(chatFromServer.getType() == ChatAdapter.TYPE_RESTAURANTS){
            var size: Int = chatFromServer.getSuggestion()!!.getRestaurants()!!.size
            LogUtil.d("size", "size tra ve: "+size.toString())
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

    fun addOneRestaurant(chatFromServer: ChatFromServer, dmm: ChatDataModel){
        //khoi tao du lieu neu null
        /*if(chatFromServer.getSuggestion()!!.getRestaurant()==null){
            addFriendChatText(chatFromServerDefault, dmmDefault)
            return
        }*/
        //het
        LogUtil.d("size", "Khi restaurant: ")
        dmm.setHeaderTitle("Bot Chat - Hôm nay 18:00")
        dmm.setMessageBot(chatFromServer.getAnswer()!!)
        dmm.setContentKeyWord(chatFromServer.getSuggestion()?.getKeyword())
        dmm.setListAction(chatFromServer.getSuggestion()?.getAction()?: arrayListOf())
        dmm.setTagView(chatFromServer.getSuggestion()?.getSearch()?: arrayListOf())
        dmm.setType(ChatAdapter.TYPE_RESTAURANT)
        restaurant.add(Restaurant(chatFromServer.getSuggestion()!!.getRestaurant()!!.getName(), chatFromServer.getSuggestion()!!.getRestaurant()!!.getLink(), chatFromServer.getSuggestion()!!.getRestaurant()!!.getAddress(), chatFromServer.getSuggestion()!!.getRestaurant()!!.getAvatar()))
        LogUtil.d("size", allSampleData.size.toString())
        //dmm.setAllItemsInSection(restaurant)
        //allSampleData.add(dmm)
        LogUtil.d("size sau", allSampleData.size.toString())

        dmm.setAllItemsInSection(restaurant)
        allSampleData.add(dmm)
        chatAdapter.replaceChatData(allSampleData)
        base.runOnUiThread {
            chatAdapter.notifyDataSetChanged()
            rcChat.smoothScrollToPosition(allSampleData.size)
        }
    }
    fun addRestaurantsIfOne(chatFromServer: ChatFromServer, dmm: ChatDataModel){
        dmm.setHeaderTitle("Bot Chat - Hôm nay 18:00")
        dmm.setMessageBot(chatFromServer.getAnswer()!!)
        dmm.setContentKeyWord(chatFromServer.getSuggestion()?.getKeyword())
        dmm.setTagView(chatFromServer.getSuggestion()?.getSearch()?: arrayListOf())
        //khi list nha hang tra ve 1 nha hang -> tao list action rong, sua lai load du lieu tra ve
        dmm.setListAction(chatFromServer.getSuggestion()?.getAction()?: arrayListOf())
        //
        dmm.setType(ChatAdapter.TYPE_RESTAURANT)
        restaurant.add(Restaurant(chatFromServer.getSuggestion()!!.getRestaurants()!!.get(0).getName(), chatFromServer.getSuggestion()!!.getRestaurants()!!.get(0).getLink(), chatFromServer.getSuggestion()!!.getRestaurants()!!.get(0).getAddress(), chatFromServer.getSuggestion()!!.getRestaurants()!!.get(0).getAvatar()))
        LogUtil.d("size", allSampleData.size.toString())
        //dmm.setAllItemsInSection(restaurant)
        //allSampleData.add(dmm)
        LogUtil.d("size sau", allSampleData.size.toString())

        dmm.setAllItemsInSection(restaurant)
        allSampleData.add(dmm)
        chatAdapter.replaceChatData(allSampleData)
        base.runOnUiThread {
            chatAdapter.notifyDataSetChanged()
            rcChat.smoothScrollToPosition(allSampleData.size)
        }
    }
    fun addRestaurants(chatFromServer: ChatFromServer, dmm: ChatDataModel){
        var size: Int = chatFromServer.getSuggestion()!!.getRestaurants()!!.size
        //khoi tao du lieu neu null
        /*if(size<=0){
            addFriendChatText(chatFromServerDefault, dmmDefault)
            return
        }*/
        //het
        for (i in 0..size - 1) {
            dmm.setHeaderTitle("Bot Chat - Hôm nay 18:00")
            dmm.setMessageBot(chatFromServer.getAnswer()!!)
            dmm.setContentKeyWord(chatFromServer.getSuggestion()?.getKeyword())
            dmm.setTagView(chatFromServer.getSuggestion()?.getSearch()?: arrayListOf())
            //add sql load more
            dmm.setSql(chatFromServer.getSuggestion()?.getSql()?:"")
            dmm.setType(ChatAdapter.TYPE_RESTAURANTS)
            restaurant.add(Restaurant(chatFromServer.getSuggestion()!!.getRestaurants()!!.get(i).getName(), chatFromServer.getSuggestion()!!.getRestaurants()!!.get(i).getLink(), chatFromServer.getSuggestion()!!.getRestaurants()!!.get(i).getAddress(), chatFromServer.getSuggestion()!!.getRestaurants()!!.get(i).getAvatar()))
            LogUtil.d("size", allSampleData.size.toString())
            //dmm.setAllItemsInSection(restaurant)
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
    }
    fun addImages(chatFromServer: ChatFromServer, dmm: ChatDataModel){
        var size: Int = chatFromServer.getSuggestion()!!.getImages()!!.size
        //khoi tao du lieu neu null
        /*if(size<=0){
            addFriendChatText(chatFromServerDefault, dmmDefault)
            return
        }*/
        //het
        LogUtil.d("size", "size tra ve: "+size.toString())
        for(i in 0..size-1){
            dmm.setHeaderTitle("Bot Chat - Hôm nay 18:00")
            dmm.setMessageBot(chatFromServer.getAnswer()!!)
            dmm.setContentKeyWord(chatFromServer.getSuggestion()?.getKeyword())
            dmm.setTagView(chatFromServer.getSuggestion()?.getSearch()?: arrayListOf())
            dmm.setType(ChatAdapter.TYPE_IMAGES)
            //neu type = 1: text
            /*if(chatFromServer.getSuggestion()!!.getKeyword()!!.get(i).getType()!!.equals("1")){
                //gan content 0 cho name, key cho link, type cho address, content cho content
                restaurant.add(Restaurant(chatFromServer.getSuggestion()!!.getKeyword()!!.get(i).getContent()!!.get(0), chatFromServer.getSuggestion()!!.getKeyword()!!.get(i).getKey()!!, chatFromServer.getSuggestion()!!.getKeyword()!!.get(i).getType()!!, chatFromServer.getSuggestion()!!.getKeyword()!!.get(i).getContent()!!))
            }else{

            }*/
            //gan content 0 cho name, key cho link, type cho address, content cho content
            restaurant.add(Restaurant(null, null, null, chatFromServer.getSuggestion()?.getImages()?.get(i), null))
            LogUtil.d("size", allSampleData.size.toString())
            //dmm.setAllItemsInSection(restaurant)
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
    }
    fun addImageIfOne(chatFromServer: ChatFromServer, dmm: ChatDataModel){
        var size: Int = chatFromServer.getSuggestion()!!.getImages()!!.size
        LogUtil.d("size", "size tra ve: "+size.toString())
        dmm.setHeaderTitle("Bot Chat - Hôm nay 18:00")
        dmm.setMessageBot(chatFromServer.getAnswer()!!)
        dmm.setContentKeyWord(chatFromServer.getSuggestion()?.getKeyword())
        dmm.setTagView(chatFromServer.getSuggestion()?.getSearch()?: arrayListOf())
        dmm.setType(ChatAdapter.TYPE_ONE_IMAGE)
        restaurant.add(Restaurant(null, null, null, chatFromServer.getSuggestion()?.getImages()?.get(0), null))
        LogUtil.d("size", allSampleData.size.toString())
        LogUtil.d("size sau", allSampleData.size.toString())

        dmm.setAllItemsInSection(restaurant)
        allSampleData.add(dmm)
        chatAdapter.replaceChatData(allSampleData)
        base.runOnUiThread {
            chatAdapter.notifyDataSetChanged()
            rcChat.smoothScrollToPosition(allSampleData.size)
        }
    }
    fun addFriendChatText(chatFromServer: ChatFromServer, dmm: ChatDataModel){
        LogUtil.d("size", "size tra ve: "+chatFromServer.getSuggestion()!!.getRestaurants()!!.size)
        dmm.setHeaderTitle("Bot Chat - Hôm nay 18:00")
        dmm.setMessageBot(chatFromServer.getAnswer()!!)
        dmm.setContentKeyWord(chatFromServer.getSuggestion()?.getKeyword())
        dmm.setTagView(chatFromServer.getSuggestion()?.getSearch()?: arrayListOf())
        dmm.setType(ChatAdapter.TYPE_FRIEND_TEXT)
        restaurant.add(Restaurant(chatFromServer.getAnswer(), null, null, null))
        //dmm.setAllItemsInSection(restaurant)
        LogUtil.d("size", allSampleData.size.toString())
        //allSampleData.add(dmm)
        LogUtil.d("size sau", allSampleData.size.toString())

        dmm.setAllItemsInSection(restaurant)
        allSampleData.add(dmm)
        chatAdapter.replaceChatData(allSampleData)
        base.runOnUiThread {
            chatAdapter.notifyDataSetChanged()
            rcChat.smoothScrollToPosition(allSampleData.size)
        }
    }

    private val onServerHello = Emitter.Listener {
        args ->
        val message = args[1].toString()
        LogUtil.d("MAIN_SERVICE", "khi co su kien moi login: " + message)

        /*
        //remove wait bot
        removeWaitBot()
        //

        var chatFromServer: ChatFromServer = GBotApp.buildInstance().gson().fromJson(message, Constant.chatFromServerType)
        var dmm = ChatDataModel()
        restaurant = arrayListOf()
        LogUtil.d("size", "size tra ve: "+chatFromServer.getSuggestion()!!.getRestaurants()!!.size)
        addFriendChatText(chatFromServer, dmm)
        */


        //remove wait bot
        removeWaitBot()
        //

        var chatFromServer: ChatFromServer = GBotApp.buildInstance().gson().fromJson(message, Constant.chatFromServerType)

        var dmm = ChatDataModel()
        //restaurant.removeAll(restaurant)
        restaurant = arrayListOf()
        chatFromServer.setType(chatFromServer.getSuggestion()?.getType()?:ChatAdapter.TYPE_FRIEND_TEXT)
        if(chatFromServer.getType() == ChatAdapter.TYPE_RESTAURANTS){
            var size: Int = chatFromServer.getSuggestion()!!.getRestaurants()!!.size
            LogUtil.d("size", "size tra ve: "+size.toString())
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
            EventDefine.CLICK_SUGGESTION -> {
                //remove suggesstion
                //removeSuggesstion()
                //
                LogUtil.d("CLICK_ITEM_CHAT", event.values.toString())

                var keyWord = event.values as KeyWord

                //gan content 0 cho name, key cho link, type cho address, content cho content
                if(keyWord.getType().equals("1")){
                    sendServerSuggestion(keyWord.getContent()?.get(0) ?: "")

                }else{
                    showDialog(keyWord)
                }
            }
            EventDefine.CLICK_FRIEND_ITEM_VIEW -> {
                LogUtil.d("CLICK_ITEM_CHAT", event.values.toString())
                resFromClick = event.values as Restaurant
                //Utils.startWebView(context!!, resFromClick.getLink()!!)
                /*mWebViewNew.loadUrl(resFromClick.getLink()?:"http://google.com.vn")
                mPageTitle.setText(resFromClick.getName())
                mPopupWebView.showAtLocation(root, Gravity.CENTER, 0, 0)*/

                edtChat.setText("${edtChat.text}${ resFromClick.getName()}")
                edtChat.setSelection(edtChat.length())
            }
            EventDefine.CLICK_FRIEND_ITEM_BUTTON -> {
                LogUtil.d("CLICK_ITEM_CHAT", event.values.toString())
                resFromClick = event.values as Restaurant
                LogUtil.d("CLICK_ITEM_CHAT", resFromClick.getName().toString())

                sendServerDatBan(resFromClick.getName().toString())
            }
            EventDefine.CLICK_FRIEND_ITEM_DETAILS_RES -> {
                LogUtil.d("CLICK_ITEM_CHAT", event.values.toString())
                resFromClick = event.values as Restaurant
                LogUtil.d("CLICK_ITEM_CHAT", resFromClick.getName().toString())

                mWebViewNew.loadUrl(resFromClick.getLink()?:"http://google.com.vn")
                mPageTitle.setText(resFromClick.getName())
                mPopupWebView.showAtLocation(root, Gravity.CENTER, 0, 0)
                base.setIsShowWebView(true)
            }
            EventDefine.CLICK_FRIEND_TEXT -> {
                LogUtil.d("CLICK_ITEM_CHAT", event.values.toString())
            }
            EventDefine.CLICK_FRIEND_ITEM_ONE_VIEW -> {
                LogUtil.d("CLICK_ITEM_CHAT", event.values.toString())
                resFromClick = event.values as Restaurant
                //Utils.startWebView(context!!, resFromClick.getLink()!!)
                /*mWebViewNew.loadUrl(resFromClick.getLink()?:"http://google.com.vn")
                mPageTitle.setText(resFromClick.getName())
                mPopupWebView.showAtLocation(root, Gravity.CENTER, 0, 0)*/

                edtChat.setText("${edtChat.text}${ resFromClick.getName()}")
                edtChat.setSelection(edtChat.length())
            }
            EventDefine.CLICK_FRIEND_ITEM_ONE_ACTION -> {
                /*LogUtil.d("CLICK_ITEM_CHAT", event.values.toString())
                resFromClick = event.values as Restaurant
                LogUtil.d("CLICK_ITEM_CHAT", resFromClick.getName().toString())
                sendServerDatBan(resFromClick.getName().toString())*/
                resFromClickAction = event.values as KeyWord
                LogUtil.d("CLICK_ITEM_CHAT", resFromClickAction.getKey()?:"")
                if(resFromClickAction.getType()!!.equals(SectionActionAdapter.TYPE_BAN_DO)){
                    resFromClickAction.getContent().let {
                        if(resFromClickAction.getContent()!!.size>0){
                            var intent = Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://maps.google.com/maps?saddr=${strLatLn}&daddr=${resFromClickAction.getContent()!!.get(0)}"))
                            startActivity(intent)
                        }
                    }
                }else if(resFromClickAction.getType()!!.equals(SectionActionAdapter.TYPE_CHI_TIET)){
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
                    resFromClickAction.getContent().let {
                        if(resFromClickAction.getContent()!!.size>0) {
                            sendServerActionText(resFromClickAction.getContent()!!.get(0))
                        }
                    }
                }
            }
            EventDefine.CLICK_FRIEND_ITEM_IMAGE -> {
                var list:MutableList<CustomImage> = event.values as MutableList<CustomImage>
                //LogUtil.d("CLICK_ITEM_CHAT", "click image ${list.get(0).getImages().toString()}")
                if(list.size > 0) {
                    setShowMenu(list)
                }
            }
            EventDefine.DISSMISS_WEBVIEW -> {
                mPopupWebView.dismiss()
                base.setIsShowWebView(false)
            }
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
                .itemsCallback { dialog, itemView, position, text -> sendServerSuggestion(dialogPickSuggesstion.items!!.get(position).toString()) }
                .build()
    }

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

    fun sendServerSuggestion(message: String){

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
            rcChat.smoothScrollToPosition(allSampleData.size)
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
            rcChat.smoothScrollToPosition(allSampleData.size)
        }

        Utils.toJsonObj("msg", "Đặt bàn "+message)
        Utils.toJsonObj("type", "1")
        Utils.toJsonObj("id", prefernce.getDeviceId())
        mSocket.emit("client-send-msg", Utils.getJsonObj())
        Utils.removeAllJsonObj()

        //create chat
        createWaitBot()
        //
    }

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
            rcChat.smoothScrollToPosition(allSampleData.size)
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
            rcChat.smoothScrollToPosition(allSampleData.size)
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

    fun removeWaitBot(){
        if(allSampleData!!.size > 0){
            postWaitBot = allSampleData!!.size-1
            if(allSampleData!!.get(postWaitBot).getType() == ChatAdapter.TYPE_WAIT_BOT){
                allSampleData.removeAt(postWaitBot)
            }
        }
    }

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
            rcChat.smoothScrollToPosition(allSampleData.size)
        }
    }

    fun createPopUp(){
        val inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        // Inflate the custom layout/view
        val customView = inflater.inflate(R.layout.view_popup_webview, null)
        mPopupWebView = PopupWindow(
                customView,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
        )
        if(Build.VERSION.SDK_INT>=21){
            mPopupWebView.setElevation(5.0f);
        }

        mWebViewNew = mPopupWebView.contentView.findViewById<WebView>(R.id.webViewNew)
        mToolBarPopUp = mPopupWebView.contentView.findViewById<ImageView>(R.id.icBack)
        mPageTitle = mPopupWebView.contentView.findViewById<FontTextView>(R.id.pageTitle)
        mImgBackPopUp = mToolBarPopUp.findViewById<ImageView>(R.id.icBack)
        mImgBackPopUp.setOnClickListener {
            mPopupWebView.dismiss()
            base.setIsShowWebView(false)
        }
        mWebViewNew.getSettings().setJavaScriptEnabled(true)
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