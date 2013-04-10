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
package org.nsesa.diff.diffUtils.service.impl;

import org.nsesa.diff.diffUtils.DiffUtils;
import org.nsesa.diff.diffUtils.service.DiffService;
import org.nsesa.diff.diffUtils.service.model.ComplexDiffCommand;
import org.nsesa.diff.diffUtils.service.model.ComplexDiffResult;
import org.nsesa.diff.diffUtils.service.model.DefaultThreeWayDiffContext;
import org.nsesa.diff.diffUtils.service.model.ThreeWayDiffContext;

import java.util.ArrayList;
import java.util.List;

public class DefaultDiffService implements DiffService {

    @Override
    public ComplexDiffResult complexDiff(String original, String modified, String overrideModified) {
        return complexDiff(original, modified, overrideModified, new DefaultThreeWayDiffContext());
    }

    @Override
    public ComplexDiffResult complexDiff(String original, String modified, String overrideModified, ThreeWayDiffContext context) {

        /*
        ----------- WORKAROUND!!!!! -------------
        Because of a bug during the consolidation of changes, we need to ensure that the templates
        are using unique tags during the diffing, which will later on be substituted by the real
        template.
        If we don't this, we risk uneven tags because they are wrongly collapsed.
        */
        final ThreeWayDiffContext defaultContext = new ThreeWayDiffContext("<bi>{0}</bi>", "<red>{0}</red>",
                "<ins>{0}</ins>", "<del>{0}</del>", "<blue>{0}</blue>",
                context.getDiffMethod());

        String[] complexDiff = DiffUtils.threeWayDiff(original, modified, overrideModified, defaultContext);

        String coloredOriginal = workaroundToReplaceDefaultContextTemplatesWithHighlightSpans(complexDiff[0], context);
        String finalOriginal = "";
        String coloredAmendment = workaroundToReplaceDefaultContextTemplatesWithHighlightSpans(complexDiff[1], context);
        String finalAmendment = "";

        return new ComplexDiffResult(finalOriginal, finalAmendment, coloredOriginal, coloredAmendment);
    }

    private String workaroundToReplaceDefaultContextTemplatesWithHighlightSpans(String original, ThreeWayDiffContext context) {
        String modified = original.replaceAll("<bi>", splitTemplate(context.getOriginalChangeTemplate())[0]);
        modified = modified.replaceAll("</bi>", splitTemplate(context.getOriginalChangeTemplate())[1]);
        modified = modified.replaceAll("<red>", splitTemplate(context.getOriginalComplexChangeTemplate())[0]);
        modified = modified.replaceAll("</red>", splitTemplate(context.getOriginalComplexChangeTemplate())[1]);
        modified = modified.replaceAll("<ins>", splitTemplate(context.getComplexInsertTemplate())[0]);
        modified = modified.replaceAll("</ins>", splitTemplate(context.getComplexInsertTemplate())[1]);
        modified = modified.replaceAll("<del>", splitTemplate(context.getComplexDeleteTemplate())[0]);
        modified = modified.replaceAll("</del>", splitTemplate(context.getComplexDeleteTemplate())[1]);
        modified = modified.replaceAll("<blue>", splitTemplate(context.getComplexChangeTemplate())[0]);
        modified = modified.replaceAll("</blue>", splitTemplate(context.getComplexChangeTemplate())[1]);
        return modified;
    }

    private String[] splitTemplate(String inputTemplate) {
        return inputTemplate.split("\\{0\\}");
    }

    @Override
    public List<ComplexDiffResult> complexDiff(List<ComplexDiffCommand> commands) {
        List<ComplexDiffResult> result = new ArrayList<ComplexDiffResult>();

        for (ComplexDiffCommand command : commands) {
            ComplexDiffResult complexDiffResult = null;
            if (command.getContext() != null) {
                complexDiffResult = complexDiff(command.getOriginal(), command.getModified(), command.getOverrideModified(),
                        command.getContext());
            } else {
                complexDiffResult = complexDiff(command.getOriginal(), command.getModified(), command.getOverrideModified());
            }

            result.add(complexDiffResult);
        }

        return result;
    }

}
