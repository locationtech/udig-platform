package net.refractions.udig.project.geoselection;

import java.util.Iterator;

import org.eclipse.core.runtime.IAdaptable;

/**
 * The interface for GeoSelection containers implementations.
 * <p>
 * The containers follow to IAdaptable design pattern to hide the 
 * implementation specifics. Usually <code>IGeoSelectionListener</code>s should not be
 * aware about actual implementation class of IGeoSelection.
 * <p>
 * 
 * 
 * @author Vitalus
 */
public interface IGeoSelection extends IAdaptable {

    /**
     * Custom implementations of interface can iterate through arbitrary set of objects with
     * "selected" semantic.
     * <p>
     * 
     * @return
     */
    public Iterator iterator();
    
    

}
