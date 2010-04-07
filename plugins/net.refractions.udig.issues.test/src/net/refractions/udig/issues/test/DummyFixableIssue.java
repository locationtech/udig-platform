package net.refractions.udig.issues.test;

import net.refractions.udig.issues.FixableIssue;

/**
 * Subclass of FixableIssue for extension point testing. 
 * <p>
 *
 * </p>
 * @author chorner
 * @since 1.1.0
 */
public class DummyFixableIssue extends FixableIssue {
    
    public static final String ID = "net.refractions.udig.issues.test.DummyFixableIssue"; //$NON-NLS-1$
    
    public String getExtensionID() {
        return ID;
    }
}
