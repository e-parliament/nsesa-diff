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
        String[] complexDiff = DiffUtils.threeWayDiff(original, modified, overrideModified, context);

        String coloredOriginal = complexDiff[0];
        String finalOriginal = "";
        String coloredAmendment = complexDiff[1];
        String finalAmendment = "";

        return new ComplexDiffResult(finalOriginal, finalAmendment, coloredOriginal, coloredAmendment);
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
