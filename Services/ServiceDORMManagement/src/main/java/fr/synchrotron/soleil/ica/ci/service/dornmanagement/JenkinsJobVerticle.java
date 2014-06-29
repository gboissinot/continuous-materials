package fr.synchrotron.soleil.ica.ci.service.dornmanagement;

import org.vertx.java.busmods.BusModBase;

/**
 * @author Gregory Boissinot
 */
public class JenkinsJobVerticle extends BusModBase {

    private static final String ACTION_TEST_JOB_EXIST = "testJobExist";
    private static final String ACTION_CREATE_JOB = "createJob";
    private static final String ACTION_UPDATE_JOB = "updateJob";

    @Override
    public void start() {
        super.start();
        //TODO
    }

}
