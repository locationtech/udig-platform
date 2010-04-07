package net.refractions.udig.issues;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.operation.IRunnableWithProgress;
/**
 * The Preference Page for configuring what issues list is used.
 *  
 * @author Jesse
 * @since 1.1.0
 */
public interface IIssuesPreferencePage {

    /**
     * Runs a runnable in a non-UI thread if mayBlock is true.  Otherwise simply runs the runnable.
     *
     * @param mayBlock true if it is possible for the Runnable to block, for example a network call is made.
     * @param runnable the runnable to execute.
     */
    public void runWithProgress( boolean mayBlock, final IRunnableWithProgress runnable )
            throws InvocationTargetException, InterruptedException;
    /**
     * Sets or clears the error message for this page.
     * 
     * @param newMessage
     *            the message, or <code>null</code> to clear the error message
     */
    public void setErrorMessage(String newMessage);
    /**
     * Sets or clears the message for this page.
     * <p>
     * This is a shortcut for <code>setMessage(newMesasge, NONE)</code>
     * </p>
     * 
     * @param newMessage
     *            the message, or <code>null</code> to clear the message
     */
    public void setMessage(String newMessage);
    /**
     * Sets the message for this page with an indication of what type of message
     * it is.
     * <p>
     * The valid message types are one of <code>NONE</code>,
     * <code>INFORMATION</code>,<code>WARNING</code>, or
     * <code>ERROR</code>.
     * </p>
     * <p>
     * Note that for backward compatibility, a message of type
     * <code>ERROR</code> is different than an error message (set using
     * <code>setErrorMessage</code>). An error message overrides the current
     * message until the error message is cleared. This method replaces the
     * current message and does not affect the error message.
     * </p>
     * 
     * @param newMessage
     *            the message, or <code>null</code> to clear the message
     * @param newType
     *            the message type
     * @since 2.0
     */
    public void setMessage(String newMessage, int newType);

}