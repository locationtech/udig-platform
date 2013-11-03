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
package org.locationtech.udig.catalog.ui;

import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.IResolve.Status;
import org.locationtech.udig.catalog.ui.internal.Messages;

import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * Provides Tree view of the Registry.
 * <p>
 * Supports the following:
 * <ul>
 * <li>List
 * <li>IService
 * <li>IGeoReference
 * <li>ICatalog
 * <li>String
 * </ul>
 * </p>
 * <p>
 * To display a message or status please use a Collections.singletonList( "hello world" ).
 * </p>
 * 
 * @author jeichar
 * @since 0.3
 */
public class CatalogTreeViewer extends TreeViewer implements ISelectionChangedListener {
    private IMessageBoard messageBoard;

    /**
     * Construct <code>CatalogTreeViewer</code>.
     * 
     * @param parent
     */
    public CatalogTreeViewer( Composite parent, boolean titles ) {
        this(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER, titles); // no border by
                                                                                	// default
    }
    /**
     * Construct <code>CatalogTreeViewer</code>.
     * 
     * @param parent
     */
    public CatalogTreeViewer( Composite parent ) {
        this(parent, true); // no border by
                                                                                	// default
    }

    /**
     * Construct <code>CatalogTreeViewer</code>.
     * <p>
     * You will need to set your input:
     * 
     * <pre><code>
     * CatalogTreeViewer viewer = new CatalogTreeViewer(parent, SWT.DEFAULT);
     * viewer.setInput(CatalogPlugin.getDefault().getLocalCatalog());
     * </code></pre>
     * 
     * </p>
     * 
     * @param parent Parent component
     * @param style The other constructor uses SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER 
     */
    public CatalogTreeViewer( Composite parent, int style, boolean titles ) {
        super(parent, style|SWT.VIRTUAL);
        setContentProvider(new ResolveContentProvider());
        ResolveLabelProviderSimple resolveLabelProviderSimple = new ResolveLabelProviderSimple();
        if (titles) {
            setLabelProvider(new DecoratingLabelProvider(resolveLabelProviderSimple,
                    new ResolveTitlesDecorator(resolveLabelProviderSimple)));
        } else {
            setLabelProvider(resolveLabelProviderSimple);
        }
        
        setUseHashlookup(true);
        setInput(CatalogPlugin.getDefault().getLocalCatalog());
        setSorter(new CatalogViewerSorter());
        
        addSelectionChangedListener(this);
        
    }
    public void selectionChanged( SelectionChangedEvent event ) {
        if( messageBoard==null )
            return;
        
        ISelection selection = event.getSelection();
        if( selection instanceof IStructuredSelection ){
            IStructuredSelection sel=(IStructuredSelection) selection;
            if( sel.size()==1 ){
                Object obj=sel.getFirstElement();
                if (obj instanceof IResolve) {
                    IResolve resolve = (IResolve) obj;
                    if( resolve.getStatus()==Status.BROKEN ){
                        if (null == resolve.getMessage()) {
                            messageBoard.putMessage(Messages.CatalogTreeViewer_broken, IMessageBoard.Type.ERROR);
                        } else {
                            messageBoard.putMessage(resolve.getMessage().getLocalizedMessage(), IMessageBoard.Type.ERROR);   
                        }
                    }else if( resolve.getStatus()==Status.RESTRICTED_ACCESS ){
                        messageBoard.putMessage(Messages.CatalogTreeViewer_permission, IMessageBoard.Type.ERROR);
                    }else{
                        messageBoard.putMessage(null, IMessageBoard.Type.NORMAL);
                    }

                    
                }else{
                    messageBoard.putMessage(null, IMessageBoard.Type.NORMAL);
                }
            }else{
                messageBoard.putMessage(null, IMessageBoard.Type.NORMAL);
            }
        }else{
            messageBoard.putMessage(null, IMessageBoard.Type.NORMAL);
        }
    }
    
    /**
     * Sets the message board that this viewer will display status messages on. 
     * 
     * @param messageBoard
     *
     * @see StatusLineMessageBoardAdapter
     * @see IResolve#getStatus()
     * @see IResolve#getMessage()
     */
    public void setMessageBoard( IMessageBoard messageBoard ){
        this.messageBoard=messageBoard;
    }
}
