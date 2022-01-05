/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IMemento;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.project.internal.ProjectPlugin;

/**
 * Provides a way to persist an arbitrary object placed on the style blackboard.
 *
 * @author Jesse
 * @since 1.0.0
 */
public abstract class StyleContent {
    /**
     * StyleContent to be used as a default; willing to save strings and serializable content.
     * Without class information we cannot do any better :-(
     */
    public static StyleContent DEFAULT = new StyleContent("StyleContent.DEFAULT") { //$NON-NLS-1$
        @Override
        public String toString() {
            return "StyleContent.DEFAULT"; //$NON-NLS-1$
        }

        @Override
        public void save(IMemento memento, Object value) {
            if (value instanceof String) {
                String text = (String) value;
                memento.putTextData(text);
            } else if (value instanceof Serializable) {
                try {
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    ObjectOutputStream store = new ObjectOutputStream(
                            new BufferedOutputStream(bytes));
                    store.writeObject(value);
                    store.close();

                    memento.putString("object", new String(bytes.toByteArray())); //$NON-NLS-1$
                    memento.putString("class", value.getClass().getName()); //$NON-NLS-1$
                } catch (Throwable t) {
                    ProjectPlugin.trace(StyleContent.class, "Unable to persist:" + value, t);
                }
            }
        }

        @Override
        public Object load(URL url, IProgressMonitor monitor) throws IOException {
            return null; // no default can be generated for default !
        }

        @Override
        public Object load(IMemento memento) {
            String text = memento.getTextData();
            if (text != null) {
                return text;
            } else {
                String type = memento.getString("class"); //$NON-NLS-1$
                if (type == null) {
                    return null; // we did not manage to store anything here :-(
                }
                try {
                    text = memento.getString("object"); //$NON-NLS-1$

                    ByteArrayInputStream bytes = new ByteArrayInputStream(text.getBytes());
                    ObjectInputStream restore = new ObjectInputStream(
                            new BufferedInputStream(bytes));
                    Object value = restore.readObject();
                    restore.close();
                    return value;
                } catch (Throwable t) {
                    ProjectPlugin.trace(StyleContent.class,
                            "Unable to restore " + type + ":" + t.getLocalizedMessage(), t); //$NON-NLS-2$
                }
            }
            return null;
        }

        @Override
        public Class<?> getStyleClass() {
            return null; // not available
        }

        @Override
        public Object createDefaultStyle(IGeoResource resource, Color colour,
                IProgressMonitor monitor) throws IOException {
            return null;
        }
    };

    /** <code>XPID</code> field */
    public static final String XPID = "org.locationtech.udig.project.style"; //$NON-NLS-1$

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
     * Construct with ID supplied by subclass. This id must be the same as the id declared by the
     * extension point.
     *
     * @param id
     */
    public StyleContent(String id) {
        if (id == null) {
            throw new NullPointerException(
                    "You MUST supply an ID when creating a Style Content. It is used as the 'key' when storing things on the blackboard");
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
     * written).
     * </p>
     *
     * @param style the style object to persist.
     * @param memento Memento used to store the style object state.
     */
    public abstract void save(IMemento memento, Object value);

    /**
     * Loads a style object from a memento.
     * <p>
     * (Currently used with XMLMemento to persist StyleEntry, it is hoped that an EMFMemento can be
     * written).
     * </p>
     *
     * @param memento object which contains previously saved object state.
     * @return Loaded object and state.
     */
    public abstract Object load(IMemento memento);

    /**
     * Loads a style object from a URL. This method is blocking.
     *
     * @param url the URL pointing to the style's location
     * @param monitor Progress monitor to report back to caller, allowed to be null.
     * @return a load style object, or null if it could not be loaded
     * @throws IOException if there is an error loading the URL
     */
    public abstract Object load(URL url, IProgressMonitor monitor) throws IOException;

    /**
     * Creates a default Style give a resource and color.
     *
     * @param resource to attempt to create a style for.
     * @param colour color to use while creating style.
     * @param monitor monitor used to show progress of style creation.
     * @return a "default" style or null if the style does not apply to the resource
     * @throws IOException if a problem occurs accessing the GeoResource.
     */
    public abstract Object createDefaultStyle(IGeoResource resource, Color colour,
            IProgressMonitor monitor) throws IOException;

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(getClass().getName());
        buf.append("( "); //$NON-NLS-1$
        if (id != null) {
            buf.append(id);
            buf.append(" "); //$NON-NLS-1$
        }
        if (getStyleClass() != null) {
            buf.append(getStyleClass().getName());
            buf.append(" "); //$NON-NLS-1$
        }
        buf.append(")"); //$NON-NLS-1$
        return buf.toString();
    }
}
