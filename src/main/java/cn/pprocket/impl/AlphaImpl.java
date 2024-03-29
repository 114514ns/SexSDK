package cn.pprocket.impl;

import cn.pprocket.Client;
import cn.pprocket.object.User;
import cn.pprocket.object.Video;
import cn.pprocket.utils.Encrypt;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.Buffer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static cn.pprocket.impl.AlphaImpl.gson;

@Slf4j
public class AlphaImpl implements Client {
    protected static final String DEVICE_TYPE = "A";
    protected static final String VERSION = "1.3.0";
    protected static String device = "";
    protected static String cookie = "";
    protected static final Gson gson = new Gson();
    protected OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(new EncryptInterceptor())
            .build();
    private String domain = "https://k887d9ove.ajdegf.com/";

    @Override
    public void login(String deviceId) {
        device = deviceId;
        LoginModel model = new LoginModel();
        model.setDevice_no(deviceId);
        String json = gson.toJson(model);
        String loginUrl = domain + "app/api/auth/login/device";
        // send a post request
        Request request = new Request.Builder()
                .url(loginUrl)
                .addHeader("Content-Type", "application/json")
                .addHeader("Cookie", cookie)
                .post(RequestBody.create(json.getBytes()))
                .build();
        try {
            String s = client.newCall(request).execute().body().string();
            JsonObject jsonObject = JsonParser.parseString(s).getAsJsonObject();
            AlphaImpl.cookie = jsonObject.getAsJsonObject("auth").get("token").getAsString();
            log.info("Succeed in login");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<Video> getRecommend(int page) {
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPage(page);
        pageInfo.setPage_size(20);
        String url = domain + "app/api/search/list";
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .addHeader("Cookie", cookie)
                .post(RequestBody.create(gson.toJson(pageInfo).getBytes()))
                .build();
        try {
            String string = client.newCall(request).execute().body().string();
            JsonArray array = JsonParser.parseString(string).getAsJsonArray();
            return parser(array);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public List<Video> parser(JsonArray array) {
        List<Video> list = new ArrayList<>();
        array.forEach(jsonElement -> {
            JsonObject object = jsonElement.getAsJsonObject();
            Video video = new Video();
            String id = object.get("id").getAsString();
            String title = object.get("title").getAsString();
            String cover = object.get("cover").getAsString();
            String duration = object.get("duration").getAsString();
            String originLink = object.get("smu").getAsString();
            List<String> tags = new ArrayList<>();
            object.getAsJsonArray("category").forEach(ele -> {
                tags.add(((JsonObject) ele).get("title").getAsString());
            });
            int views = object.get("play").getAsInt();
            video.setTags(tags);
            video.setTitle(title);
            video.setId(id);
            video.setCover(cover);
            video.setLength(Integer.parseInt(duration));
            video.setOriginLink(originLink);
            User user = new User();
            user.setName(object.get("user").getAsJsonObject().get("nick").getAsString());
            user.setAvatar(object.get("user").getAsJsonObject().get("avatar").getAsString());
            user.setFollowers(object.get("user").getAsJsonObject().get("fans").getAsInt());
            video.setAuthor(user);

            video.setTime(object.get("created_at").getAsString());
            list.add(video);
        });
        return list;
    }

    @Override
    public Video getVideo(String id) {
        return null;
    }
    public String getCookie() {
        return cookie;
    }


    @Override
    public String getPlayLink(Video video) {
        String url = "https://parser.ikuntech.xyz/parser?origin=" + video.getOriginLink() + "&&count=" + video.getLength()/10;
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        try {
            return client.newCall(request).execute().body().string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    @Override
    public List<Video> getRelated() {
        return null;
    }

    @SneakyThrows
    @Override
    public List<Video> search(String keyword, int page) {
        SearchBean searchBean = new SearchBean();
        searchBean.setKeywords(keyword);
        searchBean.setPage(String.valueOf(page));
        searchBean.setPage_size("20");
        searchBean.setOrder_key("");
        String url = domain + "app/api/search/list";
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .addHeader("Cookie", cookie)
                .post(RequestBody.create(gson.toJson(searchBean).getBytes()))
                .build();
        String string = client.newCall(request).execute().body().string();
        JsonArray array = JsonParser.parseString(string).getAsJsonArray();
        return parser(array);
    }

    @Override
    public void shutdown() {

    }


}

@Data
class LoginModel {

    private String code = "";
    private String channel = "ajsy01";
    private String device_no;
    private String device_type = "A";
    private String version = "1.3.0";
}
@Data
class PageInfo {
    private int page;
    private int page_size;

}
@NoArgsConstructor
@Data
class TokenBean {

    private String device_no = AlphaImpl.device;
    private String device_type = AlphaImpl.DEVICE_TYPE;
    private String version = AlphaImpl.VERSION;
    private String token;
}

@Data
class DataWrapper {
    private String handshake;
    private String data;
}
@Data
class SearchBean {
    private String order_key;
    private String keywords;
    private String page;
    private String page_size;

}

class EncryptInterceptor implements Interceptor {

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();
        if (request.body() == null) {
            return chain.proceed(request);
        }
        Buffer buffer = new Buffer();
        //assert request.body() != null;
        request.body().writeTo(buffer);
        String s = buffer.readString(StandardCharsets.UTF_8);
        String encrypt = encrypt(s);
        String wrapper = wrapper(encrypt);
        RequestBody requestBody = RequestBody.create(wrapper.getBytes());
        TokenBean tokenBean = new TokenBean();
        tokenBean.setDevice_no(AlphaImpl.device);
        tokenBean.setToken(AlphaImpl.cookie);
        Request.Builder builder = request.newBuilder()
                .addHeader("X-TOKEN", encrypt(gson.toJson(tokenBean)));
        builder.method(request.method(), requestBody);

        Response response = chain.proceed(builder.build());
        String string = response.body().string();
        String unwrap = unwrap(string);
        String decrypt = decrypt(unwrap);
        ResponseBody responseBody = ResponseBody.create(decrypt, response.body().contentType());
        return response.newBuilder().body(responseBody).build();

    }

    public static String wrapper(String string) {
        DataWrapper wrapper = new DataWrapper();
        wrapper.setHandshake("v20200429");
        wrapper.setData(string);
        return gson.toJson(wrapper);
    }

    public static String unwrap(String s) {
        DataWrapper wrapper = gson.fromJson(s, DataWrapper.class);
        return wrapper.getData();
    }

    public static String encrypt(String content) {
        try {
            return Encrypt.encrypt("l8N2iooyp07M9IWa", content);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String decrypt(String content) {
        try {
            return Encrypt.decrypt("l8N2iooyp07M9IWa", content);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}