package fr.synchrotron.soleil.ica.ci.lib.workflow;

/**
 * @author Gregory Boissinot
 */
public class Status {

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
