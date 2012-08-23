/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package net.refractions.udig.project.internal.impl;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.ui.XMLMemento;

/**
 * A blackboard that saves its state out as an EObject.
 * 
 * @author Jesse
 * @since 1.0.0
 * @generated
 */
public class BlackboardImpl extends EObjectImpl implements Blackboard {

    /**
     * underlying container *
     * 
     * @uml.property name="blackboard"
     * @uml.associationEnd qualifier="key:java.lang.Object
     *                     net.refractions.udig.project.internal.impl.BlackboardEntryImpl"
     */
    HashMap<String, BlackboardEntry> blackboard = new HashMap<String, BlackboardEntry>();

    /** persisters */
    ArrayList<IPersister< ? >> persisters;

    /** providers * */
    ArrayList<IProvider<Object>> providers;

    boolean initialized = false;

    /**
     * The cached value of the '{@link #getEntries() <em>Entries</em>}' containment reference list.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see #getEntries()
     * @generated
     * @ordered
     */
    protected EList<BlackboardEntry> entries;

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
    @Override
    protected EClass eStaticClass() {
        return ProjectPackage.Literals.BLACKBOARD;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    public List<BlackboardEntry> getEntries() {
        if (entries == null) {
            entries = new EObjectContainmentEList<BlackboardEntry>(BlackboardEntry.class, this,
                    ProjectPackage.BLACKBOARD__ENTRIES);
        }
        return entries;
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
        case ProjectPackage.BLACKBOARD__ENTRIES:
            return ((InternalEList< ? >) getEntries()).basicRemove(otherEnd, msgs);
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
        case ProjectPackage.BLACKBOARD__ENTRIES:
            return getEntries();
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
        case ProjectPackage.BLACKBOARD__ENTRIES:
            getEntries().clear();
            getEntries().addAll((Collection< ? extends BlackboardEntry>) newValue);
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
        case ProjectPackage.BLACKBOARD__ENTRIES:
            getEntries().clear();
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
        case ProjectPackage.BLACKBOARD__ENTRIES:
            return entries != null && !entries.isEmpty();
        }
        return super.eIsSet(featureID);
    }

    /*
     * (non-Javadoc)
     * @see net.refractions.udig.project.IBlackboard#contains(java.lang.String)
     */
    public boolean contains( String key ) {
        return get(key) != null;
    }

    /*
     * (non-Javadoc)
     * @see net.refractions.udig.project.IBlackboard#get(java.lang.String)
     */
    @SuppressWarnings("unchecked")//$NON-NLS-1$
    public Object get( String key ) {
        if (!initialized) {
            initialize();
        }
        if (key == null) return null;

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
                    if (memento2 == null || memento2.length() == 0) {
                        return null;
                    }
                    XMLMemento memento = XMLMemento.createReadRoot(new StringReader(memento2));
                    IPersister<Object> persister = (IPersister<Object>) findPersister(entry,
                            memento);
                    if (persister != null) {
                        object = persister.load(memento);
                        entry.setObject(object);
                        entry.setObjectClass(object.getClass());
                    } else {
                        // real object which cannot be saved between runs
                    }
                } catch (Exception e) {
                    String msg = "Error loading content: " + entry.getObjectClass(); //$NON-NLS-1$
                    IStatus status = new Status(IStatus.WARNING, ProjectPlugin.ID, 0, msg, e); //$NON-NLS-1$
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
        initialized = true;
        for( BlackboardEntry entry : this.getEntries() ) {
            blackboard.put(entry.getKey(), entry);
        }
    }

    public Object remove( String key ) {
        if (key == null) return null;

        // look up the entry
        BlackboardEntry entry = blackboard.remove(key);
        if (entry == null) {
            return null;
        }
        Object oldValue = entry.getObject();
        entry.setMemento(null);
        entry.setObject(null);

        BlackboardEvent event = new BlackboardEvent(this, key, oldValue, null);
        for( IBlackboardListener l : listeners ) {
            try {
                l.blackBoardChanged(event);
            } catch (Exception e) {
                ProjectPlugin.log("", e); //$NON-NLS-1$
            }
        }
        return oldValue;
    }
    /*
     * (non-Javadoc)
     * @see net.refractions.udig.project.IBlackboard#put(java.lang.String, java.lang.Object)
     */
    public void put( String key, Object value ) {
        if (key == null) {
            return; // stop you fool!
        }
        if (value == null) {
            this.remove(key);
            return;
        }
        Object oldValue = null;
        BlackboardEntry entry = blackboard.get(key);

        if (entry == null) {
            entry = createEntry(key, value);
        } else {
            oldValue = entry.getObject();
        }
        // set the cache
        entry.setObject(value);

        // find the persister to save the state
        @SuppressWarnings("unchecked")
        IPersister<Object> persister = (IPersister<Object>) findPersister(entry, null);
        try {
            if (persister != null) {
                XMLMemento memento = XMLMemento.createWriteRoot("blackboardContent"); //$NON-NLS-1$
                persister.save(value, memento);
                memento.putString("internalObjectClassStorage", entry.getObjectClass().getName()); //$NON-NLS-1$

                StringWriter writer = new StringWriter();
                memento.save(writer);
                entry.setMemento(writer.getBuffer().toString());
            } else {
                // this is a "real" object that cannot be shared between runs
            }
        } catch (Exception e) {
            String msg = "Error persisting content: " + value.getClass(); //$NON-NLS-1$
            if (persister != null) {
                IExtension ext = persister.getExtension();
                IStatus status = new Status(IStatus.WARNING, ext.getNamespaceIdentifier(), 0, msg,
                        e);
                ProjectPlugin.getPlugin().getLog().log(status);
            } else {
                ProjectPlugin.log("error loading persister", e); //$NON-NLS-1$
            }
        }
        BlackboardEvent event = new BlackboardEvent(this, key, oldValue, value);
        for( IBlackboardListener l : listeners ) {
            try {
                l.blackBoardChanged(event);
            } catch (Exception e) {
                ProjectPlugin.log("", e); //$NON-NLS-1$
            }
        }
    }

    /*
     * (non-Javadoc)
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
     * @see net.refractions.udig.project.IBlackboard#putFloat(java.lang.String, float)
     */
    public void putFloat( String key, float value ) {
        put(key, value);
    }

    /*
     * (non-Javadoc)
     * @see net.refractions.udig.project.IBlackboard#putInteger(java.lang.String, int)
     */
    public void putInteger( String key, int value ) {
        put(key, value);

    }

    /*
     * (non-Javadoc)
     * @see net.refractions.udig.project.IBlackboard#putString(java.lang.String, java.lang.String)
     */
    public void putString( String key, String value ) {
        put(key, value);
    }

    /*
     * (non-Javadoc)
     * @see net.refractions.udig.project.IBlackboard#clear()
     */
    public void clear() {
        blackboard.clear();
        for( IBlackboardListener l : listeners ) {
            l.blackBoardCleared(this);
        }
    }

    CopyOnWriteArraySet<IBlackboardListener> listeners = new CopyOnWriteArraySet<IBlackboardListener>();
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
            if (provider.getKey() != null && provider.getKey().equals(key)) return provider;
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private BlackboardEntryImpl createEntry( String key, Object object ) {

        BlackboardEntryImpl entry = new BlackboardEntryImpl();

        entry.setKey(key);
        entry.setObjectClass(object != null ? object.getClass() : null);
        blackboard.put(key, entry);

        // add to entries for persistance
        getEntries().add(entry);

        return entry;
    }

    @SuppressWarnings("unchecked")
    private IPersister< ? > findPersister( BlackboardEntry entry, XMLMemento memento ) {
        if (persisters == null) {
            synchronized (this) {
                if (persisters == null) {
                    persisters = new ArrayList<IPersister< ? >>();
                    PersisterProcessor p = new PersisterProcessor(persisters);
                    ExtensionPointUtil.process(ProjectPlugin.getPlugin(), IPersister.XPID, p);
                }
            }
        }

        // look for a class closest down in the class hierarchy
        ArrayList<IPersister< ? >> possible = new ArrayList<IPersister< ? >>();

        for( IPersister< ? > persister : persisters ) {
            Class< ? > persistenceTarget = persister.getPersistee();
            if (persistenceTarget == null) {
                continue; // this persister does not seem to be set up correctly
            }
            if (entry.getObjectClass() != null) {
                Class< ? > type = entry.getObjectClass();
                if (persistenceTarget.isAssignableFrom(type)) {
                    possible.add(persister);
                    continue;
                }
            }
            Class objectClass = entry.getObjectClass();
            if (objectClass == null && memento != null) {
                String className = memento.getString("internalObjectClassStorage");
                if (className.equals(persistenceTarget)) {
                    possible.add(persister);
                    continue; // we are good on this one
                } else {
                    try {
                        ClassLoader classLoader = persistenceTarget.getClassLoader();
                        if (classLoader != null) {
                            objectClass = classLoader.loadClass(className);
                        } else {
                            objectClass = Class.forName(className);
                        }
                        if (objectClass != null && objectClass.isAssignableFrom(persistenceTarget)) {
                            possible.add(persister);
                        }
                    } catch (Exception e) {
                        if (ProjectPlugin.isDebugging("blackboard")) {
                            ProjectPlugin.trace(BlackboardImpl.class, persister.getExtension()
                                    .getExtensionPointUniqueIdentifier()
                                    + "unable to load " + className, e); //$NON-NLS-1$
                        }
                        continue; // skip this one
                    }
                }
            }
        }
        if (possible.isEmpty()) {
            ProjectPlugin
                    .trace(BlackboardImpl.class, entry.getKey() + " cannot be persisted", null); //$NON-NLS-1$
            return null;
        }

        Collections.sort(possible, new Comparator<IPersister< ? >>(){
            public int compare( IPersister< ? > p1, IPersister< ? > p2 ) {
                if (p1.getPersistee().equals(p2.getPersistee())) {
                    return 0;
                }
                if (p1.getPersistee().isAssignableFrom(p2.getPersistee())) {
                    return -1;
                }
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

        List<IPersister< ? >> persisters;

        PersisterProcessor( List<IPersister< ? >> persisters ) {
            this.persisters = persisters;
        }

        public void process( IExtension extension, IConfigurationElement element ) throws Exception {

            try {
                IPersister< ? > persister = (IPersister< ? >) element
                        .createExecutableExtension("class"); //$NON-NLS-1$

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

    @SuppressWarnings("nls")
    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("StyleBlackBoardImpl: ");
        buf.append(blackboard.size());
        buf.append(" entries");
        for( Map.Entry entry : blackboard.entrySet() ) {
            buf.append("\n\t");
            buf.append(entry.getKey());
            buf.append("=");
            buf.append(entry.getValue());
        }
        return buf.toString();
    }
} // BlackboardImpl
