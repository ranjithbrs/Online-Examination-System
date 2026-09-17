package com.examsystem.onlineexam.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ExamDraftDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<Long, String> answers = new HashMap<>();
    private int tabSwitch;
    private int copyCount;
    private int rightClick;
    private int fullscreenExit;
    private int windowBlur;
    private long lastSavedTimestamp;

    public ExamDraftDto() {
    }

    public ExamDraftDto(Map<Long, String> answers, int tabSwitch, int copyCount, int rightClick, int fullscreenExit, int windowBlur) {
        this.answers = answers != null ? answers : new HashMap<>();
        this.tabSwitch = tabSwitch;
        this.copyCount = copyCount;
        this.rightClick = rightClick;
        this.fullscreenExit = fullscreenExit;
        this.windowBlur = windowBlur;
        this.lastSavedTimestamp = System.currentTimeMillis();
    }

    public Map<Long, String> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<Long, String> answers) {
        this.answers = answers != null ? answers : new HashMap<>();
    }

    public int getTabSwitch() {
        return tabSwitch;
    }

    public void setTabSwitch(int tabSwitch) {
        this.tabSwitch = tabSwitch;
    }

    public int getCopyCount() {
        return copyCount;
    }

    public void setCopyCount(int copyCount) {
        this.copyCount = copyCount;
    }

    public int getRightClick() {
        return rightClick;
    }

    public void setRightClick(int rightClick) {
        this.rightClick = rightClick;
    }

    public int getFullscreenExit() {
        return fullscreenExit;
    }

    public void setFullscreenExit(int fullscreenExit) {
        this.fullscreenExit = fullscreenExit;
    }

    public int getWindowBlur() {
        return windowBlur;
    }

    public void setWindowBlur(int windowBlur) {
        this.windowBlur = windowBlur;
    }

    public long getLastSavedTimestamp() {
        return lastSavedTimestamp;
    }

    public void setLastSavedTimestamp(long lastSavedTimestamp) {
        this.lastSavedTimestamp = lastSavedTimestamp;
    }
}
