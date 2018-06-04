package gcam.vn.gbot.view.adapter

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
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
import gcam.vn.gbot.manager.ext.SimpleToast
import gcam.vn.gbot.module.Restaurant
import gcam.vn.gbot.util.Utils
import kotlinx.android.synthetic.main.item_multi_restaurants.view.*

/**
 * Created by thuythu on 12/01/2018.
 */
class SectionRestaurantsAdapter : RecyclerView.Adapter<SectionRestaurantsAdapter.SingleItemRowHolder>{
    private lateinit var itemsList: MutableList<Restaurant>
    private lateinit var messageBot: String
    private lateinit var mContext: Context
    //private var onClickItemChat: OnClickItemChat? = null

    //load more
    var isLoading: Boolean = false
    private val visibleThreshold = 2
    private var lastVisibleItem: Int = 0
    private var totalItemCount:Int = 0
    private var onLoadingMore: OnLoadingMoreListener? = null

    constructor()

    constructor(context: Context, itemsList: MutableList<Restaurant>, messageBot: String, rec: RecyclerView){
        this.messageBot = messageBot
        this.itemsList = itemsList
        this.mContext = context

        rec.getLayoutManager().let {
            val linearLayoutManager = rec.getLayoutManager() as LinearLayoutManager
            rec.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    totalItemCount = linearLayoutManager.itemCount
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()

                    if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        onLoadingMore.let {
                            onLoadingMore?.onLoadingMore()
                            LogUtil.d("LOAD_MORE", "on loading more restaurant adapter ${totalItemCount} ${lastVisibleItem} ${visibleThreshold} ${isLoading}")
                        }
                        isLoading = true
                    }

                    if (!rec.canScrollHorizontally(1)) {
                        SimpleToast.showInfo(mContext, "Hãy cung cấp thêm thông tin để mình gợi ý tốt hơn nhé :)")
                    }
                }
            })
        }
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
            Utils.loadHtml(itemView.txtAboutPrice, "$$$: (${singleItem.getPriceMin()} - ${singleItem.getPriceMax()}) | ", "${singleItem.getNdtaitro()}")
            itemView.txtAddressRestaurant.setText("${singleItem.getAddress()}")
            itemView.txtChuyenMon.setText("${singleItem.getChuyenMon()}")
            //itemView.setOnClickListener { if(onClickItemChat!=null) onClickItemChat!!.onClickItemChat(position) }
            itemView.setOnClickListener { Event.postEvent(EventMessage(EventDefine.CLICK_FRIEND_ITEM_VIEW, singleItem)) }

            if(singleItem.getStatus() == 1){
                itemView.btnFriendChat.visibility = View.VISIBLE
                itemView.btnFriendChat.setOnClickListener { Event.postEvent(EventMessage(EventDefine.CLICK_FRIEND_ITEM_BUTTON, singleItem)) }
            }else{
                itemView.btnFriendChat.visibility = View.GONE
            }

            itemView.btnDetailRestaurants.setOnClickListener { Event.postEvent(EventMessage(EventDefine.CLICK_FRIEND_ITEM_DETAILS_RES, singleItem)) }
            itemView.btnMenu.setOnClickListener { Event.postEvent(EventMessage(EventDefine.CLICK_FRIEND_ITEM_MENU_RES, singleItem)) }

            Utils.loadImageByGlide(itemView, "${singleItem.getAvatar()}", itemView.itemImage)
        }
    }

    /*interface OnClickItemChat {
        fun onClickItemChat(position: Int)
    }
    fun setOnClickItemChat(onClickItemChat: OnClickItemChat) {
        this.onClickItemChat = onClickItemChat
    }*/

    interface  OnLoadingMoreListener{
        fun onLoadingMore()
    }
    fun setOnLoadingMore(onLoadingMore: OnLoadingMoreListener){
        this.onLoadingMore = onLoadingMore
    }
    fun setLoader(){
        isLoading = false
    }
}