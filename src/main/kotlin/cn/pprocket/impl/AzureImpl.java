package cn.pprocket.impl;

import cn.pprocket.Client;
import cn.pprocket.object.Video;

import java.util.List;

public class AzureImpl implements Client {
    public void login(String deviceId) {
        System.out.println("AzureImpl login");
    }

    @Override
    public List<Video> getRecommend() {
        return null;
    }

    @Override
    public Video getVideo(String id) {
        return null;
    }
}
