package org.nsesa.diff.diffUtils.service.model;


public class ComplexDiffResult extends SimpleDiffResult {

    private String trackChangesOriginal;
    private String trackChangesAmendment;

    public ComplexDiffResult() {
        super();
    }

    public ComplexDiffResult(String original, String amendment,
                             String trackChangesOriginal, String trackChangesAmendment) {
        super(original, amendment);
        this.trackChangesOriginal = trackChangesOriginal;
        this.trackChangesAmendment = trackChangesAmendment;
    }

    public String getTrackChangesOriginal() {
        return trackChangesOriginal;
    }

    public void setTrackChangesOriginal(String coloredOriginal) {
        this.trackChangesOriginal = coloredOriginal;
    }

    public String getTrackChangesAmendment() {
        return trackChangesAmendment;
    }

    public void setTrackChangesAmendment(String coloredAmendment) {
        this.trackChangesAmendment = coloredAmendment;
    }

}
