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
import org.postgis.MultiPolygon;


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
        
        // http://comments.gmane.org/gmane.comp.gis.udig.devel/6560
        // says Layer -> FeatureType -> GeometryAttributeType (based on GeoResource)
        //Boolean test = layer.getGeoResource();
        
        //System.out.println(layer.getGeoResource().getTitle());
        
        
        /*IBlackboard test = layer.getBlackboard();
        Set<String> keylist = test.keySet();
        for (String key: keylist) {
            System.out.println(key);
        }
        //Glyph icon = (Glyph)test.get("generated icon");*/

        
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
