package net.refractions.udig.issues.test;

import net.refractions.udig.core.IFixer;
import net.refractions.udig.core.enums.Resolution;
import net.refractions.udig.issues.AbstractFixableIssue;
import net.refractions.udig.issues.IIssue;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IMemento;

public class DummyIssueFixer implements IFixer {

    /**
     * Simple flag read from the fixerMemento to determine if an issue can be fixed. This is not a
     * realistic case, as we would usually look to see if certain keys exist and if their values
     * conform to our ideals.
     */
    public static final String KEY_FIXABLE = "fixable"; //$NON-NLS-1$

    MessageDialog messageDialog = null;

    public boolean canFix( Object object, IMemento fixerMemento ) {
        //not null
        if (object == null || fixerMemento == null) {
            return false;
        }
        //must be instance
        if (!(object instanceof AbstractFixableIssue)) {
            return false;
        }
        IIssue issue = (IIssue) object;
        //not already resolved
        if (issue.getResolution() == Resolution.RESOLVED) {
            return false;
        }
        String fixable = fixerMemento.getString(KEY_FIXABLE);
        return fixable != null && fixable.equalsIgnoreCase("TRUE"); //$NON-NLS-1$
    }

    public void fix( Object object, IMemento fixerMemento ) {
        IIssue issue = (IIssue) object;
        //set resolution to "in progress" (optional, but a good idea)
        issue.setResolution(Resolution.IN_PROGRESS);
        //at this point, some mystical dialog or workflow process decides to call complete
    }

    public void complete( Object object ) {
        ((IIssue) object).setResolution(Resolution.RESOLVED);
    }

    /**
     * Obtains the dialog that pops up to ask question (this method is used to programatically click
     * the button).
     *
     * @return MessageDialog
     */
    public MessageDialog getDialog() {
        return messageDialog;
    }
}
