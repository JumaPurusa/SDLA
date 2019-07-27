package com.example.jay.sdla.Models;

public class VideoFile extends AppFile{

    private String videoDuration;

    public VideoFile(String url, String name, String size) {
        super(url, name, size);
        //this.videoDuration = videoDuration;
    }

    public String getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(String videoDuration) {
        this.videoDuration = videoDuration;
    }
}
