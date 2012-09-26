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
