package com.example.wehealed.medical_lens;

public class Sentence {
    private int sentence_number;
    private String original_sentence;
    private String translated_sentence_by_google;
    private String translated_sentence_by_wehealed;


    public Sentence(int sentence_number, String original_sentence, String translated_sentence_by_google, String translated_sentence_by_wehealed) {
        this.sentence_number = sentence_number;
        this.original_sentence = original_sentence;
        this.translated_sentence_by_google = translated_sentence_by_google;
        this.translated_sentence_by_wehealed = translated_sentence_by_wehealed;
    }

    public int getSentence_number() {
        return sentence_number;
    }

    public void setSentence_number(int sentence_number) {
        this.sentence_number = sentence_number;
    }

    public String getOriginal_sentence() {
        return original_sentence;
    }

    public void setOriginal_sentence(String original_sentence) {
        this.original_sentence = original_sentence;
    }

    public String getTranslated_sentence_by_google() {
        return translated_sentence_by_google;
    }

    public void setTranslated_sentence_by_google(String translated_sentence_by_google) {
        this.translated_sentence_by_google = translated_sentence_by_google;
    }

    public String getTranslated_sentence_by_wehealed() {
        return translated_sentence_by_wehealed;
    }

    public void setTranslated_sentence_by_wehealed(String translated_sentence_by_wehealed) {
        this.translated_sentence_by_wehealed = translated_sentence_by_wehealed;
    }
}