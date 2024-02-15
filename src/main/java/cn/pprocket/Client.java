package cn.pprocket;

import cn.pprocket.object.Video;

import java.util.List;

public interface Client {
    void login(String deviceId);
    List<Video> getRecommend(int pgae);
    Video getVideo(String id);

    String getPlayLink(Video video);
    List<Video> getRelated();
    List<Video> search(String keyword, int page);
}
