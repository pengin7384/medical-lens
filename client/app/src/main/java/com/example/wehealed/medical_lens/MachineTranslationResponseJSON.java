package com.example.wehealed.medical_lens;

public class MachineTranslationResponseJSON {
    private String response_time;
    private String picture_file_name;
    private Sentence[] sentences;
    private Summary[] summaries;
    private DescribingURL[] describing_urls;

    public MachineTranslationResponseJSON(String response_time, String picture_file_name, Sentence[] sentences, Summary[] summaries, DescribingURL[] describing_urls) {
        this.response_time = response_time;
        this.picture_file_name = picture_file_name;
        this.sentences = sentences;
        this.summaries = summaries;
        this.describing_urls = describing_urls;
    }

    public String getResponse_time() {
        return response_time;
    }

    public void setResponse_time(String response_time) {
        this.response_time = response_time;
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

    public Summary[] getSummaries() {
        return summaries;
    }

    public void setSummaries(Summary[] summaries) {
        this.summaries = summaries;
    }

    public DescribingURL[] getDescribing_urls() {
        return describing_urls;
    }

    public void setDescribing_urls(DescribingURL[] describing_urls) {
        this.describing_urls = describing_urls;
    }
}
