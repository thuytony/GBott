package gcam.vn.gbot.view.adapter

import android.content.Context
import android.content.Intent
import android.os.Handler
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
import gcam.vn.gbot.manager.ext.RxUtil
import gcam.vn.gbot.manager.ext.SimpleToast
import gcam.vn.gbot.manager.rest.RestBuilder
import gcam.vn.gbot.module.CustomImage
import gcam.vn.gbot.module.KeyWord
import gcam.vn.gbot.module.LoadMoreRestaurant
import gcam.vn.gbot.module.Restaurant
import gcam.vn.gbot.util.Utils
import gcam.vn.gbot.view.fragment.ChatDataModel
import kotlinx.android.synthetic.main.item_friend_text.view.*
import kotlinx.android.synthetic.main.item_my_chat.view.*
import kotlinx.android.synthetic.main.item_friend_multi_data.view.*
import kotlinx.android.synthetic.main.item_friend_one_image.view.*
import kotlinx.android.synthetic.main.item_friend_restaurant.view.*
import rx.Observable
import rx.Observer
import java.util.concurrent.Callable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.internal.operators.OperatorReplay.observeOn



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
            else -> {
                val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_friend_text, null)
                return FriendChatHolder(v)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val sectionName = dataList.get(position).getHeaderTitle()
        val messageBot = dataList.get(position).getMessageBot()
        val singleSectionItems = dataList.get(position).getAllItemsInSection()
        val listAction = dataList.get(position).getListAction()

        //only show keyword, tag when it's last item
        var contentKeyword: MutableList<KeyWord>? = arrayListOf()
        var tagSearch: MutableList<KeyWord> = arrayListOf()
        if(position == dataList.size-1){
            contentKeyword = dataList.get(position).getContentKeyWord()
            tagSearch = dataList.get(position).getTagView()
        }

        when(getItemViewType(position)){
            TYPE_RESTAURANTS -> {
                val restaurantsHolder = holder as RestaurantsHolder
                restaurantsHolder.bind(sectionName!!, messageBot!!, singleSectionItems!!, contentKeyword, tagSearch, position)
            }
            TYPE_IMAGES -> {
                val itemImagesHolder = holder as ItemImagesHolder
                itemImagesHolder.bind(sectionName!!, messageBot!!, singleSectionItems!!, contentKeyword, tagSearch, position)
            }
            TYPE_RESTAURANT -> {
                val oneRestaurantHolder = holder as OneRestaurantHolder
                oneRestaurantHolder.bind(sectionName!!, messageBot!!, singleSectionItems!!, contentKeyword, listAction, tagSearch, position)
            }
            TYPE_FRIEND_TEXT -> {
                val friendChatHolder = holder as FriendChatHolder
                friendChatHolder.bind(sectionName!!, messageBot!!, singleSectionItems!!, contentKeyword, tagSearch, position)
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
                itemImagesOneItemHolder.bind(sectionName!!, messageBot!!, singleSectionItems!!, contentKeyword, tagSearch, position)
            }
            else -> {
                val friendChatHolder = holder as FriendChatHolder
                friendChatHolder.bind(sectionName!!, messageBot!!, singleSectionItems!!, contentKeyword, tagSearch, position)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return dataList.get(position).getType()?: TYPE_FRIEND_TEXT
    }

    override fun getItemCount(): Int {
        return if (null != dataList) dataList.size else 0
    }

    //only text
    inner class FriendChatHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(sectionName: String, messageBot: String, singleSectionItems: MutableList<Restaurant>, contentKeyword :MutableList<KeyWord>?, tagSearch: MutableList<KeyWord>, position: Int){
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

            //set tag view search
            tagSearch.let {
                var itemListTagFrTextAdapter = SectionTagSearchAdapter(mContext, tagSearch, position)
                itemView.tagViewFriendChat.setHasFixedSize(true)
                itemView.tagViewFriendChat.setLayoutManager(LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false))
                itemView.tagViewFriendChat.setAdapter(itemListTagFrTextAdapter)
            }
        }
    }

    inner class RestaurantsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(sectionName: String, messageBot: String, singleSectionItems: MutableList<Restaurant>, contentKeyword :MutableList<KeyWord>?, tagSearch: MutableList<KeyWord>, position: Int){
            //var PAGE_NUMBER = 1
            itemView.itemTitle.setText(sectionName)
            itemView.txt_answer.setText(messageBot)
            itemView.recycler_view_list.setHasFixedSize(true)
            itemView.recycler_view_list.setLayoutManager(LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false))
            var itemListDataAdapter = SectionRestaurantsAdapter(mContext, singleSectionItems, messageBot, itemView.recycler_view_list)
            itemView.recycler_view_list.setAdapter(itemListDataAdapter)

            //set load more restaurant
            itemListDataAdapter.setOnLoadingMore(object: SectionRestaurantsAdapter.OnLoadingMoreListener{
                override fun onLoadingMore() {
                    if(dataList.get(position).getPageNumber() > 1){
                        LogUtil.d("LOAD_MORE", "ko cho load them ${dataList.get(position).getPageNumber()}")
                    }else{
                        dataList.get(position).setPageNumber(dataList.get(position).getPageNumber()+1)
                        LogUtil.d("LOAD_MORE", "on loading more restaurant ${dataList.get(position).getPageNumber()}")
                        postPageRestaurant(dataList.get(position)?.getSql()?:"", "${dataList.get(position).getPageNumber()}", itemListDataAdapter, position)
                    }
                }
            })

            //set suggesstion
            contentKeyword.let {
                var itemListSeggesstionAdapter = SectionSuggesstionAdapter(mContext, contentKeyword, messageBot)
                itemView.recycler_view_suggesstion_res.setHasFixedSize(true)
                itemView.recycler_view_suggesstion_res.setLayoutManager(LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false))
                itemView.recycler_view_suggesstion_res.setAdapter(itemListSeggesstionAdapter)
            }

            //set tag view search
            tagSearch.let {
                var itemListTagFrTextAdapter = SectionTagSearchAdapter(mContext, tagSearch, position)
                itemView.tagViewMulti.setHasFixedSize(true)
                itemView.tagViewMulti.setLayoutManager(LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false))
                itemView.tagViewMulti.setAdapter(itemListTagFrTextAdapter)
            }
        }
    }

    //load image restaurant
    inner class ItemImagesHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(sectionName: String, messageBot: String, singleSectionItems: MutableList<Restaurant>, contentKeyword :MutableList<KeyWord>?, tagSearch: MutableList<KeyWord>, position: Int){
            itemView.itemTitle.setText(sectionName)
            itemView.txt_answer.setText(messageBot)
            var itemSuggessionAdapter = SectionImagesAdapter(mContext, singleSectionItems, messageBot)
            itemView.recycler_view_list.setHasFixedSize(true)
            itemView.recycler_view_list.setLayoutManager(LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false))
            itemView.recycler_view_list.setAdapter(itemSuggessionAdapter)

            //set suggesstion
            contentKeyword.let{
                var itemListSeggesstionAdapter = SectionSuggesstionAdapter(mContext, contentKeyword, messageBot)
                itemView.recycler_view_suggesstion_res.setHasFixedSize(true)
                itemView.recycler_view_suggesstion_res.setLayoutManager(LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false))
                itemView.recycler_view_suggesstion_res.setAdapter(itemListSeggesstionAdapter)
            }

            //set tag view search
            tagSearch.let {
                var itemListTagFrTextAdapter = SectionTagSearchAdapter(mContext, tagSearch, position)
                itemView.tagViewMulti.setHasFixedSize(true)
                itemView.tagViewMulti.setLayoutManager(LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false))
                itemView.tagViewMulti.setAdapter(itemListTagFrTextAdapter)
            }
        }
    }

    //when have one image
    inner class ItemImagesOneItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(sectionName: String, messageBot: String, singleSectionItems: MutableList<Restaurant>, contentKeyword :MutableList<KeyWord>?, tagSearch: MutableList<KeyWord>, position: Int){
            itemView.itemTitleOneImage.setText(sectionName)
            itemView.txt_answer_one_image.setText(messageBot)

            singleSectionItems.let{
                        if(singleSectionItems.size > 0){
                            Glide.with(mContext)
                                    .load("${singleSectionItems.get(0).getImages()}")
                                    .into(itemView.img_menu)
                        }
                    }

            //click image show list view image
            itemView.setOnClickListener {
                if (singleSectionItems.size > 0) {
                    var list = arrayListOf<CustomImage>()
                    list.add(CustomImage("${singleSectionItems.get(0).getImages()}", singleSectionItems.get(0).getSMenu().toString()))
                    Event.postEvent(EventMessage(EventDefine.CLICK_FRIEND_ITEM_IMAGE, list))
                }
            }

            //set suggesstion
            contentKeyword.let{
                var itemListSeggesstionAdapter = SectionSuggesstionAdapter(mContext, contentKeyword, messageBot)
                itemView.recycler_view_suggesstion_one_image.setHasFixedSize(true)
                itemView.recycler_view_suggesstion_one_image.setLayoutManager(LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false))
                itemView.recycler_view_suggesstion_one_image.setAdapter(itemListSeggesstionAdapter)
            }

            //set tag view search
            tagSearch.let {
                var itemListTagFrTextAdapter = SectionTagSearchAdapter(mContext, tagSearch, position)
                itemView.tagViewOneImage.setHasFixedSize(true)
                itemView.tagViewOneImage.setLayoutManager(LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false))
                itemView.tagViewOneImage.setAdapter(itemListTagFrTextAdapter)
            }
        }
    }

    //my chat
    inner class MyChatHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(sectionName: String, messageBot: String, singleSectionItems: MutableList<Restaurant>){
            itemView.itemMyTitle.setText(sectionName)
            var size = singleSectionItems.size-1
            itemView.txt_my_chat.setText(messageBot)
        }
    }

    //one restaurant
    inner class OneRestaurantHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(sectionName: String, messageBot: String, singleSectionItems: MutableList<Restaurant>, contentKeyword :MutableList<KeyWord>?, listAction :MutableList<KeyWord>, tagSearch: MutableList<KeyWord>, position: Int){
            itemView.itemOneTitle.setText(sectionName)
            itemView.txtFriendOneChat.setText(messageBot)
            itemView.txtDescripOneChat.setText(singleSectionItems.get(0).getName())

            //itemView.txtAboutPriceOne.setText("$$$: (${singleSectionItems.get(0).getPriceMin()} - ${singleSectionItems.get(0).getPriceMax()}) | ${singleSectionItems.get(0).getNdtaitro()}")
            Utils.loadHtml(itemView.txtAboutPriceOne, "$$$: (${singleSectionItems.get(0).getPriceMin()} - ${singleSectionItems.get(0).getPriceMax()}) | ", "${singleSectionItems.get(0).getNdtaitro()}")
            itemView.txtAddressRestaurantOne.setText("${singleSectionItems.get(0).getAddress()}")
            itemView.txtChuyenMonOne.setText("${singleSectionItems.get(0).getChuyenMon()}")

            //load image tu avatar
            Utils.loadImageByGlide(itemView, "${singleSectionItems.get(0).getAvatar()}", itemView.itemOneImage)

            //load list action of one restaurant
            listAction.let {
                //set ten tieu de khi action
                for(i in listAction){
                    i.setTitle(singleSectionItems.get(0).getName()?:"Chi tiết nhà hàng")
                }
                var itemListActionAdapter = SectionActionAdapter(mContext, listAction, messageBot)
                itemView.rcActionOneRestau.setHasFixedSize(true)
                itemView.rcActionOneRestau.setLayoutManager(LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false))
                itemView.rcActionOneRestau.setAdapter(itemListActionAdapter)

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

            //set tag view search
            tagSearch.let {
                var itemListTagFrTextAdapter = SectionTagSearchAdapter(mContext, tagSearch, position)
                itemView.tagViewOneRes.setHasFixedSize(true)
                itemView.tagViewOneRes.setLayoutManager(LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false))
                itemView.tagViewOneRes.setAdapter(itemListTagFrTextAdapter)
            }
        }
    }

    //item wait bot feedback
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

    //load more restaurant
    fun postPageRestaurant(sql: String, p: String, itemListDataAdapter: SectionRestaurantsAdapter, position: Int){
        RxUtil.applyHandlerStartFinish(RestBuilder.api().postPageRestaurant(sql, p),
                Runnable {  },
                Runnable {  })
                .compose(RxUtil.applyMain())
                .subscribe(
                        {
                            response ->
                            run {
                                response.data.let {
                                    LogUtil.e("GET_MORE_RESTAURANT", response.data.toString())
                                    if(response.data!=null){
                                        var listRestaurant: MutableList<LoadMoreRestaurant> = response.data!!
                                        if(dataList.get(position).getType()==1){
                                            for(i in listRestaurant){
                                                dataList.get(position).addItemsInSection(Restaurant(i.getName(), i.getLink(), i.getAddress(), i.getAvatar()))
                                            }
                                        }
                                        itemListDataAdapter.setLoader()
                                        itemListDataAdapter.notifyDataSetChanged()
                                    }
                                }
                            }
                        },
                        {t ->
                            LogUtil.e("GET_MORE_RESTAURANT", t.message!!)
                            SimpleToast.showShort(t.message!!)
                        }
                )
    }
}