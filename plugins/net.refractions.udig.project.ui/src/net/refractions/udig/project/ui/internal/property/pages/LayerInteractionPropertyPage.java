/**
 * 
 */
package net.refractions.udig.project.ui.internal.property.pages;

import net.refractions.udig.project.internal.Layer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.geometry.jts.Geometries;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.postgis.MultiPolygon;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;


/**
 * @author pfeiffp
 *
 */
public class LayerInteractionPropertyPage extends PropertyPage implements IWorkbenchPropertyPage {

    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createContents( Composite parent ) {
        final Layer layer = (Layer) getElement();
        
        boolean isPolygon = false;
        boolean isRaster = false;
        SimpleFeatureType schema = layer.getSchema();
        
        // check if layer is polygon
        if( schema != null ){
            GeometryDescriptor geomDescriptor = schema.getGeometryDescriptor();
            if( geomDescriptor != null ){
                Class< ? extends Geometry > binding = (Class< ? extends Geometry>) geomDescriptor.getType().getBinding();
                switch( Geometries.getForBinding( binding ) ){
                case MULTIPOLYGON:
                case POLYGON:
                        isPolygon = true;
                break;
                default:
                }
            }
        } 
        // check if raster layer
        else {
            if( layer.canAdaptTo(AbstractGridCoverage2DReader.class)){
                isRaster = true;
            }
        }
        
        
        Composite interactionPage = new Composite(parent, SWT.NONE);
        
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        interactionPage.setLayout(layout);
        
        Group generalGroup = new Group(interactionPage, SWT.SHADOW_ETCHED_IN);
        generalGroup.setText("General");
        
        Button visibleButton = new Button(generalGroup, SWT.CHECK);
        visibleButton.setText("Visible");
        visibleButton.setLocation(40, 20);
        visibleButton.pack();
        visibleButton.setSelection(layer.isVisible());
        visibleButton.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                Boolean selected = ((Button)event.widget).getSelection();
                layer.setVisible(selected);
            }
            public void widgetDefaultSelected(SelectionEvent event) {
            }
        });
        
        Group toolsGroup = new Group(interactionPage, SWT.SHADOW_ETCHED_IN);
        toolsGroup.setText("Tools");
        
        Button layerButton = new Button(toolsGroup, SWT.RADIO);
        layerButton.setText("Layer");
        layerButton.setLocation(20, 20);
        layerButton.pack();

        Button infromationButton = new Button(toolsGroup, SWT.CHECK);
        infromationButton.setText("Information");
        infromationButton.setLocation(40, 40);
        infromationButton.pack();
        infromationButton.setEnabled(true);
        infromationButton.setSelection(layer.isApplicable("information"));
        
        Button selectButton = new Button(toolsGroup, SWT.CHECK);
        selectButton.setText("Select");
        selectButton.setLocation(40, 60);
        selectButton.pack();
        selectButton.setSelection(layer.isSelectable());
        
        Button editButton = new Button(toolsGroup, SWT.CHECK);
        editButton.setText("Edit");
        editButton.setLocation(40, 80);
        editButton.pack();
        editButton.setSelection(layer.isApplicable("editable"));
        
        Button backgroundButton = new Button(toolsGroup, SWT.RADIO);
        backgroundButton.setText("Background");
        backgroundButton.setLocation(20, 100);
        backgroundButton.pack();

        Button boundaryButton = new Button(toolsGroup, SWT.CHECK);
        boundaryButton.setText("Boundary");
        boundaryButton.setLocation(40, 120);
        boundaryButton.pack();
        
        return interactionPage;
    }

}
