/**
 *
 */
package net.refractions.udig.issues.test;

import net.refractions.udig.issues.listeners.IIssuesListListener;
import net.refractions.udig.issues.listeners.IssuesListEvent;

class DummyListener implements IIssuesListListener{
    int changes=0;
    int timesCalled;
    public void notifyChange( IssuesListEvent event ) {
        changes+=event.getChanged().size();
        timesCalled++;
    }
}
