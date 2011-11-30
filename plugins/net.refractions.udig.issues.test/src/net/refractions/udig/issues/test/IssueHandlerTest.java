package net.refractions.udig.issues.test;

import java.awt.Dimension;

import net.refractions.udig.AbstractProjectUITestCase;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.internal.ui.MapPerspective;
import net.refractions.udig.issues.AbstractIssue;
import net.refractions.udig.issues.IssueConstants;
import net.refractions.udig.issues.internal.view.IssueHandler;
import net.refractions.udig.issues.internal.view.IssuesView;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.tests.support.MapTests;
import net.refractions.udig.project.tests.ui.ViewPart1;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.internal.MapEditor;
import net.refractions.udig.project.ui.internal.MapEditorWithPalette;
import net.refractions.udig.project.ui.internal.MapPart;
import net.refractions.udig.ui.tests.support.UDIGTestUtil;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;

public class IssueHandlerTest extends AbstractProjectUITestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /*
     * Test method for 'net.refractions.udig.project.ui.internal.view.issues.IssueHandler.restorePerspective()'
     */
    public void testRestorePerspective() throws WorkbenchException {
        int windows=PlatformUI.getWorkbench().getWorkbenchWindowCount();
        IPerspectiveDescriptor p = PlatformUI.getWorkbench().getPerspectiveRegistry().findPerspectiveWithId("net.refractions.udig.project.tests.ui.perspective.test"); //$NON-NLS-1$
        getActiveWindow().getActivePage().setPerspective(p);
        assertEquals("net.refractions.udig.project.tests.ui.perspective.test", getActiveWindow().getActivePage().getPerspective().getId()); //$NON-NLS-1$
        TestIssue issue = new TestIssue(){
            @Override
            public String getPerspectiveID() {
                return MapPerspective.ID_PERSPECTIVE;
            }
        };
        IssueHandler handler = IssueHandler.createHandler(issue);
        handler.restorePerspective();
        assertEquals(MapPerspective.ID_PERSPECTIVE, getActiveWindow().getActivePage().getPerspective().getId());
        assertEquals(windows, PlatformUI.getWorkbench().getWorkbenchWindowCount());
    }

    /**
     *
     * @return
     */
    private IWorkbenchWindow getActiveWindow() {
        return PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    }

    /*
     * Test method for 'net.refractions.udig.project.ui.internal.view.issues.IssueHandler.restoreWorkbenchPart()'
     */
    public void testViewPart() {
        TestIssue issue = new TestIssue(){
            @Override
            public String getViewPartId() {
                return IssueConstants.VIEW_ID;
            }
        };
        IssueHandler handler = IssueHandler.createHandler(issue);
        handler.restoreViewPart();
        
        assertTrue( getActivePart() instanceof IssuesView );
        
        issue = new TestIssue(){
            @Override
            public String getViewPartId() {
                return ViewPart1.ID;
            }
        };
        handler = IssueHandler.createHandler(issue);
        handler.restoreViewPart();
        
        assertTrue( getActivePart() instanceof ViewPart1 );
        
        issue = new TestIssue(){
            @Override
            public String getViewPartId() {
                return ViewPart1.ID;
            }
            @Override
            public void getViewMemento(IMemento memento) {
                memento.putString("testKey", "value");  //$NON-NLS-1$//$NON-NLS-2$
            }
        };
        handler = IssueHandler.createHandler(issue);
        handler.restoreViewPart();

        assertTrue( getActivePart() instanceof ViewPart1 );
        assertEquals("value", ((ViewPart1)getActivePart()).memento.getString("testKey")); //$NON-NLS-1$ //$NON-NLS-2$
        
    }

    /**
     *
     * @return
     */
    private IWorkbenchPart getActivePart() {
        return getActiveWindow().getActivePage().getActivePart();
    }

    /*
     * Test method for 'net.refractions.udig.project.ui.internal.view.issues.IssueHandler.restoreEditor()'
     */
    public void testRestoreEditor() throws Exception {
        
        assertNull(getActiveWindow().getActivePage().getActiveEditor());
        
        SimpleFeature[] features = UDIGTestUtil.createDefaultTestFeatures("test", 4); //$NON-NLS-1$
        IGeoResource resource = MapTests.createGeoResource(features, false);
        final Map map = MapTests.createNonDynamicMapAndRenderer(resource, new Dimension(512,512));
        
        TestIssue issue = new TestIssue(){
            @Override
            public String getEditorID() {
                return MapEditorWithPalette.ID;
          
            }
            
            @Override
            public IEditorInput getEditorInput() {
                return ApplicationGIS.getInput(map);
            }
        };
        IssueHandler handler = IssueHandler.createHandler(issue);
        handler.restoreEditor();

        assertEquals( MapEditorWithPalette.class, getActiveWindow().getActivePage().getActiveEditor().getClass());
        assertEquals( map, ((MapPart)getActiveWindow().getActivePage().getActiveEditor()).getMap());
    }
    
    class TestIssue extends AbstractIssue{

        public String getProblemObject() {
            return null;
        }

        public void fixIssue( IViewPart part, IEditorPart editor ) {
        }

		public String getExtensionID() {
			return null;
		}

        public ReferencedEnvelope getBounds() {
            return null;
        }

        public void init( IMemento memento, IMemento viewMemento, String issueId, String groupId, ReferencedEnvelope bounds ) {
        }

        public void save( IMemento memento ) {
        }
        
    }

}
