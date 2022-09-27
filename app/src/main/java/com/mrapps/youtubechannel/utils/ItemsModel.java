package com.mrapps.youtubechannel.utils;


import java.io.Serializable;

public class ItemsModel implements Serializable {

    private String names;
    private String url;

    public ItemsModel(String names, String url) {
        this.names = names;
        this.url = url;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
