package cn.pprocket.cn.pprocket.impl

import cn.pprocket.Client
import cn.pprocket.`object`.BiliVideo
import cn.pprocket.`object`.User
import cn.pprocket.`object`.Video
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import okhttp3.*
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class BilibiliImpl : Client {
    var cookie = ""
    val client = OkHttpClient.Builder()
        .addInterceptor(HeaderInterceptor())
        .build()
    val gson = Gson()
    var img = ""
    var sub = ""
    private val mappings = intArrayOf(
        46, 47, 18, 2, 53, 8, 23, 32, 15, 50, 10, 31, 58, 3, 45, 35, 27, 43, 5, 49,
        33, 9, 42, 19, 29, 28, 14, 39, 12, 38, 41, 13, 37, 48, 7, 16, 24, 55, 40,
        61, 26, 17, 0, 1, 60, 51, 30, 4, 22, 25, 54, 21, 56, 59, 6, 63, 57, 62, 11,
        36, 20, 34, 44, 52
    )

    override fun login(cookie: String) {
        this.cookie = cookie
        val url = "https://api.bilibili.com/x/web-interface/nav"
        val request = Request.Builder()
            .url(url)
            .get()
            .build()
        val response = client.newCall(request).execute()
        val string = response.body.string()
        val jsonObject = JsonParser.parseString(string).asJsonObject
        img = jsonObject.get("wbi_img").asJsonObject.get("img_url").asString
        sub = jsonObject.get("wbi_img").asJsonObject.get("sub_url").asString
        val md5Regex = Regex("[0-9a-fA-F]{32}")
        img = md5Regex.find(img)!!.value
        sub = md5Regex.find(sub)!!.value
    }

    override fun getRecommend(pgae: Int): MutableList<Video> {
        val lists = mutableListOf<Video>()
        var request = Request.Builder()
            .url("https://api.bilibili.com/x/web-interface/wbi/index/top/feed/rcmd")
            .get()
            .build()
        var string = client.newCall(request).execute().body.string()
        JsonParser.parseString(string).asJsonObject.get("item").asJsonArray.forEach {
            val video = BiliVideo()
            video.title = it.asJsonObject.get("title").asString
            video.cover = it.asJsonObject.get("pic").asString.replace("http","https")
            video.id = it.asJsonObject.get("bvid").asString
            val stat = it.asJsonObject.get("stat").asJsonObject
            video.views = stat.get("view").asInt
            val author = User()
            video.author = author
            video.author.name = it.asJsonObject.get("owner").asJsonObject.get("name").asString
            video.author.followers = Random.nextInt(25, 8000)
            video.author.avatar = it.asJsonObject.get("owner").asJsonObject.get("face").asString
            video.time = convertTime(it.asJsonObject.get("pubdate").asLong * 1000)
            video.cid = it.asJsonObject.get("cid").asString
            video.length = it.asJsonObject.get("duration").asInt
            lists.add(video)
        }
        return lists
    }

    override fun getVideo(id: String?): Video {
        TODO("Not yet implemented")
    }

    override fun getPlayLink(video: Video?): String {
        video as BiliVideo
        val url = "https://api.bilibili.com/x/player/playurl?bvid=${video.id}&cid=${video.cid}"
        val request = Request.Builder()
            .url(url)
            .get()
            .build()
        val response = client.newCall(request).execute()
        val body = response.body.string()
        val jsonObject = JsonParser.parseString(body).asJsonObject
        return jsonObject.get("durl").asJsonArray[0].asJsonObject.get("url").asString
    }

    override fun getRelated(): MutableList<Video> {
        TODO("Not yet implemented")
    }

    override fun search(keyword: String?, page: Int): MutableList<Video> {
        TODO("Not yet implemented")
    }

    override fun shutdown() {
        TODO("Not yet implemented")
    }

    private fun convertTime(timeStamp: Long): String {

        val date = Date(timeStamp)
        val pattern = "yyyy-MM-dd HH:mm:ss"
        val simpleDateFormat = SimpleDateFormat(pattern)
        val dateStr = simpleDateFormat.format(date)
        return dateStr
    }

    inner class HeaderInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request().newBuilder()
                .addHeader("cookie", cookie)
                .addHeader(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36"
                )
                .addHeader("Referer", "https://www.bilibili.com/")

            val response = chain.proceed(request.build())
            val newResponse = response.newBuilder()
            var string = response.body.string()
            if (!string.contains("stat_zhuanfa")) {
                val fromJson = gson.fromJson(string, WrapBean::class.java)
                newResponse.body(ResponseBody.create(response.body.contentType(), fromJson.data.toString()))
            }

            return newResponse.build()
        }

        fun getMixinKey(imgKey: String, subKey: String): String {
            val s = imgKey + subKey
            val key = StringBuilder()
            for (i in 0..31) {
                key.append(s[mappings[i]])
            }
            return key.toString()
        }
    }
}

class WrapBean(val code: Int, val message: String, val ttl: Int, val data: JsonObject)