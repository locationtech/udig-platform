/**
 * 
 */
package org.locationtech.udig.issues.test;

import org.locationtech.udig.core.enums.Priority;
import org.locationtech.udig.issues.AbstractIssue;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewPart;
import org.geotools.geometry.jts.ReferencedEnvelope;

class DummyIssue extends AbstractIssue {

    private String obj;

    /**
     * @param problemObject will be returned by {@link #getProblemObject()}
     */
    public DummyIssue( int problemObject ) {
        obj=String.valueOf(problemObject);
    }
    
    /**
     * 
     * @param problemObject will be returned by {@link #getProblemObject()}
     * @param groupId will be returned by {@link #getGroupId()}
     */
    public DummyIssue( int problemObject, String groupId ) {
        obj=String.valueOf(problemObject);
        setGroupId(groupId);
    }

    public String getProblemObject() {
        return obj;
    }

    public Priority getPriorityInternal() {
        return null;
    }

    public void fixIssue( IViewPart part, IEditorPart editor ) {
        //do nada
    }

	public String getExtensionID() {
		return null;
	}

    public void init( IMemento memento, IMemento viewMemento, String issueId, String groupId, ReferencedEnvelope bounds ) {
    }

    public void save( IMemento memento ) {
    }
	
    
}
