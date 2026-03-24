package com.billray.worship.infra.media;

public class BasicMediaAdapter {
    public boolean isSupported(String path) {
        if (path == null) return false;
        String normalized = path.toLowerCase();
        return normalized.endsWith(".mp4")
                || normalized.endsWith(".mov")
                || normalized.endsWith(".jpg")
                || normalized.endsWith(".jpeg")
                || normalized.endsWith(".png");
    }
}
