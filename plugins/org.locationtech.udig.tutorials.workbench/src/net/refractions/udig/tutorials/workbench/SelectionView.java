/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.tutorials.workbench;

import java.net.URL;
import java.util.Iterator;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.opengis.feature.Feature;
import org.opengis.filter.Filter;
/**
 * View used to track the workbench selection.
 * <p>
 * This view is part of the uDig workbench tutorial. Please
 * take careful note of the view part lifecycle (documented as
 * comments in this class).
 * </p>
 * @author Jody Garnett (Refractions Research)
 * @since 1.1.0
 */
public class SelectionView extends ViewPart {

    private final class WorkbenchSelectionListener implements ISelectionListener {
        public void selectionChanged( IWorkbenchPart part, ISelection selection ) {
            if( selection instanceof IStructuredSelection ){
                updateSelection( (IStructuredSelection) selection );
            }
            else {
                updateSelection( null );
            }
        }
    }

    private Text text;
    private ISelectionListener selectionListener;
    private Text description;

    public SelectionView() {
        // STEP ONE
        // the constructor is called when your view
        // is opened and displayed on screen.
        // DO NOT DO ANY WORK HERE!
    }

    protected void updateSelection( IStructuredSelection selection ) {
        if( selection == null || selection.isEmpty() ){
            text.setText("(nothing is selected");
            return;
        }
        Object object = selection.getFirstElement();
        if( object == null ){
            text.setText("(selected object is null)");
            return;
        }
        else {
            text.setText( object.toString() );
        }
        // now that we have a clue what is selected - we can should describe it!
        // DESCRIPTION
        StringBuffer buffer = new StringBuffer();
        String separator = System.getProperty("line.separator");
        for( Iterator<?> iterator=selection.iterator(); iterator.hasNext(); ){            
            object = iterator.next();
            buffer.append( "VALUE: ");
            buffer.append( object.toString());
            buffer.append(separator);
            buffer.append("==========================");
            buffer.append(separator);
            // from net.refractions.udig.project
            if( object instanceof IMap ){
                buffer.append("instance of Map");
                buffer.append(separator);                
            }
            if( object instanceof ILayer ){
                buffer.append("instance of ILayer");
                buffer.append(separator);                
            }
            // from net.refractions.udig.catalog
            if( object instanceof IService ){
                buffer.append("instance of IService");
                buffer.append(separator);                
            }
            if( object instanceof IGeoResource){
                buffer.append("instance of IGeoResource");
                buffer.append(separator);                
            }
            // from org.geotools
            if( object instanceof Filter ){
                buffer.append("instance of Filter");
                buffer.append(separator);                
            }
            if( object instanceof Feature ){
                buffer.append("instance of Feature");
                buffer.append(separator);                
            }
            // IADATABLE
            buffer.append("--------------------------");
            buffer.append(separator);            
            if( object instanceof IAdaptable){
                // IAdtable is a magic interface that allows
                // a single object to return multiple interfaces
                IAdaptable adaptable = (IAdaptable) object;
                if( adaptable.getAdapter(IMap.class) != null ){
                    buffer.append("adapts to Map");
                    buffer.append(separator);                    
                }
                if( adaptable.getAdapter(ILayer.class) != null ){
                    buffer.append("adapts to ILayer");
                    buffer.append(separator);                    
                }
                // from net.refractions.udig.catalog
                if( adaptable.getAdapter(IService.class) != null ){
                    buffer.append("adapts to IService");
                    buffer.append(separator);                    
                }
                if( adaptable.getAdapter(IGeoResource.class) != null){
                    buffer.append("adapts to IGeoResource");
                    buffer.append(separator);                    
                }
                // from org.geotools
                if( adaptable.getAdapter(Filter.class) != null ){
                    buffer.append("adapts to Filter");
                    buffer.append(separator);                    
                }
                if( adaptable.getAdapter(Feature.class) != null ){
                    buffer.append("adapts to Feature");
                    buffer.append(separator);                    
                }
                if( adaptable.getAdapter(URL.class) != null ){
                    buffer.append("adapts to URL");
                    buffer.append(separator);                    
                }
            }
        }  // NEXT
        description.setText(buffer.toString());        
    }
    
    /** Safely update the text; making sure we are in the Display thread 
    protected void setText( final String str ){
        if( text == null ) return;
        text.setText( str );
        /*
        text.getDisplay().asyncExec( new Runnable(){
            public void run() {
                if( text == null || text.isDisposed() ) return;                
                text.setText( str );
            }            
        });               
    }
    */
    /** Safely update the text; making sure we are in the Display thread 
    protected void setDescription( final String str ){
        if( description == null ) return;       
        description.getDisplay().asyncExec( new Runnable(){
            public void run() {
                if( description == null || description.isDisposed() ) return;                
                description.setText( str );                
            }            
        });
        System.out.println( str );
    }
    */
    @Override
    public void init( IViewSite site, IMemento memento ) throws PartInitException {
        super.init(site, memento);
        // STEP TWO
        // load in any settings from a previous run
        // String name = memento.getString("name");
    }
    @Override
    public void createPartControl( Composite parent ) {
        // STEP THREE
        // make a user interface; the provided parent has been created
        // for our use (we can set the layout and add widgets to this
        // parent).
        GridLayout layout = new GridLayout();
        layout.numColumns = 4;
        parent.setLayout( layout );
        Label label = new Label(parent, SWT.RIGHT );
        label.setLayoutData( new GridData(SWT.RIGHT,SWT.TOP,true,false ) );
        label.setText("Selection:");
        
        text = new Text(parent, SWT.DEFAULT | SWT.READ_ONLY | SWT.WRAP );
        text.setTextLimit(70);
        text.setLayoutData( new GridData(SWT.LEFT,SWT.TOP,true,true, 3,1 ) );
        
        label = new Label(parent, SWT.RIGHT );
        label.setLayoutData( new GridData(SWT.RIGHT,SWT.TOP,true,false ) );
        label.setText("Content:");
        
        description = new Text(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI );
        GridData gridData = new GridData(SWT.DEFAULT,SWT.DEFAULT,true,true, 3,3 );
        gridData.widthHint = 500;
        gridData.heightHint = 200;
        description.setLayoutData( gridData );
        
        selectionListener = new WorkbenchSelectionListener();
        ISelectionService selectionService = getSite().getWorkbenchWindow().getSelectionService();
        selectionService.addPostSelectionListener(selectionListener);
    }

    @Override
    public void setFocus() {
        // STEP FOUR
        // assign focus to one of the controls if you are accepting input
        // (we do not accept input in this tutorial but if we did it would be
        //  something like....)
        // description.setFocus()
    }
    
    @Override
    public void dispose() {
        // STEP FIVE
        // We should clean up after any resources we used
        // (such as widgets, colors, images and fonts)
        // please be careful not to assume that createPartControl has been called
        if( selectionListener != null ){
            // if our init method failed selectionListener would be null!
            //
            ISelectionService selectionService = getSite().getWorkbenchWindow().getSelectionService();
            selectionService.removePostSelectionListener(selectionListener);
            
            selectionListener = null;
        }
        super.dispose();
    }
}
