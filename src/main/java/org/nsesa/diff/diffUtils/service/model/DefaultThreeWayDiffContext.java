package org.nsesa.diff.diffUtils.service.model;

public class DefaultThreeWayDiffContext extends ThreeWayDiffContext {

    public DefaultThreeWayDiffContext() {
        super("<bi>{0}</bi>", "<red>{0}</red>", "<ins>{0}</ins>", "<del>{0}</del>", "<blue>{0}</blue>", DiffMethod.CHARACTER);
    }

    public DefaultThreeWayDiffContext(DiffMethod diffMethod) {
        super("<bi>{0}</bi>", "<red>{0}</red>", "<ins>{0}</ins>", "<del>{0}</del>", "<blue>{0}</blue>", diffMethod);
    }

}
