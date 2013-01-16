package org.nsesa.diff.diffUtils;

@SuppressWarnings("serial")
public class ComplexDiffException extends DiffException {

    private String overrideModified;

    public ComplexDiffException(String original, String modified, String overrideModified, String message, Throwable cause) {
        super(original, modified, message, cause);

        this.overrideModified = overrideModified;
    }

    public String getOverrideModified() {
        return overrideModified;
    }

}
