package org.nsesa.diff.diffUtils.word;


public class Word {

    private Word reference;
    private String value;
    private WordStatus status = new WordStatus();

    public Word(String value) {
        this.value = value;
    }

    public Word getReference() {
        return reference;
    }

    public void setReference(Word reference) {
        this.reference = reference;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public WordStatus getStatus() {
        return status;
    }

    public boolean isInsert() {
        return status.isInsert();
    }

    public void setInsert() {
        status.setInsert();
    }

    public boolean isDelete() {
        return status.isDelete();
    }

    public void setDelete() {
        status.setDelete();
    }

    public boolean isChange() {
        return status.isChange();
    }

    public void setChange() {
        status.setChange();
    }

    public boolean isComplexInsert() {
        return status.isComplexInsert();
    }

    public void setComplexInsert() {
        status.setComplexInsert();
    }

    public boolean isComplexDelete() {
        return status.isComplexDelete();
    }

    public void setComplexDelete() {
        status.setComplexDelete();
    }

    public boolean isCleanup() {
        return status.isCleanup();
    }

    public void setCleanup() {
        status.setCleanup();
    }

    public void takeOverChanges(Word word) {
        this.status = word.status;
    }

    @Override
    public String toString() {
        return "Word [value=" + value + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Word other = (Word) obj;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equalsIgnoreCase(other.value))
            return false;
        return true;
    }

}