package cn.pprocket

import cn.pprocket.impl.AlphaImpl
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security



fun main() {
    Security.addProvider(BouncyCastleProvider())
    var client = AlphaImpl()
    client.login("7c706e97-108a-3f61-8e07-14da33dea564")
}