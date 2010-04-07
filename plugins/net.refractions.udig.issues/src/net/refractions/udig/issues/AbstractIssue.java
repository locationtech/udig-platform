/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.issues;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

import net.refractions.udig.core.enums.Priority;
import net.refractions.udig.core.enums.Resolution;
import net.refractions.udig.issues.listeners.IIssueListener;
import net.refractions.udig.issues.listeners.IssueChangeType;
import net.refractions.udig.issues.listeners.IssueEvent;
import net.refractions.udig.issues.listeners.IssuePropertyChangeEvent;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IMemento;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;

/**
 * Implements the non-required methods of IIssue.
 * 
 * @author jones
 * @since 1.0.0
 */
public abstract class AbstractIssue implements IIssue {

    private Resolution resolution=Resolution.UNRESOLVED;
    private String description;
    private Priority priority=Priority.WARNING;
    private ReferencedEnvelope bounds=new ReferencedEnvelope(-180,180,-90,90,DefaultGeographicCRS.WGS84);
    private String groupId="default";  //$NON-NLS-1$
    private String id;
    private IMemento viewMemento;
    private Collection<IIssueListener> listeners=new CopyOnWriteArraySet<IIssueListener>();
    private Map<String, String> properties=new HashMap<String, String>(); 
    

    public String getViewPartId() {
        return null;
    }

    public void getViewMemento(IMemento memento) {
        if( viewMemento!=null )
            memento.putMemento(viewMemento);
    }
    
    /**
     * Memento is stored and is copied to memento argument in {@link #getViewMemento(IMemento)}
     *
     * @param viewMemento
     */
    protected void setViewMemento( IMemento viewMemento ) {
        this.viewMemento=viewMemento;
    }

    /**
     * Returns null;
     */
    public IEditorInput getEditorInput() {
        return null;
    }

    /**
     * returns null
     */
    public String getEditorID() {
        return null;
    }

    /**
     * Returns null
     */
    public String getPerspectiveID() {
        return null;
    }

    public final Resolution getResolution() {
        if (resolution== null)
                return Resolution.UNRESOLVED;
        return resolution;
    }

    public void setResolution( Resolution newResolution ) {
        Resolution old=resolution;
        resolution = newResolution;
        notifyListeners(IssueChangeType.RESOLUTION, newResolution, old);
    }

    public void setPriority( Priority newPriority ) {
        Priority old=priority;
        priority = newPriority;
        notifyListeners(IssueChangeType.PRIORITY, newPriority, old);
    }

    public final Priority getPriority() {
        if (priority == null)
                return Priority.WARNING;

        return priority;
    }

    public String getDescription() {
        return description;
    }

    public final void setDescription( String description ) {
        String old=this.description;
        this.description = description;
        notifyListeners(IssueChangeType.DESCRIPTION, description, old);
    }
    
    /**
     * Default implementation returns "default".
     */
    public String getGroupId() {
    	return groupId;
    }
    
    /**
     * Default implementation returns an empty array.
     */
    public String[] getPropertyNames() {
    	return properties.keySet().toArray(new String[0]);
    }
    
    /**
     * Default implementation always returns null;
     */
    public Object getProperty(String property) {
    	return properties.get(property);
    }
    
    /**
     * Sets a property
     *
     * @param propertyName name of the property added
     * @param value value of the property.
     */
    protected void setProperty(String propertyName, String value) {
        properties.put(propertyName,value);
    }

    public ReferencedEnvelope getBounds() {
        return bounds;
    }

    protected void setBounds( ReferencedEnvelope bounds ) {
        this.bounds = bounds;
    }

    protected void setGroupId( String groupId ) {
        this.groupId = groupId;
    }

    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

    public void addIssueListener( IIssueListener listener ) {
        listeners.add(listener);
    }

    public void removeIssueListener( IIssueListener listener ) {
        listeners.remove(listener);
    }
    
    protected void notifyListeners(IssueChangeType type, Object newValue, Object oldValue){
        IssueEvent event=new IssueEvent(this, type, newValue, oldValue);
        for( IIssueListener l : listeners ) {
            l.notifyChanged(event);
        }
    }
    
    protected void notifyPropertyListeners(String propertyName, Object newValue, Object oldValue){
        IssuePropertyChangeEvent event=new IssuePropertyChangeEvent(this, propertyName, newValue, oldValue);
        for( IIssueListener l : listeners ) {
            l.notifyPropertyChanged(event);
        }
        
    }
    
    public String toString(){
        return getId();
    }
}
