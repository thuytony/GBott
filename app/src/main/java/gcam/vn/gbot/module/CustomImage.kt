package gcam.vn.gbot.module

/**
 * Created by thuythu on 08/02/2018.
 */
class CustomImage{
    private val url: String
    private val description: String

    constructor(url: String, description: String) {
        this.url = url
        this.description = description
    }

    fun getUrl(): String {
        return url
    }

    fun getDescription(): String {
        return description
    }
}