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