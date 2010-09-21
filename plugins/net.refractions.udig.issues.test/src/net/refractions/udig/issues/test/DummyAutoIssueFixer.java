package net.refractions.udig.issues.test;

import net.refractions.udig.core.enums.Resolution;
import net.refractions.udig.issues.IIssue;

import org.eclipse.ui.IMemento;

/**
 * Issues are automatically fixed by this fixer, upon calling the fix method. 
 * <p>
 *
 * </p>
 * @author chorner
 * @since 1.1.0
 */
public class DummyAutoIssueFixer extends DummyIssueFixer {

    public void fix( Object object, IMemento fixerMemento ) {
        IIssue issue = (IIssue) object;
        //resolve it right now
        issue.setResolution(Resolution.RESOLVED);
    }

}
