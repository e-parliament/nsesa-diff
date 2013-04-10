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
