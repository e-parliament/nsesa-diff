package org.nsesa.diff.diffUtils.service.model;


public class WordDiffContext extends SemanticCleanupAware {

    private String changeTemplate;

    public WordDiffContext(String changeTemplate) {
        this(changeTemplate, true);
    }

    public WordDiffContext(String changeTemplate, boolean semanticCleanup) {
        super(new SemanticCleanupContext(semanticCleanup));
        this.changeTemplate = changeTemplate;
    }

    public String getChangeTemplate() {
        return changeTemplate;
    }

}
