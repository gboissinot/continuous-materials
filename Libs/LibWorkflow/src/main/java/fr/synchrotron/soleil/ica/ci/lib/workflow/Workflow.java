package fr.synchrotron.soleil.ica.ci.lib.workflow;

import java.util.*;

/**
 * @author Gregory Boissinot
 */
public class Workflow {

    public static final Workflow DEFAULT_WORKFLOW_STATUS = new Workflow("DEFAULT_WORKFLOW", Arrays.asList("BUILD", "INTEGRATION", "RELEASE"));

    private String name;
    private Map<Integer, Status> allStatus = new HashMap<Integer, Status>();

    public Workflow(String name, List<String> orderedLabels) {
        this.name = name;
        Map<Integer, Status> result = new HashMap<Integer, Status>();
        for (int k = 0; k < orderedLabels.size(); k++) {
            int ref = k + 1;
            result.put(ref, new Status(ref, orderedLabels.get(k), (ref == orderedLabels.size()) ? -1 : ref + 1));
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

        final int nextref = status.getNextRef();
        if (nextref == -1) {
            return null;
        }

        return allStatus.get(nextref).getLabel();
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

    public String getNormalizedStatus(String label) {

        if (label == null) {
            throw new NullPointerException("A label is required.");
        }

        Status status = getStatus(label);
        if (status != null) {
            return status.getLabel();
        }

        return null;

    }
}
