package gcam.vn.gbot.view.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import gcam.vn.gbot.R
import gcam.vn.gbot.manager.event.Event
import gcam.vn.gbot.manager.event.EventDefine
import gcam.vn.gbot.manager.event.EventMessage
import gcam.vn.gbot.module.Restaurant
import kotlinx.android.synthetic.main.list_single_card.view.*

/**
 * Created by thuythu on 12/01/2018.
 */
class SectionSuggesstionAdapter : RecyclerView.Adapter<SectionSuggesstionAdapter.SingleItemRowHolder>{
    private lateinit var itemsList: MutableList<Restaurant>
    private lateinit var messageBot: String
    private lateinit var mContext: Context

    constructor(context: Context, itemsList: MutableList<Restaurant>, messageBot: String){
        this.itemsList = itemsList
        this.messageBot = messageBot
        this.mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SingleItemRowHolder {
        val v = LayoutInflater.from(parent!!.context).inflate(R.layout.list_single_card_le, null)
        return SingleItemRowHolder(v)
    }

    override fun onBindViewHolder(holder: SingleItemRowHolder, position: Int) {
        val singleItem = itemsList[position]
        holder.bind(singleItem)
    }

    override fun getItemCount(): Int {
        return if (null != itemsList) itemsList.size else 0
    }

    inner class SingleItemRowHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(singleItem: Restaurant){
            itemView.txt_friend_chat.setText(singleItem.getName())
            //itemView.setOnClickListener { v-> Toast.makeText(v.getContext(), itemView.txt_friend_chat.text, Toast.LENGTH_SHORT).show(); }
            itemView.setOnClickListener { Event.postEvent(EventMessage(EventDefine.CLICK_SUGGESTION, singleItem)) }
        }
    }
}