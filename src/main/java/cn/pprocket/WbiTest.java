package cn.pprocket;


import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class WbiTest {
    private static final int[] mixinKeyEncTab = new int[]{
            46, 47, 18, 2, 53, 8, 23, 32, 15, 50, 10, 31, 58, 3, 45, 35, 27, 43, 5, 49,
            33, 9, 42, 19, 29, 28, 14, 39, 12, 38, 41, 13, 37, 48, 7, 16, 24, 55, 40,
            61, 26, 17, 0, 1, 60, 51, 30, 4, 22, 25, 54, 21, 56, 59, 6, 63, 57, 62, 11,
            36, 20, 34, 44, 52
    };

    public static String getMixinKey(String imgKey, String subKey) {
        String s = imgKey + subKey;
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            key.append(s.charAt(mixinKeyEncTab[i]));
        }
        return key.toString();
    }

    public static void main(String[] args) throws MalformedURLException {
        String imgKey = "7cd084941338484aae1ad9425b84077c";
        String subKey = "4932caff0ff746eab6f01bf08b70ac45";
        String mixinKey = getMixinKey(imgKey, subKey);
        System.out.println(mixinKey);
        String urlString =
                "https://api.bilibili.com/x/web-interface/wbi/search/all/v2?__refresh__=true&_extra=&context=&page=1&page_size=42&order=&duration=&from_source=&from_spmid=333.337&platform=pc&highlight=1&single_column=0&keyword=刺客信条&qv_id=t67Tro0nuLc8iPFKovC3Lz8QhEWjiuQP&ad_resource=5646&source_tag=3&web_location=1430654&w_rid=c5990fc80d5c3d4fc96f3d20578e0126&wts=1708078699";
        Map<String, Object> map;
        URL url = new URL(urlString);
        map = parseQueryParameters(url);
        map.put("wts", 1708078699);
        StringJoiner param = new StringJoiner("&");

        map.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> param.add(entry.getKey() + "=" + URLEncoder.encode(entry.getValue().toString())));
        String s = param + mixinKey;
        String wbiSign = md5(s);
        System.out.println(wbiSign);
        String finalParam = param + "&w_rid=" + wbiSign;
        System.out.println(finalParam);
    }

    static String md5(String s) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] digest = messageDigest.digest(s.getBytes());
        BigInteger bigInt = new BigInteger(1, digest);
        return String.format("%032x", bigInt);
    }

    private static Map<String, Object> parseQueryParameters(URL url) {
        Map<String, Object> paramMap = new LinkedHashMap<>();

        String query = url.getQuery();
        if (query != null) {
            String[] queryParams = query.split("&");

            for (String param : queryParams) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2) {
                    paramMap.put(keyValue[0], keyValue[1]);
                }
            }
        }

        return paramMap;
    }
}