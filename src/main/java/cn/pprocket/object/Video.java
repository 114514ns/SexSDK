package cn.pprocket.object;

import lombok.Data;

import java.util.List;

@Data
public class Video {
    private String title;
    private String cover;
    private User author;
    private int length;
    private List<String> tags;
    private String originLink;
    private String time;
    private int id;
    private int views;
}
