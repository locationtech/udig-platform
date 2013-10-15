/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.issues;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import net.refractions.udig.issues.listeners.IIssuesListListener;
import net.refractions.udig.issues.listeners.IssuesListEvent;
import net.refractions.udig.issues.listeners.IssuesListEventType;
import net.refractions.udig.ui.PlatformGIS;
import net.refractions.udig.ui.ProgressManager;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * An implementation that uses a strategy object to communicate with 
 * the storage device.  This object takes care of notification listening
 * and saving.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class StrategizedIssuesList implements IRemoteIssuesList {
    
    private final IssuesList wrapped;
    private IListStrategy dsStrategy;
    private IIssuesListListener listener = new IIssuesListListener(){
        
        public void notifyChange( IssuesListEvent event ) {

            final IssuesListEventType type = event.getType();
            final Collection< ? extends IIssue> changed = event.getChanged();

            switch( type ) {
            case ADD:
                try {
                        dsStrategy.addIssues(new ArrayList<IIssue>(changed));
                    } catch (IOException e) {
                        throw (RuntimeException) new RuntimeException( ).initCause( e );
                    }
                break;
            case REMOVE:
                try {
                        dsStrategy.removeIssues(changed);
                    } catch (IOException e) {
                        throw (RuntimeException) new RuntimeException( ).initCause( e );
                    }
                break;
            default:
                break;
            }
        }
        
    };
    
    public StrategizedIssuesList() {
        wrapped=new IssuesList();
    }
    
    public void init(IListStrategy strategy) throws IOException{
        this.dsStrategy=strategy;
        refresh();
    }
    

    @SuppressWarnings("unchecked")
    public void refresh() throws IOException {
        try {
            PlatformGIS.runBlockingOperation(new IRunnableWithProgress(){

                public void run( IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException {

                    wrapped.removeListener(listener);
                    
                    List<IIssue> issues = new ArrayList<IIssue>();
                    Collection< ? extends IIssue> remoteList;
                    try {
                        remoteList = dsStrategy.getIssues();
                    } catch (IOException e) {
                        throw (RuntimeException) new RuntimeException( ).initCause( e );
                    }
                    for( IIssue issue : remoteList ) {
                        issues.add(issue);
                    }
                    
                    for ( Iterator<IIssue> iter=wrapped.iterator(); iter.hasNext(); ){
                        IIssue issue=iter.next();
                        IIssue newVersion =find(issue, issues);
                        if( newVersion==null ){
                            iter.remove();
                        }else {
                            issues.remove(newVersion);
                        }
                    }
                    wrapped.addAll(issues);
                    
                    wrapped.notify(issues, IssuesListEventType.REFRESH);
                    
                    wrapped.addListener(listener);
                }
                
            }, ProgressManager.instance().get());
        } catch (Exception e) {
            throw (IOException) new IOException( ).initCause( e );
        } 
        
    }

    private IIssue find( IIssue issue, Collection< ? extends IIssue> issues ) {
        for( IIssue issue2 : issues ) {
            if( issue2.getId()!=null && issue2.getId().equals(issue.getId()) )
                return issue2;
        }
        return null;
    }

    // All methods below are delegate methods
    public boolean add( IIssue o ) {
        return wrapped.add(o);
    }

    public void add( int index, IIssue element ) {
        wrapped.add(index, element);
    }

    public boolean addAll( Collection< ? extends IIssue> c ) {
        return wrapped.addAll(c);
    }

    public boolean addAll( int index, Collection< ? extends IIssue> c ) {
        return wrapped.addAll(index, c);
    }

    public void addFirst( IIssue o ) {
        wrapped.addFirst(o);
    }

    public void addLast( IIssue o ) {
        wrapped.addLast(o);
    }

    public void addListener( IIssuesListListener listener ) {
        wrapped.addListener(listener);
    }

    public void clear() {
        wrapped.clear();
    }

    public boolean contains( Object arg0 ) {
        return wrapped.contains(arg0);
    }

    public boolean containsAll( Collection< ? > arg0 ) {
        return wrapped.containsAll(arg0);
    }

    public boolean equals( Object arg0 ) {
        return wrapped.equals(arg0);
    }

    public IIssue get( int index ) {
        IIssue issue = wrapped.get(index);
        return issue;
    }

    public Set<String> getGroups() {
        return wrapped.getGroups();
    }

    public List<IIssue> getIssues( String groupId ) {
        return wrapped.getIssues(groupId);
    }

    public int hashCode() {
        return wrapped.hashCode();
    }

    public int indexOf( Object arg0 ) {
        return wrapped.indexOf(arg0);
    }

    public boolean isEmpty() {
        return wrapped.isEmpty();
    }

    public Iterator<IIssue> iterator() {
        return wrapped.iterator();
    }

    public int lastIndexOf( Object arg0 ) {
        return wrapped.lastIndexOf(arg0);
    }

    public ListIterator<IIssue> listIterator() {
        return wrapped.listIterator();
    }

    public ListIterator<IIssue> listIterator( int index ) {
        return wrapped.listIterator(index);
    }

    public IIssue remove() {
        return wrapped.remove();
    }

    public IIssue remove( int index ) {
        return wrapped.remove(index);
    }

    public boolean remove( Object o ) {
        return wrapped.remove(o);
    }

    public boolean removeAll( Collection< ? > c ) {
        return wrapped.removeAll(c);
    }

    public IIssue removeFirst() {
        return wrapped.removeFirst();
    }

    public void removeIssues( String groupId ) {
        wrapped.removeIssues(groupId);
    }

    public IIssue removeLast() {
        return wrapped.removeLast();
    }

    public void removeListener( IIssuesListListener listener ) {
        wrapped.removeListener(listener);
    }

    public boolean retainAll( Collection< ? > c ) {
        return wrapped.retainAll(c);
    }

    public IIssue set( int index, IIssue element ) {
        return wrapped.set(index, element);
    }

    public int size() {
        return wrapped.size();
    }

    public List<IIssue> subList( int arg0, int arg1 ) {
        return wrapped.subList(arg0, arg1);
    }

    public Object[] toArray() {
        return wrapped.toArray();
    }

    public <T> T[] toArray( T[] arg0 ) {
        return wrapped.toArray(arg0);
    }

    public String toString() {
        return wrapped.toString();
    }

    public void save( final IIssue issue ) throws IOException {
        dsStrategy.modifyIssue(issue);
        wrapped.notify(issue, IssuesListEventType.SAVE);
    }

    public void setStrategy( IListStrategy strategy ) {
        dsStrategy=strategy;
    }

    public String getExtensionID() {
        return dsStrategy.getExtensionID();
    }

}
