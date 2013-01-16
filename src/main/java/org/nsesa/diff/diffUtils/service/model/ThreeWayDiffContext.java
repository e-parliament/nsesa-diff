package org.nsesa.diff.diffUtils.service.model;

public class ThreeWayDiffContext extends SemanticCleanupAware {

    private String originalChangeTemplate, originalComplexChangeTemplate, complexInsertTemplate, complexDeleteTemplate,
            complexChangeTemplate;

    private DiffMethod diffMethod = DiffMethod.CHARACTER;

    public ThreeWayDiffContext() {
        super(new SemanticCleanupContext(false));
    }

    public ThreeWayDiffContext(String originalChangeTemplate, String originalComplexChangeTemplate, String complexInsertTemplate,
                               String complexDeleteTemplate, String complexChangeTemplate, DiffMethod diffMethod) {
        this(originalChangeTemplate, originalComplexChangeTemplate, complexInsertTemplate, complexDeleteTemplate, complexChangeTemplate,
                diffMethod, true);
    }

    public ThreeWayDiffContext(String originalChangeTemplate, String originalComplexChangeTemplate, String complexInsertTemplate,
                               String complexDeleteTemplate, String complexChangeTemplate, DiffMethod diffMethod, boolean semanticCleanup) {
        super(new SemanticCleanupContext(semanticCleanup));
        this.originalChangeTemplate = originalChangeTemplate;
        this.originalComplexChangeTemplate = originalComplexChangeTemplate;
        this.complexInsertTemplate = complexInsertTemplate;
        this.complexDeleteTemplate = complexDeleteTemplate;
        this.complexChangeTemplate = complexChangeTemplate;
        this.diffMethod = diffMethod;
    }

    public String getOriginalChangeTemplate() {
        return originalChangeTemplate;
    }

    public ThreeWayDiffContext setOriginalChangeTemplate(String originalChangeTemplate) {
        this.originalChangeTemplate = originalChangeTemplate;
        return this;
    }

    public String getOriginalComplexChangeTemplate() {
        return originalComplexChangeTemplate;
    }

    public ThreeWayDiffContext setOriginalComplexChangeTemplate(String originalComplexChangeTemplate) {
        this.originalComplexChangeTemplate = originalComplexChangeTemplate;
        return this;
    }

    public String getComplexInsertTemplate() {
        return complexInsertTemplate;
    }

    public ThreeWayDiffContext setComplexInsertTemplate(String complexInsertTemplate) {
        this.complexInsertTemplate = complexInsertTemplate;
        return this;
    }

    public String getComplexDeleteTemplate() {
        return complexDeleteTemplate;
    }

    public ThreeWayDiffContext setComplexDeleteTemplate(String complexDeleteTemplate) {
        this.complexDeleteTemplate = complexDeleteTemplate;
        return this;
    }

    public String getComplexChangeTemplate() {
        return complexChangeTemplate;
    }

    public ThreeWayDiffContext setComplexChangeTemplate(String complexChangeTemplate) {
        this.complexChangeTemplate = complexChangeTemplate;
        return this;
    }

    public DiffMethod getDiffMethod() {
        return diffMethod;
    }

    public ThreeWayDiffContext setDiffMethod(DiffMethod diffMethod) {
        this.diffMethod = diffMethod;
        return this;
    }

}
