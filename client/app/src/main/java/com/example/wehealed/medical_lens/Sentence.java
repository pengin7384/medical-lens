package com.example.wehealed.medical_lens;

public class Sentence {
    private int sentence_number;
    private String original_sentence;
    private String translated_sentence;

    public Sentence(int sentence_number, String original_sentence, String translated_sentence) {
        this.sentence_number = sentence_number;
        this.original_sentence = original_sentence;
        this.translated_sentence = translated_sentence;
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

    public String getTranslated_sentence() {
        return translated_sentence;
    }

    public void setTranslated_sentence(String translated_sentence) {
        this.translated_sentence = translated_sentence;
    }
}