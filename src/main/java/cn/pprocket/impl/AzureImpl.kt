package cn.pprocket.impl

import cn.pprocket.Client
import cn.pprocket.`object`.User
import cn.pprocket.`object`.Video
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.math.BigInteger
import java.net.Proxy
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.security.Key
import java.security.MessageDigest
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random

val gson = Gson()
var cookie = ""

class AzureImpl : Client {
    var base = "http://192.74.224.65:8080/mh/"

    val client = OkHttpClient.Builder()
        //.addInterceptor(EncryptInterceptor())
        .proxy(Proxy(Proxy.Type.HTTP, java.net.InetSocketAddress("127.0.0.1", 9000)))
        .addInterceptor(EncryptInterceptor())
        .build()

    override fun login(security: String) {
        val split = security.split("|")
        val username = split[0]
        val password = split[1]
        val loginObject = LoginObject(
            username = username,
            password = password,
            sign = md5("${username}${password}mhIkfewg843"),
            inviteId = ""
        )
        val toJson = gson.toJson(loginObject)
        val encrypt = encrypt(base64(toJson))
        //val encrypt = "Tu T/ibTuOB5VKBTP1EQBZYIEFy6dTVyeMBBU/WcyH6leem8ccKgeHLXhhclBUOsWm6bW8vYSK5zE/0chvxxCptUko9el8/0uyGIHJiIbhkBfWOfqO6R/aiDJ511UgHQ2TrUQBfuc9OxsOrikLNK6/FbyTXrDZO5rAy6hyQwAbC/JtuvuvM7b1PV7koEvteY"
        var url = base + "user/login?prams=${URLEncoder.encode(encrypt)}&type=1&sign=${genSign(toJson)}"
        url =
            "http://192.74.224.65:8080/mh/user/login?type=1&sign=7e84cee816879b16534464e9cd6afd6d&params=Tu T/ibTuOB5VKBTP1EQBZYIEFy6dTVyeMBBU/WcyH6leem8ccKgeHLXhhclBUOsWm6bW8vYSK5zE/0chvxxCptUko9el8/0uyGIHJiIbhkBfWOfqO6R/aiDJ511UgHQ2TrUQBfuc9OxsOrikLNK6/FbyTXrDZO5rAy6hyQwAbC/JtuvuvM7b1PV7koEvteY"
        //send request
        val request = Request.Builder().url(url).post(RequestBody.create("application/json".toMediaType(), "")).build()
        val response = client.newCall(request).execute()
        val body = response.body.string()
        val token = JsonParser.parseString(body).asJsonObject.get("token").asString
        cookie = token
        println(body)
    }

    override fun getRecommend(page: Int): List<Video> {
        val params = "id=1004&userId=1039689&page=${page}&clickNum=1"
        val url = base + "video/getHotList?params=${encrypt(base64(params))}&type=0&sign=${genSign(params)}"
        var request = Request.Builder()
            .url(url)
            .post(RequestBody.create("application/json".toMediaType(), ""))
            .build()
        var string = client.newCall(request).execute().body.string()
        var fromJson = gson.fromJson(string, VideoList::class.java)
        val list = mutableListOf<Video>()
        fromJson.list.forEach {
            var element = it.asJsonObject
            if (!element.has("ad")) {
                val title = element.get("intro").asString
                val image = "http://img.qianniuhua088.top/" + element.get("faceUrl").asString
                val id = element.get("id").asString
                val tags = mutableListOf("")
                element.get("tag").asString.split(",").forEach {
                    tags.add(it)
                }
                val author = User()
                author.name = element.get("nickName").asString
                author.followers = Random.nextInt(25, 8000)
                val plays = element.get("showNum").asInt
                val length = element.get("videoTime").asInt
                val video = Video()
                video.id = Integer.decode(id)
                video.title = title
                video.cover = image
                video.author = author
                video.views = plays
                video.length = length
                video.tags = tags
                list.add(video)
            }

        }
        return list
    }

    override fun getVideo(id: String): Video {
        return Video()
    }

    override fun getPlayLink(video: Video): String {
        return ""
    }


    override fun getRelated(): MutableList<Video> {
        TODO("Not yet implemented")
    }

    override fun search(keyword: String?, page: Int): MutableList<Video> {
        TODO("Not yet implemented")
    }


    private fun genSign(params: String): String {
        println("Main.genSign   $params")

        val result: String = md5(Base64.getEncoder().encodeToString(params.toByteArray()) + "mhIkfewg843")
        println("Main.genSign  $result")
        return result
    }

    fun base64(input: String): String {
        return Base64.getEncoder().encodeToString(input.toByteArray())
    }

    private fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        val messageDigest = md.digest(input.toByteArray())
        val no = BigInteger(1, messageDigest)
        var hashtext = no.toString(16)
        while (hashtext.length < 32) {
            hashtext = "0$hashtext"
        }
        return hashtext
    }

    fun encrypt(text: String): String {
        val key = "jVdUx90vPHyLKb975d7NkjRoNcKWLump"
        val iv = "o923Gz0REK8KHlMc"
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val secretKey: Key = SecretKeySpec(key.toByteArray(), "AES")
        val ivSpec = IvParameterSpec(iv.toByteArray())
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)

        val encryptedBytes = cipher.doFinal(text.toByteArray(StandardCharsets.UTF_8))
        return Base64.getEncoder().encodeToString(encryptedBytes)
    }

    inner class EncryptInterceptor : okhttp3.Interceptor {
        override fun intercept(chain: okhttp3.Interceptor.Chain): okhttp3.Response {
            // 添加拦截器
            val request = chain.request()
            val newBuilder = request.newBuilder()

                .addHeader("Content-Type", "application/json;charset=utf-8")
                .addHeader("androidId", "ffffffff-a197-1a34-a197-1a3400000000")
                .addHeader("versionCode", "165")
                .addHeader("token", cookie)
                .build()
            val response = chain.proceed(newBuilder)
            var string = response.body.string();
            string = string.replace("\"", "")
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            val keyspec = SecretKeySpec("jVdUx90vPHyLKb975d7NkjRoNcKWLump".toByteArray(), "AES")
            val ivspec = IvParameterSpec("o923Gz0REK8KHlMc".toByteArray())

            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec)

            val original = cipher.doFinal(Base64.getDecoder().decode(string))
            val fromJson = gson.fromJson(String(original), WrapBean::class.java)
            val response1 =
                response.newBuilder()
                    .body(okhttp3.ResponseBody.create(response.body.contentType(), fromJson.data.toString())).build()
            return response1
        }
    }


}

class LoginObject(val inviteId: String, val username: String, val password: String, val sign: String)

class VideoList(val currentPage: Int, val list: JsonArray, val totalCount: Int, val totalPage: Int)

class WrapBean(val code: Int, val data: JsonObject, val msg: String)

fun decrypt(string: String): String {
    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    val keyspec = SecretKeySpec("jVdUx90vPHyLKb975d7NkjRoNcKWLump".toByteArray(), "AES")
    val ivspec = IvParameterSpec("o923Gz0REK8KHlMc".toByteArray())

    cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec)

    val string1 = string.replace("\"", "")
    val decryptedBytes = cipher.doFinal(string1.toByteArray())
    return String(decryptedBytes)
}