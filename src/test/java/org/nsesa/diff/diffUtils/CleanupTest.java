package org.nsesa.diff.diffUtils;

import junit.framework.Assert;
import org.junit.Test;
import org.nsesa.diff.diffUtils.service.model.WordDiffContext;

public class CleanupTest {

    @Test
    public void testCleanupBetweenChanges() {
        String original = "This is a test.";
        String revised = "This iss a tests.";

        String[] result = DiffUtils.wordDiff(original, revised);
        assertResult(result, "This <bi>is a test</bi>.", "This <bi>iss a tests</bi>.");
    }

    @Test
    public void testCleanupBetweenInserts() {
        String original = "This is a test.";
        String revised = "This is a test.";

        String[] result = DiffUtils.wordDiff(original, revised);
        assertResult(result, "This is a test.", "This is a test.");
    }

    @Test
    public void testCleanupBetweenDeletes() {
        String original = "This is a test.";
        String revised = "This a.";

        String[] result = DiffUtils.wordDiff(original, revised);
        assertResult(result, "This <bi>is a test</bi>.", "This <bi>a</bi>.");

    }

    @Test
    public void testCleanupBetweenChangeAndDelete() {
        String original = "This is a test.";
        String revised = "This isss a.";

        String[] result = DiffUtils.wordDiff(original, revised);
        assertResult(result, "This <bi>is a test</bi>.", "This <bi>isss a</bi>.");
    }

    @Test
    public void testNoCleanupOnInsert() {
        String original = "";
        String revised = "This is a test.";

        String[] result = DiffUtils.wordDiff(original, revised);
        assertResult(result, "", "<bi>This is a test.</bi>");
    }

    @Test
    public void testNoCleanupOnDelete() {
        String original = "This is a test.";
        String revised = "";

        String[] result = DiffUtils.wordDiff(original, revised);
        assertResult(result, "<bi>This is a test.</bi>", "");
    }

    @Test
    public void testNoCleanupOnChange() {
        String original = "This";
        String revised = "That";

        String[] result = DiffUtils.wordDiff(original, revised);
        assertResult(result, "<bi>This</bi>", "<bi>That</bi>");
    }

    @Test
    public void testNoCleanupMixOfOperations() {
        String original = "This is a broader test of what cleanup could do.";
        String revised = "This iss a tests of xx what cleanup do?";

        String[] result = DiffUtils.wordDiff(original, revised, new WordDiffContext("<bi>{0}</bi>", false));
        assertResult(result, "This <bi>is</bi> a <bi>broader test</bi> of what cleanup <bi>could</bi> do<bi>.</bi>", "This <bi>iss</bi> a <bi>tests</bi> of <bi>xx</bi> what cleanup do<bi>?</bi>");
    }

    @Test
    public void testCleanupMixOfOperations() {
        String original = "This is a broader test of what cleanup could do.";
        String revised = "This iss a tests of xx what cleanup do?";

        String[] result = DiffUtils.wordDiff(original, revised);
        assertResult(result, "This <bi>is a broader test of</bi> what cleanup <bi>could do.</bi>", "This <bi>iss a tests of xx</bi> what cleanup <bi>do?</bi>");

    }

    private void assertResult(String[] result, String expectedLeft, String expectedRight) {
        Assert.assertEquals(expectedLeft, result[0]);
        Assert.assertEquals(expectedRight, result[1]);
    }
}
