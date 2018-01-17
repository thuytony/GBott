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
import gcam.vn.gbot.module.ChatFromServer
import gcam.vn.gbot.module.Restaurant
import gcam.vn.gbot.view.fragment.ChatDataModel
import kotlinx.android.synthetic.main.item_friend_chat.view.*
import kotlinx.android.synthetic.main.item_my_chat.view.*
import kotlinx.android.synthetic.main.list_item_friend_chat.view.*
import kotlinx.android.synthetic.main.list_one_item_friend_chat.view.*

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
        val TYPE_FRIEND_TEXT     = 1
        val TYPE_FRIEND_ITEM     = 2
        val TYPE_SUGGESTION      = 3
        val TYPE_MY              = 4
        val TYPE_FRIEND_ONE_ITEM = 5
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder? {
        when(i){
            TYPE_FRIEND_TEXT -> {
                //val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_item_friend_chat, null)
                val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_friend_chat, null)
                return FriendChatHolder(v)
            }
            TYPE_FRIEND_ITEM -> {
                val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_item_friend_chat, null)
                //val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_item_friend_chat, viewGroup, false)
                //v.setLayoutParams(RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
                return ItemRowHolder(v)
            }
            TYPE_SUGGESTION -> {
                val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_item_friend_chat, null)
                return ItemSuggesionHolder(v)
            }
            TYPE_MY -> {
                //val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_item_my_chat, null)
                val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_my_chat, null)
                return MyChatHolder(v)
            }
            TYPE_FRIEND_ONE_ITEM -> {
                //val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_item_my_chat, null)
                val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_one_item_friend_chat, null)
                return FriendOneItemHolder(v)
            }
            else -> return null
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val sectionName = dataList.get(position).getHeaderTitle()
        val messageBot = dataList.get(position).getMessageBot()
        val singleSectionItems = dataList.get(position).getAllItemsInSection()
        when(getItemViewType(position)){
            TYPE_FRIEND_TEXT -> {
                val friendChatHolder = holder as FriendChatHolder
                friendChatHolder.bind(sectionName!!, messageBot!!, singleSectionItems!!, position)
            }
            TYPE_FRIEND_ITEM -> {
                val itemRowHolder = holder as ItemRowHolder
                itemRowHolder.bind(sectionName!!, messageBot!!, singleSectionItems!!, position)
            }
            TYPE_SUGGESTION -> {
                val itemSuggesionHolder = holder as ItemSuggesionHolder
                itemSuggesionHolder.bind(sectionName!!, messageBot!!, singleSectionItems!!, position)
            }
            TYPE_MY -> {
                val myChatHolder = holder as MyChatHolder
                myChatHolder.bind(sectionName!!, messageBot!!, singleSectionItems!!)
            }
            TYPE_FRIEND_ONE_ITEM -> {
                val friendOneItemHolder = holder as FriendOneItemHolder
                friendOneItemHolder.bind(sectionName!!, messageBot!!, singleSectionItems!!, position)
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
        fun bind(sectionName: String, messageBot: String, singleSectionItems: MutableList<Restaurant>, position: Int){
            itemView.itemFriendTitle.setText(sectionName)

            itemView.setOnClickListener { Event.postEvent(EventMessage(EventDefine.CLICK_FRIEND_TEXT, position)) }

            /*var myChatAdapter = MyChatAdapter(mContext, singleSectionItems)

            itemView.recycler_view_my_chat.setHasFixedSize(true)
            itemView.recycler_view_my_chat.setLayoutManager(LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false))
            itemView.recycler_view_my_chat.setAdapter(myChatAdapter)

            itemView.btnMyMore.setOnClickListener({ v -> Toast.makeText(v.context, "click event on more my chat, " + sectionName, Toast.LENGTH_SHORT).show() })*/
            var size = singleSectionItems.size-1
            itemView.txt_friend_chat.setText(messageBot)

            /*Glide.with(mContext)
                .load(feedItem.getImageURL())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .error(R.drawable.bg)
                .into(feedListRowHolder.thumbView);*/
        }
    }

    inner class ItemRowHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(sectionName: String, messageBot: String, singleSectionItems: MutableList<Restaurant>, position: Int){
            itemView.itemTitle.setText(sectionName)
            itemView.txt_answer.setText(messageBot)

            var itemListDataAdapter = SectionListDataAdapter(mContext, singleSectionItems, messageBot)

            itemView.recycler_view_list.setHasFixedSize(true)
            itemView.recycler_view_list.setLayoutManager(LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false))
            itemView.recycler_view_list.setAdapter(itemListDataAdapter)

            itemView.btnMore.setOnClickListener({ v -> Toast.makeText(v.context, "click event on more, " + sectionName, Toast.LENGTH_SHORT).show() })

            /*Glide.with(mContext)
                .load(feedItem.getImageURL())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .error(R.drawable.bg)
                .into(feedListRowHolder.thumbView);*/
        }
    }

    inner class ItemSuggesionHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(sectionName: String, messageBot: String, singleSectionItems: MutableList<Restaurant>, position: Int){
            itemView.itemTitle.setText(sectionName)
            itemView.txt_answer.setText(messageBot)

            var itemSuggessionAdapter = SectionSuggesstionAdapter(mContext, singleSectionItems, messageBot)

            itemView.recycler_view_list.setHasFixedSize(true)
            itemView.recycler_view_list.setLayoutManager(LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false))
            itemView.recycler_view_list.setAdapter(itemSuggessionAdapter)

            itemView.btnMore.setOnClickListener({ v -> Toast.makeText(v.context, "click event on more le, " + sectionName, Toast.LENGTH_SHORT).show() })
            /*Glide.with(mContext)
                .load(feedItem.getImageURL())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .error(R.drawable.bg)
                .into(feedListRowHolder.thumbView);*/
        }
    }

    inner class MyChatHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(sectionName: String, messageBot: String, singleSectionItems: MutableList<Restaurant>){
            itemView.itemMyTitle.setText(sectionName)

            /*var myChatAdapter = MyChatAdapter(mContext, singleSectionItems)

            itemView.recycler_view_my_chat.setHasFixedSize(true)
            itemView.recycler_view_my_chat.setLayoutManager(LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false))
            itemView.recycler_view_my_chat.setAdapter(myChatAdapter)

            itemView.btnMyMore.setOnClickListener({ v -> Toast.makeText(v.context, "click event on more my chat, " + sectionName, Toast.LENGTH_SHORT).show() })*/

            var size = singleSectionItems.size-1
            itemView.txt_my_chat.setText(messageBot)

            /*Glide.with(mContext)
                .load(feedItem.getImageURL())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .error(R.drawable.bg)
                .into(feedListRowHolder.thumbView);*/
        }
    }

    inner class FriendOneItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(sectionName: String, messageBot: String, singleSectionItems: MutableList<Restaurant>, position: Int){
            itemView.itemOneTitle.setText(sectionName)
            itemView.txtFriendOneChat.setText(messageBot)
            itemView.txtDescripOneChat.setText(singleSectionItems.get(0).getName())

            Glide.with(itemView.context)
                    .load(R.drawable.img_restau)
                    .into(itemView.itemOneImage)

            itemView.btnFriendOneChat.setOnClickListener { v ->  Event.postEvent(EventMessage(EventDefine.CLICK_FRIEND_ITEM_ONE_BUTTON, singleSectionItems.get(0)))}
            itemView.setOnClickListener { v ->  Event.postEvent(EventMessage(EventDefine.CLICK_FRIEND_ITEM_ONE_VIEW, singleSectionItems.get(0)))}
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