package com.example.wehealed.medical_lens;

public class DescribingURL {
    private String key;
    private String url;

    public DescribingURL(String key, String url) {
        this.key = key;
        this.url = url;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
