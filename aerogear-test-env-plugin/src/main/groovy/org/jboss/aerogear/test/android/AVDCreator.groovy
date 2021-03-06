package org.jboss.aerogear.test.android

import org.arquillian.spacelift.execution.Task
import org.jboss.aerogear.test.GradleSpacelift

class AVDCreator extends Task<Object, Void> {

    private String target

    public AVDCreator target(String target) {
        this.target = target
        this
    }

    @Override
    protected Void process(Object input) throws Exception {
        GradleSpacelift.tools("android")
                .parameters(["create" ,"avd", "-n", target.replaceAll("\\W", ""), "-t", target, "--force"])
                .interaction(GradleSpacelift.ECHO_OUTPUT)
                .execute().await()

        return null
    }
}
