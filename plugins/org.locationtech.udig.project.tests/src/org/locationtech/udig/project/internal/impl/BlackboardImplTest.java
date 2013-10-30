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
package net.refractions.udig.project.internal.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import net.refractions.udig.project.internal.Blackboard;
import net.refractions.udig.project.internal.ProjectFactory;
import net.refractions.udig.project.tests.support.AbstractProjectTestCase;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class BlackboardImplTest extends AbstractProjectTestCase {
	
	Blackboard blackboard;
	
	@Before
	public void setUp() throws Exception {
		blackboard = ProjectFactory.eINSTANCE.createBlackboard();
		APersister.enabled = true;
		BPersister.enabled = true;
		CPersister.enabled = true;
	}
	
	@Test
	public void testPersist0() {
		//test a simple put
		A a = new A("a"); //$NON-NLS-1$
		blackboard.put("a", a); //$NON-NLS-1$
		blackboard.flush();
		
		a = (A) blackboard.get("a"); //$NON-NLS-1$
		assertNotNull(a);
		assertEquals("a", a.getMessage()); //$NON-NLS-1$
	}
	
	@Ignore
	@Test
	public void testPersist1() {
		//test persistance through inheritance
		B b = new B("b"); //$NON-NLS-1$
		blackboard.put("b", b); //$NON-NLS-1$
		blackboard.flush();
		
		BPersister.enabled = false;
		
		b = (B)blackboard.get("b"); //$NON-NLS-1$
		assertNotNull(b);
		assertEquals("c", b.getMessage()); //$NON-NLS-1$
	}
	
	@Ignore
    @Test
	public void testPersist2() {
		//test persistance through inheritance with multiple persisters
		B b = new B("b"); //$NON-NLS-1$
		blackboard.put("b", b); //$NON-NLS-1$
		blackboard.flush();
		
		b = (B)blackboard.get("b"); //$NON-NLS-1$
		assertNotNull(b);
		assertEquals("b", b.getMessage()); //$NON-NLS-1$
	}
	
    @Test
	public void testProvide() {
		A a = (A) blackboard.get("a"); //$NON-NLS-1$
		assertNotNull(a);
	}
}