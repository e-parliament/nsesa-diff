package org.nsesa.diff.diffUtils;

import org.junit.Test;

public class WordDiffTest {

    @Test
    public void testWordDiff() {
        String[] wordDiff = DiffUtils.wordDiff("This is a test.", "This isss ssa test.");

        System.out.println(wordDiff[0]);
        System.out.println(wordDiff[1]);
    }

    @Test
    public void testWordDiffProcedure() {
        String[] wordDiff = DiffUtils.wordDiff("This is 000/1234/44 a test.", "This 000!1234/44 isss ssa test.");

        System.out.println(wordDiff[0]);
        System.out.println(wordDiff[1]);
    }

    @Test
    public void testCleanup() {
        String[] wordDiff = DiffUtils.wordDiff("This is a test.", "This isss a testss.");

        System.out.println(wordDiff[0]);
        System.out.println(wordDiff[1]);
    }

}
