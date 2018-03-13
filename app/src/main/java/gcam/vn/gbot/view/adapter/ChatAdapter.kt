package gcam.vn.gbot.view.adapter

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import gcam.vn.gbot.R
import gcam.vn.gbot.manager.event.Event
import gcam.vn.gbot.manager.event.EventDefine
import gcam.vn.gbot.manager.event.EventMessage
import gcam.vn.gbot.manager.ext.LogUtil
import gcam.vn.gbot.module.CustomImage
import gcam.vn.gbot.module.KeyWord
import gcam.vn.gbot.module.Restaurant
import gcam.vn.gbot.view.fragment.ChatDataModel
import kotlinx.android.synthetic.main.item_friend_text.view.*
import kotlinx.android.synthetic.main.item_my_chat.view.*
import kotlinx.android.synthetic.main.item_friend_multi_data.view.*
import kotlinx.android.synthetic.main.item_friend_one_image.view.*
import kotlinx.android.synthetic.main.item_friend_restaurant.view.*
import kotlinx.android.synthetic.main.item_multi_images.view.*

/**
 * Created by thuythu on 12/01/2018.
 */
class ChatAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private lateinit var dataList: MutableList<ChatDataModel>
    private lateinit var mContext: Context

    constructor(context: Context, dataList: MutableList<ChatDataModel>){
        this.dataList = dataList
        this.mContext = context
    }

    companion object {
        val TYPE_RESTAURANTS     = 1
        val TYPE_RESTAURANT      = 2
        val TYPE_IMAGES          = 3
        val TYPE_FRIEND_TEXT     = 4
        val TYPE_MY              = 5
        val TYPE_WAIT_BOT        = 6
        val TYPE_ONE_IMAGE       = 7
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder? {
        when(i){
            TYPE_RESTAURANTS -> {
                val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_friend_multi_data, null)
                return RestaurantsHolder(v)
            }
            TYPE_RESTAURANT -> {
                val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_friend_restaurant, null)
                return OneRestaurantHolder(v)
            }
            TYPE_IMAGES -> {
                val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_friend_multi_data, null)
                return ItemImagesHolder(v)
            }
            TYPE_FRIEND_TEXT -> {
                val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_friend_text, null)
                return FriendChatHolder(v)
            }
            TYPE_MY -> {
                val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_my_chat, null)
                return MyChatHolder(v)
            }
            TYPE_WAIT_BOT -> {
                val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_wait_bot, null)
                return WaitBotHolder(v)
            }
            TYPE_ONE_IMAGE -> {
                val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_friend_one_image, null)
                return ItemImagesOneItemHolder(v)
            }
            else -> return null
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val sectionName = dataList.get(position).getHeaderTitle()
        val messageBot = dataList.get(position).getMessageBot()
        val singleSectionItems = dataList.get(position).getAllItemsInSection()
        //val contentKeyword = dataList.get(position).getContentKeyWord()
        //sửa lại:
        var contentKeyword: MutableList<KeyWord>? = arrayListOf()
        if(position == dataList.size-1){
            contentKeyword = dataList.get(position).getContentKeyWord()
        }

        when(getItemViewType(position)){
            TYPE_RESTAURANTS -> {
                val restaurantsHolder = holder as RestaurantsHolder
                restaurantsHolder.bind(sectionName!!, messageBot!!, singleSectionItems!!, contentKeyword, position)
            }
            TYPE_IMAGES -> {
                val itemImagesHolder = holder as ItemImagesHolder
                itemImagesHolder.bind(sectionName!!, messageBot!!, singleSectionItems!!, contentKeyword, position)
            }
            TYPE_RESTAURANT -> {
                val oneRestaurantHolder = holder as OneRestaurantHolder
                oneRestaurantHolder.bind(sectionName!!, messageBot!!, singleSectionItems!!, contentKeyword, position)
            }
            TYPE_FRIEND_TEXT -> {
                val friendChatHolder = holder as FriendChatHolder
                friendChatHolder.bind(sectionName!!, messageBot!!, singleSectionItems!!, contentKeyword, position)
            }
            TYPE_MY -> {
                val myChatHolder = holder as MyChatHolder
                myChatHolder.bind(sectionName!!, messageBot!!, singleSectionItems!!)
            }
            TYPE_WAIT_BOT -> {
                val waitBotHolder = holder as WaitBotHolder
                waitBotHolder.bind(sectionName!!, messageBot!!, singleSectionItems!!, position)
            }
            TYPE_ONE_IMAGE -> {
                val itemImagesOneItemHolder = holder as ItemImagesOneItemHolder
                itemImagesOneItemHolder.bind(sectionName!!, messageBot!!, singleSectionItems!!, contentKeyword, position)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return dataList.get(position).getType()!!
    }

    override fun getItemCount(): Int {
        return if (null != dataList) dataList.size else 0
    }

    inner class FriendChatHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(sectionName: String, messageBot: String, singleSectionItems: MutableList<Restaurant>, contentKeyword :MutableList<KeyWord>?, position: Int){
            itemView.itemFriendTitle.setText(sectionName)
            itemView.setOnClickListener {
                Event.postEvent(EventMessage(EventDefine.CLICK_FRIEND_TEXT, position))
            }
            itemView.txt_friend_chat.setText(messageBot)

            //set suggesstion
            contentKeyword.let {
                var itemListSeggesstionAdapter = SectionSuggesstionAdapter(mContext, contentKeyword, messageBot)
                itemView.recycler_view_suggesstion.setHasFixedSize(true)
                itemView.recycler_view_suggesstion.setLayoutManager(LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false))
                itemView.recycler_view_suggesstion.setAdapter(itemListSeggesstionAdapter)
            }
        }
    }

    inner class RestaurantsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(sectionName: String, messageBot: String, singleSectionItems: MutableList<Restaurant>, contentKeyword :MutableList<KeyWord>?, position: Int){
            itemView.itemTitle.setText(sectionName)
            itemView.txt_answer.setText(messageBot)
            var itemListDataAdapter = SectionRestaurantsAdapter(mContext, singleSectionItems, messageBot)
            itemView.recycler_view_list.setHasFixedSize(true)
            itemView.recycler_view_list.setLayoutManager(LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false))
            itemView.recycler_view_list.setAdapter(itemListDataAdapter)

            /*itemView.btnMore.setOnClickListener({
                v -> Toast.makeText(v.context, "click event on more, " + sectionName, Toast.LENGTH_SHORT).show()
            })*/

            //set suggesstion
            contentKeyword.let {
                var itemListSeggesstionAdapter = SectionSuggesstionAdapter(mContext, contentKeyword, messageBot)
                itemView.recycler_view_suggesstion_res.setHasFixedSize(true)
                itemView.recycler_view_suggesstion_res.setLayoutManager(LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false))
                itemView.recycler_view_suggesstion_res.setAdapter(itemListSeggesstionAdapter)
            }
        }
    }

    inner class ItemImagesHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(sectionName: String, messageBot: String, singleSectionItems: MutableList<Restaurant>, contentKeyword :MutableList<KeyWord>?, position: Int){
            itemView.itemTitle.setText(sectionName)
            itemView.txt_answer.setText(messageBot)
            var itemSuggessionAdapter = SectionImagesAdapter(mContext, singleSectionItems, messageBot)
            itemView.recycler_view_list.setHasFixedSize(true)
            itemView.recycler_view_list.setLayoutManager(LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false))
            itemView.recycler_view_list.setAdapter(itemSuggessionAdapter)

            /*itemView.btnMore.setOnClickListener({
                v -> Toast.makeText(v.context, "click event on more le, " + sectionName, Toast.LENGTH_SHORT).show()
            })*/

            //set suggesstion
            contentKeyword.let{
                var itemListSeggesstionAdapter = SectionSuggesstionAdapter(mContext, contentKeyword, messageBot)
                itemView.recycler_view_suggesstion_res.setHasFixedSize(true)
                itemView.recycler_view_suggesstion_res.setLayoutManager(LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false))
                itemView.recycler_view_suggesstion_res.setAdapter(itemListSeggesstionAdapter)
            }
        }
    }

    inner class ItemImagesOneItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(sectionName: String, messageBot: String, singleSectionItems: MutableList<Restaurant>, contentKeyword :MutableList<KeyWord>?, position: Int){
            itemView.itemTitleOneImage.setText(sectionName)
            itemView.txt_answer_one_image.setText(messageBot)

            singleSectionItems.let{
                        if(singleSectionItems.size > 0){
                            Glide.with(mContext)
                                    .load("http://192.168.1.3/pasbot/uploads/${singleSectionItems.get(0).getImages()}")
                                    .into(itemView.img_menu)
                        }
                    }

            itemView.setOnClickListener {
                if (singleSectionItems.size > 0) {
                    var list = arrayListOf<CustomImage>()
                    list.add(CustomImage("http://192.168.1.3/pasbot/uploads/${singleSectionItems.get(0).getImages()}", singleSectionItems.get(0).getSMenu().toString()))
                    Event.postEvent(EventMessage(EventDefine.CLICK_FRIEND_ITEM_IMAGE, list))
                }
            }
            /*itemView.btnMore.setOnClickListener({
                v -> Toast.makeText(v.context, "click event on more le, " + sectionName, Toast.LENGTH_SHORT).show()
            })*/

            //set suggesstion
            contentKeyword.let{
                var itemListSeggesstionAdapter = SectionSuggesstionAdapter(mContext, contentKeyword, messageBot)
                itemView.recycler_view_suggesstion_one_image.setHasFixedSize(true)
                itemView.recycler_view_suggesstion_one_image.setLayoutManager(LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false))
                itemView.recycler_view_suggesstion_one_image.setAdapter(itemListSeggesstionAdapter)
            }
        }
    }

    inner class MyChatHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(sectionName: String, messageBot: String, singleSectionItems: MutableList<Restaurant>){
            itemView.itemMyTitle.setText(sectionName)
            var size = singleSectionItems.size-1
            itemView.txt_my_chat.setText(messageBot)
        }
    }

    inner class OneRestaurantHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(sectionName: String, messageBot: String, singleSectionItems: MutableList<Restaurant>, contentKeyword :MutableList<KeyWord>?, position: Int){
            itemView.itemOneTitle.setText(sectionName)
            itemView.txtFriendOneChat.setText(messageBot)
            itemView.txtDescripOneChat.setText(singleSectionItems.get(0).getName())

            Glide.with(itemView.context)
                    .load(R.drawable.img_restau)
                    .into(itemView.itemOneImage)

            itemView.btnFriendOneChat.setOnClickListener {
                v ->  Event.postEvent(EventMessage(EventDefine.CLICK_FRIEND_ITEM_ONE_BUTTON, singleSectionItems.get(0)))
            }
            itemView.setOnClickListener {
                v ->  Event.postEvent(EventMessage(EventDefine.CLICK_FRIEND_ITEM_ONE_VIEW, singleSectionItems.get(0)))
            }

            //set suggesstion
            contentKeyword.let {
                var itemListSeggesstionAdapter = SectionSuggesstionAdapter(mContext, contentKeyword, messageBot)
                itemView.recycler_view_suggesstion_one.setHasFixedSize(true)
                itemView.recycler_view_suggesstion_one.setLayoutManager(LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false))
                itemView.recycler_view_suggesstion_one.setAdapter(itemListSeggesstionAdapter)
            }
        }
    }

    inner class WaitBotHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(sectionName: String, messageBot: String, singleSectionItems: MutableList<Restaurant>, position: Int){
            itemView.itemFriendTitle.setText(sectionName)
            var size = singleSectionItems.size-1
            itemView.txt_friend_chat.setText(". . . ")
        }
    }

    fun addChatData(chatDataModel: ChatDataModel){
        dataList.add(chatDataModel)
    }

    fun getDataList(): MutableList<ChatDataModel>{
        return dataList
    }

    fun replaceChatData(dataListt: MutableList<ChatDataModel>){
        this.dataList = dataListt
    }
}