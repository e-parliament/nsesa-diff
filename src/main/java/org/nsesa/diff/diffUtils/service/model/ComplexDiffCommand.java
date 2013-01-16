package org.nsesa.diff.diffUtils.service.model;

public class ComplexDiffCommand {

    private String original;
    private String modified;
    private String overrideModified;
    private ThreeWayDiffContext context;

    public ComplexDiffCommand() {
    }

    public ComplexDiffCommand(String original, String modified, String overrideModified) {
        this.original = original;
        this.modified = modified;
        this.overrideModified = overrideModified;
    }

    public ComplexDiffCommand(String original, String modified, String overrideModified, ThreeWayDiffContext context) {
        this.original = original;
        this.modified = modified;
        this.overrideModified = overrideModified;
        this.context = context;
    }

    public String getOriginal() {
        return original;
    }

    public String getModified() {
        return modified;
    }

    public String getOverrideModified() {
        return overrideModified;
    }

    public ThreeWayDiffContext getContext() {
        return context;
    }

}
