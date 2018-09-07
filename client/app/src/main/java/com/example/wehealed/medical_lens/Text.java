package com.example.wehealed.medical_lens;

public class Text {
    private String content;
    private int begin_offset;

    public Text(String content, int begin_offset) {
        this.content = content;
        this.begin_offset = begin_offset;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getBegin_offset() {
        return begin_offset;
    }

    public void setBegin_offset(int begin_offset) {
        this.begin_offset = begin_offset;
    }
}
