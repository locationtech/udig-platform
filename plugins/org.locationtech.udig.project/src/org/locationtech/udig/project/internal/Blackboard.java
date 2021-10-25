/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal;

import java.util.List;

import org.locationtech.udig.project.IBlackboard;

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
