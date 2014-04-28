package fr.synchrotron.soleil.ica.ci.lib.workflow;

import java.util.*;

/**
 * @author Gregory Boissinot
 */
public class Workflow {

    public static final Workflow DEFAULT_WORKFLOW_STATUS = new Workflow("DEFAULT_WORKFLOW", Arrays.asList("BUILD", "INTEGRATION", "RELEASE"));

    private Map<Integer, Status> internalStatusMap = new HashMap<Integer, Status>();
    private class Status {

        private int ref;
        private String label;
        private int nextRef;

        Status(int ref, String label, int nextRef) {
            this.ref = ref;
            this.label = label;
            this.nextRef = nextRef;
        }

        String getLabel() {
            return label;
        }

        int getNextRef() {
            return nextRef;
        }
    }

    private String name;
    private String latestPromotedStatus;

    public Workflow(String name, List<String> orderedLabels) {
        this.name = name;
        Map<Integer, Status> result = new HashMap<Integer, Status>();
        for (int k = 0; k < orderedLabels.size(); k++) {
            int ref = k + 1;
            boolean last = (ref == orderedLabels.size());
            final String label = orderedLabels.get(k);
            if (last) {
                result.put(ref, new Status(ref, label, -1));
                latestPromotedStatus = label;
            } else {
                result.put(ref, new Status(ref, label, ref + 1));
            }
        }
        internalStatusMap = result;
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

        return internalStatusMap.get(nextref).getLabel();
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

    private Status getStatus(String label) {

        assert label != null;

        final Collection<Status> values = internalStatusMap.values();
        for (Status status : values) {
            if (label.equalsIgnoreCase(status.getLabel())) {
                return status;
            }
        }

        return null;
    }

    public String getLatestPromotedStatus() {
        return latestPromotedStatus;
    }

    public String getName() {
        return name;
    }
}
