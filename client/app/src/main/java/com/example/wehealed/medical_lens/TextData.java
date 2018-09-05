package com.example.wehealed.medical_lens;

public class TextData implements Comparable<TextData> {
    private int y;
    private String text;

    public TextData(int y, String text) {
        this.y = y;
        this.text = text;
    }

    public int getY() {
        return y;
    }

    public String getText() {
        return text;
    }

    public int compareTo(TextData textData) {
        if(this.y < textData.y ) {
            return -1;
        } else if(this.y == textData.y) {
            return 0;
        }
        else {
            return 1;
        }
    }
}
