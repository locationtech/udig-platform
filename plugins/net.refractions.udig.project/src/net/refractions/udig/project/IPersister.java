package net.refractions.udig.project;

import org.eclipse.core.runtime.IExtension;
import org.eclipse.ui.IMemento;

/**
 * Allows blackboard to persist objects on the blackboard.
 * 
 * @author Jesse
 * @since 1.0.0
 */
public abstract class IPersister<T> {

    /** Extension point id. * */
    public static final String XPID = "net.refractions.udig.project.persister"; //$NON-NLS-1$

    /** the extension configuration element * */
    IExtension extension;

    /**
     * Sets the extension that persister originated from. This method should not be called by client
     * code.
     * 
     * @param extension The extension in which the persister was instantiated.
     * @uml.property name="extension"
     */
    public void setExtension( IExtension extension ) {
        this.extension = extension;
    }

    /**
     * @return the extension the persister originated from.
     * @uml.property name="extension"
     */
    public IExtension getExtension() {
        return extension;
    }

    /**
     * Returns the class of the object being persisted. How this class relates to the class of
     * objects being persisted (via inheritance) is up to the client of the persister.
     * 
     * @return The type of the object being persisted (the persistee).
     */
    public abstract Class<T> getPersistee();

    /**
     * Loads an object from a memento containing the objects internaObjectl state.
     * 
     * @param memento A memento.
     * @return The object with state restored.
     */
    public abstract T load( IMemento memento );

    /**
     * Saves the internal state of an object instance to a memento.
     * 
     * @param object The object being persisted (the persistee).
     * @param memento The memento in which to save object state.
     */
    public abstract void save( T object, IMemento memento );
}