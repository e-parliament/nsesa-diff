/**
 * Copyright 2013 European Parliament
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
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

import java.util.ArrayList;
import java.util.List;

public class SemanticCleanupContext {

    private boolean enabled;
    private int wordLength = 4;
    private List<String> blackList = new ArrayList<String>();

    public SemanticCleanupContext(boolean enabled) {
        this.enabled = enabled;
    }

    public SemanticCleanupContext(boolean enabled, int wordLength) {
        this.enabled = enabled;
        this.wordLength = wordLength;
    }

    public SemanticCleanupContext(boolean enabled, int wordLength, List<String> blackList) {
        this.enabled = enabled;
        this.wordLength = wordLength;
        this.blackList = new ArrayList<String>(blackList);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getWordLength() {
        return wordLength;
    }

    public List<String> getBlackList() {
        return blackList;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setWordLength(int wordLength) {
        this.wordLength = wordLength;
    }

    public void setBlackList(List<String> blackList) {
        this.blackList = blackList;
    }

}
