package org.nsesa.diff.diffUtils.service.model;

import java.util.ArrayList;
import java.util.List;

public class SemanticCleanupContext {

    private boolean enabled;
    private int wordLength = 4;
    private List<String> blackList = new ArrayList<String>();

    public SemanticCleanupContext(boolean enabled) {
        this.enabled = enabled;
    }

    public SemanticCleanupContext(boolean enabled, int wordLength) {
        this.enabled = enabled;
        this.wordLength = wordLength;
    }

    public SemanticCleanupContext(boolean enabled, int wordLength, List<String> blackList) {
        this.enabled = enabled;
        this.wordLength = wordLength;
        this.blackList = new ArrayList<String>(blackList);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getWordLength() {
        return wordLength;
    }

    public List<String> getBlackList() {
        return blackList;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setWordLength(int wordLength) {
        this.wordLength = wordLength;
    }

    public void setBlackList(List<String> blackList) {
        this.blackList = blackList;
    }

}
