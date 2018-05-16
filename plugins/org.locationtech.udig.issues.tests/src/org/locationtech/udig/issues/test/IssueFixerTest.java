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
package org.locationtech.udig.issues.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.locationtech.udig.AbstractProjectUITestCase;
import org.locationtech.udig.core.enums.Resolution;
import org.locationtech.udig.issues.FixableIssue;
import org.locationtech.udig.issues.IIssue;

import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class IssueFixerTest extends AbstractProjectUITestCase {

    DummyIssueFixer fixer;
    FixableIssue issue1;
    IIssue issue2;
    IMemento fixerMemento;
    
    @Before
    public void setUp() throws Exception {
        fixer = new DummyIssueFixer();
        issue1 = new FixableIssue();
        issue2 = new DummyIssue(0);
        fixerMemento = XMLMemento.createWriteRoot("fixerMemento"); //$NON-NLS-1$
    }
    
    @After
    public void tearDown() throws Exception {
        fixer = null;
        fixerMemento = null;
        issue1 = null;
        issue2 = null;
    }

    @Test
    public void testCanFix() {
        fixerMemento.putString(DummyIssueFixer.KEY_FIXABLE, "FALSE"); //$NON-NLS-1$
        assertFalse(fixer.canFix(issue1, fixerMemento));
        fixerMemento.putString(DummyIssueFixer.KEY_FIXABLE, "TRUE"); //$NON-NLS-1$
        assertFalse(fixer.canFix(issue2, fixerMemento));
        assertTrue(fixer.canFix(issue1, fixerMemento));
    }
    
    @Test
    public void testFix() {
        fixerMemento.putString(DummyIssueFixer.KEY_FIXABLE, "TRUE"); //$NON-NLS-1$
        assertEquals(Resolution.UNRESOLVED, issue1.getResolution());
        fixer.fix(issue1, fixerMemento);       
        assertEquals(Resolution.IN_PROGRESS, issue1.getResolution());
        fixer.complete(issue1);
        assertEquals(Resolution.RESOLVED, issue1.getResolution());
    }
    
    @Test
    public void testExtension() {
        fixerMemento.putString(DummyIssueFixer.KEY_FIXABLE, "TRUE"); //$NON-NLS-1$
        issue1.setFixerMemento(fixerMemento);
        issue1.setResolution(Resolution.UNRESOLVED);
        issue1.fixIssue(null, null);
        assertEquals(Resolution.RESOLVED, issue1.getResolution());
    }

}
