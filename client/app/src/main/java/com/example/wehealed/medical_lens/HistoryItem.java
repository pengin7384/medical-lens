package com.example.wehealed.medical_lens;

public class HistoryItem {

    protected int historyId = 0;
    protected String pictureFileName = "";
    protected String picturePathAndFileName = "";
    protected int pictureTime = 0;
    protected String originalText = "";
    protected String machineTranslationResult = "";
    protected String humanTranslationRequested = "";
    protected int humanTranslationRequestTime = 0;
    protected int humanTranslationResponseTime = 0;
    protected String humanTranslationResult = "";
    protected String humanTranslationConfirmed = "";
    protected String summaryText = "";

    public HistoryItem(int historyId, String picturePathAndFileName, String pictureFileName, int pictureTime, String summaryText) {
        this.historyId = historyId;
        this.picturePathAndFileName = picturePathAndFileName;
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

    public String getPicturePathAndFileName() {
        return picturePathAndFileName;
    }

    public void setPicturePathAndFileName(String picturePathAndFileName) {
        this.picturePathAndFileName = picturePathAndFileName;
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
        if (summaryText == null) {
            summaryText = new String("");
        }
        this.summaryText = summaryText;
    }
}
