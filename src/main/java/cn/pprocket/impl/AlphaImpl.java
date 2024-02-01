package cn.pprocket.impl;

import cn.pprocket.Client;
import cn.pprocket.object.Video;
import cn.pprocket.utils.Enctypt;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Data;
import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

public class AlphaImpl implements Client {
    private String cookie = "";
    private static final Gson gson = new Gson();
    private OkHttpClient client = new OkHttpClient();
    private String domain = "https://k887d9ove.ajdegf.com/";
    @Override
    public void login(String deviceId) {
        LoginModel model = new LoginModel();
        model.setDevice_no(deviceId);
        String json = gson.toJson(model);
        String encrypt = encrypt(json);
        String wrapper = wrapper(encrypt);
        String loginUrl = domain + "app/api/auth/login/device";
        // send a post request
        Request request = new Request.Builder()
                .url(loginUrl)
                .addHeader("Content-Type", "application/json")
                .addHeader("Cookie", cookie)
                .post(RequestBody.create(wrapper.getBytes()))
                .build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    String s = response.body().string();
                    String unwrap = unwrap(s);
                    unwrap = decrypt(unwrap);
                    JsonParser parser = new JsonParser();
                    JsonObject jsonObject = JsonParser.parseString(unwrap).getAsJsonObject();
                    String token = jsonObject.getAsJsonObject("auth").get("token").getAsString();
                    System.gc();
                }
            }
        });

    }

    @Override
    public List<Video> getRecommend() {
        return null;
    }

    @Override
    public Video getVideo(String id) {
        return null;
    }
    // AES PKCS7Padding
    public static String encrypt(String content) {
        try {
            return Enctypt.encrypt("l8N2iooyp07M9IWa",content);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String decrypt(String content)  {
        try {
            return Enctypt.decrypt("l8N2iooyp07M9IWa",content);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
class DataWrapper {
    private String handshake;
    private String data;
}