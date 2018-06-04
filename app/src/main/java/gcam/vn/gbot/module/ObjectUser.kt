package gcam.vn.gbot.module

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


/**
 * Created by thuythu on 11/04/2018.
 */
class ObjectUser: Serializable {

    @SerializedName("pre_topic")
    @Expose
    private var preTopic: String? = null
    @SerializedName("tuvannhahang")
    @Expose
    private var tuvannhahang: Any? = null
    @SerializedName("id")
    @Expose
    private var id: String? = null
    @SerializedName("datban")
    @Expose
    private var datban: DatBan? = null
    @SerializedName("sid")
    @Expose
    private var sid: String? = null
    @SerializedName("thongtincanhan")
    @Expose
    private var thongtincanhan: ThongTinCaNhan? = null
    @SerializedName("pre_keyword")
    @Expose
    private var preKeyword: List<Any>? = null
    @SerializedName("thongtinnhahang")
    @Expose
    private var thongtinnhahang: Any? = null
    @SerializedName("note_text")
    @Expose
    private var noteText: String? = null
    @SerializedName("current_topic")
    @Expose
    private var currentTopic: String? = null
    @SerializedName("current_block_command")
    @Expose
    private var currentBlockCommand: Any? = null

    fun getPreTopic(): String {
        return preTopic ?: ""
    }

    fun setPreTopic(preTopic: String) {
        this.preTopic = preTopic
    }

    fun getTuvannhahang(): Any? {
        return tuvannhahang
    }

    fun setTuvannhahang(tuvannhahang: Any) {
        this.tuvannhahang = tuvannhahang
    }

    fun getId(): String? {
        return id
    }

    fun setId(id: String) {
        this.id = id
    }

    fun getDatban(): DatBan {
        return datban ?: DatBan()
    }

    fun setDatban(datban: DatBan) {
        this.datban = datban
    }

    fun getSid(): String? {
        return sid
    }

    fun setSid(sid: String) {
        this.sid = sid
    }

    fun getThongtincanhan(): ThongTinCaNhan {
        return thongtincanhan ?: ThongTinCaNhan()
    }

    fun setThongtincanhan(thongtincanhan: ThongTinCaNhan) {
        this.thongtincanhan = thongtincanhan
    }

    fun getPreKeyword(): List<Any>? {
        return preKeyword
    }

    fun setPreKeyword(preKeyword: List<Any>) {
        this.preKeyword = preKeyword
    }

    fun getThongtinnhahang(): Any? {
        return thongtinnhahang
    }

    fun setThongtinnhahang(thongtinnhahang: Any) {
        this.thongtinnhahang = thongtinnhahang
    }

    fun getNoteText(): String? {
        return noteText
    }

    fun setNoteText(noteText: String) {
        this.noteText = noteText
    }

    fun getCurrentTopic(): String? {
        return currentTopic
    }

    fun setCurrentTopic(currentTopic: String) {
        this.currentTopic = currentTopic
    }

    fun getCurrentBlockCommand(): Any? {
        return currentBlockCommand
    }

    fun setCurrentBlockCommand(currentBlockCommand: Any) {
        this.currentBlockCommand = currentBlockCommand
    }

}