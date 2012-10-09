/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.refractions.udig.issues.test;

import static org.junit.Assert.assertTrue;
import net.refractions.udig.issues.IssueConfiguration;
import net.refractions.udig.issues.IssueConstants;
import net.refractions.udig.issues.internal.view.IssuesContentProvider;
import net.refractions.udig.issues.internal.view.IssuesView;

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
