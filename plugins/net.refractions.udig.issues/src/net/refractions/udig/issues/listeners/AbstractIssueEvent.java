package net.refractions.udig.issues.listeners;

import net.refractions.udig.issues.IIssue;

public class AbstractIssueEvent {

    protected final IIssue source;
    protected final Object newValue;
    protected final Object oldValue;

    /**
     * 
     * @param source the issue has changed
     * @param newValue the new value after the change
     * @param oldValue the value before the change
     */
    public AbstractIssueEvent(IIssue source2, Object newValue2, Object oldValue2) {
        this.source=source2;
        this.newValue=newValue2;
        this.oldValue=oldValue2;
    }

    /**
     * Returns the new value after the change
     *
     * @return the new value after the change
     */
    public Object getNewValue() {
        return newValue;
    }

    /**
     * Returns the value before the change
     *
     * @return the value before the change
     */
    public Object getOldValue() {
        return oldValue;
    }

    /**
     * Returns the issue has changed
     *
     * @return the issue has changed
     */
    public IIssue getSource() {
        return source;
    }

}