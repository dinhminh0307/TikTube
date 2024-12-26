package com.example.tiktube.backend.controllers;

import com.example.tiktube.backend.callbacks.DataFetchCallback;
import com.example.tiktube.backend.models.Interaction;
import com.example.tiktube.backend.models.Video;
import com.example.tiktube.backend.services.VideoService;

public class VideoController {
    private VideoService videoService;

    public VideoController() {
        this.videoService = new VideoService();
    }

}
