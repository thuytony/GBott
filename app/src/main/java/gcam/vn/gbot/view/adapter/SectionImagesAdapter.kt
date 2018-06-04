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
import gcam.vn.gbot.manager.ext.LogUtil
import gcam.vn.gbot.module.CustomImage
import gcam.vn.gbot.module.Restaurant
import gcam.vn.gbot.util.Utils
import kotlinx.android.synthetic.main.item_multi_images.view.*

/**
 * Created by thuythu on 07/02/2018.
 */
class SectionImagesAdapter: RecyclerView.Adapter<SectionImagesAdapter.ItemImageHolder>{
    private lateinit var itemsList: MutableList<Restaurant>
    private lateinit var messageBot: String
    private lateinit var mContext: Context

    constructor(context: Context, itemsList: MutableList<Restaurant>, messageBot: String){
        this.messageBot = messageBot
        this.itemsList = itemsList
        this.mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ItemImageHolder {
        val v = LayoutInflater.from(parent!!.context).inflate(R.layout.item_multi_images, null)
        return ItemImageHolder(v)
    }

    override fun onBindViewHolder(holder: ItemImageHolder, position: Int) {
        val singleItem = itemsList[position]
        holder.bind(singleItem, position)
    }

    override fun getItemCount(): Int {
        return if (null != itemsList) itemsList.size else 0
    }

    inner class ItemImageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(singleItem: Restaurant, position: Int){
            //click image show list image to view
            itemView.setOnClickListener {
                var list = arrayListOf<CustomImage>()
                if(itemsList.size > 0) {
                    for (i in itemsList) {
                        list.add(CustomImage("${i.getImages()}", i.getSMenu().toString()))
                    }
                }
                Event.postEvent(EventMessage(EventDefine.CLICK_FRIEND_ITEM_IMAGE, list))
            }
            Utils.loadImageByGlide(itemView, "${singleItem.getImages()}", itemView.itemImage)
        }
    }
}