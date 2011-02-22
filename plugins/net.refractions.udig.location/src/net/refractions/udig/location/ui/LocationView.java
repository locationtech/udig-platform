package net.refractions.udig.location.ui;

import java.lang.reflect.Method;

import net.refractions.udig.location.AddressSeeker;
import net.refractions.udig.location.LocationUIPlugin;
import net.refractions.udig.location.USGLocation;
import net.refractions.udig.location.internal.Images;
import net.refractions.udig.location.internal.Messages;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.command.NavCommand;
import net.refractions.udig.project.command.NavigationCommandFactory;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.ui.SearchPart;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.geotools.feature.Feature;
import org.geotools.geometry.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

public class LocationView extends SearchPart {

    private Label label;
    private Text text;
    private Button bbox;
    private Action showAction;
    private USGLocation usg;
    private AddressSeeker seeker;

    /**
     * @param dialogSettings
     */
    public LocationView() {
        super(LocationUIPlugin.getDefault().getDialogSettings());
    }
    @Override
    public void init( IViewSite site, IMemento memento ) throws PartInitException {
        super.init(site, memento);
        usg = new USGLocation();
        seeker = new AddressSeeker();
    }

    static class Query {
        String text; // match against everything we can
        Envelope bbox; // latlong bbox
    }
    /**
     * Construct a query based on the state of the user interface controls, and possibly workbecnh.
     *
     * @return A catalog query
     */
    Query createQuery() {
        Query filter = new Query();
        filter.text = text.getText();

        text.setText("1500 Poydras St, New Orleans, LA"); //$NON-NLS-1$

        filter.bbox = new Envelope();
        if( bbox.getSelection()) {
            // TODO get current editor
            try {
                IEditorPart editor = getSite().getPage().getActiveEditor();
                Object obj = editor.getEditorInput();
                Class mapType = obj.getClass();
                Method get = mapType.getMethod("getExtent", null ); //$NON-NLS-1$
                Object value = get.invoke( obj, null );
                filter.bbox = (Envelope) value;
            }
            catch( Throwable t ) {
                LocationUIPlugin.log( "ha ha", t ); //$NON-NLS-1$
            }
        }
        return filter;
    }

    /**
     * TODO: called AddressSeeker!
     */
    @Override
    protected void searchImplementation( Object filter, IProgressMonitor monitor, ResultSet results ) {
        // STUB IT!
//        AttributeType[] types = new AttributeType[9];
//        types[0] = AttributeTypeFactory.newAttributeType("number", Number.class);
//        types[1] = AttributeTypeFactory.newAttributeType("prefix", String.class);
//        types[2] = AttributeTypeFactory.newAttributeType("type", String.class);
//        types[3] = AttributeTypeFactory.newAttributeType("street", String.class);
//        types[4] = AttributeTypeFactory.newAttributeType("suffix", String.class);
//        types[5] = AttributeTypeFactory.newAttributeType("city", String.class);
//        types[6] = AttributeTypeFactory.newAttributeType("state", String.class);
//        types[7] = AttributeTypeFactory.newAttributeType("zip", String.class);
//        types[8] = AttributeTypeFactory.newAttributeType("location", Point.class);
//        FeatureType type;
//        try {
//            type = FeatureTypeBuilder.newFeatureType(types, "Address");
//            GeometryFactory gf = new GeometryFactory();
//            Feature f = type.create(new Object[]{
//                 new Integer(1),
//                 "Home",
//                 "Address",
//                 "Bowker",
//                 "Ave",
//                 "Victoria",
//                 "bc",
//                 "v8r 2e4",
//                 gf.createPoint( new Coordinate(2,3))
//            });
//            List<Feature> stuff = new ArrayList<Feature>();
//            stuff.add( f );
//            return stuff;
//        } catch (Throwable e) {
//            e.printStackTrace();
//            return Collections.EMPTY_LIST;
//        }
        Query query = (Query) filter;

        try {
             results.addAll(seeker.geocode( query.text ));
        } catch (Exception e) {
            e.printStackTrace();
            results.add(Messages.LocationView_no_results);
       }
    }

    @Override
    public void createPartControl( Composite aParent ) {
        label = new Label(aParent, SWT.NONE);
        label.setText(Messages.LocationView_prompt);

        text = new Text(aParent, SWT.BORDER);
        text.setText(Messages.LocationView_default);
        text.setEditable(true);
        text.addSelectionListener(new SelectionListener(){
            public void widgetDefaultSelected( SelectionEvent e ) {
                search(createQuery()); // seach according to filter
            }
            public void widgetSelected( SelectionEvent e ) {
                quick(text.getText());
            }
        });

        // Create bbox button
        bbox = new Button(aParent, SWT.CHECK);
        bbox.setText(Messages.LocationView_bbox);
        bbox.setToolTipText(Messages.LocationView_bboxTooltip);

        super.createPartControl(aParent);

        // Layout using Form Layout (+ indicates FormAttachment)
        // +
        // +label+text+bbox+
        // +
        // contents
        // +
        FormLayout layout = new FormLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.spacing = 0;
        aParent.setLayout(layout);

        FormData dLabel = new FormData(); // bind to left & text
        dLabel.left = new FormAttachment(0);
        dLabel.top = new FormAttachment(text, 5, SWT.CENTER);
        label.setLayoutData(dLabel);

        FormData dText = new FormData(); // bind to top, label, bbox
        dText.top = new FormAttachment(1);
        dText.left = new FormAttachment(label, 5);
        dText.right = new FormAttachment(bbox, -5);
        text.setLayoutData(dText);

        FormData dBbox = new FormData(); // text & right
        dBbox.right = new FormAttachment(100);
        dBbox.top = new FormAttachment(text, 0, SWT.CENTER);
        bbox.setLayoutData(dBbox);

        FormData dsashForm = new FormData(100, 100); // text & bottom
        dsashForm.right = new FormAttachment(100); // bind to right of form
        dsashForm.left = new FormAttachment(0); // bind to left of form
        dsashForm.top = new FormAttachment(text, 2); // attach with 5 pixel offset
        dsashForm.bottom = new FormAttachment(100); // bind to bottom of form

        splitter.setWeights(new int[]{60,40});
        splitter.setLayoutData(dsashForm);
        createContextMenu();
    }

    /**
     * Must go places!
     *
     * @param selection
     */
    public void showLocation( Object selection ){
        // selection should be an Feture (of some sort)
        Feature feature = (Feature) selection;
        Geometry geom = feature.getDefaultGeometry();
        Point point = geom.getCentroid();

        IMap imap = ApplicationGIS.getActiveMap();
        if( imap == ApplicationGIS.NO_MAP ) return;

        CoordinateReferenceSystem world = imap.getViewportModel().getCRS();
        CoordinateReferenceSystem wsg84 = DefaultGeographicCRS.WGS84;

        double buffer = 0.01; // how much of the wgs84 world to see
        Envelope view = point.buffer( buffer ).getEnvelopeInternal();

        MathTransform transform;
        try {
            transform = CRS.transform( wsg84, world, true ); // relaxed
        } catch (FactoryException e) {
            return; // no go
        }
        Envelope areaOfInterest;
        try {
            areaOfInterest = JTS.transform( view, transform, 10 );
        } catch (TransformException e) {
            return; // no go
        }

        NavigationCommandFactory navigate = NavigationCommandFactory.getInstance();

        NavCommand show = navigate.createSetViewportBBoxCommand( areaOfInterest );
        imap.sendCommandASync( show );
    }

    /**
    *
    * @return
    */
   protected IBaseLabelProvider createLabelProvider() {
       return new LabelProvider(){
           public String getText( Object element ) {
               if( element instanceof Feature ){
                   Feature feature = (Feature) element;
                   return feature.getID();
               }
               return super.getText(element);
            }
       };
   }

    private void createContextMenu() {
        final MenuManager contextMenu = new MenuManager();
        showAction = new Action() {
            public void run() {
                IStructuredSelection sel = (IStructuredSelection)viewer.getSelection();
                showLocation( sel.getFirstElement() );
            }
        };

        Messages.initAction(showAction, "action_show"); //$NON-NLS-1$
        contextMenu.setRemoveAllWhenShown(true);
        contextMenu.addMenuListener(new IMenuListener() {

            public void menuAboutToShow(IMenuManager mgr) {
                contextMenu.add(new GroupMarker(
                        IWorkbenchActionConstants.MB_ADDITIONS));
                contextMenu.add(new Separator());

                showAction.setImageDescriptor( Images.getDescriptor(Images.SHOW_CO));

                contextMenu.add(showAction);
            }

        });

        // Create menu.
        Menu menu = contextMenu.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);

        // Register menu for extension.
        getSite().registerContextMenu(contextMenu, viewer);

    }
}
