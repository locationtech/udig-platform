package net.refractions.udig.project.internal;

import java.util.List;

import net.refractions.udig.project.IBlackboard;

import org.eclipse.emf.ecore.EObject;

/**
 * Blackboard for the internal emf model.
 * 
 * @author Justin Deoliveira,Refractions Research Inc,jdeolive@refractions.net
 * @model
 */
public interface Blackboard extends EObject, IBlackboard {

    /**
     * This method should not be accessed by client code. It is used by the framework to provide
     * persistance.
     * 
     * @return the content of the blackboard.
     * @model containment="true" type="BlackboardEntry"
     */
    List<BlackboardEntry> getEntries();

}