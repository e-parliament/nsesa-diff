package org.nsesa.diff.diffUtils;

@SuppressWarnings("serial")
public class DiffException extends RuntimeException {

    private String original;
    private String modified;

    public DiffException(String original, String modified, String message, Throwable cause) {
        super(message, cause);
    }

    public String getOriginal() {
        return original;
    }

    public String getModified() {
        return modified;
    }

}
