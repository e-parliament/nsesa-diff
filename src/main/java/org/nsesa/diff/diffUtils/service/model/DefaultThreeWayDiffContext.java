/**
 * Copyright 2013 European Parliament
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */
package org.nsesa.diff.diffUtils.service.model;

public class DefaultThreeWayDiffContext extends ThreeWayDiffContext {

    public DefaultThreeWayDiffContext() {
        super("<bi>{0}</bi>", "<red>{0}</red>", "<ins>{0}</ins>", "<del>{0}</del>", "<blue>{0}</blue>", DiffMethod.CHARACTER);
    }

    public DefaultThreeWayDiffContext(DiffMethod diffMethod) {
        super("<bi>{0}</bi>", "<red>{0}</red>", "<ins>{0}</ins>", "<del>{0}</del>", "<blue>{0}</blue>", diffMethod);
    }

}
