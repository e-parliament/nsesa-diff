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
