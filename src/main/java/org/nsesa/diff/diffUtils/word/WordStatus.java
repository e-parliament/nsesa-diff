/**
 * Copyright 2013 European Parliament
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */
package org.nsesa.diff.diffUtils.word;


public class WordStatus {

    private boolean insert, delete, change, complexInsert, complexDelete, cleanup;

    public boolean isInsert() {
        return insert;
    }

    public void setInsert() {
        this.insert = true;
        this.delete = false;
        this.change = false;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete() {
        this.delete = true;
        this.insert = false;
        this.change = false;
    }

    public boolean isChange() {
        return change;
    }

    public void setChange() {
        this.change = true;
        this.insert = false;
        this.delete = false;
    }

    public boolean isComplexInsert() {
        return complexInsert;
    }

    public void setComplexInsert() {
        this.complexInsert = true;
        this.complexDelete = false;
    }

    public boolean isComplexDelete() {
        return complexDelete;
    }

    public void setComplexDelete() {
        this.complexDelete = true;
        this.complexInsert = false;
    }

    public boolean isCleanup() {
        return cleanup;
    }

    public void setCleanup() {
        this.cleanup = true;
        this.change = false;
        this.insert = false;
        this.delete = false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (change ? 1231 : 1237);
        result = prime * result + (cleanup ? 1231 : 1237);
        result = prime * result + (complexDelete ? 1231 : 1237);
        result = prime * result + (complexInsert ? 1231 : 1237);
        result = prime * result + (delete ? 1231 : 1237);
        result = prime * result + (insert ? 1231 : 1237);
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
        WordStatus other = (WordStatus) obj;
        if (change != other.change)
            return false;
        if (cleanup != other.cleanup)
            return false;
        if (complexDelete != other.complexDelete)
            return false;
        if (complexInsert != other.complexInsert)
            return false;
        if (delete != other.delete)
            return false;
        if (insert != other.insert)
            return false;
        return true;
    }

}
