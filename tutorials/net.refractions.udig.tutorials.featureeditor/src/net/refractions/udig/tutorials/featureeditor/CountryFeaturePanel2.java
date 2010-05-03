package net.refractions.udig.tutorials.featureeditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.miginfocom.swt.MigLayout;
import net.refractions.udig.feature.editor.field.AttributeField;
import net.refractions.udig.feature.editor.field.ComboAttributeField2;
import net.refractions.udig.feature.editor.field.StringAttributeField;
import net.refractions.udig.project.ui.IFeaturePanel;
import net.refractions.udig.project.ui.IFeatureSite;
import net.refractions.udig.project.ui.feature.EditFeature;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.PartInitException;

/**
 * Country Feature Panel used to show the use of AttributeFields.
 * @since 1.2.0
 */
public class CountryFeaturePanel2 extends IFeaturePanel {
    // private static final String DIRTY = "DIRTY";

    /** Attribute name for attribute GMI_CNTRY */
    public final static String GMI_CNTRY = "GMI_CNTRY";

    /** Attribute name for attribute REGION */
    public final static String COLOR_MAP = "COLOR_MAP";

    /** Attribute name for attribute NAME */
    public final static String NAME = "CNTRY_NAME";

    public final static Object[] COLOR_MAP_OPTS = new Object[]{1, 2, 3, 4, 5, 6, 7, 8};

    StringAttributeField gmiCntry;    
    StringAttributeField name;
    ComboAttributeField2 colorMap;

    List<AttributeField> fields = new ArrayList<AttributeField>();

    private IPropertyChangeListener listener = new IPropertyChangeListener(){        
        public void propertyChange( PropertyChangeEvent event ) {
            AttributeField field = (AttributeField) event.getSource();
            System.out.println( field.getAttributeName() + " = "+event.getNewValue() );
            if( field.isValid()){
                field.store();
            }
            else {
                System.out.println( field.getAttributeName() + " invalid!" );                
            }
        }
    };
    
    @Override
    public void aboutToBeShown() {
        for( AttributeField field : fields ){
            field.setPropertyChangeListener(listener);
            field.setFeature(getSite().getEditFeature());
        }
    }

    @Override
    public void aboutToBeHidden() {
        for( AttributeField field : fields ){
            field.setPropertyChangeListener(null);
            // field.setFeature(null);
        }
    }
    
    @Override
    public void dispose() {
        for( AttributeField field : fields ){
            field.dispose();
            field.setPropertyChangeListener(null);
            field.setFeature(null);
        }
        super.dispose();        
    }

    /**
     * Step 0 - Default constructor.
     */
    public CountryFeaturePanel2() {
    }

    /**
     * Step 1 - init using the editor site and memento holding any information from last time
     */
    @Override
    public void init( IFeatureSite site, IMemento memento ) throws PartInitException {
        super.init(site, memento);
    }

    @Override
    public void createPartControl( Composite parent ) {
        parent.setLayout(new MigLayout("", "[right]10[left, grow][min!][min!]", "30"));

        name = new StringAttributeField(NAME, "Country", parent);
        fields.add(name);

        gmiCntry = new StringAttributeField(GMI_CNTRY, "Code", parent);
        fields.add(gmiCntry);

        // JFace Viewer
        colorMap = new ComboAttributeField2(COLOR_MAP, "Color Map", Arrays.asList(COLOR_MAP_OPTS),
                parent);
        fields.add(colorMap);
    }
    
    @Override
    public void refresh() {
        EditFeature feature = getSite().getEditFeature();
        for( AttributeField field : fields ){
            field.setFeature( feature );
            field.load();
        }
    }

    @Override
    public String getDescription() {
        return "Details on the selected country.";
    }

    @Override
    public String getName() {
        return "Country2";
    }

    @Override
    public String getTitle() {
        return "Country Details";
    }

}
