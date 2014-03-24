package fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects;

import java.lang.annotation.*;

/**
 * @author Gregory Boissinot
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface Required {
}
