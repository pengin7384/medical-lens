package com.example.wehealed.medical_lens;

public class HistoryItem {

    int historyId = 0;
    String pictureFileName = "";
    int pictureTime = 0;
    String originalText = "";
    String machineTranslationResult = "";
    String humanTranslationRequested = "";
    int humanTranslationRequestTime = 0;
    int humanTranslationResponseTime = 0;
    String humanTranslationResult = "";
    String humanTranslationConfirmed = "";
    String summaryText = "";

    public HistoryItem(int historyId, String pictureFileName) {
        this.historyId = historyId;
        this.pictureFileName = pictureFileName;
    }

    public HistoryItem(int historyId, String pictureFileName, int pictureTime, String summaryText) {
        this.historyId = historyId;
        this.pictureFileName = pictureFileName;
        this.pictureTime = pictureTime;
        this.summaryText = summaryText;
    }

    public int getHistoryId() {
        return historyId;
    }

    public void setHistoryId(int historyId) {
        this.historyId = historyId;
    }

    public String getPictureFileName() {
        return pictureFileName;
    }

    public void setPictureFileName(String pictureFileName) {
        this.pictureFileName = pictureFileName;
    }

    public int getPictureTime() {
        return pictureTime;
    }

    public void setPictureTime(int pictureTime) {
        this.pictureTime = pictureTime;
    }

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public String getMachineTranslationResult() {
        return machineTranslationResult;
    }

    public void setMachineTranslationResult(String machineTranslationResult) {
        this.machineTranslationResult = machineTranslationResult;
    }

    public String getHumanTranslationRequested() {
        return humanTranslationRequested;
    }

    public void setHumanTranslationRequested(String humanTranslationRequested) {
        this.humanTranslationRequested = humanTranslationRequested;
    }

    public int getHumanTranslationRequestTime() {
        return humanTranslationRequestTime;
    }

    public void setHumanTranslationRequestTime(int humanTranslationRequestTime) {
        this.humanTranslationRequestTime = humanTranslationRequestTime;
    }

    public int getHumanTranslationResponseTime() {
        return humanTranslationResponseTime;
    }

    public void setHumanTranslationResponseTime(int humanTranslationResponseTime) {
        this.humanTranslationResponseTime = humanTranslationResponseTime;
    }

    public String getHumanTranslationResult() {
        return humanTranslationResult;
    }

    public void setHumanTranslationResult(String humanTranslationResult) {
        this.humanTranslationResult = humanTranslationResult;
    }

    public String getHumanTranslationConfirmed() {
        return humanTranslationConfirmed;
    }

    public void setHumanTranslationConfirmed(String humanTranslationConfirmed) {
        this.humanTranslationConfirmed = humanTranslationConfirmed;
    }

    public String getSummaryText() {
        return summaryText;
    }

    public void setSummaryText(String summaryText) {
        this.summaryText = summaryText;
    }
}
