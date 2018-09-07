package com.example.wehealed.medical_lens;

public class Token {
    private Text text;
    private PartOfSpeech part_of_speech;
    private DependencyEdge dependency_edge;

    public Token(Text text, PartOfSpeech part_of_speech, DependencyEdge dependency_edge) {
        this.text = text;
        this.part_of_speech = part_of_speech;
        this.dependency_edge = dependency_edge;
    }

    public Text getText() {
        return text;
    }

    public void setText(Text text) {
        this.text = text;
    }

    public PartOfSpeech getPart_of_speech() {
        return part_of_speech;
    }

    public void setPart_of_speech(PartOfSpeech part_of_speech) {
        this.part_of_speech = part_of_speech;
    }

    public DependencyEdge getDependency_edge() {
        return dependency_edge;
    }

    public void setDependency_edge(DependencyEdge dependency_edge) {
        this.dependency_edge = dependency_edge;
    }
}
