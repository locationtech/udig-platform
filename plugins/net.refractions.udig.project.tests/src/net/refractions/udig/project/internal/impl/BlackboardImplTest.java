package net.refractions.udig.project.internal.impl;

import net.refractions.udig.project.internal.Blackboard;
import net.refractions.udig.project.internal.ProjectFactory;
import net.refractions.udig.project.tests.support.AbstractProjectTestCase;

public class BlackboardImplTest extends AbstractProjectTestCase {

	Blackboard blackboard;

	 @Override
	protected void setUp() throws Exception {
         super.setUp();
		 blackboard = ProjectFactory.eINSTANCE.createBlackboard();
		 APersister.enabled = true;
		 BPersister.enabled = true;
		 CPersister.enabled = true;
	}

	public void testPersist0() {
		//test a simple put
		A a = new A("a"); //$NON-NLS-1$
		blackboard.put("a", a); //$NON-NLS-1$
		blackboard.flush();

		a = (A) blackboard.get("a"); //$NON-NLS-1$
		assertNotNull(a);
		assertEquals("a", a.getMessage()); //$NON-NLS-1$
	}

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

	public void testPersist2() {
		//test persistance through inheritance with multiple persisters
		B b = new B("b"); //$NON-NLS-1$
		blackboard.put("b", b); //$NON-NLS-1$
		blackboard.flush();

		b = (B)blackboard.get("b"); //$NON-NLS-1$
		assertNotNull(b);
		assertEquals("b", b.getMessage()); //$NON-NLS-1$
	}

	public void testProvide() {
		A a = (A) blackboard.get("a"); //$NON-NLS-1$
		assertNotNull(a);
	}
}
