package gcam.vn.gbot.module

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by thuythu on 29/01/2018.
 */
class KeyWord: Serializable {
    @SerializedName("key")
    @Expose
    private var key: String? = null
    @SerializedName("content")
    @Expose
    private var content:List<String>? = null
    @SerializedName("type")
    @Expose
    private var type: String? = null

    private var title: String? = null


    constructor()

    constructor(key: String, content: List<String>, type: String){
        this.key = key
        this.content = content
        this.type = type
    }

    fun setKey(key: String){
        this.key = key
    }
    fun getKey(): String?{
        return key
    }

    fun setContent(content: List<String>){
        this.content = content
    }
    fun getContent(): List<String>?{
        return content
    }

    fun setType(type: String){
        this.type = type
    }
    fun getType(): String?{
        return type
    }

    //set noi dung cho text tieu de khi action webview
    fun setTitle(title: String){
        this.title = title
    }
    fun getTitle(): String{
        return title?:""
    }

}