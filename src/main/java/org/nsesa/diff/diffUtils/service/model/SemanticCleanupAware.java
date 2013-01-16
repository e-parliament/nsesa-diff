package org.nsesa.diff.diffUtils.service.model;

import java.util.List;

public abstract class SemanticCleanupAware {

    private SemanticCleanupContext semanticCleanupContext;

    public SemanticCleanupAware(SemanticCleanupContext semanticCleanupContext) {
        this.semanticCleanupContext = semanticCleanupContext;
    }

    public boolean isSemanticCleanup() {
        return semanticCleanupContext.isEnabled();
    }

    public int getWordLength() {
        return semanticCleanupContext.getWordLength();
    }

    public void setWordLength(int wordLength) {
        semanticCleanupContext.setWordLength(wordLength);
    }

    public List<String> getBlackListedWords() {
        return semanticCleanupContext.getBlackList();
    }

    public void setBlackListedWords(List<String> blackListedWords) {
        semanticCleanupContext.setBlackList(blackListedWords);
    }
}
