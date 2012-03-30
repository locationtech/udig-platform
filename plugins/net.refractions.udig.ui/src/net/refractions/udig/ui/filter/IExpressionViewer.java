package net.refractions.udig.ui.filter;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.geotools.data.Parameter;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.expression.Expression;

public abstract class IExpressionViewer extends Viewer {
    
    /** Maximum value (may be used by a spinner or slider) */
    public static int MIN = 0;
    /** Minium value (may be used by a spinner or slider) */
    public static int MAX = 255;
    /** Used to list sample values */
    public static String OPTIONS = Parameter.OPTIONS;
    /** Used to indicate List element type */
    public static String ELEMENT = Parameter.ELEMENT;

    /**
     * 
     */
    public IExpressionViewer( Composite parent) {
        this(parent, SWT.SINGLE);
    }
    
    /**
     * 
     */
    public IExpressionViewer( Composite parent, int style ) {
        
    }
    
    public abstract Control getControl();
    
    public abstract void setRequired(boolean required);
    
    public abstract boolean isRequired();

    // self validate based on listening to input
    public abstract String getValidationMessage(); // List<String>?
    public abstract Expression getInput();
    public abstract ISelection getSelection();
    public abstract void refresh();
    public abstract void setInput(Object input);
    public abstract void setSelection(ISelection selection, boolean reveal);
    public abstract void feedback();
    public abstract void feedback(String warning);
    public abstract void feedback(String exception, Exception eek);

    // context!
    public abstract void setSchema(SimpleFeatureType schema); // pass in context to list attributes name
    public abstract SimpleFeatureType getSchema(); 
    public abstract void setExpected( Class<?> binding );
    public abstract Class<?> getExpected(); // Expected class - example Colour

}
