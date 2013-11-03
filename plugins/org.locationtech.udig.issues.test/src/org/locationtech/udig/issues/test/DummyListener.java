/**
 * 
 */
package org.locationtech.udig.issues.test;

import org.locationtech.udig.issues.listeners.IIssuesListListener;
import org.locationtech.udig.issues.listeners.IssuesListEvent;

class DummyListener implements IIssuesListListener{
    int changes=0;
    int timesCalled;
    public void notifyChange( IssuesListEvent event ) {
        changes+=event.getChanged().size();
        timesCalled++;    
    }
}
