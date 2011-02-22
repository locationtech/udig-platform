/**
 * <copyright>
 * </copyright>
 *
 * $Id: BlackboardImpl.java 27774 2007-11-07 03:31:55Z jeichar $
 */
package net.refractions.udig.project.internal.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import net.refractions.udig.core.internal.ExtensionPointProcessor;
import net.refractions.udig.core.internal.ExtensionPointUtil;
import net.refractions.udig.project.BlackboardEvent;
import net.refractions.udig.project.IBlackboard;
import net.refractions.udig.project.IBlackboardListener;
import net.refractions.udig.project.IPersister;
import net.refractions.udig.project.IProvider;
import net.refractions.udig.project.internal.Blackboard;
import net.refractions.udig.project.internal.BlackboardEntry;
import net.refractions.udig.project.internal.ProjectPackage;
import net.refractions.udig.project.internal.ProjectPlugin;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.ui.XMLMemento;

/**
 *
 * @author Jesse
 * @since 1.0.0
 * @generated
 */
public class BlackboardImpl extends EObjectImpl implements Blackboard {

    // MutablePicoContainer picoContainer = new DefaultPicoContainer();
    /**
     * underlying container *
     *
     * @uml.property name="blackboard"
     * @uml.associationEnd qualifier="key:java.lang.Object
     *                     net.refractions.udig.project.internal.impl.BlackboardEntryImpl"
     */
    HashMap<String, BlackboardEntry> blackboard = new HashMap<String, BlackboardEntry>();

    /** persisters * */
    ArrayList<IPersister<Object>> persisters;
    /** providers * */
    ArrayList<IProvider<Object>> providers;

    boolean initialized=false;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details."; //$NON-NLS-1$

    /**
     * The cached value of the '{@link #getEntries() <em>Entries</em>}' containment reference list.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see #getEntries()
     * @generated
     * @ordered
     */
    protected EList entries = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    protected BlackboardImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    protected EClass eStaticClass() {
        return ProjectPackage.eINSTANCE.getBlackboard();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    public List<BlackboardEntry> getEntries() {
        if (entries == null) {
            entries = new EObjectContainmentEList(BlackboardEntry.class, this,
                    ProjectPackage.BLACKBOARD__ENTRIES);
        }
        return entries;
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
            case ProjectPackage.BLACKBOARD__ENTRIES:
                return ((InternalEList) getEntries()).basicRemove(otherEnd, msgs);
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
        case ProjectPackage.BLACKBOARD__ENTRIES:
            return getEntries();
        }
        return eDynamicGet(eFeature, resolve);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")//$NON-NLS-1$
    public void eSet( EStructuralFeature eFeature, Object newValue ) {
        switch( eDerivedStructuralFeatureID(eFeature) ) {
        case ProjectPackage.BLACKBOARD__ENTRIES:
            getEntries().clear();
            getEntries().addAll((Collection) newValue);
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
        case ProjectPackage.BLACKBOARD__ENTRIES:
            getEntries().clear();
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
        case ProjectPackage.BLACKBOARD__ENTRIES:
            return entries != null && !entries.isEmpty();
        }
        return eDynamicIsSet(eFeature);
    }

    /*
     * (non-Javadoc)
     *
     * @see net.refractions.udig.project.IBlackboard#contains(java.lang.String)
     */
    public boolean contains( String key ) {
        return get(key) != null;
    }

    /*
     * (non-Javadoc)
     *
     * @see net.refractions.udig.project.IBlackboard#get(java.lang.String)
     */
    @SuppressWarnings("unchecked")//$NON-NLS-1$
    public Object get( String key ) {
        if( !initialized ){
            initialize();
        }
        if (key == null)
            return null;

        // look up the entry
        BlackboardEntry entry = blackboard.get(key);
        if (entry != null) {

            if (entry.getObject() != null) {
                return entry.getObject();
            } else {
                Object object = null;
                try {
                    // have to load from memento
                    String memento2 = entry.getMemento();
                    if( memento2==null || memento2.length()==0 )
                        return null;
                    XMLMemento memento = XMLMemento.createReadRoot(new StringReader(memento2));
                    IPersister persister = findPersister(entry, memento);
                    if (persister != null) {
                        object = persister.load(memento);
                        entry.setObject(object);
                        entry.setObjectClass(object.getClass());
                    } else {
                        // try serializability
                        if (entry.getObjectClass() != null
                                && entry.getObjectClass().isAssignableFrom(Serializable.class)) {
                            ByteArrayInputStream bin = new ByteArrayInputStream(memento2
                                    .getBytes());
                            ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(
                                    bin));
                            object = in.readObject();
                            in.close();

                            entry.setObject(object);
                            entry.setObjectClass(object.getClass());
                        } else {
                            // either object not serializable, or type can not
                            // derminated
                        }

                    }
                } catch (Exception e) {
                    String msg = "Error loading content: " + entry.getObjectClass(); //$NON-NLS-1$
                    IStatus status = new Status(IStatus.WARNING, "", 0, msg, e); //$NON-NLS-1$
                    ProjectPlugin.getPlugin().getLog().log(status);
                }

                return object;
            }
        } else {
            // object does not exists, try to find a provider
            IProvider provider = findProvider(key);
            if (provider != null) {
                try {
                    Object object = provider.provide();
                    if (object != null) {
                        createEntry(key, object);
                        return object;
                    }
                } catch (Exception e) {
                    String msg = "provider exception :" + key; //$NON-NLS-1$
                    String id = provider.getExtension().getNamespace();
                    IStatus status = new Status(IStatus.WARNING, id, 0, msg, e);

                    ProjectPlugin.getPlugin().getLog().log(status);
                }
            }
        }

        return null;
    }

    private void initialize() {
        initialized=true;
        for( BlackboardEntry entry : this.getEntries() ) {
            blackboard.put(entry.getKey(), entry);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see net.refractions.udig.project.IBlackboard#put(java.lang.String, java.lang.Object)
     */
    public void put( String key, Object value ) {
        if (key == null )
            return;

        // look up the entry
        BlackboardEntry entry = blackboard.get(key);
        if (entry == null) {
            entry = createEntry(key, value);
        }

        Object oldValue=entry.getObject();

        // set the cache
        entry.setObject(value);

        // find the persister to save the state
        IPersister<Object> persister = findPersister(entry, null );
        try {
            if (persister != null) {
                XMLMemento memento = XMLMemento.createWriteRoot("blackboardContent"); //$NON-NLS-1$
                persister.save(value, memento);
                memento.putString("internalObjectClassStorage", entry.getObjectClass().getName()); //$NON-NLS-1$

                StringWriter writer = new StringWriter();
                memento.save(writer);
                entry.setMemento(writer.getBuffer().toString());
            } else {
                // no persister, try using serializability
                if (value instanceof Serializable) {
                    ByteArrayOutputStream bout = new ByteArrayOutputStream();

                    ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(bout));
                    out.writeObject(value);

                    entry.setMemento(new String(bout.toByteArray()));
                    out.close();
                }
            }
        } catch (Exception e) {
            String msg = "Error persisting content: " + value.getClass(); //$NON-NLS-1$
            if( persister!=null ){
                IExtension ext = persister.getExtension();
                IStatus status = new Status(IStatus.WARNING, ext.getNamespaceIdentifier(), 0, msg, e);
                ProjectPlugin.getPlugin().getLog().log(status);
            }else{
                ProjectPlugin.log("error loading persister", e); //$NON-NLS-1$
            }
        }
        BlackboardEvent event=new BlackboardEvent(this, key, oldValue, value);
        for( IBlackboardListener l : listeners ) {
            try{
                l.blackBoardChanged(event);
            } catch (Exception e) {
                ProjectPlugin.log("", e); //$NON-NLS-1$
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see net.refractions.udig.project.IBlackboard#getFloat(java.lang.String)
     */
    public Float getFloat( String key ) {
        Object o = get(key);
        if (o != null && o instanceof Float) {
            return (Float) o;
        }

        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see net.refractions.udig.project.IBlackboard#getInteger(java.lang.String)
     */
    public Integer getInteger( String key ) {
        Object o = get(key);
        if (o != null && o instanceof Integer) {
            return (Integer) o;
        }

        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see net.refractions.udig.project.IBlackboard#getString(java.lang.String)
     */
    public String getString( String key ) {
        Object o = get(key);
        if (o != null && o instanceof String) {
            return (String) o;
        }

        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see net.refractions.udig.project.IBlackboard#putFloat(java.lang.String, float)
     */
    public void putFloat( String key, float value ) {
        put(key, value);
    }

    /*
     * (non-Javadoc)
     *
     * @see net.refractions.udig.project.IBlackboard#putInteger(java.lang.String, int)
     */
    public void putInteger( String key, int value ) {
        put(key, value);

    }

    /*
     * (non-Javadoc)
     *
     * @see net.refractions.udig.project.IBlackboard#putString(java.lang.String, java.lang.String)
     */
    public void putString( String key, String value ) {
        put(key, value);
    }

    /*
     * (non-Javadoc)
     *
     * @see net.refractions.udig.project.IBlackboard#clear()
     */
    public void clear() {
        blackboard.clear();
        for( IBlackboardListener l : listeners ) {
            l.blackBoardCleared(this);
        }
    }

    CopyOnWriteArraySet<IBlackboardListener> listeners=new CopyOnWriteArraySet<IBlackboardListener>();
    public boolean addListener( IBlackboardListener listener ) {
        return listeners.add(listener);
    }


    public boolean removeListener( IBlackboardListener listener ) {
        return listeners.remove(listener);
    }

    /**
     * Flushes all the cached objects. Useful for testing.
     */
    public void flush() {
        for( BlackboardEntry entry : blackboard.values() ) {
            entry.setObject(null);
        }
    }

    private IProvider findProvider( String key ) {
        if (providers == null) {
            providers = new ArrayList<IProvider<Object>>();
            ProviderProcessor p = new ProviderProcessor(providers);
            ExtensionPointUtil.process(ProjectPlugin.getPlugin(), IProvider.XPID, p);
        }

        // search for provider matching key
        for( IProvider<Object> provider : providers ) {
            if (provider.getKey() != null && provider.getKey().equals(key))
                return provider;
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private BlackboardEntryImpl createEntry( String key, Object object ) {

        BlackboardEntryImpl entry = new BlackboardEntryImpl();

        entry.setKey(key);
        entry.setObjectClass(object!=null?object.getClass():null);
        blackboard.put(key, entry);

        // add to entries for persistance
        getEntries().add(entry);

        return entry;
    }

    @SuppressWarnings("unchecked")
    private IPersister<Object> findPersister( BlackboardEntry entry, XMLMemento memento ) {

        if(persisters == null) {
            synchronized(this){
                if (persisters == null) {
                    persisters = new ArrayList<IPersister<Object>>();
                    PersisterProcessor p = new PersisterProcessor(persisters);
                    ExtensionPointUtil.process(ProjectPlugin.getPlugin(), IPersister.XPID, p);
                }
            }
        }

        // look for a class closest down in the class hierarchy
        ArrayList<IPersister<Object>> possible = new ArrayList<IPersister<Object>>();

        for( IPersister<Object> persister : persisters ) {
            Class persistee = persister.getPersistee();
            if (persistee == null)
                continue;

            Class objectClass = entry.getObjectClass();
            if( objectClass==null && memento!=null ){
                try{
                    objectClass=persister.getClass().getClassLoader().loadClass(memento.getString("internalObjectClassStorage")); //$NON-NLS-1$
                }catch (Exception e) {
                    ProjectPlugin.log("", e); //$NON-NLS-1$
                    continue;
                }
            }
            if (objectClass!=null && objectClass.isAssignableFrom(persistee)) {
                possible.add(persister);
            }
        }
        if (possible.isEmpty())
            return null;

        Collections.sort(possible, new Comparator<IPersister<Object>>(){
            public int compare( IPersister<Object> p1, IPersister<Object> p2 ) {
                if (p1.getPersistee().equals(p2.getPersistee()))
                    return 0;
                if (p1.getPersistee().isAssignableFrom(p2.getPersistee()))
                    return -1;

                return 1;
            }
        });

        return possible.get(0);
    }

    static class ProviderProcessor implements ExtensionPointProcessor {

        List<IProvider<Object>> providers;

        ProviderProcessor( List<IProvider<Object>> providers ) {
            this.providers = providers;
        }

        @SuppressWarnings("unchecked")
        public void process( IExtension extension, IConfigurationElement element ) throws Exception {
            try {
                IProvider<Object> provider = (IProvider<Object>) element
                        .createExecutableExtension("class"); //$NON-NLS-1$

                if (provider != null) {
                    providers.add(provider);
                    provider.setExtension(extension);
                    if (element.getAttribute("key") != null) { //$NON-NLS-1$
                        provider.setKey(element.getAttribute("key")); //$NON-NLS-1$
                    }
                }
            } catch (Throwable t) {
                ProjectPlugin.log(t.getLocalizedMessage(), t);
            }
        }
    }

    static class PersisterProcessor implements ExtensionPointProcessor {

        List persisters;

        PersisterProcessor( List persisters ) {
            this.persisters = persisters;
        }

        @SuppressWarnings("unchecked")
        public void process( IExtension extension, IConfigurationElement element ) throws Exception {

            try {
                IPersister persister = (IPersister) element.createExecutableExtension("class"); //$NON-NLS-1$

                if (persister != null) {
                    persister.setExtension(extension);
                    persisters.add(persister);
                }
            } catch (Throwable t) {
                ProjectPlugin.log(t.getLocalizedMessage(), t);
            }

        }
    }

    public void addAll( IBlackboard blackboard ) {
        Set<String> keySet = blackboard.keySet();
        for( String key : keySet ) {
            put(key, blackboard.get(key));
        }
    }

    public Set<String> keySet() {
        return this.blackboard.keySet();
    }
} // BlackboardImpl
