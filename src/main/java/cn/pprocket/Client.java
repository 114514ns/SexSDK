package cn.pprocket;

import cn.pprocket.object.Video;

import java.util.List;

public interface Client {
    void login(String deviceId);
    List<Video> getRecommend();
    Video getVideo(String id);

    String getPlayLink(Video video);
    String getCookie();
    List<Video> getRelated();
}
