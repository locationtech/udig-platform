package net.refractions.udig.project.internal.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.locks.Lock;

import net.refractions.udig.core.internal.ExtensionPointProcessor;
import net.refractions.udig.core.internal.ExtensionPointUtil;
import net.refractions.udig.project.BlackboardEvent;
import net.refractions.udig.project.IBlackboard;
import net.refractions.udig.project.IBlackboardListener;
import net.refractions.udig.project.StyleContent;
import net.refractions.udig.project.internal.ProjectFactory;
import net.refractions.udig.project.internal.ProjectPackage;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.internal.StyleBlackboard;
import net.refractions.udig.project.internal.StyleEntry;
import net.refractions.udig.ui.UDIGDisplaySafeLock;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;

/**
 * The default implementation.
 * 
 * @author Jesse
 * @since 1.0.0
 * @generated
 */
public class StyleBlackboardImpl extends EObjectImpl implements StyleBlackboard {
    /**
     * The cached value of the '{@link #getContent() <em>Content</em>}' containment reference list.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see #getContent()
     * @generated
     * @ordered
     */
    protected EList<StyleEntry> content;

    Lock contentLock = new UDIGDisplaySafeLock();

    /**
     * Map of style id to StyleContent
     * 
     * @uml.property name="id2content"
     * @uml.associationEnd qualifier="key:java.lang.Object
     *                     net.refractions.udig.project.StyleContent"
     * @generated NOT
     */
    protected HashMap<String, StyleContent> id2content = new HashMap<String, StyleContent>();

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    protected StyleBlackboardImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return ProjectPackage.Literals.STYLE_BLACKBOARD;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    public List<StyleEntry> getContent() {
        if (content == null) {
            content = new EObjectContainmentEList<StyleEntry>(StyleEntry.class, this,
                    ProjectPackage.STYLE_BLACKBOARD__CONTENT);
        }
        return content;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public Object get( String styleId ) {
        StyleEntry entry = getEntry(styleId);
        if (entry == null) return null;
        return getObject(entry);
    }

    private StyleEntry getEntry( String styleId ) {
        StyleEntry entry = null;
        contentLock.lock();
        try {
            for( Iterator seItr = getContent().iterator(); seItr.hasNext(); ) {
                StyleEntry se = (StyleEntry) seItr.next();
                if (se.getID().equals(styleId)) {
                    entry = se;
                    break;
                }
            }
        } finally {
            contentLock.unlock();
        }
        return entry;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public Object lookup( Class< ? > theClass ) {
        StyleEntry entry = null;
        contentLock.lock();
        try {
            for( Iterator seItr = getContent().iterator(); seItr.hasNext(); ) {
                StyleEntry se = (StyleEntry) seItr.next();
                try {
                    if (se.getStyleClass() == null) {
                        StyleContent styleContent = getStyleContent(se.getID());
                        if (styleContent != null) {
                            Class< ? > type = styleContent.getStyleClass();
                            if (type == null) {
                                se.getStyle(); // force the load
                                type = styleContent.getStyleClass();
                                if (type == null) {
                                    continue;
                                }
                            }
                            se.setStyleClass(type);
                        } else {
                            // this shoudl no longer happen as we have a DEFAULT
                            continue;
                        }
                    }
                    if (theClass.isAssignableFrom(se.getStyleClass())) {
                        entry = se;
                        break;
                    }
                } catch (Throwable t) {
                    // protect against a StyleEntry/StyleContent taking us down
                    ProjectPlugin.log(
                            "Style " + se.getID() + " not restored:" + t.getLocalizedMessage(), t);
                }

            }
        } finally {
            contentLock.unlock();
        }

        if (entry == null) return null;
        return getObject(entry);
    }

    /**
     * Gets the style object from a StyleEntry. Either from the StyleEntry cache or from the
     * StyleContent associated with the entry.
     * 
     * @generated NOT
     */
    protected Object getObject( StyleEntry styleEntry ) {
        // reload style state if necessary
        if (styleEntry.getStyle() == null) {
            try {
                StyleContent styleContent = getStyleContent(styleEntry.getID());
                String mementoString = styleEntry.getMemento();
                if (mementoString != null) {
                    XMLMemento memento = XMLMemento.createReadRoot(new StringReader(mementoString));
                    Object style = styleContent.load(memento);
                    styleEntry.setStyle(style);
                    if (style != null) {
                        styleEntry.setStyleClass(style.getClass());
                    }
                }
            } catch (WorkbenchException e) {
                ProjectPlugin.getPlugin().log(styleEntry.getID() + ":" + e);
                e.printStackTrace();
            }
        }

        return styleEntry.getStyle();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public boolean contains( String styleId ) {
        return get(styleId) != null;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @SuppressWarnings("unchecked")
    public void put( String styleId, Object style ) {
        Object oldValue = remove(styleId);
        StyleEntry se = ProjectFactory.eINSTANCE.createStyleEntry();

        se.setID(styleId);
        se.setStyle(style);

        content.add(se);

        StyleContent styleContent = getStyleContent(styleId);

        if (styleContent == null) {

            return;
        }
        try {
            // save the state of the style
            XMLMemento memento = XMLMemento.createWriteRoot("styleEntry"); //$NON-NLS-1$
            styleContent.save(memento, style);

            StringWriter writer = new StringWriter();
            memento.save(writer);
            se.setMemento(writer.getBuffer().toString());

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        BlackboardEvent event = new BlackboardEvent(this, styleId, oldValue, style);
        for( IBlackboardListener l : listeners ) {
            try {
                l.blackBoardChanged(event);
            } catch (Exception e) {
                ProjectPlugin.log("", e); //$NON-NLS-1$
            }
        }

    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public void put( URL url, IProgressMonitor monitor ) {
        createStyleEntry(url, monitor);
    }

    /**
     * @generated NOT
     */
    private StyleContent getStyleContent( String styleId ) {
        // look in local cache first
        StyleContent styleContent = id2content.get(styleId);
        if (styleContent == null) {
            loadStyleContent(styleId);
            styleContent = id2content.get(styleId);
        }
        return styleContent;
    }

    /**
     * @generated NOT
     */
    private void loadStyleContent( final String styleId ) {
        id2content.put(styleId, StyleContent.DEFAULT); // default to use of we cannot find a specific one
        ExtensionPointProcessor p = new ExtensionPointProcessor(){
            boolean found = false;
            public void process( IExtension extension, IConfigurationElement element )
                    throws Exception {
                if (!found && element.getAttribute("id").equals(styleId)) { //$NON-NLS-1$
                    found = true;
                    StyleContent styleContent = (StyleContent) element
                            .createExecutableExtension("class");
                    id2content.put(styleId, styleContent);
                }
            }
        };
        ExtensionPointUtil.process(ProjectPlugin.getPlugin(), StyleContent.XPID, p);
    }

    class URLProcessor implements ExtensionPointProcessor {
        boolean found = false;

        Object style = null;

        String id;

        URL url;

        IProgressMonitor monitor;

        public URLProcessor( URL url, IProgressMonitor monitor ) {
            this.url = url;
            this.monitor = monitor;
        }

        /*
         * @see
         * net.refractions.udig.core.internal.ExtensionPointProcessor#process(org.eclipse.core.runtime
         * .IExtension, org.eclipse.core.runtime.IConfigurationElement)
         */
        public void process( IExtension extension, IConfigurationElement element ) throws Exception {
            if (found) return;

            StyleContent styleContent = (StyleContent) element.createExecutableExtension("class"); //$NON-NLS-1$
            style = styleContent.load(url, monitor);
            if (style != null) {
                id = styleContent.getId();
                id2content.put(styleContent.getId(), styleContent);
                found = true;
            }
        }
    }

    /**
     * @generated NOT
     */
    private Object createStyleEntry( URL url, IProgressMonitor monitor ) {
        URLProcessor p = new URLProcessor(url, monitor);
        ExtensionPointUtil.process(ProjectPlugin.getPlugin(), StyleContent.XPID, p);

        if (p.style != null) {
            put(p.id, p.style);
        }

        return p.style;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public Object remove( String styleId ) {
        Object style = null;
        contentLock.lock();
        try {
            for( Iterator seItr = getContent().iterator(); seItr.hasNext(); ) {
                StyleEntry se = (StyleEntry) seItr.next();
                if (se.getID().equals(styleId)) {
                    style = se.getStyle();
                    seItr.remove();
                }
            }
        } finally {
            contentLock.unlock();
        }

        return style;
    }

    /**
     * <!-- begin-user-doc --> TODO: This method does not actually clone the underlying style
     * objects, which it should. <!-- end-user-doc -->
     * 
     * @throws CloneNotSupportedException
     * @generated NOT
     */
    public Object clone() {
        // clone the entire blackboard
        StyleBlackboardImpl clone;
        try {
            clone = (StyleBlackboardImpl) super.clone();
        } catch (CloneNotSupportedException e) {
            clone = (StyleBlackboardImpl) ProjectFactory.eINSTANCE.createStyleBlackboard();
        }

        clone.content = null;

        contentLock.lock();
        try {
            for( Iterator seItr = getContent().iterator(); seItr.hasNext(); ) {
                StyleEntry styleEntry = (StyleEntry) seItr.next();

                StyleEntry styleEntryClone = ProjectFactory.eINSTANCE.createStyleEntry();

                // clone the entry by copying the id + memento over
                final String ID = styleEntry.getID();
                styleEntryClone.setID(ID);
                styleEntryClone.setMemento(styleEntry.getMemento());

                Object style = styleEntry.getStyle();
                if (style instanceof String) {
                    styleEntryClone.setStyle(style); // immutable
                } else if (style instanceof Serializable) {
                    try {
                        Serializable serializable = (Serializable) style;
                        ByteArrayOutputStream save = new ByteArrayOutputStream();
                        ObjectOutputStream out = new ObjectOutputStream(save);
                        out.writeObject(style);
                        out.close();

                        byte data[] = save.toByteArray();

                        ByteArrayInputStream restore = new ByteArrayInputStream(data);
                        ObjectInputStream in = new ObjectInputStream(restore);

                        Object copy = in.readObject();
                        in.close();

                        styleEntryClone.setStyle(copy);
                    } catch (Throwable t) {
                        ProjectPlugin.trace(StyleBlackboardImpl.class, "Unable to copy style " + ID
                                + ":" + style, t);
                    }
                } else {
                    // unable to preserve independence of this style object
                    styleEntryClone.setStyle(style); // warning!
                    ProjectPlugin.trace(StyleBlackboardImpl.class, "Unable to copy style " + ID
                            + ":" + style, null);
                }
                clone.getContent().add(styleEntryClone);
            }
        } finally {
            contentLock.unlock();
        }
        return clone;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove( InternalEObject otherEnd, int featureID,
            NotificationChain msgs ) {
        switch( featureID ) {
        case ProjectPackage.STYLE_BLACKBOARD__CONTENT:
            return ((InternalEList< ? >) getContent()).basicRemove(otherEnd, msgs);
        }
        return super.eInverseRemove(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet( int featureID, boolean resolve, boolean coreType ) {
        switch( featureID ) {
        case ProjectPackage.STYLE_BLACKBOARD__CONTENT:
            return getContent();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public void eSet( int featureID, Object newValue ) {
        switch( featureID ) {
        case ProjectPackage.STYLE_BLACKBOARD__CONTENT:
            getContent().clear();
            getContent().addAll((Collection< ? extends StyleEntry>) newValue);
            return;
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eUnset( int featureID ) {
        switch( featureID ) {
        case ProjectPackage.STYLE_BLACKBOARD__CONTENT:
            getContent().clear();
            return;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean eIsSet( int featureID ) {
        switch( featureID ) {
        case ProjectPackage.STYLE_BLACKBOARD__CONTENT:
            return content != null && !content.isEmpty();
        }
        return super.eIsSet(featureID);
    }

    /**
     * @see net.refractions.udig.project.IBlackboard#getFloat(java.lang.String)
     */
    public Float getFloat( String key ) {
        if (contains(key)) {
            Object value = get(key);
            if (value instanceof Float) {
                return (Float) value;
            }
        }
        return null;
    }

    public Integer getInteger( String key ) {
        if (contains(key)) {
            Object value = get(key);
            if (value instanceof Integer) {
                return (Integer) value;
            }
        }
        return null;
    }

    public String getString( String key ) {
        if (contains(key)) {
            Object value = get(key);
            if (value instanceof String) {
                return (String) value;
            }
        }
        return null;
    }

    public void putFloat( String key, float value ) {
        put(key, value);
    }

    public void putInteger( String key, int value ) {
        put(key, value);
    }

    public void putString( String key, String value ) {
        put(key, value);
    }

    public void clear() {
        if (content == null) {
            return;
        }
        content.clear();

        for( IBlackboardListener l : listeners ) {
            try {
                l.blackBoardCleared(this);
            } catch (Exception e) {
                ProjectPlugin.log("", e); //$NON-NLS-1$
            }
        }
    }

    public void flush() {

    }

    CopyOnWriteArraySet<IBlackboardListener> listeners = new CopyOnWriteArraySet<IBlackboardListener>();
    public boolean addListener( IBlackboardListener listener ) {
        return listeners.add(listener);
    }

    public boolean removeListener( IBlackboardListener listener ) {
        return listeners.remove(listener);
    }

    public void setSelected( String[] ids ) {
        List<String> idList = Arrays.asList(ids);
        contentLock.lock();
        try {
            List<StyleEntry> entries = getContent();
            for( StyleEntry entry : entries ) {
                if (idList.contains(entry.getID()))
                    entry.setSelected(true);
                else
                    entry.setSelected(false);
            }
        } finally {
            contentLock.unlock();
        }

    }

    public boolean isSelected( String styleId ) {
        StyleEntry entry = getEntry(styleId);

        if (entry != null && entry.isSelected()) return true;
        return false;
    }

    public void addAll( IBlackboard blackboard ) {
        Set<String> keySet = blackboard.keySet();

        for( String key : keySet ) {
            put(key, blackboard.get(key));
        }
    }

    public Set<String> keySet() {
        Set<String> keys = new HashSet<String>();
        for( StyleEntry entry : content ) {
            if (entry == null) {
                continue; // huh?
            }
            keys.add(entry.getID());
        }
        return keys;
    }
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("StyleBlackBoardImpl: ");
        buf.append(content.size());
        buf.append(" entries");
        for( StyleEntry entry : content ) {
            buf.append("\n\t");
            buf.append(entry.getID());
            buf.append("=");
            buf.append(entry.getStyle());
        }
        return buf.toString();
    }

} // StyleBlackboardImpl
