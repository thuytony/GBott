package gcam.vn.gbot.view.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import gcam.vn.gbot.R
import gcam.vn.gbot.manager.event.Event
import gcam.vn.gbot.manager.event.EventDefine
import gcam.vn.gbot.manager.event.EventMessage
import gcam.vn.gbot.module.KeyWord
import kotlinx.android.synthetic.main.item_multi_action.view.*

/**
 * Created by thuythu on 14/03/2018.
 */
class SectionActionAdapter: RecyclerView.Adapter<SectionActionAdapter.ItemActionHolder>{

    private var itemsListAction: MutableList<KeyWord>? = null
    private lateinit var messageBot: String
    private lateinit var mContext: Context

    constructor(context: Context, itemsListAction: MutableList<KeyWord>?, messageBot: String){
        this.itemsListAction = itemsListAction
        this.messageBot = messageBot
        this.mContext = context
    }

    companion object {
        val TYPE_DAT_BAN  = "1"
        val TYPE_CHI_TIET = "2"
        val TYPE_BAN_DO   = "3"
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SectionActionAdapter.ItemActionHolder {
        val v = LayoutInflater.from(parent!!.context).inflate(R.layout.item_multi_action, null)
        return ItemActionHolder(v)
    }

    override fun onBindViewHolder(holder: SectionActionAdapter.ItemActionHolder, position: Int) {
        val singleItem = itemsListAction!![position]
        holder.bind(singleItem)
    }

    override fun getItemCount(): Int {
        return if (null != itemsListAction) itemsListAction!!.size else 0
    }

    class ItemActionHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(itemAction: KeyWord){
            itemView.txt_one_item_action.setText((itemAction.getKey() ?: ""))
            when(itemAction.getType()){
                TYPE_DAT_BAN -> {
                    itemView.imgAction.setImageResource(R.drawable.ic_datban)
                }
                TYPE_CHI_TIET -> {
                    itemView.imgAction.setImageResource(R.drawable.ic_chitiet)
                }
                TYPE_BAN_DO -> {
                    itemView.imgAction.setImageResource(R.drawable.ic_bando)
                }
            }
            itemView.setOnClickListener { Event.postEvent(EventMessage(EventDefine.CLICK_FRIEND_ITEM_ONE_ACTION, itemAction)) }
        }
    }

}