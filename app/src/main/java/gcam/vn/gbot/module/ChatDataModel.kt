package gcam.vn.gbot.view.fragment

import gcam.vn.gbot.module.ChatFromServer
import gcam.vn.gbot.module.KeyWord
import gcam.vn.gbot.module.Restaurant

/**
 * Created by thuythu on 11/01/2018.
 */
class ChatDataModel{

    private var headerTitle: String? = null
    private var messageBot: String? = null
    private var allItemsInSection: MutableList<Restaurant>? = null
    private var type: Int? = null
    private var contentKeyword :MutableList<KeyWord>? = null
    private var listAction: MutableList<KeyWord> = arrayListOf()
    private var sql: String = ""
    private var pageNumber = 1
    private var tagView: MutableList<KeyWord>? = null


    constructor(){

    }

    constructor(headerTitle: String, messageBot: String, allItemsInSection: ArrayList<Restaurant>){
        this.headerTitle = headerTitle
        this.messageBot = messageBot
        this.allItemsInSection = allItemsInSection
    }


    fun getHeaderTitle(): String? {
        return headerTitle
    }

    fun setHeaderTitle(headerTitle: String) {
        this.headerTitle = headerTitle
    }

    fun getMessageBot(): String? {
        return messageBot
    }

    fun setMessageBot(messageBot: String) {
        this.messageBot = messageBot
    }

    fun getAllItemsInSection(): MutableList<Restaurant>? {
        return allItemsInSection
    }

    fun setAllItemsInSection(allItemsInSection: MutableList<Restaurant>) {
        this.allItemsInSection = allItemsInSection
    }

    fun addItemsInSection(restaurant: Restaurant){
        this.allItemsInSection!!.add(restaurant)
    }

    fun getType(): Int? {
        return type
    }

    fun setType(type: Int) {
        this.type = type
    }

    fun setContentKeyWord(contentKeyword: MutableList<KeyWord>?){
        this.contentKeyword = contentKeyword
    }
    fun getContentKeyWord(): MutableList<KeyWord>? {
        return contentKeyword
    }

    fun setListAction(listAction: MutableList<KeyWord>){
        this.listAction = listAction
    }
    fun getListAction(): MutableList<KeyWord> {
        return listAction
    }

    fun setSql(sql: String){
        this.sql = sql
    }
    fun getSql(): String?{
        return sql
    }

    fun setPageNumber(pageNumber: Int){
        this.pageNumber = pageNumber
    }
    fun getPageNumber(): Int{
        return pageNumber
    }

    fun setTagView(tagView: MutableList<KeyWord>){
        this.tagView = tagView
    }
    fun getTagView(): MutableList<KeyWord>{
        return tagView?: arrayListOf()
    }
}