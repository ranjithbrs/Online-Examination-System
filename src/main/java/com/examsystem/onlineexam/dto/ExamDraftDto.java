package com.examsystem.onlineexam.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExamDraftDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<Long, String> answers = new HashMap<>();
    private List<Long> markedQuestions = new ArrayList<>();
    private int tabSwitch;
    private int copyCount;
    private int rightClick;
    private int fullscreenExit;
    private int windowBlur;
    private int audioSpikes;
    private long lastSavedTimestamp;

    public ExamDraftDto() {
    }

    public ExamDraftDto(Map<Long, String> answers, int tabSwitch, int copyCount, int rightClick, int fullscreenExit, int windowBlur) {
        this(answers, new ArrayList<>(), tabSwitch, copyCount, rightClick, fullscreenExit, windowBlur, 0);
    }

    public ExamDraftDto(Map<Long, String> answers, List<Long> markedQuestions, int tabSwitch, int copyCount, int rightClick, int fullscreenExit, int windowBlur) {
        this(answers, markedQuestions, tabSwitch, copyCount, rightClick, fullscreenExit, windowBlur, 0);
    }

    public ExamDraftDto(Map<Long, String> answers, List<Long> markedQuestions, int tabSwitch, int copyCount, int rightClick, int fullscreenExit, int windowBlur, int audioSpikes) {
        this.answers = answers != null ? answers : new HashMap<>();
        this.markedQuestions = markedQuestions != null ? markedQuestions : new ArrayList<>();
        this.tabSwitch = tabSwitch;
        this.copyCount = copyCount;
        this.rightClick = rightClick;
        this.fullscreenExit = fullscreenExit;
        this.windowBlur = windowBlur;
        this.audioSpikes = audioSpikes;
        this.lastSavedTimestamp = System.currentTimeMillis();
    }

    public Map<Long, String> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<Long, String> answers) {
        this.answers = answers != null ? answers : new HashMap<>();
    }

    public List<Long> getMarkedQuestions() {
        return markedQuestions;
    }

    public void setMarkedQuestions(List<Long> markedQuestions) {
        this.markedQuestions = markedQuestions != null ? markedQuestions : new ArrayList<>();
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

    public int getAudioSpikes() {
        return audioSpikes;
    }

    public void setAudioSpikes(int audioSpikes) {
        this.audioSpikes = audioSpikes;
    }

    public long getLastSavedTimestamp() {
        return lastSavedTimestamp;
    }

    public void setLastSavedTimestamp(long lastSavedTimestamp) {
        this.lastSavedTimestamp = lastSavedTimestamp;
    }
}
