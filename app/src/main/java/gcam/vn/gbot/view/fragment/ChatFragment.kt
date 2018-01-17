package gcam.vn.gbot.view.fragment

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import gcam.vn.gbot.BuildConfig
import gcam.vn.gbot.R
import gcam.vn.gbot.application.GBotApp
import gcam.vn.gbot.manager.event.Event
import gcam.vn.gbot.manager.event.EventDefine
import gcam.vn.gbot.manager.event.EventMessage
import gcam.vn.gbot.manager.ext.LogUtil
import gcam.vn.gbot.view.adapter.ChatAdapter
import gcam.vn.gbot.view.widget.BaseFragment
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.android.synthetic.main.fragment_chat.*
import gcam.vn.gbot.manager.ext.Constant
import gcam.vn.gbot.module.ChatFromServer
import gcam.vn.gbot.module.Restaurant
import gcam.vn.gbot.util.Utils
import io.socket.emitter.Emitter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * Created by thuythu on 12/01/2018.
 */
class ChatFragment: BaseFragment(){
    private lateinit var root: View
    private lateinit var allSampleData: MutableList<ChatDataModel>
    private lateinit var mSocket : Socket
    private lateinit var restaurant: MutableList<Restaurant>
    private lateinit var chatAdapter: ChatAdapter
    private var resFromClick: Restaurant = Restaurant()

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

        listenSocket()

        allSampleData = arrayListOf()
        restaurant = arrayListOf()

        rcChat.setHasFixedSize(true)
        chatAdapter = ChatAdapter(context!!, allSampleData)
        rcChat.layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.VERTICAL, false)
        rcChat.adapter = chatAdapter

        imgSend.setOnClickListener { v->
            Utils.toJsonObj("msg", edtChat.text.toString())
            Utils.toJsonObj("type", "1")
            Utils.toJsonObj("id", "Nguyen_Thuy")
            mSocket.emit("client-send-msg", Utils.getJsonObj())
            Utils.removeAllJsonObj()

            /**val dm = ChatDataModel()
            dm.setHeaderTitle("My Chat")
            dm.setType(ChatAdapter.TYPE_MY)
            //singleItem.removeAll(singleItem)
            /*for (j in 0..5) {
                singleItem.add(SingleItemModel("Item " + j, "URL " + j))
            }*/
            restaurant.add(Restaurant(edtChat.text.toString(), null, null))
            //dm.setAllItemsInSection(restaurant)
            dm.setAllItemsInSection(restaurant)
            chatAdapter.addChatData(dm)
            base.runOnUiThread {
                chatAdapter.notifyDataSetChanged()
            }**/

            var dm = ChatDataModel()
            dm.setHeaderTitle("My Chat")
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

        mSocket.on("server-botchat", onBotChat)
        mSocket.on("server-hello", onServerHello)
    }

    override fun onDestroy() {
        super.onDestroy()
        if(mSocket.connected()){
            mSocket.disconnect()
        }
    }

    fun listenSocket(){
        mSocket = IO.socket(BuildConfig.DOMAIN_SOCKET)
        if(!mSocket.connected()) {
            try {
                //mSocket = IO.socket(BuildConfig.DOMAIN_SOCKET)
                mSocket.connect()

                Utils.toJsonObj("id", "Nguyen_Thuy")
                mSocket.emit("client-connect", Utils.getJsonObj())
                Utils.removeAllJsonObj()
            } catch (e: Exception) {
                e.printStackTrace()
                LogUtil.d("MAIN_SERVICE", e.printStackTrace().toString())
            }
        }
    }
    private val onBotChat = Emitter.Listener {
        args ->
        val message = args[0].toString()
        LogUtil.d("MAIN_SERVICE", "khi co su kien moi login: " + message)
        var chatFromServer: ChatFromServer = GBotApp.buildInstance().gson().fromJson(message, Constant.chatFromServerType)
        //LogUtil.d("MAIN_SERVICE", "khi co su kien moi login: " + chatFromServer.getSuggestion()!!.getRestaurants()!!.get(0).getLink())

        /**val dm = ChatDataModel()
        dm.setHeaderTitle("Friend Chat")
        //singleItem.removeAll(singleItem)
        if(chatFromServer.getSuggestion()!!.getRestaurants()!!.size>0){
            var size: Int = chatFromServer.getSuggestion()!!.getRestaurants()!!.size
            for(i in 0..size-1){
                dm.setType(ChatAdapter.TYPE_FRIEND_ITEM)
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
        if(chatFromServer.getSuggestion()!!.getRestaurants()!!.size>0){
            var size: Int = chatFromServer.getSuggestion()!!.getRestaurants()!!.size
            LogUtil.d("size", "size tra ve: "+size.toString())
            if(size==1){
                //khi restaurants co 1 item
                dmm.setHeaderTitle("Bot Chat")
                dmm.setMessageBot(chatFromServer.getAnswer()!!)
                dmm.setType(ChatAdapter.TYPE_FRIEND_ONE_ITEM)
                restaurant.add(Restaurant(chatFromServer.getSuggestion()!!.getRestaurants()!!.get(0).getName(), chatFromServer.getSuggestion()!!.getRestaurants()!!.get(0).getLink(), chatFromServer.getSuggestion()!!.getRestaurants()!!.get(0).getAddress()))
                LogUtil.d("size", allSampleData.size.toString())
                //dmm.setAllItemsInSection(restaurant)
                //allSampleData.add(dmm)
                LogUtil.d("size sau", allSampleData.size.toString())
            }else {
                for (i in 0..size - 1) {
                    dmm.setHeaderTitle("Bot Chat")
                    dmm.setMessageBot(chatFromServer.getAnswer()!!)
                    dmm.setType(ChatAdapter.TYPE_FRIEND_ITEM)
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
            dmm.setType(ChatAdapter.TYPE_FRIEND_ITEM)
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
                dmm.setType(ChatAdapter.TYPE_SUGGESTION)
                restaurant.add(Restaurant(chatFromServer.getSuggestion()!!.getKeyword()!!.get(i), null, null))
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
    }

    private val onServerHello = Emitter.Listener {
        args ->
        val message = args[1].toString()
        LogUtil.d("MAIN_SERVICE", "khi co su kien moi login: " + message)
        var chatFromServer: ChatFromServer = GBotApp.buildInstance().gson().fromJson(message, Constant.chatFromServerType)
        //LogUtil.d("MAIN_SERVICE", "khi co su kien moi login: " + chatFromServer.getSuggestion()!!.getRestaurants()!!.get(0).getLink())

        /**val dm = ChatDataModel()
        dm.setHeaderTitle("Friend Chat")
        //singleItem.removeAll(singleItem)
        if(chatFromServer.getSuggestion()!!.getRestaurants()!!.size>0){
        var size: Int = chatFromServer.getSuggestion()!!.getRestaurants()!!.size
        for(i in 0..size-1){
        dm.setType(ChatAdapter.TYPE_FRIEND_ITEM)
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
        if(chatFromServer.getSuggestion()!!.getRestaurants()!!.size>0){
            var size: Int = chatFromServer.getSuggestion()!!.getRestaurants()!!.size
            LogUtil.d("size", "size tra ve: "+size.toString())
            for(i in 0..size-1){
                dmm.setHeaderTitle("Bot Chat")
                dmm.setMessageBot(chatFromServer.getAnswer()!!)
                dmm.setType(ChatAdapter.TYPE_FRIEND_ITEM)
                restaurant.add(Restaurant(chatFromServer.getSuggestion()!!.getRestaurants()!!.get(i).getName(), chatFromServer.getSuggestion()!!.getRestaurants()!!.get(i).getLink(), chatFromServer.getSuggestion()!!.getRestaurants()!!.get(i).getAddress()))
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
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: EventMessage) {
        when (event.key) {
            EventDefine.CLICK_SUGGESTION -> {
                LogUtil.d("CLICK_ITEM_CHAT", event.values.toString())
                resFromClick = event.values as Restaurant

                var dm = ChatDataModel()
                dm.setHeaderTitle("My Chat")
                dm.setMessageBot(resFromClick.getName().toString())
                dm.setType(ChatAdapter.TYPE_MY)
                //restaurant.removeAll(restaurant)
                restaurant = arrayListOf()
                restaurant.add(Restaurant(resFromClick.getName().toString(), null, null))
                dm.setAllItemsInSection(restaurant)
                LogUtil.d("size", allSampleData.size.toString())
                allSampleData.add(dm)
                LogUtil.d("size sau", allSampleData.size.toString())
                chatAdapter.replaceChatData(allSampleData)
                base.runOnUiThread {
                    chatAdapter.notifyDataSetChanged()
                    rcChat.smoothScrollToPosition(allSampleData.size)
                }

                Utils.toJsonObj("msg", resFromClick.getName().toString())
                Utils.toJsonObj("type", "1")
                Utils.toJsonObj("id", "Nguyen_Thuy")
                mSocket.emit("client-send-msg", Utils.getJsonObj())
                Utils.removeAllJsonObj()
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

                var dm = ChatDataModel()
                dm.setHeaderTitle("My Chat")
                dm.setMessageBot(resFromClick.getName().toString())
                dm.setType(ChatAdapter.TYPE_MY)
                //restaurant.removeAll(restaurant)
                restaurant = arrayListOf()
                restaurant.add(Restaurant("Đặt bàn "+resFromClick.getName().toString(), null, null))
                dm.setAllItemsInSection(restaurant)
                LogUtil.d("size", allSampleData.size.toString())
                allSampleData.add(dm)
                LogUtil.d("size sau", allSampleData.size.toString())
                chatAdapter.replaceChatData(allSampleData)
                base.runOnUiThread {
                    chatAdapter.notifyDataSetChanged()
                    rcChat.smoothScrollToPosition(allSampleData.size)
                }

                Utils.toJsonObj("msg", "Đặt bàn "+resFromClick.getName().toString())
                Utils.toJsonObj("type", "1")
                Utils.toJsonObj("id", "Nguyen_Thuy")
                mSocket.emit("client-send-msg", Utils.getJsonObj())
                Utils.removeAllJsonObj()
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

                var dm = ChatDataModel()
                dm.setHeaderTitle("My Chat")
                dm.setMessageBot(resFromClick.getName().toString())
                dm.setType(ChatAdapter.TYPE_MY)
                //restaurant.removeAll(restaurant)
                restaurant = arrayListOf()
                restaurant.add(Restaurant("Đặt bàn "+resFromClick.getName().toString(), null, null))
                dm.setAllItemsInSection(restaurant)
                LogUtil.d("size", allSampleData.size.toString())
                allSampleData.add(dm)
                LogUtil.d("size sau", allSampleData.size.toString())
                chatAdapter.replaceChatData(allSampleData)
                base.runOnUiThread {
                    chatAdapter.notifyDataSetChanged()
                    rcChat.smoothScrollToPosition(allSampleData.size)
                }

                Utils.toJsonObj("msg", "Đặt bàn "+resFromClick.getName().toString())
                Utils.toJsonObj("type", "1")
                Utils.toJsonObj("id", "Nguyen_Thuy")
                mSocket.emit("client-send-msg", Utils.getJsonObj())
                Utils.removeAllJsonObj()
            }

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