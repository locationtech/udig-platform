package net.refractions.udig.catalog.ui;

import java.util.Stack;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;

/**
 * Abstract implementation of UDIGImportPage.
 *
 * @author jdeolive
 */
public abstract class AbstractUDIGImportPage extends WizardPage implements UDIGConnectionPage {

    private Stack<Entry> messages = new Stack<Entry>();
    private Stack<String> errors = new Stack<String>();

    public AbstractUDIGImportPage( String pageName ) {
        super(pageName);
    }

    /**
     * Sets a Message on the wizard page. Since these pages are decorated by a connection page the
     * default implementation Fails
     */
    @Override
    public void setMessage( String newMessage, int newType ) {
        if (newMessage == null) {
            popMessage();
        } else {
            messages.push(new Entry(getMessage(), getMessageType()));
        }
        setMessagePrivate(newMessage, newType);
    }

    private void setMessagePrivate( String newMessage, int newType ) {
        super.setMessage(newMessage, newType);

        // wizard pages are decorated by a connection page, so the default
        // implementation of this method will not do anything
        IWizardPage page = getContainer().getCurrentPage();
        if (page != this && page instanceof WizardPage) {
            ((WizardPage) page).setMessage(newMessage, newType);
        } else {
            CatalogUIPlugin.log("A WizardPage was expected but instead was a "
                    + page.getClass().getName(), new Exception());
        }
    }

    /**
     * Sets the error Message on the wizard page. Since these pages are decorated by a connection
     * page the default implementation Fails to display the error.
     */
    @Override
    public void setErrorMessage( String newMessage ) {
        if (newMessage == null) {
            popErrorMessage();
        } else {
            errors.push(getErrorMessage());
        }
        setErrorMessagePrivate(newMessage);
    }

    private void setErrorMessagePrivate( String newMessage ) {
        super.setErrorMessage(newMessage);

        // wizard pages are decorated by a connection page, so the default
        // implementation of this method will not do anything
        IWizardPage page = getContainer().getCurrentPage();
        if (page != this && page instanceof WizardPage) {
            ((WizardPage) page).setErrorMessage(newMessage);
        } else {
            CatalogUIPlugin.log("A WizardPage was expected but instead was a "
                    + page.getClass().getName(), new Exception());
        }
    }

    /**
     * Removes the current error message and replaces it with the previously visible error message.
     */
    protected void popErrorMessage() {
        if (errors.isEmpty()) {
            setErrorMessagePrivate(null);
        } else {
            String previous = errors.pop();

            setErrorMessagePrivate(previous);
        }
    }

    /**
     * Removes the current message and replaces it with the previously visible message.
     */
    protected void popMessage() {
        if (messages.isEmpty()) {
            setMessagePrivate(null, getMessageType());
        } else {
            Entry previous = messages.pop();
            setMessagePrivate(previous.message, previous.type);
        }
    }

    @Override
    public final IWizardPage getNextPage() {
        return super.getNextPage();
    }

    /**
     * Called by framework as the page is about to be left.
     * <p>
     * There are two main use cases for this method. The first is to save settings for the next time
     * the wizard is visited. The other is to perform some checks or do some loading that is too expensive to do every
     * time isPageComplete() is called.  For example a database wizard page might try to connect to the database in this method
     * rather than isPageComplete() because it is such an expensive method to call.
     * </p>
     * <p>
     * If an expensive method is called make sure to run it in the container:
     *         <pre>getContainer().run(true, cancelable, runnable);</pre>
     * </p>
     *
     * @return true if it is acceptable to leave the page false if the page must not be left
     */
    public boolean leavingPage() {
        // default does nothing
        return true;
    }

    /**
     * Just a simple little container class
     *
     * @author jesse
     * @since 1.1.0
     */
    private class Entry {
        final String message;
        final int type;
        public Entry( String message, int type ) {
            super();
            this.message = message;
            this.type = type;
        }

    }

    @Override
    public void dispose() {
        super.dispose();
        setControl(null);
    }
}
