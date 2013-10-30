/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package net.refractions.udig.tool.info.internal;

import java.util.List;

import net.refractions.udig.tool.info.LayerPointInfo;
import net.refractions.udig.ui.ImageCache;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

/**
 * Display Information from InfoTool as a Tree.
 * <p>
 * InfoTool currently grabs a list from the Renderstack, this may be replaced
 * with real Information object at somepoint in the future.
 * </p>
 * <p>
 * For the List&gt;LayerPointInfo&lt;:
 * <ul>
 * <li>Each LayerPointInfo is a node in the Tree
 * <li>LayerPointInfo of type gml the value will be a FeatureCollection, each SimpleFeature will
 * be a node in the tree (at least until we get a FeatureCollection Viewer)
 * </ul>
 * </p>
 * 
 * @author Jody Garnett, Refractions Research 
 * @since 0.6
 */
public class InfoTreeViewer extends TreeViewer implements ITreeContentProvider,ILabelProvider {
    
    List<LayerPointInfo> information = null;
    ImageCache imageCache = new ImageCache();
    
    /**
     * Construct <code>CatalogTreeViewer</code>.
     *
     * @param parent
     */
    public InfoTreeViewer( Composite parent ) {
        super(parent);
        setContentProvider( this );
        setLabelProvider( this );
        setInput( information );
        expandToLevel(2);
    }

    /**
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose() {
        if( information != null ) {
            information.clear();
            information = null;
        }    
        if( imageCache != null ) {
            imageCache.dispose();
            imageCache = null;
        }
    }    
    
    /**
     * Provides a mapping from Information to TreeViewer.
     * <p>
     * For the List&gt;LayerPointInfo&lt;:
     * <ul>
     * <li>Each LayerPointInfo is a node in the Tree
     * <li>LayerPointInfo of type gml the value will be a FeatureCollection, each SimpleFeature will
     * be a node in the tree (at least until we get a FeatureCollection Viewer)
     * </ul>
     * </p>
     */
    
    /**
     * Returns the child elements of the given parent element.
     * <p>
     * The difference between this method and <code>IStructuredContentProvider.getElements</code> 
     * is that <code>getElements</code> is called to obtain the 
     * tree viewer's root elements, whereas <code>getChildren</code> is used
     * to obtain the children of a given parent element in the tree (including a root).
     * </p>
     * The result is not modified by the viewer.
     * </p>
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     *
     * @param parent the parent element
     * @return an array of child elements
     * 
     */
    public Object[] getChildren(Object parent) {
        if( parent == information ) {
            return information.toArray();
        }
        return null;
    }        
    /**
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     */
    public Object getParent(Object element) {
        if( element == information ) {
            return null;
        }
        else if (element instanceof LayerPointInfo) {
            return information;
        }
        return null;
    } 
    
    /**
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     */
    public boolean hasChildren(Object element) {
        return (element == information );
    }

    /**
     * Returns the elements to display in the viewer 
     * when its input is set to the given element. 
     * These elements can be presented as rows in a table, items in a list, etc.
     * The result is not modified by the viewer.
     *
     * @param inputElement the input element
     * @return the array of elements to display in the viewer
     */
    public Object[] getElements(Object inputElement) {
        return getChildren(inputElement);
    }
    
    /**
     * Notificaiton that the input has been changed.
     * 
     * @see org.eclipse.jface.viewers.AbstractTreeViewer#inputChanged(java.lang.Object, java.lang.Object)
     * @param input
     * @param oldInput
     */
    @SuppressWarnings("unchecked")
    protected void inputChanged( Object input, Object oldInput ) {
        if( oldInput != null && oldInput instanceof List) {
            List oldInformation = (List) oldInput;
            oldInformation.clear();                
        }
        if( input == null ) {
            information = null;
        }
        else if( input instanceof List) {
            information = (List<LayerPointInfo>) input;
        }
    }
    /**
     * Reset the viewer to new information
     * 
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     * @param viewer
     * @param oldInput
     * @param newInput
     */
    public void inputChanged( Viewer viewer, Object oldInput, Object newInput ) {
        // viewer should equal InfoTreeViewer.this
        inputChanged( newInput, oldInput );
    }                         
    
    /*
     * Image based on layer of selected info
     */
    public Image getImage(Object element) {
        if( element == information ) {
            return null; // Use information symbol?
        }
        if (element instanceof LayerPointInfo){
            LayerPointInfo info = (LayerPointInfo) element;
            return imageCache.getImage( info.getLayer().getIcon() );            
        }
        return null;
    }

    public String getText(Object element) {
        if( element == information ) {
            return "information"; //$NON-NLS-1$
        }
        if (element instanceof LayerPointInfo){
            LayerPointInfo info = (LayerPointInfo) element;
            return info.getLayer().getName();
        }
        return null;
    }

    public void addListener( ILabelProviderListener listener ) {
        // ignore as we reset the information on each request
    }

    public boolean isLabelProperty( Object element, String property ) {
        return false;  // we don't issue events
    }

    public void removeListener( ILabelProviderListener listener ) {
        // ignore as we reset the information on each request
    }
}