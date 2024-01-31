package cn.pprocket.impl;

import cn.pprocket.Client;
import cn.pprocket.object.Video;

import java.util.List;

public class AlphaImpl implements Client {
    @Override
    public void login(String deviceId) {

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
