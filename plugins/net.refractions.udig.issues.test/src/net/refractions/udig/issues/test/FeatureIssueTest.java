package net.refractions.udig.issues.test;

import java.awt.Dimension;

import net.refractions.udig.issues.FeatureIssue;
import net.refractions.udig.issues.IIssue;
import net.refractions.udig.issues.internal.view.IssueHandler;
import net.refractions.udig.issues.listeners.IIssueListener;
import net.refractions.udig.issues.listeners.IssueChangeType;
import net.refractions.udig.issues.listeners.IssueEvent;
import net.refractions.udig.issues.listeners.IssuePropertyChangeEvent;
import net.refractions.udig.AbstractProjectUITestCase;
import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.core.enums.Priority;
import net.refractions.udig.core.enums.Resolution;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.command.MapCommand;
import net.refractions.udig.project.command.UndoableCommand;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.tests.support.MapTests;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.ui.WaitCondition;
import net.refractions.udig.ui.tests.support.UDIGTestUtil;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.XMLMemento;
import org.geotools.data.FeatureSource;
import org.geotools.feature.Feature;
import org.geotools.filter.FidFilter;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

public class FeatureIssueTest extends AbstractProjectUITestCase {

	public class BlankCommand extends AbstractCommand implements MapCommand, UndoableCommand {

        public void run( IProgressMonitor monitor ) throws Exception {
        }

        public String getName() {
            return null;
        }

        public void rollback( IProgressMonitor monitor ) throws Exception {
        }

    }

    private Map map;
	private IGeoResource resource;
	private Feature[] features;
	protected void setUp() throws Exception {
        super.setUp();
		features = UDIGTestUtil.createDefaultTestFeatures("test", 20); //$NON-NLS-1$
		resource = MapTests.createGeoResource(features, true);
		map=MapTests.createNonDynamicMapAndRenderer(resource, new Dimension(10,10));
	}

	protected void tearDown() throws Exception {
		map.getProjectInternal().getElementsInternal().remove(map);
		CatalogPlugin.getDefault().getLocalCatalog().remove(resource.service(null));
        super.tearDown();
	}

	/*
	 * Test method for 'net.refractions.udig.project.ui.FeatureIssue.fixIssue(IViewPart, IEditorPart)'
	 */
	public void testFixIssue() throws Exception {
		Layer layer= map.getLayersInternal().get(0);
		layer.setCRS(DefaultGeographicCRS.WGS84);
		CoordinateReferenceSystem crs = CRS.decode("EPSG:3005");//$NON-NLS-1$
		final MathTransform mt=CRS.findMathTransform(layer.getCRS(), crs, true);
		map.getViewportModelInternal().setCRS(crs);
		FeatureIssue issue=new FeatureIssue(Priority.LOW, "Description",layer, features[0], "test" ); //$NON-NLS-1$ //$NON-NLS-2$

		if( issue.getViewPartId()!=null  ){
			IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IViewPart view = activePage.findView(issue.getViewPartId());
			activePage.hideView(view);
		}

		assertFalse(ApplicationGIS.getActiveMap()==map);


        IssueHandler handler = IssueHandler.createHandler(issue);

        handler.fixIssue();
        map.sendCommandSync(new BlankCommand());
        UDIGTestUtil.inDisplayThreadWait(10000,new WaitCondition(){

			public boolean isTrue()  {
                try{
				return features[0].equals(map.getEditManager().getEditFeature()) &&
				map.getViewportModel().getBounds().contains(JTS.transform(features[0].getBounds(),null,mt, 5));
                }catch (Exception e) {
                    throw (RuntimeException)new RuntimeException().initCause(e);
                }
			}

        }, true);
        assertTrue(layer.getFilter() instanceof FidFilter);
        assertEquals(features[0].getID(), ((FidFilter)layer.getFilter()).getFids()[0]);
        assertEquals(features[0], map.getEditManager().getEditFeature());
        assertEquals("active map should be the map of the issue", map, ApplicationGIS.getActiveMap()); //$NON-NLS-1$
        assertTrue("Map must contain feature", map.getViewportModel().getBounds().contains(JTS.transform(features[0].getBounds(),null,mt, 5))); //$NON-NLS-1$
	}

    public void testSetDescriptionEvents() throws Exception {
        FeatureIssue issue=IssuesListTestHelper.createFeatureIssue("Issue"); //$NON-NLS-1$

        Listener l=new Listener();
        issue.addIssueListener(l);
        Object oldValue=issue.getDescription();
        String newDescription="new Description";//$NON-NLS-1$
        issue.setDescription(newDescription);
        assertEquals(issue, l.source);
        assertEquals(newDescription, l.newValue);
        assertEquals(oldValue, l.oldValue);
        assertEquals(IssueChangeType.DESCRIPTION, l.change);
    }

    public void testSetPriorityEvents() throws Exception {
        FeatureIssue issue=IssuesListTestHelper.createFeatureIssue("Issue"); //$NON-NLS-1$

        Listener l=new Listener();
        issue.addIssueListener(l);
        Object oldValue=issue.getPriority();
        Priority newPriority=Priority.TRIVIAL;
        issue.setPriority(newPriority);
        assertEquals(issue, l.source);
        assertEquals(newPriority, l.newValue);
        assertEquals(oldValue, l.oldValue);
        assertEquals(IssueChangeType.PRIORITY, l.change);
    }

    public void testSetResolutionEvents() throws Exception {
        FeatureIssue issue=IssuesListTestHelper.createFeatureIssue("Issue"); //$NON-NLS-1$

        Listener l=new Listener();
        issue.addIssueListener(l);
        Object oldValue=issue.getResolution();
        Resolution newValue=Resolution.UNKNOWN;
        issue.setResolution(newValue);
        assertEquals(issue, l.source);
        assertEquals(newValue, l.newValue);
        assertEquals(oldValue, l.oldValue);
        assertEquals(IssueChangeType.RESOLUTION, l.change);
    }

    public void testPersistence() throws Exception {
        IMap map=MapTests.createDefaultMap("name", 1, true, new Dimension(10,10)); //$NON-NLS-1$
        ILayer layer=map.getMapLayers().get(0);
        Feature feature=layer.getResource(FeatureSource.class, null).getFeatures().features().next();

        IIssue original=new FeatureIssue(Priority.WARNING, "test description", layer, feature, "groupID"); //$NON-NLS-1$ //$NON-NLS-2$
        XMLMemento memento=XMLMemento.createWriteRoot("memento"); //$NON-NLS-1$
        XMLMemento viewMemento=XMLMemento.createWriteRoot("viewMemento"); //$NON-NLS-1$
        original.save(memento);
        original.getViewMemento(viewMemento);
        FeatureIssue restored=new FeatureIssue();
        restored.init(memento, viewMemento, original.getId(), original.getGroupId(), original.getBounds());
        restored.setDescription(original.getDescription());
        restored.setPriority(original.getPriority());
        restored.setResolution(original.getResolution());

        assertEquals(original.getBounds(), restored.getBounds());
        assertEquals(original.getDescription(), restored.getDescription());
        assertEquals(original.getEditorID(), restored.getEditorID());
        assertEquals(original.getEditorInput(), restored.getEditorInput());
        assertEquals(original.getExtensionID(), restored.getExtensionID());
        assertEquals(original.getGroupId(), restored.getGroupId());
        assertEquals(original.getId(), restored.getId());
        assertEquals(original.getPerspectiveID(), restored.getPerspectiveID());
        assertEquals(original.getPriority(), restored.getPriority());
        assertEquals(original.getProblemObject(), restored.getProblemObject());
        for( int i = 0; i < original.getPropertyNames().length; i++ ) {
            assertEquals(original.getPropertyNames()[i], restored.getPropertyNames()[i]);
        }
        assertEquals(original.getResolution(), restored.getResolution());
        assertEquals(original.getViewPartId(), restored.getViewPartId());
    }

    class Listener implements IIssueListener{
        IIssue source;
        IssueChangeType change;
        Object newValue, oldValue;
        String propertyKey;
        public void notifyChanged( IssueEvent event){
            this.source=event.getSource();
            this.change=event.getChange();
            this.newValue=event.getNewValue();
            this.oldValue=event.getOldValue();
            this.propertyKey=null;
        }
        public void notifyPropertyChanged( IssuePropertyChangeEvent event){
            this.source=event.getSource();
            this.change=null;
            this.newValue=event.getNewValue();
            this.oldValue=event.getOldValue();
            this.propertyKey=event.getPropertyKey();
        }
    }
}
