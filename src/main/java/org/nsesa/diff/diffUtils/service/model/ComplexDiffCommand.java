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
