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
package org.nsesa.diff.diffUtils;

import org.nsesa.diff.diffUtils.word.Word;

import java.util.List;

@SuppressWarnings("serial")
public class DiffIllegalStateException extends RuntimeException {

    private final List<Word> originalWords;
    private final List<Word> revisedWords;

    public DiffIllegalStateException(String message, List<Word> originalWords, List<Word> revisedWords) {
        super(message);

        this.originalWords = originalWords;
        this.revisedWords = revisedWords;
    }

    public List<Word> getRevisedWords() {
        return revisedWords;
    }

    public List<Word> getOriginalWords() {
        return originalWords;
    }

}
