package net.refractions.udig.tutorials.genericprojectelement;

import java.util.Collections;
import java.util.List;

import net.refractions.udig.project.element.AbstractGenericProjectElement;
import net.refractions.udig.project.element.IGenericProjectElement;

import org.eclipse.ui.IMemento;

/**
 * A silly implementation.  
 * @author jesse
 * @since 1.1.0
 */
public class MyProjectElement extends AbstractGenericProjectElement implements IGenericProjectElement {

    public static final String EXT_ID = "net.refractions.udig.tutorials.genericprojectelement.element1"; //$NON-NLS-1$
	public static final String PROP_LABEL_CHANGE = "custom_event";
    private String m_label;

    /**
     * Called when the element is loaded from disk.  This restores state
     */
    public void init( IMemento memento ) {
        m_label = memento.getTextData();
    }

    /**
     * Called when saving to disk.  This saves state
     */
    public void save( IMemento memento ) {
        memento.putTextData(m_label);
    }

    public String getLabel() {
        return m_label;
    }

    public void setLabel( String label ) {
        m_label = label;
        // this is needed to that the project element knows that data is changed
        // and will save on shutdown
        setDirty(true);
    }

    public List getElements( Class type ) {
        return Collections.emptyList();
    }

    public List getElements() {
        return Collections.emptyList();
    }
    
}
