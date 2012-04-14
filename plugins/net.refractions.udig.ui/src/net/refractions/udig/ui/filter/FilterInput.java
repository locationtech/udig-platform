package net.refractions.udig.ui.filter;

import org.opengis.feature.simple.SimpleFeatureType;

/**
 * Used with {@link IFilterViewer#setInput(Object)} offer the user context
 * to help construct a Filter.
 * <p>
 * Example:<pre>filterViewer.setInput( new IFilterViewer.Input( schema, true ) )</pre>
 * <p>
 * 
 * @author Jody Garnett
 */
public class FilterInput {
    protected boolean required = false;
    protected SimpleFeatureType schema;
    public FilterInput(){
        this( null );
    }
    public FilterInput(SimpleFeatureType schema){
        this( schema, false);
    }
    public FilterInput(SimpleFeatureType schema, boolean required){
        this.schema = schema;
        this.required = required;
    }
    /**
     * @return true if this is a required field
     */
    public boolean isRequired() {
        return required;
    }
    /**
     * The isRequired flag will be used to determine the default decoration to show (if there is no
     * warning or error to take precedence).
     * <p>
     * Please note that if this is a required field Filter.EXLCUDE is not considered to be a valid
     * state.
     * </p>
     * 
     * @param isRequired true if this is a required field
     */
    public void setRequired(boolean required){
        this.required = required;
    }
    
    /**
     * Feature Type to use as a reference point for defining this filter.
     * <p>
     * This is often used to suggest attribute names to the user; for example comparable attributes
     * when defining PropertyIsGreater filter.
     * 
     * @param schema
     */
    public void setSchema(SimpleFeatureType schema){
        this.schema = schema;
    }

    /**
     * Feature Type used by the FilterViewer
     * 
     * @param schema
     */
    public SimpleFeatureType getSchema(){
        return schema;
    }
}