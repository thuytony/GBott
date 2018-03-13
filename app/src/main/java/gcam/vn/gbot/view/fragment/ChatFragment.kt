package gcam.vn.gbot.view.fragment

import android.content.Context
import android.location.Address
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import android.util.Log
import com.afollestad.materialdialogs.MaterialDialog
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.google.android.gms.maps.model.LatLng
import com.stfalcon.frescoimageviewer.ImageViewer
import gcam.vn.gbot.manager.ext.PreferenceUtil
import gcam.vn.gbot.manager.ext.SimpleToast
import gcam.vn.gbot.module.*
import gcam.vn.gbot.service.TrackLocation
import gcam.vn.gbot.service.TrackLocationInterface


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

        //them
        var s = "{\"answer\":\"Danh sách các món ăn đặc sắc: None .\\nMenu đang cập nhập, a\\/c thông cảm \",\"suggestion\":{\"images\":[],\"restaurants\":[],\"type\":2,\"restaurant\":{\"open_time\":\"6:30:00-21:30:00\",\"menu\":[],\"name\":\"Món Huế - Trần Duy Hưng\",\"s_menu\":null,\"address\":\"67 Trần Duy Hưng, Q. Cầu Giấy\",\"avatar\":null,\"price_min\":100000,\"link\":\"https:\\/\\/pasgo.vn\\/nha-hang\\/nha-hang-mon-hue-tran-duy-hung-1308\",\"price_max\":200000},\"keyword\":[{\"key\":\"cần hỗ trợ\",\"type\":1,\"content\":[\"Tôi cần hỗ trợ\"]},{\"key\":\"tư vấn\",\"type\":1,\"content\":[\"Tôi cần tư vấn\"]},{\"key\":\"test\",\"type\":\"4\",\"content\":[\"menu\"]}]},\"objectUser\":{\"datban\":{\"@@hourofday\":\"@@unk\",\"@@dayofmonth\":\"@@unk\",\"@@people\":0,\"@@restaurant\":\"@@unk\",\"@@note\":\"@@unk\",\"@@children\":0},\"current_topic\":\"@@unk\",\"thongtinnhahang\":{\"@@restaurant\":\"@@unk\"},\"pre_topic\":\"@@unk\",\"thongtincanhan\":{\"@@phone\":\"@@unk\",\"@@username\":\"@@unk\"},\"current_block_command\":{},\"sid\":\"ea583394fccc4154bdb46d298e978aef\",\"id\":\"358079050710906\",\"tuvannhahang\":{\"@@fit\":[],\"@@price\":[],\"@@res_type\":[]}}}"
        var have_image = "{\"answer\":\"Danh sách các món ăn đặc sắc: Lẩu riêu cá chép; Tu hài hấp rượu vang; Baba rang muối; Tôm hùm; Cá song hấp xì dầu; Cá chình nướng riềng mẻ; Gà lên mâm .\\nThực đơn chính của nhà hàng: \",\"suggestion\":{\"images\":[\"menus\\/quananngon_phandinhphung.jpg\"],\"restaurants\":[],\"type\":3,\"restaurant\":{\"open_time\":\"6:30:00-21:00:00\",\"menu\":[\"menus\\/quananngon_phandinhphung.jpg\"],\"name\":\"Quán Ăn Ngon - Phan Đình Phùng\",\"s_menu\":\"Lẩu riêu cá chép; Tu hài hấp rượu vang; Baba rang muối; Tôm hùm; Cá song hấp xì dầu; Cá chình nướng riềng mẻ; Gà lên mâm\",\"address\":\"34 Phan Đình Phùng, Q. Ba Đình\",\"avatar\":\"restaurants\\/avatar_quananngonpdp.jpg\",\"price_min\":120000,\"link\":\"https:\\/\\/pasgo.vn\\/nha-hang\\/quan-an-ngon-phan-dinh-phung-gin-giu-linh-hon-am-thuc-viet-888\",\"price_max\":150000},\"keyword\":[{\"key\":\"cần hỗ trợ\",\"type\":\"1\",\"content\":[\"Tôi cần hỗ trợ\"]},{\"key\":\"test\",\"type\":\"4\",\"content\":[\"menu\"]}]},\"objectUser\":{\"datban\":{\"@@hourofday\":\"@@unk\",\"@@dayofmonth\":\"@@unk\",\"@@people\":0,\"@@restaurant\":\"@@unk\",\"@@note\":\"@@unk\",\"@@children\":0},\"current_topic\":\"@@unk\",\"thongtinnhahang\":{\"@@restaurant\":\"@@unk\"},\"pre_topic\":\"@@unk\",\"thongtincanhan\":{\"@@phone\":\"@@unk\",\"@@username\":\"@@unk\"},\"current_block_command\":{},\"sid\":\"5af1a9b7948f4247980e97f12b095006\",\"id\":\"358079050710906\",\"tuvannhahang\":{\"@@fit\":[],\"@@price\":[],\"@@res_type\":[]}}}"
        var chatFromServer: ChatFromServer = GBotApp.buildInstance().gson().fromJson(s, Constant.chatFromServerType)
        var dmm = ChatDataModel()
        //addOneRestaurant(chatFromServer, dmm)
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
        restaurant.add(Restaurant(edtChat.text.toString(), null, null))
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
        chatFromServer.setType(chatFromServer.getSuggestion()!!.getType()!!)
        if(chatFromServer.getType() == ChatAdapter.TYPE_RESTAURANTS){
            var size: Int = chatFromServer.getSuggestion()!!.getRestaurants()!!.size
            LogUtil.d("size", "size tra ve: "+size.toString())
            if(size==1){
                //khi restaurants co 1 item
                addRestaurantsIfOne(chatFromServer, dmm)
            }else {
                addRestaurants(chatFromServer, dmm)
            }
        }else if(chatFromServer.getType() == ChatAdapter.TYPE_RESTAURANT){
            addOneRestaurant(chatFromServer, dmm)
        }else if(chatFromServer.getType() == ChatAdapter.TYPE_IMAGES){
            var sizeImage: Int = chatFromServer.getSuggestion()!!.getImages()!!.size
            if(sizeImage==1){
                //khi image co 1 item
                addImageIfOne(chatFromServer, dmm)
            }else {
                addImages(chatFromServer, dmm)
            }
        }else{
            addFriendChatText(chatFromServer, dmm)
        }
    }

    fun addOneRestaurant(chatFromServer: ChatFromServer, dmm: ChatDataModel){
        LogUtil.d("size", "Khi restaurant: ")
        dmm.setHeaderTitle("Bot Chat - Hôm nay 18:00")
        dmm.setMessageBot(chatFromServer.getAnswer()!!)
        dmm.setContentKeyWord(chatFromServer.getSuggestion()?.getKeyword())
        dmm.setType(ChatAdapter.TYPE_RESTAURANT)
        restaurant.add(Restaurant(chatFromServer.getSuggestion()!!.getRestaurant()!!.getName(), chatFromServer.getSuggestion()!!.getRestaurant()!!.getLink(), chatFromServer.getSuggestion()!!.getRestaurant()!!.getAddress()))
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
        dmm.setType(ChatAdapter.TYPE_RESTAURANT)
        restaurant.add(Restaurant(chatFromServer.getSuggestion()!!.getRestaurants()!!.get(0).getName(), chatFromServer.getSuggestion()!!.getRestaurants()!!.get(0).getLink(), chatFromServer.getSuggestion()!!.getRestaurants()!!.get(0).getAddress()))
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
        for (i in 0..size - 1) {
            dmm.setHeaderTitle("Bot Chat - Hôm nay 18:00")
            dmm.setMessageBot(chatFromServer.getAnswer()!!)
            dmm.setContentKeyWord(chatFromServer.getSuggestion()?.getKeyword())
            dmm.setType(ChatAdapter.TYPE_RESTAURANTS)
            restaurant.add(Restaurant(chatFromServer.getSuggestion()!!.getRestaurants()!!.get(i).getName(), chatFromServer.getSuggestion()!!.getRestaurants()!!.get(i).getLink(), chatFromServer.getSuggestion()!!.getRestaurants()!!.get(i).getAddress()))
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
        LogUtil.d("size", "size tra ve: "+size.toString())
        for(i in 0..size-1){
            dmm.setHeaderTitle("Bot Chat - Hôm nay 18:00")
            dmm.setMessageBot(chatFromServer.getAnswer()!!)
            dmm.setContentKeyWord(chatFromServer.getSuggestion()?.getKeyword())
            dmm.setType(ChatAdapter.TYPE_IMAGES)
            //neu type = 1: text
            /*if(chatFromServer.getSuggestion()!!.getKeyword()!!.get(i).getType()!!.equals("1")){
                //gan content 0 cho name, key cho link, type cho address, content cho content
                restaurant.add(Restaurant(chatFromServer.getSuggestion()!!.getKeyword()!!.get(i).getContent()!!.get(0), chatFromServer.getSuggestion()!!.getKeyword()!!.get(i).getKey()!!, chatFromServer.getSuggestion()!!.getKeyword()!!.get(i).getType()!!, chatFromServer.getSuggestion()!!.getKeyword()!!.get(i).getContent()!!))
            }else{

            }*/
            //gan content 0 cho name, key cho link, type cho address, content cho content
            restaurant.add(Restaurant(null, null, null, chatFromServer.getSuggestion()?.getImages()?.get(i)))
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
        dmm.setType(ChatAdapter.TYPE_ONE_IMAGE)
        restaurant.add(Restaurant(null, null, null, chatFromServer.getSuggestion()?.getImages()?.get(0)))
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
        dmm.setType(ChatAdapter.TYPE_FRIEND_TEXT)
        restaurant.add(Restaurant(chatFromServer.getAnswer(), null, null))
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

        //remove wait bot
        removeWaitBot()
        //

        var chatFromServer: ChatFromServer = GBotApp.buildInstance().gson().fromJson(message, Constant.chatFromServerType)
        //LogUtil.d("MAIN_SERVICE", "khi co su kien moi login: " + chatFromServer.getSuggestion()!!.getRestaurants()!!.get(0).getLink())

        /**val dm = ChatDataModel()
        dm.setHeaderTitle("Friend Chat")
        //singleItem.removeAll(singleItem)
        if(chatFromServer.getSuggestion()!!.getRestaurants()!!.size>0){
        var size: Int = chatFromServer.getSuggestion()!!.getRestaurants()!!.size
        for(i in 0..size-1){
        dm.setType(ChatAdapter.TYPE_RESTAURANTS)
        restaurant.add(Restaurant(chatFromServer.getSuggestion()!!.getRestaurants()!!.get(i).getName(), chatFromServer.getSuggestion()!!.getRestaurants()!!.get(i).getLink(), chatFromServer.getSuggestion()!!.getRestaurants()!!.get(i).getAddress()))
        }
        }else{
        dm.setType(ChatAdapter.TYPE_FRIEND_TEXT)
        restaurant.add(Restaurant(chatFromServer.getAnswer(), null, null))
        }
        dm.setAllItemsInSection(restaurant)
        chatAdapter.addChatData(dm)
        base.runOnUiThread {
        chatAdapter.notifyDataSetChanged()
        }**/

        var dmm = ChatDataModel()
        //restaurant.removeAll(restaurant)
        restaurant = arrayListOf()
        LogUtil.d("size", "size tra ve: "+chatFromServer.getSuggestion()!!.getRestaurants()!!.size)

        addFriendChatText(chatFromServer, dmm)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: EventMessage) {


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
                Utils.startWebView(context!!, resFromClick.getLink()!!)
            }
            EventDefine.CLICK_FRIEND_ITEM_BUTTON -> {
                LogUtil.d("CLICK_ITEM_CHAT", event.values.toString())
                resFromClick = event.values as Restaurant
                LogUtil.d("CLICK_ITEM_CHAT", resFromClick.getName().toString())

                sendServerDatBan(resFromClick.getName().toString())
            }
            EventDefine.CLICK_FRIEND_TEXT -> {
                LogUtil.d("CLICK_ITEM_CHAT", event.values.toString())
            }
            EventDefine.CLICK_FRIEND_ITEM_ONE_VIEW -> {
                LogUtil.d("CLICK_ITEM_CHAT", event.values.toString())
                resFromClick = event.values as Restaurant
                Utils.startWebView(context!!, resFromClick.getLink()!!)
            }
            EventDefine.CLICK_FRIEND_ITEM_ONE_BUTTON -> {
                LogUtil.d("CLICK_ITEM_CHAT", event.values.toString())
                resFromClick = event.values as Restaurant
                LogUtil.d("CLICK_ITEM_CHAT", resFromClick.getName().toString())
                sendServerDatBan(resFromClick.getName().toString())
            }
            EventDefine.CLICK_FRIEND_ITEM_IMAGE -> {
                var list:MutableList<CustomImage> = event.values as MutableList<CustomImage>
                //LogUtil.d("CLICK_ITEM_CHAT", "click image ${list.get(0).getImages().toString()}")
                if(list.size > 0) {
                    setShowMenu(list)
                }
            }

        }
    }

    /*TrackLocationInterface*/
    override fun onLocationSuccess(locationToSer: LocationToServer) {
        locationToSer.id = prefernce.getDeviceId()
        LogUtil.d("LOCATION", Utils.convertObjectToJson(locationToSer))

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
        restaurant.add(Restaurant(message, null, null))
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
        dm.setMessageBot(message)
        dm.setType(ChatAdapter.TYPE_MY)
        //restaurant.removeAll(restaurant)
        restaurant = arrayListOf()
        restaurant.add(Restaurant("Đặt bàn "+message, null, null))
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

    fun removeSuggesstion(){
//        if(allSampleData!!.size > 0){
//            if(allSampleData.get(allSampleData.size-1).getType()!=ChatAdapter.TYPE_MY ||
//                    allSampleData.get(allSampleData.size-1).getType()!=ChatAdapter.TYPE_WAIT_BOT) {
//                if (allSampleData.get(allSampleData.size - 1).getContentKeyWord()!!.size > 0) {
//                    allSampleData.get(allSampleData.size - 1).getContentKeyWord()!!.removeAll(allSampleData.get(allSampleData.size - 1).getContentKeyWord()!!)
//                }
//            }
//        }
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
        restaurant.add(Restaurant("wait", null, null))
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

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }
}