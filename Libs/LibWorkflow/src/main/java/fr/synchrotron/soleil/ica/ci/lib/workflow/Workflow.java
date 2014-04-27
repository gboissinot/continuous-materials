package fr.synchrotron.soleil.ica.ci.lib.workflow;

import java.util.*;

/**
 * @author Gregory Boissinot
 */
public class Workflow {

    public static final Workflow SOFTWARE_WORKFLOW_3_STATUS = new Workflow("SOLEIL_STATUS", Arrays.asList("BUILD", "INTEGRATION", "RELEASE"));

    private String name;
    private Map<Integer, Status> allStatus = new HashMap<Integer, Status>();

    public Workflow(String name, List<String> orderedLabels) {
        this.name = name;
        Map<Integer, Status> result = new HashMap<Integer, Status>();
        for (int k = 0; k < orderedLabels.size(); k++) {
            result.put(k, new Status(k, orderedLabels.get(k), (k == orderedLabels.size() - 1) ? -1 : ++k));
        }
        allStatus = result;
    }

    public Status getNextStatusLabel(Status status) {

        if (status == null) {
            throw new NullPointerException("A status object is required.");
        }

        final int nextref = status.getNextRef();
        if (nextref == -1) {
            return null;
        }

        return allStatus.get(nextref);
    }

    public String getNextStatusLabel(String statusLabel) {

        if (statusLabel == null) {
            throw new NullPointerException("A status label is required.");
        }

        Status status = getStatus(statusLabel);
        if (status == null) {
            return null;
        }

        return allStatus.get(status.getNextRef()).getLabel();
    }

    private Status getStatus(String label) {
        assert label != null;

        final Collection<Status> values = allStatus.values();
        for (Status status : values) {
            if (label.equalsIgnoreCase(status.getLabel())) {
                return status;
            }
        }

        return null;
    }

}
