package gcam.vn.gbot.view.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import gcam.vn.gbot.R
import gcam.vn.gbot.module.ChatFromServer
import gcam.vn.gbot.module.Restaurant
import kotlinx.android.synthetic.main.item_my_chat.view.*

/**
 * Created by thuythu on 12/01/2018.
 */
class MyChatAdapter: RecyclerView.Adapter<MyChatAdapter.MyChatHolder>{
    private lateinit var itemsList: MutableList<Restaurant>
    private lateinit var mContext: Context

    constructor(context: Context, itemsList: MutableList<Restaurant>){
        this.itemsList = itemsList
        this.mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyChatHolder {
        val v = LayoutInflater.from(parent!!.context).inflate(R.layout.item_my_chat, null)
        return MyChatHolder(v)
    }

    override fun onBindViewHolder(holder: MyChatHolder, position: Int) {
        val myChatItem = itemsList[position]
        holder.bind(myChatItem)
    }

    override fun getItemCount(): Int {
        return if (null != itemsList) itemsList.size else 0
    }

    inner class MyChatHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(myChatItem: Restaurant){
            itemView.txt_my_chat.setText(myChatItem.getName())
            itemView.setOnClickListener { v-> Toast.makeText(v.getContext(), itemView.txt_my_chat.text, Toast.LENGTH_SHORT).show(); }
        }
    }
}