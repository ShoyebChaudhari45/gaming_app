package com.example.gameapp.models.response;

import com.example.gameapp.models.response.TapsResponse;
public class GameItem {

    private final String gameName;
    private final TapsResponse.Tap openTap;
    private final TapsResponse.Tap closeTap;

    public GameItem(String gameName, TapsResponse.Tap openTap, TapsResponse.Tap closeTap) {
        this.gameName = gameName;
        this.openTap = openTap;
        this.closeTap = closeTap;
    }

    public String getGameName() {
        return gameName;
    }

    public TapsResponse.Tap getOpenTap() {
        return openTap;
    }

    public TapsResponse.Tap getCloseTap() {
        return closeTap;
    }

    public boolean hasOpenTap() {
        return openTap != null;
    }
}
