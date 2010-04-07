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
package net.refractions.udig.project.internal.provider;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.refractions.udig.project.internal.ProjectPackage;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;

class ChildFetcher extends Job {

    private AbstractLazyLoadingItemProvider provider;
    private ChildFetcher() {
        super("Fetcher"); //$NON-NLS-1$
        setSystem(true);
        setPriority(Job.DECORATE);
    }

    public ChildFetcher( AbstractLazyLoadingItemProvider provider ) {
        this();
        this.provider = provider;
        setRule(new ChildFetcherScheduleRule());
        reset();
    }

    protected Object next() {
        Object tmp = next;
        next=null;
        return tmp;
    }
    int childIndex;
    Object next;
    protected boolean hasNext(){
        if( next!=null )
            return true;
        try{
            next = provider.getChild(parent, childIndex);
        }catch(Throwable e){
            ProjectEditPlugin.log("Error obtaining next child...", e); //$NON-NLS-1$
        }
        childIndex++;
        return next!=null;
    }
    private volatile List<Object> childrenInternal; 
    Object parent;
    protected volatile boolean dataReady;
    @Override
    protected IStatus run( IProgressMonitor monitor ) {
        long start=System.currentTimeMillis();
        while( hasNext() ){
            try {
                Object child = next();
                if( !childrenInternal.contains(child)) {
                    childrenInternal.add(childrenInternal.size()-1, child);
                    if (System.currentTimeMillis()-start>1000){
                        start=System.currentTimeMillis();
                        dataReady=true;
                        notifyChanged();
                    }
                }
            } catch (Exception e) {
                return Status.OK_STATUS;
            }
        }
        childrenInternal=null;
        dataReady=true;
        notifyChanged();
        return Status.OK_STATUS;
    }

    protected void notifyChanged() {
        provider.notifyChanged(new ENotificationImpl((InternalEObject) parent,
                Notification.SET, ProjectPackage.PROJECT__ELEMENTS_INTERNAL, null, null));
    }

    public void reset() {
        childIndex=0;
        next=null;
        dataReady=false;
        childrenInternal=new CopyOnWriteArrayList<Object>();
        childrenInternal.add(provider.getLoadingItem());
    }

    public Collection getChildren() {
        if( childrenInternal==null )
            return provider.getConcreteChildren(parent);
        return childrenInternal;
    }

}