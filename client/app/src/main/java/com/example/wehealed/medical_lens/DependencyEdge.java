package com.example.wehealed.medical_lens;

public class DependencyEdge {
    private int head_token_index;
    private String label;

    public DependencyEdge(int headTokenIndex, String label) {
        this.head_token_index = headTokenIndex;
        this.label = label;
    }

    public int getHeadTokenIndex() {
        return head_token_index;
    }

    public void setHeadTokenIndex(int headTokenIndex) {
        this.head_token_index = headTokenIndex;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
