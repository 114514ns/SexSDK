package cn.pprocket;

import cn.pprocket.object.Video;

import java.util.List;

public interface Client {
    public void login(String deviceId);
    public List<Video> getRecommend();
    public Video getVideo(String id);

}
