package net.refractions.udig.project.internal.impl;

import java.io.IOException;
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
import net.refractions.udig.ui.graphics.SLDs;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;
import org.geotools.styling.Style;
import org.geotools.styling.StyledLayerDescriptor;
import org.geotools.styling.StyledLayerDescriptorImpl;
import org.geotools.styling.UserLayer;
import org.geotools.styling.UserLayerImpl;

/**
 * The default implementation.
 *
 * @author Jesse
 * @since 1.0.0
 * @generated
 */
public class StyleBlackboardImpl extends EObjectImpl implements StyleBlackboard {
    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details."; //$NON-NLS-1$

    /**
     * The cached value of the '{@link #getContent() <em>Content</em>}' containment reference list.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see #getContent()
     * @generated
     * @ordered
     */
    protected EList content = null;

    Lock contentLock=new UDIGDisplaySafeLock();

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
    protected EClass eStaticClass() {
        return ProjectPackage.eINSTANCE.getStyleBlackboard();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    public List<StyleEntry> getContent() {
        if (content == null) {
            content = new EObjectContainmentEList(StyleEntry.class, this,
                    ProjectPackage.STYLE_BLACKBOARD__CONTENT){

                /** long serialVersionUID field */
                        private static final long serialVersionUID = 1L;

                @Override
                protected Object assign( int index, Object object ) {
                    contentLock.lock();
                    try{
                        return super.assign(index, object);
                    }finally{
                        contentLock.unlock();
                    }
                }

                @Override
                protected void doClear() {
                    contentLock.lock();
                    try{
                        super.doClear();
                    }finally{
                        contentLock.unlock();
                    }
                }

                @Override
                protected Object doRemove( int index ) {
                    contentLock.lock();
                    try{
                        return super.doRemove(index);
                    }finally{
                        contentLock.unlock();
                    }
                }

            };
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
        if (entry == null)
            return null;
        return getObject(entry);
    }

    private StyleEntry getEntry( String styleId ) {
        StyleEntry entry = null;
        contentLock.lock();
        try{
        for( Iterator seItr = getContent().iterator(); seItr.hasNext(); ) {
            StyleEntry se = (StyleEntry) seItr.next();
            if (se.getID().equals(styleId)) {
                entry = se;
                break;
            }
        }
        }finally{
            contentLock.unlock();
        }
        return entry;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    public Object lookup( Class<?> theClass ) {
        StyleEntry entry = null;
        contentLock.lock();
        try{
            for( Iterator seItr = getContent().iterator(); seItr.hasNext(); ) {
                StyleEntry se = (StyleEntry) seItr.next();
                if (se.getStyleClass() == null) {
                    StyleContent styleContent = getStyleContent(se.getID());
                    if( styleContent!=null )
                    	se.setStyleClass(styleContent.getStyleClass());
                    else{
                    	continue;
                    }
                }
                if (theClass.isAssignableFrom(se.getStyleClass())) {
                    entry = se;
                    break;
                }
            }
        }finally{
            contentLock.unlock();
        }

        if (entry == null)
            return null;
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
                String mementoString = styleEntry
                        .getMemento();
                if( mementoString!=null ){
                    XMLMemento memento = XMLMemento.createReadRoot(new StringReader(mementoString));
                    Object style = styleContent.load(memento);
                    styleEntry.setStyle(style);
                }
            } catch (WorkbenchException e) {
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

        //Validate (add an SLD to the style if one does not exist)
        //TODO: move to extension point
        if (style instanceof Style) {
            Style theStyle = (Style) style;
            Object SLD = SLDs.styledLayerDescriptor(theStyle);
            if (SLD == null) {
                StyledLayerDescriptor sld = new StyledLayerDescriptorImpl();
                UserLayer layer = new UserLayerImpl();
                layer.getNote().setParent(sld);
                theStyle.getNote().setParent(layer);
            }
        }

        StyleEntry se = ProjectFactory.eINSTANCE.createStyleEntry();

        se.setID(styleId);
        se.setStyle(style);

        content.add(se);

        StyleContent styleContent = getStyleContent(styleId);

        if( styleContent==null )
            return;
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

        BlackboardEvent event=new BlackboardEvent(this, styleId, oldValue, style);
        for( IBlackboardListener l : listeners ) {
            try{
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

        ExtensionPointProcessor p = new ExtensionPointProcessor(){
            boolean found = false;

            public void process( IExtension extension, IConfigurationElement element )
                    throws Exception {
                if (!found && element.getAttribute("id").equals(styleId)) { //$NON-NLS-1$
                    found = true;
                    id2content.put(styleId, (StyleContent) element
                            .createExecutableExtension("class") //$NON-NLS-1$
                            );
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
         * @see net.refractions.udig.core.internal.ExtensionPointProcessor#process(org.eclipse.core.runtime.IExtension,
         *      org.eclipse.core.runtime.IConfigurationElement)
         */
        public void process( IExtension extension, IConfigurationElement element ) throws Exception {
            if (found)
                return;

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
        try{
        for( Iterator seItr = getContent().iterator(); seItr.hasNext(); ) {
            StyleEntry se = (StyleEntry) seItr.next();
            if (se.getID().equals(styleId)) {
                style = se.getStyle();
                seItr.remove();
            }
        }
        }finally{
            contentLock.unlock();
        }

        return style;
    }

    /**
     * <!-- begin-user-doc --> TODO: This method does not actually clone the underlying style
     * objects, which it should. <!-- end-user-doc -->
     * @throws CloneNotSupportedException
     *
     * @generated NOT
     */
    public  Object clone() {
        // clone the entire blackboard
        StyleBlackboardImpl clone;
        try {
            clone = (StyleBlackboardImpl) super.clone();
        } catch (CloneNotSupportedException e) {
            clone=(StyleBlackboardImpl) ProjectFactory.eINSTANCE.createStyleBlackboard();
        }

        clone.content=null;

        contentLock.lock();
        try{
            for( Iterator seItr = getContent().iterator(); seItr.hasNext(); ) {
                StyleEntry styleEntry = (StyleEntry) seItr.next();

                StyleEntry styleEntryClone = ProjectFactory.eINSTANCE.createStyleEntry();

                // clone the entry by copying the id + memento over
                styleEntryClone.setID(styleEntry.getID());
                styleEntryClone.setMemento(styleEntry.getMemento());
                clone.getContent().add(styleEntryClone);
            }
        }finally{
            contentLock.unlock();
        }
        return clone;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    public NotificationChain eInverseRemove( InternalEObject otherEnd, int featureID,
            Class baseClass, NotificationChain msgs ) {
        if (featureID >= 0) {
            switch( eDerivedStructuralFeatureID(featureID, baseClass) ) {
            case ProjectPackage.STYLE_BLACKBOARD__CONTENT:
                return ((InternalEList) getContent()).basicRemove(otherEnd, msgs);
            default:
                return eDynamicInverseRemove(otherEnd, featureID, baseClass, msgs);
            }
        }
        return eBasicSetContainer(null, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public Object eGet( EStructuralFeature eFeature, boolean resolve ) {
        switch( eDerivedStructuralFeatureID(eFeature) ) {
        case ProjectPackage.STYLE_BLACKBOARD__CONTENT:
            return getContent();
        }
        return eDynamicGet(eFeature, resolve);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    public void eSet( EStructuralFeature eFeature, Object newValue ) {
        switch( eDerivedStructuralFeatureID(eFeature) ) {
        case ProjectPackage.STYLE_BLACKBOARD__CONTENT:
            getContent().clear();
            getContent().addAll((Collection) newValue);
            return;
        }
        eDynamicSet(eFeature, newValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void eUnset( EStructuralFeature eFeature ) {
        switch( eDerivedStructuralFeatureID(eFeature) ) {
        case ProjectPackage.STYLE_BLACKBOARD__CONTENT:
            getContent().clear();
            return;
        }
        eDynamicUnset(eFeature);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public boolean eIsSet( EStructuralFeature eFeature ) {
        switch( eDerivedStructuralFeatureID(eFeature) ) {
        case ProjectPackage.STYLE_BLACKBOARD__CONTENT:
            return content != null && !content.isEmpty();
        }
        return eDynamicIsSet(eFeature);
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
        if( content==null )
            return;
        content.clear();

        for( IBlackboardListener l : listeners ) {
            try{
                l.blackBoardCleared(this);
            } catch (Exception e) {
                ProjectPlugin.log("", e); //$NON-NLS-1$
            }
        }
    }

    public void flush() {

    }

    CopyOnWriteArraySet<IBlackboardListener> listeners=new CopyOnWriteArraySet<IBlackboardListener>();
    public boolean addListener( IBlackboardListener listener ) {
        return listeners.add(listener);
    }


    public boolean removeListener( IBlackboardListener listener ) {
        return listeners.remove(listener);
    }

    public void setSelected( String[] ids ) {
        List<String> idList=Arrays.asList(ids);
        contentLock.lock();
        try{
            List<StyleEntry> entries = getContent();
            for( StyleEntry entry : entries ) {
                if( idList.contains(entry.getID()))
                    entry.setSelected(true);
                else
                    entry.setSelected(false);
            }
        }finally{
            contentLock.unlock();
        }

    }

    public boolean isSelected( String styleId ) {
        StyleEntry entry = getEntry(styleId);

        if (entry!=null && entry.isSelected())
            return true;
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
        Iterator iterator = content.iterator();
        while(iterator.hasNext()){
            StyleEntry se = (StyleEntry) iterator.next();
            keys.add(se.getID());
        }
        return keys;
    }

} // StyleBlackboardImpl
