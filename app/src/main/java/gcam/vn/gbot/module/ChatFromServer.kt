package gcam.vn.gbot.module

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by thuythu on 12/01/2018.
 */
class ChatFromServer : Serializable {
    @SerializedName("answer")
    @Expose
    private var answer:String? = null
    @SerializedName("objectUser")
    @Expose
    private var objectUser:ObjectUser? = null
    @SerializedName("suggestion")
    @Expose
    private var suggestion:Suggestion? = null
    @SerializedName("type")
    @Expose
    private var type:Int? = null

    constructor(answer: String?, objectUser: ObjectUser?, suggestion: Suggestion?, type: Int?){
        this.answer = answer
        this.objectUser = objectUser
        this.suggestion = suggestion
        this.type       = type
    }

    fun setAnswer(answer: String){
        this.answer = answer
    }
    fun getAnswer(): String?{
        return answer
    }

    fun setObjectUser(objectUser: ObjectUser){
        this.objectUser = objectUser
    }
    fun getObjectUser(): ObjectUser?{
        return objectUser
    }

    fun setSuggestion(suggestion: Suggestion){
        this.suggestion = suggestion
    }
    fun getSuggestion(): Suggestion?{
        return suggestion
    }

    fun setType(type: Int){
        this.type = type
    }
    fun getType(): Int?{
        return type
    }
}