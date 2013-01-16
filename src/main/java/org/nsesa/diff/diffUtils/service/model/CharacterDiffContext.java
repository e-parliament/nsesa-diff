package org.nsesa.diff.diffUtils.service.model;

public class CharacterDiffContext {

    private String insertTemplate;
    private String deleteTemplate;

    public CharacterDiffContext(String insertTemplate, String deleteTemplate) {
        this.insertTemplate = insertTemplate;
        this.deleteTemplate = deleteTemplate;
    }

    public String getInsertTemplate() {
        return insertTemplate;
    }

    public String getDeleteTemplate() {
        return deleteTemplate;
    }

}
