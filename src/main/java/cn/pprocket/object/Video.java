package cn.pprocket.object;

import lombok.Data;

@Data
public class Video {
    public String title;
    public String cover;
    public User author;
    public int length;
    public String[] tag;
    public String originLink;
}
