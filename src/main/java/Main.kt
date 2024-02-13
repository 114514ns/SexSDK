package cn.pprocket

import cn.pprocket.impl.AlphaImpl
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.junit.jupiter.api.Test
import java.io.File
import java.io.FileReader
import java.security.Security
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

fun main() {
    Security.addProvider(BouncyCastleProvider())
    val client = AlphaImpl()
    client.login("7c706e97-108a-3f61-8e07-14da33dea564")
    val recommend = client.getRecommend(15)
    client.getPlayLink(recommend[0])
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


