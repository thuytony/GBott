package gcam.vn.gbot.view.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import gcam.vn.gbot.R
import gcam.vn.gbot.manager.event.Event
import gcam.vn.gbot.manager.event.EventDefine
import gcam.vn.gbot.manager.event.EventMessage
import gcam.vn.gbot.module.Restaurant
import kotlinx.android.synthetic.main.item_multi_restaurants.view.*

/**
 * Created by thuythu on 12/01/2018.
 */
class SectionRestaurantsAdapter : RecyclerView.Adapter<SectionRestaurantsAdapter.SingleItemRowHolder>{
    private lateinit var itemsList: MutableList<Restaurant>
    private lateinit var messageBot: String
    private lateinit var mContext: Context
    private var onClickItemChat: OnClickItemChat? = null

    constructor(context: Context, itemsList: MutableList<Restaurant>, messageBot: String){
        this.messageBot = messageBot
        this.itemsList = itemsList
        this.mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SingleItemRowHolder {
        val v = LayoutInflater.from(parent!!.context).inflate(R.layout.item_multi_restaurants, null)
        return SingleItemRowHolder(v)
    }

    override fun onBindViewHolder(holder: SingleItemRowHolder, position: Int) {
        val singleItem = itemsList[position]
        holder.bind(singleItem, position)
    }

    override fun getItemCount(): Int {
        return if (null != itemsList) itemsList.size else 0
    }

    inner class SingleItemRowHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(singleItem: Restaurant, position: Int){
            itemView.txt_friend_chat.setText(singleItem.getName())
            //itemView.setOnClickListener { v-> Toast.makeText(v.getContext(), itemView.txt_friend_chat.text, Toast.LENGTH_SHORT).show(); }
            itemView.setOnClickListener { if(onClickItemChat!=null) onClickItemChat!!.onClickItemChat(position) }
            itemView.setOnClickListener { Event.postEvent(EventMessage(EventDefine.CLICK_FRIEND_ITEM_VIEW, singleItem)) }
            itemView.btnFriendChat.setOnClickListener { Event.postEvent(EventMessage(EventDefine.CLICK_FRIEND_ITEM_BUTTON, singleItem)) }

            Glide.with(mContext)
                    .load(R.drawable.img_restau)
                    .into(itemView.itemImage)
        }
    }

    interface OnClickItemChat {
        fun onClickItemChat(position: Int)
    }

    fun setOnClickItemChat(onClickItemChat: OnClickItemChat) {
        this.onClickItemChat = onClickItemChat
    }
}