package com.example.wehealed.medical_lens;

public class MachineTranslationRequestJSON {
    private String picture_file_name;
    private Sentence[] sentences;

    public MachineTranslationRequestJSON(String picture_file_name, Sentence[] sentences) {
        this.picture_file_name = picture_file_name;
        this.sentences = sentences;
    }

    public String getPicture_file_name() {
        return picture_file_name;
    }

    public void setPicture_file_name(String picture_file_name) {
        this.picture_file_name = picture_file_name;
    }

    public Sentence[] getSentences() {
        return sentences;
    }

    public void setSentences(Sentence[] sentences) {
        this.sentences = sentences;
    }
}
