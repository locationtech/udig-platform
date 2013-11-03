/**
 * 
 */
package org.locationtech.udig.issues.test;

import org.locationtech.udig.issues.IssuesList;

class DummyIssueList extends IssuesList{
    /** long serialVersionUID field */
    private static final long serialVersionUID = 1L;

    void clearlisteners(){
        listeners.clear();            
    }

}
