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

import static org.junit.Assert.assertTrue;
import org.locationtech.udig.issues.IssueConfiguration;
import org.locationtech.udig.issues.IssueConstants;
import org.locationtech.udig.issues.internal.view.IssuesContentProvider;
import org.locationtech.udig.issues.internal.view.IssuesView;

import org.eclipse.ui.PlatformUI;
import org.junit.Test;

public class IssuesViewRefresherTest {
    
    @Test
    public void testRefresh() throws Exception {
        IssuesView view = (IssuesView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(IssueConstants.VIEW_ID);
        
        final boolean[] refresh=new boolean[1];
        
        view.setContentProvider(new IssuesContentProvider(){
            @Override
            public Object[] getElements( Object inputElement ) {
                refresh[0]=true;
                return super.getElements(inputElement);
            }
        });
        
        
        assertTrue(refresh[0]);
        
        refresh[0]=false;
        
        IssueConfiguration.get().createViewRefeshControl().refresh();
        
        assertTrue(refresh[0]);
        
    }

}
