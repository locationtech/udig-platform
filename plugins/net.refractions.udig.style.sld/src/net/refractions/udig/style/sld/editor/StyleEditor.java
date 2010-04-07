package net.refractions.udig.style.sld.editor;

import javax.xml.transform.TransformerException;

import org.geotools.styling.SLDTransformer;
import org.geotools.styling.StyledLayerDescriptor;

/**
 * StyleEditor facade.
 */
public class StyleEditor {
    /**
     * Extension point processed for sld editor
     */
    public static final String ID = "net.refractions.udig.style.sld.editor"; //$NON-NLS-1$
    /**
     * Amount to indent nested SLD documents
     */
    public static final int INDENT = 4;
    
    public static String styleToXML(StyledLayerDescriptor sld) {
        SLDTransformer aTransformer = new SLDTransformer();
        aTransformer.setIndentation(StyleEditor.INDENT);
        try {
            return aTransformer.transform(sld);
        } catch (TransformerException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    
}
