import com.google.gson.JsonParser
import java.io.FileReader

class TestClass {

}
fun main() {
    FileReader("json.txt").use {
        val result = it.readText()
        val array =
            JsonParser.parseString(result).asJsonObject.get("result").asJsonObject.get("comments").asJsonArray
        for (i in 0 until array.size()) {
            val sub = array[i].asJsonObject.getAsJsonArray("comment")
            for (j in 0 until sub.size()) {
                val comment = sub[j].asJsonObject
                val content = comment.get("text").asString

            }
        }
    }
}