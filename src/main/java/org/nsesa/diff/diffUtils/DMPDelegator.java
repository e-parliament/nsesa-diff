package org.nsesa.diff.diffUtils;

import org.nsesa.diff.diffUtils.DiffMatchPatch.Diff;

import java.util.LinkedList;

public enum DMPDelegator {
    INSTANCE;

    private static final DiffMatchPatch dmp = new DiffMatchPatch() {
        {
            Diff_Timeout = 0f;
        }
    };

    public LinkedList<Diff> diff_main(String text1, String text2) {
        return dmp.diff_main(text1, text2, false);
    }

}