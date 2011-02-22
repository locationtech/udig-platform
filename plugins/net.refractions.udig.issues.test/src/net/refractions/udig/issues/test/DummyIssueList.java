/**
 *
 */
package net.refractions.udig.issues.test;

import net.refractions.udig.issues.IssuesList;

class DummyIssueList extends IssuesList{
    /** long serialVersionUID field */
    private static final long serialVersionUID = 1L;

    void clearlisteners(){
        listeners.clear();
    }

}
