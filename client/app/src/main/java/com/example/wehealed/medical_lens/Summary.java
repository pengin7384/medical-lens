package com.example.wehealed.medical_lens;

public class Summary {
    private String summary_text;
    private int summary_number;

    public Summary(String summary_text, int summary_number) {
        this.summary_text = summary_text;
        this.summary_number = summary_number;
    }

    public String getSummary_text() {
        return summary_text;
    }

    public void setSummary_text(String summary_text) {
        this.summary_text = summary_text;
    }

    public int getSummary_number() {
        return summary_number;
    }

    public void setSummary_number(int summary_number) {
        this.summary_number = summary_number;
    }
}
