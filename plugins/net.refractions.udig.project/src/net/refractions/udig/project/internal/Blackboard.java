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
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    String copyright = "uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details."; //$NON-NLS-1$

    /**
     * This method should not be accessed by client code. It is used by the framework to provide
     * persistance.
     * 
     * @return the content of the blackboard.
     * @model containment="true" type="BlackboardEntry"
     */
    List<BlackboardEntry> getEntries();

}