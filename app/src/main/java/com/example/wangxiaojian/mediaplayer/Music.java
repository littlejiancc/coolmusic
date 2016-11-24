package com.example.wangxiaojian.mediaplayer;

/**
 * Created by Wangxiaojian on 2016/11/24.
 */

public class Music{
    private String music_title;
    private String duration;
    private String artist;
    private String uri;
    public Music(String music_title,String artist,String duration,String uri){
        this.music_title=music_title;

        this.duration=duration;
        this.uri=uri;
        this.artist=artist;
    }
    public String getMusic_title(){
        return music_title;
    }
    public String getDuration(){
        return duration;
    }
    public String getArtist(){
        return artist;
    }
    public String getUri(){
        return uri;
    }
}
