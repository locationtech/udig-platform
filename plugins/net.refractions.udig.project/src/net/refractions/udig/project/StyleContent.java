package net.refractions.udig.project;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;

import net.refractions.udig.catalog.IGeoResource;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IMemento;

/**
 * Provides a way to persist an arbitrary object placed on the style blackboard.
 * 
 * @author Jesse
 * @since 1.0.0
 */
public abstract class StyleContent {

    /** <code>XPID</code> field */
    public static final String XPID = "net.refractions.udig.project.style"; //$NON-NLS-1$

    private final String id;

    /**
     * Unique identifier of the style. This id must be the same as the id declared by the extension
     * point.
     * <p>
     * This id is also used by Renderer and StyleConfigurator, often implementation will have a
     * <code>static final String ID</code> defined for programmers.
     * </p>
     * 
     * @return The style id which identifies the style.
     * @uml.property name="id"
     */
    public String getId() {
        return id;
    }

    /**
     * Construct with ID supplied by subclass.  This id must be the same as the id declared by the extension
     * point.
     * 
     * @param id
     */
    public StyleContent( String id ) {
        if( id == null ) {
            throw new NullPointerException("You MUST supply an ID when creating a Style Content. It is used as the 'key' when storing things on the blackboard");
        }
        this.id = id;
    }

    /**
     * Returns the class of the object which does the actual styling work.
     * 
     * @return the class of the style object.
     */
    public abstract Class<?> getStyleClass();

    /**
     * Saves the state of a style object.
     * <p>
     * (Currently used with XMLMemento to persist StyleEntry, it is hoped that an EMFMemento can be
     * writen).
     * </p>
     * 
     * @param style the style object to persisit.
     * @param memento Momento used to store the style object state.
     */
    public abstract void save( IMemento memento, Object value );

    /**
     * Loads a style object from a memento.
     * <p>
     * (Currently used with XMLMemento to persist StyleEntry, it is hoped that an EMFMemento can be
     * writen).
     * </p>
     * 
     * @param memento object which contains previously saved object state.
     * @return Loaded object and state.
     */
    public abstract Object load( IMemento memento );

    /**
     * Loads a style object from a URL. This method is blocking.
     * 
     * @param url the URL pointing to the style's location
     * @param monitor Progress monitor to report back to caller, allowed to be null.
     * @return a load style object, or null if it could not be loaded
     * @throws IOException if there is an error loading the URL
     */
    public abstract Object load( URL url, IProgressMonitor monitor ) throws IOException;

    /**
     * Creates a default Style give a resource and color.
     * 
     * @param resource to attempt to create a style for.
     * @param colour color to use while creating style.
     * @param monitor monitor used to show progress of style creation.
     * @return a "default" style or null if the style does not apply to the resource
     * @throws IOException if a problem occurs accessing the GeoResource.
     */
    public abstract Object createDefaultStyle( IGeoResource resource, Color colour,
            IProgressMonitor monitor ) throws IOException;

}