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

import java.util.List;

public abstract class SemanticCleanupAware {

    private SemanticCleanupContext semanticCleanupContext;

    public SemanticCleanupAware(SemanticCleanupContext semanticCleanupContext) {
        this.semanticCleanupContext = semanticCleanupContext;
    }

    public boolean isSemanticCleanup() {
        return semanticCleanupContext.isEnabled();
    }

    public int getWordLength() {
        return semanticCleanupContext.getWordLength();
    }

    public void setWordLength(int wordLength) {
        semanticCleanupContext.setWordLength(wordLength);
    }

    public List<String> getBlackListedWords() {
        return semanticCleanupContext.getBlackList();
    }

    public void setBlackListedWords(List<String> blackListedWords) {
        semanticCleanupContext.setBlackList(blackListedWords);
    }
}
