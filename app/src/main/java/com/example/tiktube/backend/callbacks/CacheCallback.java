package com.example.tiktube.backend.callbacks;

import java.io.File;

public interface CacheCallback {
    void onCacheComplete(File cachedFile);
    void onCacheError(Exception e);
}
