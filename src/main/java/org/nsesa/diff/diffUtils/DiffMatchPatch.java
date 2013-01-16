/*
 * Diff Match and Patch
 *
 * Copyright 2006 Google Inc.
 * http://code.google.com/p/google-diff-match-patch/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.nsesa.diff.diffUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Pattern;

/*
 * Functions for diff, match and patch.
 * Computes the difference between two texts to create a patch.
 * Applies the patch onto another text, allowing for errors.
 *
 * @author fraser@google.com (Neil Fraser)
 */

/**
 * Class containing the diff, match and patch methods. Also contains the
 * behaviour settings.
 */
class DiffMatchPatch {

    /**
     * Number of seconds to map a diff before giving up (0 for infinity).
     */
    public float Diff_Timeout = 1.0f;

    /**
     * Cost of an empty edit operation in terms of edit characters.
     */
    public short Diff_EditCost = 4;

    /**
     * Internal class for returning results from diff_linesToChars(). Other less
     * paranoid languages just use a three-element array.
     */
    protected static class LinesToCharsResult {
        protected String chars1;
        protected String chars2;
        protected List<String> lineArray;

        protected LinesToCharsResult(final String chars1, final String chars2, final List<String> lineArray) {
            this.chars1 = chars1;
            this.chars2 = chars2;
            this.lineArray = lineArray;
        }
    }

    // DIFF FUNCTIONS

    /**
     * The data structure representing a diff is a Linked list of Diff objects:
     * {Diff(Operation.DELETE, "Hello"), Diff(Operation.INSERT, "Goodbye"),
     * Diff(Operation.EQUAL, " world.")} which means: delete "Hello", add
     * "Goodbye" and keep " world."
     */
    public enum Operation {
        DELETE, INSERT, EQUAL
    }

    /**
     * Find the differences between two texts. Run a faster, slightly less
     * optimal diff. This method allows the 'checklines' of diff_main() to be
     * optional. Most of the time checklines is wanted, so default to true.
     *
     * @param text1 Old string to be diffed.
     * @param text2 New string to be diffed.
     * @return Linked List of Diff objects.
     */
    public LinkedList<Diff> diff_main(final String text1, final String text2) {
        return diff_main(text1, text2, true);
    }

    /**
     * Find the differences between two texts.
     *
     * @param text1      Old string to be diffed.
     * @param text2      New string to be diffed.
     * @param checklines Speedup flag. If false, then don't run a line-level diff first
     *                   to identify the changed areas. If true, then run a faster
     *                   slightly less optimal diff.
     * @return Linked List of Diff objects.
     */
    public LinkedList<Diff> diff_main(final String text1, final String text2, final boolean checklines) {
        // Set a deadline by which time the diff must be complete.
        long deadline;
        if (Diff_Timeout <= 0) {
            deadline = Long.MAX_VALUE;
        } else {
            deadline = System.currentTimeMillis() + (long) (Diff_Timeout * 1000);
        }
        return diff_main(text1, text2, checklines, deadline);
    }

    /**
     * Find the differences between two texts. Simplifies the problem by
     * stripping any common prefix or suffix off the texts before diffing.
     *
     * @param text1      Old string to be diffed.
     * @param text2      New string to be diffed.
     * @param checklines Speedup flag. If false, then don't run a line-level diff first
     *                   to identify the changed areas. If true, then run a faster
     *                   slightly less optimal diff.
     * @param deadline   Time when the diff should be complete by. Used internally for
     *                   recursive calls. Users should set DiffTimeout instead.
     * @return Linked List of Diff objects.
     */
    private LinkedList<Diff> diff_main(String text1, String text2, final boolean checklines, final long deadline) {
        // Check for null inputs.
        if (text1 == null || text2 == null) {
            throw new IllegalArgumentException("Null inputs. (diff_main)");
        }

        // Check for equality (speedup).
        LinkedList<Diff> diffs;
        if (text1.equals(text2)) {
            diffs = new LinkedList<Diff>();
            if (text1.length() != 0) {
                diffs.add(new Diff(Operation.EQUAL, text1));
            }
            return diffs;
        }

        // Trim off common prefix (speedup).
        int commonlength = diff_commonPrefix(text1, text2);
        final String commonprefix = text1.substring(0, commonlength);
        text1 = text1.substring(commonlength);
        text2 = text2.substring(commonlength);

        // Trim off common suffix (speedup).
        commonlength = diff_commonSuffix(text1, text2);
        final String commonsuffix = text1.substring(text1.length() - commonlength);
        text1 = text1.substring(0, text1.length() - commonlength);
        text2 = text2.substring(0, text2.length() - commonlength);

        // Compute the diff on the middle block.
        diffs = diff_compute(text1, text2, checklines, deadline);

        // Restore the prefix and suffix.
        if (commonprefix.length() != 0) {
            diffs.addFirst(new Diff(Operation.EQUAL, commonprefix));
        }
        if (commonsuffix.length() != 0) {
            diffs.addLast(new Diff(Operation.EQUAL, commonsuffix));
        }

        diff_cleanupMerge(diffs);
        return diffs;
    }

    /**
     * Find the differences between two texts. Assumes that the texts do not
     * have any common prefix or suffix.
     *
     * @param text1      Old string to be diffed.
     * @param text2      New string to be diffed.
     * @param checklines Speedup flag. If false, then don't run a line-level diff first
     *                   to identify the changed areas. If true, then run a faster
     *                   slightly less optimal diff.
     * @param deadline   Time when the diff should be complete by.
     * @return Linked List of Diff objects.
     */
    private LinkedList<Diff> diff_compute(final String text1, final String text2, final boolean checklines, final long deadline) {
        LinkedList<Diff> diffs = new LinkedList<Diff>();

        if (text1.length() == 0) {
            // Just add some text (speedup).
            diffs.add(new Diff(Operation.INSERT, text2));
            return diffs;
        }

        if (text2.length() == 0) {
            // Just delete some text (speedup).
            diffs.add(new Diff(Operation.DELETE, text1));
            return diffs;
        }

        String longtext = text1.length() > text2.length() ? text1 : text2;
        String shorttext = text1.length() > text2.length() ? text2 : text1;
        final int i = longtext.indexOf(shorttext);
        if (i != -1) {
            // Shorter text is inside the longer text (speedup).
            final Operation op = (text1.length() > text2.length()) ? Operation.DELETE : Operation.INSERT;
            diffs.add(new Diff(op, longtext.substring(0, i)));
            diffs.add(new Diff(Operation.EQUAL, shorttext));
            diffs.add(new Diff(op, longtext.substring(i + shorttext.length())));
            return diffs;
        }

        if (shorttext.length() == 1) {
            // Single character string.
            // After the previous speedup, the character can't be an equality.
            diffs.add(new Diff(Operation.DELETE, text1));
            diffs.add(new Diff(Operation.INSERT, text2));
            return diffs;
        }
        longtext = shorttext = null; // Garbage collect.

        // Check to see if the problem can be split in two.
        final String[] hm = diff_halfMatch(text1, text2);
        if (hm != null) {
            // A half-match was found, sort out the return data.
            final String text1_a = hm[0];
            final String text1_b = hm[1];
            final String text2_a = hm[2];
            final String text2_b = hm[3];
            final String mid_common = hm[4];
            // Send both pairs off for separate processing.
            final LinkedList<Diff> diffs_a = diff_main(text1_a, text2_a, checklines, deadline);
            final LinkedList<Diff> diffs_b = diff_main(text1_b, text2_b, checklines, deadline);
            // Merge the results.
            diffs = diffs_a;
            diffs.add(new Diff(Operation.EQUAL, mid_common));
            diffs.addAll(diffs_b);
            return diffs;
        }

        if (checklines && text1.length() > 100 && text2.length() > 100) {
            return diff_lineMode(text1, text2, deadline);
        }

        return diff_bisect(text1, text2, deadline);
    }

    /**
     * Do a quick line-level diff on both strings, then rediff the parts for
     * greater accuracy. This speedup can produce non-minimal diffs.
     *
     * @param text1    Old string to be diffed.
     * @param text2    New string to be diffed.
     * @param deadline Time when the diff should be complete by.
     * @return Linked List of Diff objects.
     */
    private LinkedList<Diff> diff_lineMode(String text1, String text2, final long deadline) {
        // Scan the text on a line-by-line basis first.
        final LinesToCharsResult b = diff_linesToChars(text1, text2);
        text1 = b.chars1;
        text2 = b.chars2;
        final List<String> linearray = b.lineArray;

        final LinkedList<Diff> diffs = diff_main(text1, text2, false, deadline);

        // Convert the diff back to original text.
        diff_charsToLines(diffs, linearray);
        // Eliminate freak matches (e.g. blank lines)
        diff_cleanupSemantic(diffs);

        // Rediff any replacement blocks, this time character-by-character.
        // Add a dummy entry at the end.
        diffs.add(new Diff(Operation.EQUAL, ""));
        int count_delete = 0;
        int count_insert = 0;
        String text_delete = "";
        String text_insert = "";
        final ListIterator<Diff> pointer = diffs.listIterator();
        Diff thisDiff = pointer.next();
        while (thisDiff != null) {
            switch (thisDiff.operation) {
                case INSERT:
                    count_insert++;
                    text_insert += thisDiff.text;
                    break;
                case DELETE:
                    count_delete++;
                    text_delete += thisDiff.text;
                    break;
                case EQUAL:
                    // Upon reaching an equality, check for prior redundancies.
                    if (count_delete >= 1 && count_insert >= 1) {
                        // Delete the offending records and add the merged ones.
                        pointer.previous();
                        for (int j = 0; j < count_delete + count_insert; j++) {
                            pointer.previous();
                            pointer.remove();
                        }
                        for (final Diff newDiff : diff_main(text_delete, text_insert, false, deadline)) {
                            pointer.add(newDiff);
                        }
                    }
                    count_insert = 0;
                    count_delete = 0;
                    text_delete = "";
                    text_insert = "";
                    break;
            }
            thisDiff = pointer.hasNext() ? pointer.next() : null;
        }
        diffs.removeLast(); // Remove the dummy entry at the end.

        return diffs;
    }

    /**
     * Find the 'middle snake' of a diff, split the problem in two and return
     * the recursively constructed diff. See Myers 1986 paper: An O(ND)
     * Difference Algorithm and Its Variations.
     *
     * @param text1    Old string to be diffed.
     * @param text2    New string to be diffed.
     * @param deadline Time at which to bail if not yet complete.
     * @return LinkedList of Diff objects.
     */
    protected LinkedList<Diff> diff_bisect(final String text1, final String text2, final long deadline) {
        // Cache the text lengths to prevent multiple calls.
        final int text1_length = text1.length();
        final int text2_length = text2.length();
        final int max_d = (text1_length + text2_length + 1) / 2;
        final int v_offset = max_d;
        final int v_length = 2 * max_d;
        final int[] v1 = new int[v_length];
        final int[] v2 = new int[v_length];
        for (int x = 0; x < v_length; x++) {
            v1[x] = -1;
            v2[x] = -1;
        }
        v1[v_offset + 1] = 0;
        v2[v_offset + 1] = 0;
        final int delta = text1_length - text2_length;
        // If the total number of characters is odd, then the front path will
        // collide with the reverse path.
        final boolean front = (delta % 2 != 0);
        // Offsets for start and end of k loop.
        // Prevents mapping of space beyond the grid.
        int k1start = 0;
        int k1end = 0;
        int k2start = 0;
        int k2end = 0;
        for (int d = 0; d < max_d; d++) {
            // Bail out if deadline is reached.
            if (System.currentTimeMillis() > deadline) {
                break;
            }

            // Walk the front path one step.
            for (int k1 = -d + k1start; k1 <= d - k1end; k1 += 2) {
                final int k1_offset = v_offset + k1;
                int x1;
                if (k1 == -d || k1 != d && v1[k1_offset - 1] < v1[k1_offset + 1]) {
                    x1 = v1[k1_offset + 1];
                } else {
                    x1 = v1[k1_offset - 1] + 1;
                }
                int y1 = x1 - k1;
                while (x1 < text1_length && y1 < text2_length && text1.charAt(x1) == text2.charAt(y1)) {
                    x1++;
                    y1++;
                }
                v1[k1_offset] = x1;
                if (x1 > text1_length) {
                    // Ran off the right of the graph.
                    k1end += 2;
                } else if (y1 > text2_length) {
                    // Ran off the bottom of the graph.
                    k1start += 2;
                } else if (front) {
                    final int k2_offset = v_offset + delta - k1;
                    if (k2_offset >= 0 && k2_offset < v_length && v2[k2_offset] != -1) {
                        // Mirror x2 onto top-left coordinate system.
                        final int x2 = text1_length - v2[k2_offset];
                        if (x1 >= x2) {
                            // Overlap detected.
                            return diff_bisectSplit(text1, text2, x1, y1, deadline);
                        }
                    }
                }
            }

            // Walk the reverse path one step.
            for (int k2 = -d + k2start; k2 <= d - k2end; k2 += 2) {
                final int k2_offset = v_offset + k2;
                int x2;
                if (k2 == -d || k2 != d && v2[k2_offset - 1] < v2[k2_offset + 1]) {
                    x2 = v2[k2_offset + 1];
                } else {
                    x2 = v2[k2_offset - 1] + 1;
                }
                int y2 = x2 - k2;
                while (x2 < text1_length && y2 < text2_length && text1.charAt(text1_length - x2 - 1) == text2.charAt(text2_length - y2 - 1)) {
                    x2++;
                    y2++;
                }
                v2[k2_offset] = x2;
                if (x2 > text1_length) {
                    // Ran off the left of the graph.
                    k2end += 2;
                } else if (y2 > text2_length) {
                    // Ran off the top of the graph.
                    k2start += 2;
                } else if (!front) {
                    final int k1_offset = v_offset + delta - k2;
                    if (k1_offset >= 0 && k1_offset < v_length && v1[k1_offset] != -1) {
                        final int x1 = v1[k1_offset];
                        final int y1 = v_offset + x1 - k1_offset;
                        // Mirror x2 onto top-left coordinate system.
                        x2 = text1_length - x2;
                        if (x1 >= x2) {
                            // Overlap detected.
                            return diff_bisectSplit(text1, text2, x1, y1, deadline);
                        }
                    }
                }
            }
        }
        // Diff took too long and hit the deadline or
        // number of diffs equals number of characters, no commonality at all.
        final LinkedList<Diff> diffs = new LinkedList<Diff>();
        diffs.add(new Diff(Operation.DELETE, text1));
        diffs.add(new Diff(Operation.INSERT, text2));
        return diffs;
    }

    /**
     * Given the location of the 'middle snake', split the diff in two parts and
     * recurse.
     *
     * @param text1    Old string to be diffed.
     * @param text2    New string to be diffed.
     * @param x        Index of split point in text1.
     * @param y        Index of split point in text2.
     * @param deadline Time at which to bail if not yet complete.
     * @return LinkedList of Diff objects.
     */
    private LinkedList<Diff> diff_bisectSplit(final String text1, final String text2, final int x, final int y, final long deadline) {
        final String text1a = text1.substring(0, x);
        final String text2a = text2.substring(0, y);
        final String text1b = text1.substring(x);
        final String text2b = text2.substring(y);

        // Compute both diffs serially.
        final LinkedList<Diff> diffs = diff_main(text1a, text2a, false, deadline);
        final LinkedList<Diff> diffsb = diff_main(text1b, text2b, false, deadline);

        diffs.addAll(diffsb);
        return diffs;
    }

    /**
     * Split two texts into a list of strings. Reduce the texts to a string of
     * hashes where each Unicode character represents one line.
     *
     * @param text1 First string.
     * @param text2 Second string.
     * @return An object containing the encoded text1, the encoded text2 and the
     *         List of unique strings. The zeroth element of the List of unique
     *         strings is intentionally blank.
     */
    protected LinesToCharsResult diff_linesToChars(final String text1, final String text2) {
        final List<String> lineArray = new ArrayList<String>();
        final Map<String, Integer> lineHash = new HashMap<String, Integer>();
        // e.g. linearray[4] == "Hello\n"
        // e.g. linehash.get("Hello\n") == 4

        // "\x00" is a valid character, but various debuggers don't like it.
        // So we'll insert a junk entry to avoid generating a null character.
        lineArray.add("");

        final String chars1 = diff_linesToCharsMunge(text1, lineArray, lineHash);
        final String chars2 = diff_linesToCharsMunge(text2, lineArray, lineHash);
        return new LinesToCharsResult(chars1, chars2, lineArray);
    }

    /**
     * Split a text into a list of strings. Reduce the texts to a string of
     * hashes where each Unicode character represents one line.
     *
     * @param text      String to encode.
     * @param lineArray List of unique strings.
     * @param lineHash  Map of strings to indices.
     * @return Encoded string.
     */
    private String diff_linesToCharsMunge(final String text, final List<String> lineArray, final Map<String, Integer> lineHash) {
        int lineStart = 0;
        int lineEnd = -1;
        String line;
        final StringBuilder chars = new StringBuilder();
        // Walk the text, pulling out a substring for each line.
        // text.split('\n') would would temporarily double our memory footprint.
        // Modifying text would create many large strings to garbage collect.
        while (lineEnd < text.length() - 1) {
            lineEnd = text.indexOf('\n', lineStart);
            if (lineEnd == -1) {
                lineEnd = text.length() - 1;
            }
            line = text.substring(lineStart, lineEnd + 1);
            lineStart = lineEnd + 1;

            if (lineHash.containsKey(line)) {
                chars.append(String.valueOf((char) (int) lineHash.get(line)));
            } else {
                lineArray.add(line);
                lineHash.put(line, lineArray.size() - 1);
                chars.append(String.valueOf((char) (lineArray.size() - 1)));
            }
        }
        return chars.toString();
    }

    /**
     * Rehydrate the text in a diff from a string of line hashes to real lines
     * of text.
     *
     * @param diffs     LinkedList of Diff objects.
     * @param lineArray List of unique strings.
     */
    protected void diff_charsToLines(final LinkedList<Diff> diffs, final List<String> lineArray) {
        StringBuilder text;
        for (final Diff diff : diffs) {
            text = new StringBuilder();
            for (int y = 0; y < diff.text.length(); y++) {
                text.append(lineArray.get(diff.text.charAt(y)));
            }
            diff.text = text.toString();
        }
    }

    /**
     * Determine the common prefix of two strings
     *
     * @param text1 First string.
     * @param text2 Second string.
     * @return The number of characters common to the start of each string.
     */
    public int diff_commonPrefix(final String text1, final String text2) {
        // Performance analysis: http://neil.fraser.name/news/2007/10/09/
        final int n = Math.min(text1.length(), text2.length());
        for (int i = 0; i < n; i++) {
            if (text1.charAt(i) != text2.charAt(i)) {
                return i;
            }
        }
        return n;
    }

    /**
     * Determine the common suffix of two strings
     *
     * @param text1 First string.
     * @param text2 Second string.
     * @return The number of characters common to the end of each string.
     */
    public int diff_commonSuffix(final String text1, final String text2) {
        // Performance analysis: http://neil.fraser.name/news/2007/10/09/
        final int text1_length = text1.length();
        final int text2_length = text2.length();
        final int n = Math.min(text1_length, text2_length);
        for (int i = 1; i <= n; i++) {
            if (text1.charAt(text1_length - i) != text2.charAt(text2_length - i)) {
                return i - 1;
            }
        }
        return n;
    }

    /**
     * Determine if the suffix of one string is the prefix of another.
     *
     * @param text1 First string.
     * @param text2 Second string.
     * @return The number of characters common to the end of the first string
     *         and the start of the second string.
     */
    protected int diff_commonOverlap(String text1, String text2) {
        // Cache the text lengths to prevent multiple calls.
        final int text1_length = text1.length();
        final int text2_length = text2.length();
        // Eliminate the null case.
        if (text1_length == 0 || text2_length == 0) {
            return 0;
        }
        // Truncate the longer string.
        if (text1_length > text2_length) {
            text1 = text1.substring(text1_length - text2_length);
        } else if (text1_length < text2_length) {
            text2 = text2.substring(0, text1_length);
        }
        final int text_length = Math.min(text1_length, text2_length);
        // Quick check for the worst case.
        if (text1.equals(text2)) {
            return text_length;
        }

        // Start by looking for a single character match
        // and increase length until no match is found.
        // Performance analysis: http://neil.fraser.name/news/2010/11/04/
        int best = 0;
        int length = 1;
        while (true) {
            final String pattern = text1.substring(text_length - length);
            final int found = text2.indexOf(pattern);
            if (found == -1) {
                return best;
            }
            length += found;
            if (found == 0 || text1.substring(text_length - length).equals(text2.substring(0, length))) {
                best = length;
                length++;
            }
        }
    }

    /**
     * Do the two texts share a substring which is at least half the length of
     * the longer text? This speedup can produce non-minimal diffs.
     *
     * @param text1 First string.
     * @param text2 Second string.
     * @return Five element String array, containing the prefix of text1, the
     *         suffix of text1, the prefix of text2, the suffix of text2 and the
     *         common middle. Or null if there was no match.
     */
    protected String[] diff_halfMatch(final String text1, final String text2) {
        if (Diff_Timeout <= 0) {
            // Don't risk returning a non-optimal diff if we have unlimited
            // time.
            return null;
        }
        final String longtext = text1.length() > text2.length() ? text1 : text2;
        final String shorttext = text1.length() > text2.length() ? text2 : text1;
        if (longtext.length() < 4 || shorttext.length() * 2 < longtext.length()) {
            return null; // Pointless.
        }

        // First check if the second quarter is the seed for a half-match.
        final String[] hm1 = diff_halfMatchI(longtext, shorttext, (longtext.length() + 3) / 4);
        // Check again based on the third quarter.
        final String[] hm2 = diff_halfMatchI(longtext, shorttext, (longtext.length() + 1) / 2);
        String[] hm;
        if (hm1 == null && hm2 == null) {
            return null;
        } else if (hm2 == null) {
            hm = hm1;
        } else if (hm1 == null) {
            hm = hm2;
        } else {
            // Both matched. Select the longest.
            hm = hm1[4].length() > hm2[4].length() ? hm1 : hm2;
        }

        // A half-match was found, sort out the return data.
        if (text1.length() > text2.length()) {
            return hm;
            // return new String[]{hm[0], hm[1], hm[2], hm[3], hm[4]};
        } else {
            return new String[]{hm[2], hm[3], hm[0], hm[1], hm[4]};
        }
    }

    /**
     * Does a substring of shorttext exist within longtext such that the
     * substring is at least half the length of longtext?
     *
     * @param longtext  Longer string.
     * @param shorttext Shorter string.
     * @param i         Start index of quarter length substring within longtext.
     * @return Five element String array, containing the prefix of longtext, the
     *         suffix of longtext, the prefix of shorttext, the suffix of
     *         shorttext and the common middle. Or null if there was no match.
     */
    private String[] diff_halfMatchI(final String longtext, final String shorttext, final int i) {
        // Start with a 1/4 length substring at position i as a seed.
        final String seed = longtext.substring(i, i + longtext.length() / 4);
        int j = -1;
        String best_common = "";
        String best_longtext_a = "", best_longtext_b = "";
        String best_shorttext_a = "", best_shorttext_b = "";
        while ((j = shorttext.indexOf(seed, j + 1)) != -1) {
            final int prefixLength = diff_commonPrefix(longtext.substring(i), shorttext.substring(j));
            final int suffixLength = diff_commonSuffix(longtext.substring(0, i), shorttext.substring(0, j));
            if (best_common.length() < suffixLength + prefixLength) {
                best_common = shorttext.substring(j - suffixLength, j) + shorttext.substring(j, j + prefixLength);
                best_longtext_a = longtext.substring(0, i - suffixLength);
                best_longtext_b = longtext.substring(i + prefixLength);
                best_shorttext_a = shorttext.substring(0, j - suffixLength);
                best_shorttext_b = shorttext.substring(j + prefixLength);
            }
        }
        if (best_common.length() * 2 >= longtext.length()) {
            return new String[]{best_longtext_a, best_longtext_b, best_shorttext_a, best_shorttext_b, best_common};
        } else {
            return null;
        }
    }

    /**
     * Reduce the number of edits by eliminating semantically trivial
     * equalities.
     *
     * @param diffs LinkedList of Diff objects.
     */
    public void diff_cleanupSemantic(final LinkedList<Diff> diffs) {
        if (diffs.isEmpty()) {
            return;
        }
        boolean changes = false;
        final Stack<Diff> equalities = new Stack<Diff>(); // Stack of qualities.
        String lastequality = null; // Always equal to
        // equalities.lastElement().text
        ListIterator<Diff> pointer = diffs.listIterator();
        // Number of characters that changed prior to the equality.
        int length_insertions1 = 0;
        int length_deletions1 = 0;
        // Number of characters that changed after the equality.
        int length_insertions2 = 0;
        int length_deletions2 = 0;
        Diff thisDiff = pointer.next();
        while (thisDiff != null) {
            if (thisDiff.operation == Operation.EQUAL) {
                // Equality found.
                equalities.push(thisDiff);
                length_insertions1 = length_insertions2;
                length_deletions1 = length_deletions2;
                length_insertions2 = 0;
                length_deletions2 = 0;
                lastequality = thisDiff.text;
            } else {
                // An insertion or deletion.
                if (thisDiff.operation == Operation.INSERT) {
                    length_insertions2 += thisDiff.text.length();
                } else {
                    length_deletions2 += thisDiff.text.length();
                }
                // Eliminate an equality that is smaller or equal to the edits
                // on both
                // sides of it.
                if (lastequality != null && (lastequality.length() <= Math.max(length_insertions1, length_deletions1))
                        && (lastequality.length() <= Math.max(length_insertions2, length_deletions2))) {
                    // System.out.println("Splitting: '" + lastequality + "'");
                    // Walk back to offending equality.
                    while (thisDiff != equalities.lastElement()) {
                        thisDiff = pointer.previous();
                    }
                    pointer.next();

                    // Replace equality with a delete.
                    pointer.set(new Diff(Operation.DELETE, lastequality));
                    // Insert a corresponding an insert.
                    pointer.add(new Diff(Operation.INSERT, lastequality));

                    equalities.pop(); // Throw away the equality we just
                    // deleted.
                    if (!equalities.empty()) {
                        // Throw away the previous equality (it needs to be
                        // reevaluated).
                        equalities.pop();
                    }
                    if (equalities.empty()) {
                        // There are no previous equalities, walk back to the
                        // start.
                        while (pointer.hasPrevious()) {
                            pointer.previous();
                        }
                    } else {
                        // There is a safe equality we can fall back to.
                        thisDiff = equalities.lastElement();
                        while (thisDiff != pointer.previous()) {
                            // Intentionally empty loop.
                        }
                    }

                    length_insertions1 = 0; // Reset the counters.
                    length_insertions2 = 0;
                    length_deletions1 = 0;
                    length_deletions2 = 0;
                    lastequality = null;
                    changes = true;
                }
            }
            thisDiff = pointer.hasNext() ? pointer.next() : null;
        }

        // Normalize the diff.
        if (changes) {
            diff_cleanupMerge(diffs);
        }
        diff_cleanupSemanticLossless(diffs);

        // Find any overlaps between deletions and insertions.
        // e.g: <del>abcxxx</del><ins>xxxdef</ins>
        // -> <del>abc</del>xxx<ins>def</ins>
        // Only extract an overlap if it is as big as the edit ahead or behind
        // it.
        pointer = diffs.listIterator();
        Diff prevDiff = null;
        thisDiff = null;
        if (pointer.hasNext()) {
            prevDiff = pointer.next();
            if (pointer.hasNext()) {
                thisDiff = pointer.next();
            }
        }
        while (thisDiff != null) {
            if (prevDiff.operation == Operation.DELETE && thisDiff.operation == Operation.INSERT) {
                final String deletion = prevDiff.text;
                final String insertion = thisDiff.text;
                final int overlap_length = this.diff_commonOverlap(deletion, insertion);
                if (overlap_length >= deletion.length() / 2.0 || overlap_length >= insertion.length() / 2.0) {
                    // Overlap found. Insert an equality and trim the
                    // surrounding edits.
                    pointer.previous();
                    pointer.add(new Diff(Operation.EQUAL, insertion.substring(0, overlap_length)));
                    prevDiff.text = deletion.substring(0, deletion.length() - overlap_length);
                    thisDiff.text = insertion.substring(overlap_length);
                    // pointer.add inserts the element before the cursor, so
                    // there is
                    // no need to step past the new element.
                }
                thisDiff = pointer.hasNext() ? pointer.next() : null;
            }
            prevDiff = thisDiff;
            thisDiff = pointer.hasNext() ? pointer.next() : null;
        }
    }

    /**
     * Look for single edits surrounded on both sides by equalities which can be
     * shifted sideways to align the edit to a word boundary. e.g: The c<ins>at
     * c</ins>ame. -> The <ins>cat </ins>came.
     *
     * @param diffs LinkedList of Diff objects.
     */
    public void diff_cleanupSemanticLossless(final LinkedList<Diff> diffs) {
        String equality1, edit, equality2;
        String commonString;
        int commonOffset;
        int score, bestScore;
        String bestEquality1, bestEdit, bestEquality2;
        // Create a new iterator at the start.
        final ListIterator<Diff> pointer = diffs.listIterator();
        Diff prevDiff = pointer.hasNext() ? pointer.next() : null;
        Diff thisDiff = pointer.hasNext() ? pointer.next() : null;
        Diff nextDiff = pointer.hasNext() ? pointer.next() : null;
        // Intentionally ignore the first and last element (don't need
        // checking).
        while (nextDiff != null) {
            if (prevDiff.operation == Operation.EQUAL && nextDiff.operation == Operation.EQUAL) {
                // This is a single edit surrounded by equalities.
                equality1 = prevDiff.text;
                edit = thisDiff.text;
                equality2 = nextDiff.text;

                // First, shift the edit as far left as possible.
                commonOffset = diff_commonSuffix(equality1, edit);
                if (commonOffset != 0) {
                    commonString = edit.substring(edit.length() - commonOffset);
                    equality1 = equality1.substring(0, equality1.length() - commonOffset);
                    edit = commonString + edit.substring(0, edit.length() - commonOffset);
                    equality2 = commonString + equality2;
                }

                // Second, step character by character right, looking for the
                // best fit.
                bestEquality1 = equality1;
                bestEdit = edit;
                bestEquality2 = equality2;
                bestScore = diff_cleanupSemanticScore(equality1, edit) + diff_cleanupSemanticScore(edit, equality2);
                while (edit.length() != 0 && equality2.length() != 0 && edit.charAt(0) == equality2.charAt(0)) {
                    equality1 += edit.charAt(0);
                    edit = edit.substring(1) + equality2.charAt(0);
                    equality2 = equality2.substring(1);
                    score = diff_cleanupSemanticScore(equality1, edit) + diff_cleanupSemanticScore(edit, equality2);
                    // The >= encourages trailing rather than leading whitespace
                    // on edits.
                    if (score >= bestScore) {
                        bestScore = score;
                        bestEquality1 = equality1;
                        bestEdit = edit;
                        bestEquality2 = equality2;
                    }
                }

                if (!prevDiff.text.equals(bestEquality1)) {
                    // We have an improvement, save it back to the diff.
                    if (bestEquality1.length() != 0) {
                        prevDiff.text = bestEquality1;
                    } else {
                        pointer.previous(); // Walk past nextDiff.
                        pointer.previous(); // Walk past thisDiff.
                        pointer.previous(); // Walk past prevDiff.
                        pointer.remove(); // Delete prevDiff.
                        pointer.next(); // Walk past thisDiff.
                        pointer.next(); // Walk past nextDiff.
                    }
                    thisDiff.text = bestEdit;
                    if (bestEquality2.length() != 0) {
                        nextDiff.text = bestEquality2;
                    } else {
                        pointer.remove(); // Delete nextDiff.
                        nextDiff = thisDiff;
                        thisDiff = prevDiff;
                    }
                }
            }
            prevDiff = thisDiff;
            thisDiff = nextDiff;
            nextDiff = pointer.hasNext() ? pointer.next() : null;
        }
    }

    /**
     * Given two strings, compute a score representing whether the internal
     * boundary falls on logical boundaries. Scores range from 5 (best) to 0
     * (worst).
     *
     * @param one First string.
     * @param two Second string.
     * @return The score.
     */
    private int diff_cleanupSemanticScore(final String one, final String two) {
        if (one.length() == 0 || two.length() == 0) {
            // Edges are the best.
            return 5;
        }

        // Each port of this function behaves slightly differently due to
        // subtle differences in each language's definition of things like
        // 'whitespace'. Since this function's purpose is largely cosmetic,
        // the choice has been made to use each language's native features
        // rather than force total conformity.
        int score = 0;
        // One point for non-alphanumeric.
        if (!Character.isLetterOrDigit(one.charAt(one.length() - 1)) || !Character.isLetterOrDigit(two.charAt(0))) {
            score++;
            // Two points for whitespace.
            if (Character.isWhitespace(one.charAt(one.length() - 1)) || Character.isWhitespace(two.charAt(0))) {
                score++;
                // Three points for line breaks.
                if (Character.getType(one.charAt(one.length() - 1)) == Character.CONTROL
                        || Character.getType(two.charAt(0)) == Character.CONTROL) {
                    score++;
                    // Four points for blank lines.
                    if (BLANKLINEEND.matcher(one).find() || BLANKLINESTART.matcher(two).find()) {
                        score++;
                    }
                }
            }
        }
        return score;
    }

    private final Pattern BLANKLINEEND = Pattern.compile("\\n\\r?\\n\\Z", Pattern.DOTALL);
    private final Pattern BLANKLINESTART = Pattern.compile("\\A\\r?\\n\\r?\\n", Pattern.DOTALL);

    /**
     * Reduce the number of edits by eliminating operationally trivial
     * equalities.
     *
     * @param diffs LinkedList of Diff objects.
     */
    public void diff_cleanupEfficiency(final LinkedList<Diff> diffs) {
        if (diffs.isEmpty()) {
            return;
        }
        boolean changes = false;
        final Stack<Diff> equalities = new Stack<Diff>(); // Stack of
        // equalities.
        String lastequality = null; // Always equal to
        // equalities.lastElement().text
        final ListIterator<Diff> pointer = diffs.listIterator();
        // Is there an insertion operation before the last equality.
        boolean pre_ins = false;
        // Is there a deletion operation before the last equality.
        boolean pre_del = false;
        // Is there an insertion operation after the last equality.
        boolean post_ins = false;
        // Is there a deletion operation after the last equality.
        boolean post_del = false;
        Diff thisDiff = pointer.next();
        Diff safeDiff = thisDiff; // The last Diff that is known to be
        // unsplitable.
        while (thisDiff != null) {
            if (thisDiff.operation == Operation.EQUAL) {
                // Equality found.
                if (thisDiff.text.length() < Diff_EditCost && (post_ins || post_del)) {
                    // Candidate found.
                    equalities.push(thisDiff);
                    pre_ins = post_ins;
                    pre_del = post_del;
                    lastequality = thisDiff.text;
                } else {
                    // Not a candidate, and can never become one.
                    equalities.clear();
                    lastequality = null;
                    safeDiff = thisDiff;
                }
                post_ins = post_del = false;
            } else {
                // An insertion or deletion.
                if (thisDiff.operation == Operation.DELETE) {
                    post_del = true;
                } else {
                    post_ins = true;
                }
                /*
                 * Five types to be split:
				 * <ins>A</ins><del>B</del>XY<ins>C</ins><del>D</del>
				 * <ins>A</ins>X<ins>C</ins><del>D</del>
				 * <ins>A</ins><del>B</del>X<ins>C</ins>
				 * <ins>A</del>X<ins>C</ins><del>D</del>
				 * <ins>A</ins><del>B</del>X<del>C</del>
				 */
                if (lastequality != null
                        && ((pre_ins && pre_del && post_ins && post_del) || ((lastequality.length() < Diff_EditCost / 2) && ((pre_ins ? 1
                        : 0) + (pre_del ? 1 : 0) + (post_ins ? 1 : 0) + (post_del ? 1 : 0)) == 3))) {
                    // System.out.println("Splitting: '" + lastequality + "'");
                    // Walk back to offending equality.
                    while (thisDiff != equalities.lastElement()) {
                        thisDiff = pointer.previous();
                    }
                    pointer.next();

                    // Replace equality with a delete.
                    pointer.set(new Diff(Operation.DELETE, lastequality));
                    // Insert a corresponding an insert.
                    pointer.add(thisDiff = new Diff(Operation.INSERT, lastequality));

                    equalities.pop(); // Throw away the equality we just
                    // deleted.
                    lastequality = null;
                    if (pre_ins && pre_del) {
                        // No changes made which could affect previous entry,
                        // keep going.
                        post_ins = post_del = true;
                        equalities.clear();
                        safeDiff = thisDiff;
                    } else {
                        if (!equalities.empty()) {
                            // Throw away the previous equality (it needs to be
                            // reevaluated).
                            equalities.pop();
                        }
                        if (equalities.empty()) {
                            // There are no previous questionable equalities,
                            // walk back to the last known safe diff.
                            thisDiff = safeDiff;
                        } else {
                            // There is an equality we can fall back to.
                            thisDiff = equalities.lastElement();
                        }
                        while (thisDiff != pointer.previous()) {
                            // Intentionally empty loop.
                        }
                        post_ins = post_del = false;
                    }

                    changes = true;
                }
            }
            thisDiff = pointer.hasNext() ? pointer.next() : null;
        }

        if (changes) {
            diff_cleanupMerge(diffs);
        }
    }

    /**
     * Reorder and merge like edit sections. Merge equalities. Any edit section
     * can move as long as it doesn't cross an equality.
     *
     * @param diffs LinkedList of Diff objects.
     */
    public void diff_cleanupMerge(final LinkedList<Diff> diffs) {
        diffs.add(new Diff(Operation.EQUAL, "")); // Add a dummy entry at the
        // end.
        ListIterator<Diff> pointer = diffs.listIterator();
        int count_delete = 0;
        int count_insert = 0;
        String text_delete = "";
        String text_insert = "";
        Diff thisDiff = pointer.next();
        Diff prevEqual = null;
        int commonlength;
        while (thisDiff != null) {
            switch (thisDiff.operation) {
                case INSERT:
                    count_insert++;
                    text_insert += thisDiff.text;
                    prevEqual = null;
                    break;
                case DELETE:
                    count_delete++;
                    text_delete += thisDiff.text;
                    prevEqual = null;
                    break;
                case EQUAL:
                    if (count_delete + count_insert > 1) {
                        final boolean both_types = count_delete != 0 && count_insert != 0;
                        // Delete the offending records.
                        pointer.previous(); // Reverse direction.
                        while (count_delete-- > 0) {
                            pointer.previous();
                            pointer.remove();
                        }
                        while (count_insert-- > 0) {
                            pointer.previous();
                            pointer.remove();
                        }
                        if (both_types) {
                            // Factor out any common prefixies.
                            commonlength = diff_commonPrefix(text_insert, text_delete);
                            if (commonlength != 0) {
                                if (pointer.hasPrevious()) {
                                    thisDiff = pointer.previous();
                                    assert thisDiff.operation == Operation.EQUAL : "Previous diff should have been an equality.";
                                    thisDiff.text += text_insert.substring(0, commonlength);
                                    pointer.next();
                                } else {
                                    pointer.add(new Diff(Operation.EQUAL, text_insert.substring(0, commonlength)));
                                }
                                text_insert = text_insert.substring(commonlength);
                                text_delete = text_delete.substring(commonlength);
                            }
                            // Factor out any common suffixies.
                            commonlength = diff_commonSuffix(text_insert, text_delete);
                            if (commonlength != 0) {
                                thisDiff = pointer.next();
                                thisDiff.text = text_insert.substring(text_insert.length() - commonlength) + thisDiff.text;
                                text_insert = text_insert.substring(0, text_insert.length() - commonlength);
                                text_delete = text_delete.substring(0, text_delete.length() - commonlength);
                                pointer.previous();
                            }
                        }
                        // Insert the merged records.
                        if (text_delete.length() != 0) {
                            pointer.add(new Diff(Operation.DELETE, text_delete));
                        }
                        if (text_insert.length() != 0) {
                            pointer.add(new Diff(Operation.INSERT, text_insert));
                        }
                        // Step forward to the equality.
                        thisDiff = pointer.hasNext() ? pointer.next() : null;
                    } else if (prevEqual != null) {
                        // Merge this equality with the previous one.
                        prevEqual.text += thisDiff.text;
                        pointer.remove();
                        thisDiff = pointer.previous();
                        pointer.next(); // Forward direction
                    }
                    count_insert = 0;
                    count_delete = 0;
                    text_delete = "";
                    text_insert = "";
                    prevEqual = thisDiff;
                    break;
            }
            thisDiff = pointer.hasNext() ? pointer.next() : null;
        }
        if (diffs.getLast().text.length() == 0) {
            diffs.removeLast(); // Remove the dummy entry at the end.
        }

		/*
		 * Second pass: look for single edits surrounded on both sides by
		 * equalities which can be shifted sideways to eliminate an equality.
		 * e.g: A<ins>BA</ins>C -> <ins>AB</ins>AC
		 */
        boolean changes = false;
        // Create a new iterator at the start.
        // (As opposed to walking the current one back.)
        pointer = diffs.listIterator();
        Diff prevDiff = pointer.hasNext() ? pointer.next() : null;
        thisDiff = pointer.hasNext() ? pointer.next() : null;
        Diff nextDiff = pointer.hasNext() ? pointer.next() : null;
        // Intentionally ignore the first and last element (don't need
        // checking).
        while (nextDiff != null) {
            if (prevDiff.operation == Operation.EQUAL && nextDiff.operation == Operation.EQUAL) {
                // This is a single edit surrounded by equalities.
                if (thisDiff.text.endsWith(prevDiff.text)) {
                    // Shift the edit over the previous equality.
                    thisDiff.text = prevDiff.text + thisDiff.text.substring(0, thisDiff.text.length() - prevDiff.text.length());
                    nextDiff.text = prevDiff.text + nextDiff.text;
                    pointer.previous(); // Walk past nextDiff.
                    pointer.previous(); // Walk past thisDiff.
                    pointer.previous(); // Walk past prevDiff.
                    pointer.remove(); // Delete prevDiff.
                    pointer.next(); // Walk past thisDiff.
                    thisDiff = pointer.next(); // Walk past nextDiff.
                    nextDiff = pointer.hasNext() ? pointer.next() : null;
                    changes = true;
                } else if (thisDiff.text.startsWith(nextDiff.text)) {
                    // Shift the edit over the next equality.
                    prevDiff.text += nextDiff.text;
                    thisDiff.text = thisDiff.text.substring(nextDiff.text.length()) + nextDiff.text;
                    pointer.remove(); // Delete nextDiff.
                    nextDiff = pointer.hasNext() ? pointer.next() : null;
                    changes = true;
                }
            }
            prevDiff = thisDiff;
            thisDiff = nextDiff;
            nextDiff = pointer.hasNext() ? pointer.next() : null;
        }
        // If shifts were made, the diff needs reordering and another shift
        // sweep.
        if (changes) {
            diff_cleanupMerge(diffs);
        }
    }

    /**
     * loc is a location in text1, compute and return the equivalent location in
     * text2. e.g. "The cat" vs "The big cat", 1->1, 5->8
     *
     * @param diffs LinkedList of Diff objects.
     * @param loc   Location within text1.
     * @return Location within text2.
     */
    public int diff_xIndex(final LinkedList<Diff> diffs, final int loc) {
        int chars1 = 0;
        int chars2 = 0;
        int last_chars1 = 0;
        int last_chars2 = 0;
        Diff lastDiff = null;
        for (final Diff aDiff : diffs) {
            if (aDiff.operation != Operation.INSERT) {
                // Equality or deletion.
                chars1 += aDiff.text.length();
            }
            if (aDiff.operation != Operation.DELETE) {
                // Equality or insertion.
                chars2 += aDiff.text.length();
            }
            if (chars1 > loc) {
                // Overshot the location.
                lastDiff = aDiff;
                break;
            }
            last_chars1 = chars1;
            last_chars2 = chars2;
        }
        if (lastDiff != null && lastDiff.operation == Operation.DELETE) {
            // The location was deleted.
            return last_chars2;
        }
        // Add the remaining character length.
        return last_chars2 + (loc - last_chars1);
    }

    /**
     * Compute and return the source text (all equalities and deletions).
     *
     * @param diffs LinkedList of Diff objects.
     * @return Source text.
     */
    public String diff_text1(final LinkedList<Diff> diffs) {
        final StringBuilder text = new StringBuilder();
        for (final Diff aDiff : diffs) {
            if (aDiff.operation != Operation.INSERT) {
                text.append(aDiff.text);
            }
        }
        return text.toString();
    }

    /**
     * Compute and return the destination text (all equalities and insertions).
     *
     * @param diffs LinkedList of Diff objects.
     * @return Destination text.
     */
    public String diff_text2(final LinkedList<Diff> diffs) {
        final StringBuilder text = new StringBuilder();
        for (final Diff aDiff : diffs) {
            if (aDiff.operation != Operation.DELETE) {
                text.append(aDiff.text);
            }
        }
        return text.toString();
    }

    /**
     * Compute the Levenshtein distance; the number of inserted, deleted or
     * substituted characters.
     *
     * @param diffs LinkedList of Diff objects.
     * @return Number of changes.
     */
    public int diff_levenshtein(final LinkedList<Diff> diffs) {
        int levenshtein = 0;
        int insertions = 0;
        int deletions = 0;
        for (final Diff aDiff : diffs) {
            switch (aDiff.operation) {
                case INSERT:
                    insertions += aDiff.text.length();
                    break;
                case DELETE:
                    deletions += aDiff.text.length();
                    break;
                case EQUAL:
                    // A deletion and an insertion is one substitution.
                    levenshtein += Math.max(insertions, deletions);
                    insertions = 0;
                    deletions = 0;
                    break;
            }
        }
        levenshtein += Math.max(insertions, deletions);
        return levenshtein;
    }

    /**
     * Crush the diff into an encoded string which describes the operations
     * required to transform text1 into text2. E.g. =3\t-2\t+ing -> Keep 3
     * chars, delete 2 chars, insert 'ing'. Operations are tab-separated.
     * Inserted text is escaped using %xx notation.
     *
     * @param diffs Array of diff tuples.
     * @return Delta text.
     */
    public String diff_toDelta(final LinkedList<Diff> diffs) {
        final StringBuilder text = new StringBuilder();
        for (final Diff aDiff : diffs) {
            switch (aDiff.operation) {
                case INSERT:
                    try {
                        text.append("+").append(URLEncoder.encode(aDiff.text, "UTF-8").replace('+', ' ')).append("\t");
                    } catch (final UnsupportedEncodingException e) {
                        // Not likely on modern system.
                        throw new Error("This system does not support UTF-8.", e);
                    }
                    break;
                case DELETE:
                    text.append("-").append(aDiff.text.length()).append("\t");
                    break;
                case EQUAL:
                    text.append("=").append(aDiff.text.length()).append("\t");
                    break;
            }
        }
        String delta = text.toString();
        if (delta.length() != 0) {
            // Strip off trailing tab character.
            delta = delta.substring(0, delta.length() - 1);
            delta = unescapeForEncodeUriCompatability(delta);
        }
        return delta;
    }

    /**
     * Given the original text1, and an encoded string which describes the
     * operations required to transform text1 into text2, compute the full diff.
     *
     * @param text1 Source string for the diff.
     * @param delta Delta text.
     * @return Array of diff tuples or null if invalid.
     * @throws IllegalArgumentException If invalid input.
     */
    public LinkedList<Diff> diff_fromDelta(final String text1, final String delta) throws IllegalArgumentException {
        final LinkedList<Diff> diffs = new LinkedList<Diff>();
        int pointer = 0; // Cursor in text1
        final String[] tokens = delta.split("\t");
        for (final String token : tokens) {
            if (token.length() == 0) {
                // Blank tokens are ok (from a trailing \t).
                continue;
            }
            // Each token begins with a one character parameter which specifies
            // the
            // operation of this token (delete, insert, equality).
            String param = token.substring(1);
            switch (token.charAt(0)) {
                case '+':
                    // decode would change all "+" to " "
                    param = param.replace("+", "%2B");
                    try {
                        param = URLDecoder.decode(param, "UTF-8");
                    } catch (final UnsupportedEncodingException e) {
                        // Not likely on modern system.
                        throw new Error("This system does not support UTF-8.", e);
                    } catch (final IllegalArgumentException e) {
                        // Malformed URI sequence.
                        throw new IllegalArgumentException("Illegal escape in diff_fromDelta: " + param, e);
                    }
                    diffs.add(new Diff(Operation.INSERT, param));
                    break;
                case '-':
                    // Fall through.
                case '=':
                    int n;
                    try {
                        n = Integer.parseInt(param);
                    } catch (final NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid number in diff_fromDelta: " + param, e);
                    }
                    if (n < 0) {
                        throw new IllegalArgumentException("Negative number in diff_fromDelta: " + param);
                    }
                    String text;
                    try {
                        text = text1.substring(pointer, pointer += n);
                    } catch (final StringIndexOutOfBoundsException e) {
                        throw new IllegalArgumentException("Delta length (" + pointer + ") larger than source text length (" + text1.length()
                                + ").", e);
                    }
                    if (token.charAt(0) == '=') {
                        diffs.add(new Diff(Operation.EQUAL, text));
                    } else {
                        diffs.add(new Diff(Operation.DELETE, text));
                    }
                    break;
                default:
                    // Anything else is an error.
                    throw new IllegalArgumentException("Invalid diff operation in diff_fromDelta: " + token.charAt(0));
            }
        }
        if (pointer != text1.length()) {
            throw new IllegalArgumentException("Delta length (" + pointer + ") smaller than source text length (" + text1.length() + ").");
        }
        return diffs;
    }

    /**
     * Class representing one diff operation.
     */
    public static class Diff {
        /**
         * One of: INSERT, DELETE or EQUAL.
         */
        public Operation operation;
        /**
         * The text associated with this diff operation.
         */
        public String text;

        /**
         * Constructor. Initializes the diff with the provided values.
         *
         * @param operation One of INSERT, DELETE or EQUAL.
         * @param text      The text being applied.
         */
        public Diff(final Operation operation, final String text) {
            // Construct a diff with the specified operation and text.
            this.operation = operation;
            this.text = text;
        }

        /**
         * @return size
         */
        public int getSize() {
            return text.length();
        }

        /**
         * Display a human-readable version of this Diff.
         *
         * @return text version.
         */
        @Override
        public String toString() {
            final String prettyText = this.text.replace('\n', '\u00b6');
            return "Diff(" + this.operation + ",\"" + prettyText + "\")";
        }

        /**
         * Is this Diff equivalent to another Diff?
         *
         * @param d Another Diff to compare against.
         * @return true or false.
         */
        @Override
        public boolean equals(final Object d) {
            try {
                return (((Diff) d).operation == this.operation) && (((Diff) d).text.equals(this.text));
            } catch (final ClassCastException e) {
                return false;
            }
        }
    }

    /**
     * Unescape selected chars for compatability with JavaScript's encodeURI. In
     * speed critical applications this could be dropped since the receiving
     * application will certainly decode these fine. Note that this function is
     * case-sensitive. Thus "%3f" would not be unescaped. But this is ok because
     * it is only called with the output of URLEncoder.encode which returns
     * uppercase hex.
     * <p/>
     * Example: "%3F" -> "?", "%24" -> "$", etc.
     *
     * @param str The string to escape.
     * @return The escaped string.
     */
    private static String unescapeForEncodeUriCompatability(final String str) {
        return str.replace("%21", "!").replace("%7E", "~").replace("%27", "'").replace("%28", "(").replace("%29", ")").replace("%3B", ";")
                .replace("%2F", "/").replace("%3F", "?").replace("%3A", ":").replace("%40", "@").replace("%26", "&").replace("%3D", "=")
                .replace("%2B", "+").replace("%24", "$").replace("%2C", ",").replace("%23", "#");
    }
}
