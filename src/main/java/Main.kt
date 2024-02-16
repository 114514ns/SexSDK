package cn.pprocket

import cn.pprocket.cn.pprocket.impl.BilibiliImpl
import cn.pprocket.impl.AlphaImpl
import cn.pprocket.impl.AzureImpl
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.junit.jupiter.api.Test
import java.io.File
import java.io.FileReader
import java.security.Security
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

fun main() {
    Security.addProvider(BouncyCastleProvider())
    val client = BilibiliImpl()
    FileReader(File("cookie.txt")).use {
        client.login(it.readText())
    }
    var recommend = client.getRecommend(1)
    var playLink = client.getPlayLink(recommend[0])
    //client.login("pheuxb7xu|m2zkrg")
    //client.getRecommend(1)
    System.nanoTime()



}
private fun decryptImage(body: ByteArray): ByteArray {
    // 使用AES/ECB/NoPadding解密
    val cipher = Cipher.getInstance("AES/ECB/NoPadding", BouncyCastleProvider.PROVIDER_NAME)
    val key = SecretKeySpec("wPK8CxWaOwPuVzgs".toByteArray(), "AES")
    cipher.init(Cipher.DECRYPT_MODE, key)

    // 读取响应体并解密
    return cipher.doFinal(body)
}


