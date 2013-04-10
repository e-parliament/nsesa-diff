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
package org.nsesa.diff.diffUtils;

import org.nsesa.diff.diffUtils.DiffMatchPatch.Diff;
import org.nsesa.diff.diffUtils.DiffMatchPatch.Operation;
import org.nsesa.diff.diffUtils.service.model.CharacterDiffContext;
import org.nsesa.diff.diffUtils.service.model.DefaultThreeWayDiffContext;
import org.nsesa.diff.diffUtils.service.model.ThreeWayDiffContext;
import org.nsesa.diff.diffUtils.service.model.WordDiffContext;
import org.nsesa.diff.diffUtils.util.MessageFormatter;
import org.nsesa.diff.diffUtils.word.Word;
import org.nsesa.diff.diffUtils.word.WordExtractor;
import org.nsesa.diff.diffUtils.word.WordExtractor.WordExtractionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.*;

/**
 * <p>
 * This Class' main purpose is to perform several types of diffing, going from
 * character/word diff to the more complex three-way diff.
 * </p>
 * <p>
 * The input text for all methods can be plain text or any kind of valid XML,
 * HTML. In case of the latter the actual structural differences in markup won't
 * be made visual in the end result. The purpose is not to diff the actual XML,
 * HTML but rather the text it contains.
 * </p>
 *
 * @author Stefan Koulouris - stefan.koulouris@gmail.com
 */
public final class DiffUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiffUtils.class);

    private static final WordDiffContext WORD_DIFF_CONTEXT = new WordDiffContext("<bi>{0}</bi>");

    private static final CharacterDiffContext CHARACTER_DIFF_CONTEXT = new CharacterDiffContext("<ins>{0}</ins>", "<del>{0}</del>");

    private static final WordExtractor WORD_EXTRACTOR = new WordExtractor();

    private static class WordToCharContext {
        public String original;
        public String revised;

        public WordToCharContext(String original, String revised) {
            this.original = original;
            this.revised = revised;
        }
    }

    public static String characterDiff(final String original, String revised) {
        return characterDiff(original, revised, CHARACTER_DIFF_CONTEXT);
    }

    public static String characterDiff(final String original, String revised, CharacterDiffContext characterDiffContext) {
        LinkedList<Diff> diffResult = DMPDelegator.INSTANCE.diff_main(original, revised);

        String[] insertTags = extractStartEndFromTemplate(characterDiffContext.getInsertTemplate());
        String[] deleteTags = extractStartEndFromTemplate(characterDiffContext.getDeleteTemplate());

        StringBuilder resultBuilder = new StringBuilder();
        for (Diff diff : diffResult) {
            switch (diff.operation) {
                case INSERT:
                    resultBuilder.append(insertTags[0]).append(diff.text).append(insertTags[1]);
                    break;
                case DELETE:
                    resultBuilder.append(deleteTags[0]).append(diff.text).append(deleteTags[1]);
                    break;
                case EQUAL:
                    resultBuilder.append(diff.text);
                    break;
            }
        }

        return resultBuilder.toString();
    }

    public static String[] wordDiff(final String original, String revised) {
        return wordDiff(original, revised, WORD_DIFF_CONTEXT);
    }

    public static String[] wordDiff(final String original, String revised, WordDiffContext wordDiffContext) {
        LOGGER.debug("original: {}", original);
        LOGGER.debug("revised: {}", revised);

        WordExtractionResult originalWordExtractionResult = WORD_EXTRACTOR.extractWords(original);
        WordExtractionResult revisedWordExtractionResult = WORD_EXTRACTOR.extractWords(revised);

        WordToCharContext context = wordToChar(originalWordExtractionResult.words, revisedWordExtractionResult.words);

        LinkedList<Diff> diffResult = DMPDelegator.INSTANCE.diff_main(context.original, context.revised);
        verifyResult(diffResult, originalWordExtractionResult.words, revisedWordExtractionResult.words);
        if (wordDiffContext.isSemanticCleanup()) {
            applySemanticCleanup(diffResult, originalWordExtractionResult.words, revisedWordExtractionResult.words, wordDiffContext.getWordLength());
        }

        List<String> originalArgs = new ArrayList<String>();
        List<String> revisedArgs = new ArrayList<String>();

        String[] changeTags = extractStartEndFromTemplate(wordDiffContext.getChangeTemplate());

        int originalIndex = 0;
        int revisedIndex = 0;
        while (diffResult.peek() != null) {
            Diff next = diffResult.poll();
            String sequence = next.text;
            switch (next.operation) {
                case DELETE:
                    for (int i = 0; i < sequence.length(); i++) {
                        Word originalWord = originalWordExtractionResult.words.get(originalIndex++);
                        originalArgs.add(changeTags[0] + originalWord.getValue() + changeTags[1]);
                    }
                    break;
                case INSERT:
                    for (int i = 0; i < sequence.length(); i++) {
                        Word revisedWord = revisedWordExtractionResult.words.get(revisedIndex++);
                        revisedArgs.add(changeTags[0] + revisedWord.getValue() + changeTags[1]);
                    }
                    break;
                case EQUAL:
                    for (int i = 0; i < sequence.length(); i++) {
                        Word originalWord = originalWordExtractionResult.words.get(originalIndex++);
                        Word revisedWord = revisedWordExtractionResult.words.get(revisedIndex++);

                        String originalWordValue = originalWord.getValue();
                        String revisedWordValue = revisedWord.getValue();
                        if (originalWord.isCleanup() && revisedWord.isCleanup()) {
                            originalWordValue = changeTags[0] + originalWordValue + changeTags[1];
                            revisedWordValue = changeTags[0] + revisedWordValue + changeTags[1];
                        }

                        originalArgs.add(originalWordValue);
                        revisedArgs.add(revisedWordValue);
                    }
                    break;
                default:
                    throw new IllegalStateException(next.operation.name() + " operation not treated.");
            }
        }

        String originalResult = MessageFormatter.arrayFormat(originalWordExtractionResult.template, originalArgs.toArray()).getMessage();
        String revisedResult = MessageFormatter.arrayFormat(revisedWordExtractionResult.template, revisedArgs.toArray()).getMessage();

        String[] result = new String[]{originalResult, revisedResult};

        return cleanupWordDiff(result, wordDiffContext);
    }

    private static void applySemanticCleanup(LinkedList<Diff> diffResult, LinkedList<Word> left, LinkedList<Word> right, int wordLength) {
        if (diffResult.isEmpty() || diffResult.size() <= 2) {
            return;
        }

        LinkedList<Diff> diffResultCopy = new LinkedList<DiffMatchPatch.Diff>(diffResult);

        int leftIndex = 0;
        int rightIndex = 0;
        if (diffResultCopy.getFirst().operation == Operation.EQUAL) {
            leftIndex += diffResultCopy.poll().getSize();
            rightIndex = leftIndex;
        }

        if (diffResultCopy.getLast().operation == Operation.EQUAL) {
            diffResultCopy.pollLast();
        }

        LinkedList<Integer> equalIndexes = new LinkedList<Integer>();
        for (int i = 0; i < diffResultCopy.size(); i++) {
            Diff diff = diffResultCopy.get(i);
            if (diff.operation == Operation.EQUAL) {
                equalIndexes.add(i);
            }
        }

        if (!equalIndexes.isEmpty()) {
            Integer previous = null;
            while (equalIndexes.peek() != null) {
                Integer index = equalIndexes.poll();

                int fromIndex = 0;
                if (previous != null) {
                    fromIndex = previous + 1;
                }
                int toIndex = diffResultCopy.size();
                if (equalIndexes.peek() != null) {
                    toIndex = equalIndexes.peek();
                }

                List<Diff> beforeDiffs = diffResultCopy.subList(fromIndex, index);
                List<Diff> afterDiffs = diffResultCopy.subList(index + 1, toIndex);

                Diff beforeInsert = null;
                Diff beforeDelete = null;
                for (Diff beforeDiff : beforeDiffs) {
                    if (beforeDiff.operation == Operation.DELETE) {
                        beforeDelete = beforeDiff;
                        leftIndex += beforeDelete.getSize();
                    } else if (beforeDiff.operation == Operation.INSERT) {
                        beforeInsert = beforeDiff;
                        rightIndex += beforeInsert.getSize();
                    }
                }

                Diff afterInsert = null;
                Diff afterDelete = null;
                for (Diff afterDiff : afterDiffs) {
                    if (afterDiff.operation == Operation.DELETE) {
                        afterDelete = afterDiff;
                    } else if (afterDiff.operation == Operation.INSERT) {
                        afterInsert = afterDiff;
                    }
                }

                if ((beforeInsert != null && afterInsert != null) || (beforeDelete != null && afterDelete != null)) {
                    Diff candidate = diffResultCopy.get(index);
                    // We have, what seems like, an innocent candidate, please step forward...
                    List<Word> leftEqualWords = new ArrayList<Word>();
                    List<Word> rightEqualWords = new ArrayList<Word>();
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < candidate.text.length(); i++) {
                        Word equalWord = left.get(leftIndex++);
                        builder.append(equalWord.getValue());
                        leftEqualWords.add(equalWord);
                        rightEqualWords.add(right.get(rightIndex++));
                    }

                    if (evaluateWord(builder.toString(), 4)) {
                        // Hmmm, not so innocent afterall. Guillotine falling on your frail existence. Who would have thought...
                        for (int i = 0; i < leftEqualWords.size(); i++) {
                            leftEqualWords.get(i).setCleanup();
                            rightEqualWords.get(i).setCleanup();
                        }
                    } else {
                        // Ok, somebody thinks you still have some value out there. Destiny...?
                    }
                } else {
                    leftIndex += diffResultCopy.get(index).getSize();
                    rightIndex += diffResultCopy.get(index).getSize();
                }

                previous = index;
            }
        }
    }

    private static boolean evaluateWord(String text, int wordLength) {
        // if text contains the end punctuation of a couple
        if (text.matches("(\"|\\)|>|}|\\]).*")) {
            return false;
        }

        String[] splitOnPunctuation = text.trim().split("\\p{P}");
        if (splitOnPunctuation.length == 2) {
            return evaluateWord(splitOnPunctuation[0].trim(), wordLength) && evaluateWord(splitOnPunctuation[1].trim(), wordLength);
        } else {
            if (text.trim().split("\\s").length <= 2 && text.trim().length() <= wordLength && !text.matches("\\p{P}")) {
                return true;
            }
        }

        return false;
    }

    private static String[] cleanupWordDiff(String result[], WordDiffContext wordDiffContext) {
        String[] change = extractStartEndFromTemplate(wordDiffContext.getChangeTemplate());

        result[0] = result[0].replaceAll(appendTemplates(change[0], change[1], true), " ")
                .replaceAll(appendTemplates(change[1], change[0], true), " ").replaceAll(appendTemplates(change[1], change[0], false), "");
        result[1] = result[1].replaceAll(appendTemplates(change[0], change[1], true), " ")
                .replaceAll(appendTemplates(change[1], change[0], true), " ").replaceAll(appendTemplates(change[1], change[0], false), "");

        return result;
    }

    /**
     * Performs a three-way diff on the given texts.
     *
     * @param original
     * @param revised
     * @param verification
     * @return
     */
    public static String[] threeWayDiff(final String original, final String revised, final String verification) {
        return threeWayDiff(original, revised, verification, new DefaultThreeWayDiffContext());
    }

    /**
     * Performs a three-way diff on the given texts.
     *
     * @param original
     * @param revised
     * @param verification
     * @param context
     * @return
     */
    public static String[] threeWayDiff(final String original, final String revised, final String verification,
                                        final ThreeWayDiffContext context) {
        LOGGER.debug("original: {}", original);
        LOGGER.debug("revised: {}", revised);
        LOGGER.debug("overrideRevised: {}", verification);

        WordExtractionResult originalWordExtractionResult = WORD_EXTRACTOR.extractWords(original);
        WordExtractionResult revisedWordExtractionResult = WORD_EXTRACTOR.extractWords(revised);
        WordExtractionResult verifiedWordExtractionResult = WORD_EXTRACTOR.extractWords(verification);

        WordToCharContext phase1Context = wordToChar(originalWordExtractionResult.words, revisedWordExtractionResult.words);

        LinkedList<Diff> phase1 = DMPDelegator.INSTANCE.diff_main(phase1Context.original, phase1Context.revised);
        verifyResult(phase1, originalWordExtractionResult.words, revisedWordExtractionResult.words);
        if (context.isSemanticCleanup()) {
            applySemanticCleanup(phase1, originalWordExtractionResult.words, revisedWordExtractionResult.words, context.getWordLength());
        }
        processFirstPhase(originalWordExtractionResult.words, revisedWordExtractionResult.words, phase1);

        WordToCharContext phase2Context = wordToChar(revisedWordExtractionResult.words, verifiedWordExtractionResult.words);

        LinkedList<Diff> phase2 = DMPDelegator.INSTANCE.diff_main(phase2Context.original, phase2Context.revised);
        verifyResult(phase2, revisedWordExtractionResult.words, verifiedWordExtractionResult.words);
        //applySemanticCleanup(phase1);
        processSecondPhase(revisedWordExtractionResult.words, verifiedWordExtractionResult.words, phase2);

        return cleanup(
                print(phase1, phase2, originalWordExtractionResult.words, revisedWordExtractionResult.words,
                        verifiedWordExtractionResult.words, context, originalWordExtractionResult, verifiedWordExtractionResult), context);
    }

    private static String[] cleanup(String[] print, ThreeWayDiffContext context) {
        String[] change = extractStartEndFromTemplate(context.getOriginalChangeTemplate());
        String[] complexChange = extractStartEndFromTemplate(context.getComplexChangeTemplate());
        String[] complexDelete = extractStartEndFromTemplate(context.getComplexDeleteTemplate());
        String[] complexInsert = extractStartEndFromTemplate(context.getComplexInsertTemplate());
        String[] originalComplexChange = extractStartEndFromTemplate(context.getOriginalComplexChangeTemplate());

        print[0] = print[0].replaceAll(appendTemplates(change[0], change[1], true), " ")
                .replaceAll(appendTemplates(change[1], change[0], true), " ").replaceAll(appendTemplates(change[1], change[0], false), "")
                .replaceAll(appendTemplates(complexChange[0], complexChange[1], true), " ")
                .replaceAll(appendTemplates(complexChange[1], complexChange[0], true), " ")
                .replaceAll(appendTemplates(originalComplexChange[0], originalComplexChange[1], true), " ")
                .replaceAll(appendTemplates(originalComplexChange[1], originalComplexChange[0], true), " ")
                .replaceAll(appendTemplates(complexInsert[0], complexInsert[1], true), " ")
                .replaceAll(appendTemplates(complexInsert[1], complexInsert[0], true), " ")
                .replaceAll(appendTemplates(complexDelete[0], complexDelete[1], true), " ")
                .replaceAll(appendTemplates(complexDelete[1], complexDelete[0], true), " ");
        print[1] = print[1].replaceAll(appendTemplates(change[0], change[1], true), " ")
                .replaceAll(appendTemplates(change[1], change[0], true), " ").replaceAll(appendTemplates(change[1], change[0], false), "")
                .replaceAll(appendTemplates(complexChange[0], complexChange[1], true), " ")
                .replaceAll(appendTemplates(complexChange[1], complexChange[0], true), " ")
                .replaceAll(appendTemplates(originalComplexChange[0], originalComplexChange[1], true), " ")
                .replaceAll(appendTemplates(originalComplexChange[1], originalComplexChange[0], true), " ")
                .replaceAll(appendTemplates(complexInsert[0], complexInsert[1], true), " ")
                .replaceAll(appendTemplates(complexInsert[1], complexInsert[0], true), " ")
                .replaceAll(appendTemplates(complexDelete[0], complexDelete[1], true), " ")
                .replaceAll(appendTemplates(complexDelete[1], complexDelete[0], true), " ");

        return print;
    }

    private static final String appendTemplates(String one, String two, boolean withSpace) {
        String result = one;
        if (withSpace) {
            result += " ";
        }
        result += two;

        return result;
    }

    private static final String[] extractStartEndFromTemplate(String template) {
        String[] result = new String[2];

        String format = MessageFormat.format(template, "%");
        result[0] = format.substring(0, format.indexOf("%"));
        result[1] = format.substring(format.indexOf("%") + 1);

        return result;
    }

    private static WordToCharContext wordToChar(LinkedList<Word> originalWords, LinkedList<Word> revisedWords) {
        Map<String, Character> map = new HashMap<String, Character>();

        StringBuilder orCharBuilder = new StringBuilder();
        StringBuilder revCharBuilder = new StringBuilder();
        char x = 'a';
        for (Word word : originalWords) {
            Character character = map.get(word.getValue().toLowerCase());
            if (character != null) {
                orCharBuilder.append(character);
            } else {
                orCharBuilder.append(x);
                map.put(word.getValue().toLowerCase(), x++);
            }
        }
        for (Word word : revisedWords) {
            Character character = map.get(word.getValue().toLowerCase());
            if (character != null) {
                revCharBuilder.append(character);
            } else {
                revCharBuilder.append(x);
                map.put(word.getValue().toLowerCase(), x++);
            }
        }

        if (orCharBuilder.toString().length() != originalWords.size() || revCharBuilder.toString().length() != revisedWords.size()) {
            throw new IllegalStateException("The impossible just happened...wtf??");
        }

        return new WordToCharContext(orCharBuilder.toString(), revCharBuilder.toString());
    }

    private static void verifyResult(LinkedList<Diff> phase1, LinkedList<Word> originalWords, LinkedList<Word> revisedWords) {
        int originalSize = 0;
        int revisedSize = 0;
        for (Diff diff : phase1) {
            switch (diff.operation) {
                case DELETE:
                    originalSize += diff.text.length();
                    break;
                case EQUAL:
                    originalSize += diff.text.length();
                    revisedSize += diff.text.length();
                    break;
                case INSERT:
                    revisedSize += diff.text.length();
                    break;
            }
        }

        if (originalSize != originalWords.size() || revisedSize != revisedWords.size()) {
            throw new IllegalStateException("Validation failed");
        }
    }

    private static String[] print(LinkedList<Diff> phase1, LinkedList<Diff> phase2, LinkedList<Word> originalWords,
                                  LinkedList<Word> revisedWords, LinkedList<Word> verifiedWords, ThreeWayDiffContext context,
                                  WordExtractionResult originalWordExtractionResult, WordExtractionResult verifiedWordExtractionResult) {
        List<String> originalArgs = new ArrayList<String>();
        List<String> verifiedArgs = new ArrayList<String>();

        String[] change = extractStartEndFromTemplate(context.getOriginalChangeTemplate());
        String[] complexDelete = extractStartEndFromTemplate(context.getComplexDeleteTemplate());
        String[] complexInsert = extractStartEndFromTemplate(context.getComplexInsertTemplate());
        String[] originalComplexChange = extractStartEndFromTemplate(context.getOriginalComplexChangeTemplate());

        int originalIndex = 0;
        while (phase1.peek() != null) {
            Diff poll = phase1.poll();

            switch (poll.operation) {
                case DELETE:
                    for (int i = 0; i < poll.text.length(); i++) {
                        originalArgs.add(change[0] + originalWords.get(originalIndex++).getValue() + change[1]);
                    }
                    break;
                case INSERT:
                    break;
                case EQUAL:
                    for (int i = 0; i < poll.text.length(); i++) {
                        Word word = originalWords.get(originalIndex++);
                        if (word.isComplexDelete() && !word.getValue().trim().isEmpty()) {
                            originalArgs.add(originalComplexChange[0] + word.getValue() + originalComplexChange[1]);
                        } else if (word.isCleanup()) {
                            originalArgs.add(change[0] + word.getValue() + change[1]);
                        } else {
                            originalArgs.add(word.getValue());
                        }
                    }
                    break;
            }
        }

        int revisedIndex = 0;
        int verifiedIndex = 0;
        while (phase2.peek() != null) {
            Diff actual = phase2.poll();

            switch (actual.operation) {
                case DELETE:
                    if (phase2.peek() != null && phase2.peek().operation == Operation.INSERT) {
                        Diff inserted = phase2.poll();
                        verifiedArgs.add(printComplexChange(actual, inserted,
                                revisedWords.subList(revisedIndex, revisedIndex + actual.text.length()),
                                verifiedWords.subList(verifiedIndex, verifiedIndex + inserted.text.length()), context));
                        revisedIndex += actual.text.length();
                        verifiedIndex += inserted.text.length();
                        for (int i = 0; i < inserted.text.length() - 1; i++) {
                            verifiedArgs.add("");
                        }
                    } else {
                        StringBuffer deletionBuilder = new StringBuffer();
                        for (int i = 0; i < actual.text.length(); i++) {
                            deletionBuilder.append(complexDelete[0]).append(revisedWords.get(revisedIndex++).getValue())
                                    .append(complexDelete[1]);
                        }
                        verifiedArgs.add(deletionBuilder.toString());
                        // change the template to take this additional deletion

                        verifiedWordExtractionResult.template += "{}";

                    }
                    break;
                case INSERT:
                    for (int i = 0; i < actual.text.length(); i++) {
                        verifiedArgs.add(complexInsert[0] + verifiedWords.get(verifiedIndex++).getValue() + complexInsert[1]);
                    }
                    break;
                case EQUAL:
                    for (int i = 0; i < actual.text.length(); i++) {
                        Word word = revisedWords.get(revisedIndex++);
                        if (word.isChange() || word.isInsert() || word.isCleanup()) {
                            verifiedArgs.add(change[0] + word.getValue() + change[1]);
                        } else {
                            verifiedArgs.add(word.getValue());
                        }
                        verifiedIndex++;
                    }
                    break;
                default:
                    break;
            }
        }

        return new String[]{MessageFormatter.arrayFormat(originalWordExtractionResult.template, originalArgs.toArray()).getMessage(),
                MessageFormatter.arrayFormat(verifiedWordExtractionResult.template, verifiedArgs.toArray()).getMessage()};
    }

    private static String printComplexChange(Diff original, Diff revised, List<Word> revisedWords, List<Word> verifiedWords,
                                             ThreeWayDiffContext context) {
        String[] complexDelete = extractStartEndFromTemplate(context.getComplexDeleteTemplate());
        String[] complexInsert = extractStartEndFromTemplate(context.getComplexInsertTemplate());

        final StringBuilder html = new StringBuilder();

        switch (context.getDiffMethod()) {
            case CHARACTER:
                printCharaterDiff(original, revised, revisedWords, verifiedWords, html, context);
                break;
            case WORD:
                LinkedList<Diff> diffs = DMPDelegator.INSTANCE.diff_main(original.text, revised.text);
                int revisedIndex = 0;
                int verifiedIndex = 0;
                for (final Diff diff : diffs) {
                    switch (diff.operation) {
                        case INSERT:
                            html.append(complexInsert[0]);
                            for (int i = 0; i < diff.text.length(); i++) {
                                html.append(verifiedWords.get(verifiedIndex++).getValue());
                            }
                            html.append(complexInsert[1]);
                            break;
                        case DELETE:
                            html.append(complexDelete[0]);
                            for (int i = 0; i < diff.text.length(); i++) {
                                html.append(revisedWords.get(revisedIndex++).getValue());
                            }
                            html.append(complexDelete[1]);
                            break;
                        case EQUAL:
                            for (int i = 0; i < diff.text.length(); i++) {
                                html.append(revisedWords.get(revisedIndex++).getValue());
                            }
                            break;
                    }
                }
                break;
        }

        return html.toString();
    }

    private static void printCharaterDiff(Diff revised, Diff verified, List<Word> revisedWords, List<Word> verifiedWords,
                                          final StringBuilder html, ThreeWayDiffContext context) {
        String[] complexDelete = extractStartEndFromTemplate(context.getComplexDeleteTemplate());
        String[] complexInsert = extractStartEndFromTemplate(context.getComplexInsertTemplate());
        String[] complexChange = extractStartEndFromTemplate(context.getComplexChangeTemplate());

        StringBuilder revisedToDiff = new StringBuilder();
        for (int i = 0; i < revised.text.length(); i++) {
            revisedToDiff.append(revisedWords.get(i).getValue());
        }

        StringBuilder verifiedToDiff = new StringBuilder();
        for (int i = 0; i < verified.text.length(); i++) {
            verifiedToDiff.append(verifiedWords.get(i).getValue());
        }

        LinkedList<Diff> diffResult = DMPDelegator.INSTANCE.diff_main(revisedToDiff.toString(), verifiedToDiff.toString());
        html.append(complexChange[0]);
        for (final Diff diff : diffResult) {
            switch (diff.operation) {
                case INSERT:
                    html.append(complexInsert[0]);
                    html.append(diff.text);
                    html.append(complexInsert[1]);
                    break;
                case DELETE:
                    html.append(complexDelete[0]);
                    html.append(diff.text);
                    html.append(complexDelete[1]);
                    break;
                case EQUAL:
                    html.append(diff.text);
                    break;
            }
        }
        html.append(complexChange[1]);
    }

    private static void processSecondPhase(LinkedList<Word> revisedWords, LinkedList<Word> verifiedWords, LinkedList<Diff> phase2) {
        ListIterator<Diff> listIterator = phase2.listIterator();

        int revisedIndex = 0;
        int verifiedIndex = 0;
        while (listIterator.hasNext()) {
            Diff next = listIterator.next();
            String sequence = next.text;
            switch (next.operation) {
                case DELETE:
                    for (int i = 0; i < sequence.length(); i++) {
                        Word deletedWord = revisedWords.get(revisedIndex++);
                        if (deletedWord.getReference() != null) {
                            deletedWord.getReference().setComplexDelete();
                        }
                    }
                    break;
                case INSERT:
                    for (int i = 0; i < sequence.length(); i++) {
                        Word insertedWord = verifiedWords.get(verifiedIndex++);
                        insertedWord.setComplexInsert();
                    }
                    break;
                case EQUAL:
                    for (int i = 0; i < sequence.length(); i++) {
                        Word equalWord = revisedWords.get(revisedIndex);
                        Word verifiedWord = verifiedWords.get(verifiedIndex++);

                        if (!equalWord.equals(verifiedWord)) {
                            throw new IllegalStateException();
                        }

                        verifiedWord.takeOverChanges(equalWord);
                        revisedWords.set(revisedIndex++, verifiedWord);
                    }
                    break;
            }

        }
    }

    private static void processFirstPhase(LinkedList<Word> originalWords, LinkedList<Word> revisedWords, LinkedList<Diff> phase1) {
        ListIterator<Diff> listIterator = phase1.listIterator();

        int originalIndex = 0;
        int revisedIndex = 0;
        while (listIterator.hasNext()) {
            Diff next = listIterator.next();
            String sequence = next.text;
            switch (next.operation) {
                case DELETE:
                    for (int i = 0; i < sequence.length(); i++) {
                        Word originalWord = originalWords.get(originalIndex++);
                        originalWord.setDelete();
                    }
                    break;
                case INSERT:
                    for (int i = 0; i < sequence.length(); i++) {
                        Word insertedWord = revisedWords.get(revisedIndex++);
                        insertedWord.setInsert();
                    }
                    break;
                case EQUAL:
                    for (int i = 0; i < sequence.length(); i++) {
                        Word equalWord = originalWords.get(originalIndex++);
                        Word revisedWord = revisedWords.get(revisedIndex++);

                        if (!equalWord.equals(revisedWord)) {
                            throw new IllegalStateException();
                        }

                        revisedWord.setReference(equalWord);
                    }
                    break;
            }
        }
    }

}
