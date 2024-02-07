package cn.pprocket.impl

import cn.pprocket.Client
import cn.pprocket.`object`.Video

class AzureImpl : Client {
    override fun login(deviceId: String) {
        println("AzureImpl login")
    }

    override fun getRecommend(): List<Video> {
        return listOf()
    }

    override fun getVideo(id: String): Video {
        return Video()
    }

    override fun getPlayLink(video: Video): String {
        return ""
    }


    override fun getCookie(): String {
        TODO("Not yet implemented")
    }

    override fun getRelated(): MutableList<Video> {
        TODO("Not yet implemented")
    }

    protected fun buildURL(): String {
        return ""
    }
}
