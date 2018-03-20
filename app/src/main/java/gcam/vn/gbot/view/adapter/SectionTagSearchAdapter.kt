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
import gcam.vn.gbot.manager.ext.LogUtil
import gcam.vn.gbot.module.KeyWord
import gcam.vn.gbot.module.ModuleDeleteTagSearch
import kotlinx.android.synthetic.main.item_multi_search.view.*
import java.util.*

/**
 * Created by thuythu on 20/03/2018.
 */
class SectionTagSearchAdapter: RecyclerView.Adapter<SectionTagSearchAdapter.SectionTagSearchRowHolder>{

    private var itemsList: MutableList<KeyWord>? = null
    private lateinit var mContext: Context
    private var positionOfListChat = 1

    constructor(context: Context, itemsList: MutableList<KeyWord>?, positionOfListChat: Int){
        this.itemsList = itemsList
        this.mContext = context
        this.positionOfListChat = positionOfListChat
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SectionTagSearchRowHolder {
        val v = LayoutInflater.from(parent!!.context).inflate(R.layout.item_multi_search, null)
        return SectionTagSearchRowHolder(v)
    }

    override fun onBindViewHolder(holder: SectionTagSearchRowHolder, position: Int) {
        val singleItem = itemsList!![position]
        holder.bind(singleItem, position)
    }

    override fun getItemCount(): Int {
        return if (null != itemsList) itemsList!!.size else 0
    }

    inner class SectionTagSearchRowHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(singleItem: KeyWord, position: Int){
            var random = Random()
            when(random.nextInt(2)+1){
                1 -> {
                    itemView.layout_content.setBackgroundResource(R.drawable.bg_tag_search_one)
                }
                2 -> {
                    itemView.layout_content.setBackgroundResource(R.drawable.bg_tag_search_two)
                }
                3 -> {
                    itemView.layout_content.setBackgroundResource(R.drawable.bg_tag_search_three)
                }
                else -> {
                    itemView.layout_content.setBackgroundResource(R.drawable.bg_tag_search_four)
                }
            }
            itemView.txt_multi_search.setText(singleItem.getKey() ?: "")
            itemView.img_delete_tag.setOnClickListener {
                var deleteTagSearch = ModuleDeleteTagSearch(position, positionOfListChat, singleItem)
                Event.postEvent(EventMessage(EventDefine.CLICK_DELETE_TAG_SEARCH, deleteTagSearch))
            }
        }
    }
}