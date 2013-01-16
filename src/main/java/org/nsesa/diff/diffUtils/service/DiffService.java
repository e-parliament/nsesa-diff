package org.nsesa.diff.diffUtils.service;

import org.nsesa.diff.diffUtils.service.model.ComplexDiffCommand;
import org.nsesa.diff.diffUtils.service.model.ComplexDiffResult;
import org.nsesa.diff.diffUtils.service.model.ThreeWayDiffContext;

import java.util.List;

public interface DiffService {

    /**
     * Performs a diff between an existing diff and its changed version.
     *
     * @param original
     * @param modified
     * @param overrideModified
     * @return 4 strings, 2 for the deletions (colored and non-colored) and 2
     *         for the insertions (colored and non-colored)
     */
    ComplexDiffResult complexDiff(String original, String modified, String overrideModified);

    /**
     * @param original
     * @param modified
     * @param overrideModified
     * @param context
     * @return
     */
    ComplexDiffResult complexDiff(String original, String modified, String overrideModified, ThreeWayDiffContext context);

    /**
     * @param commands
     * @return
     */
    List<ComplexDiffResult> complexDiff(List<ComplexDiffCommand> commands);

}
