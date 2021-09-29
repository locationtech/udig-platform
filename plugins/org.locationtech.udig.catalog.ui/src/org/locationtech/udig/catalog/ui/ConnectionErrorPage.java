/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.ui;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.internal.ui.ConnectionPageDecorator;
import org.locationtech.udig.catalog.ui.internal.Messages;
import org.locationtech.udig.catalog.ui.workflow.ConnectionErrorState;
import org.locationtech.udig.catalog.ui.workflow.WorkflowWizardPage;

/**
 * A wizard page which displays a set of IConnectionErrorHandler objects.
 * <p>
 * This page is intended for use in a wizard extending
 *
 * @see org.locationtech.udig.catalog.ui.UDIGImportPage to connect to a service.
 *      </p>
 * @author Justin Deoliveira
 */
public class ConnectionErrorPage extends WorkflowWizardPage {

    /** the current handler * */
    IConnectionErrorHandler handler;

    public ConnectionErrorPage() {
        super(Messages.ConnectionErrorPage_pageName);

        setDescription(Messages.ConnectionErrorPage_pageDescription);
        setTitle(Messages.ConnectionErrorPage_pageTitle);
    }

    @Override
    public boolean isPageComplete() {
        return handler.isComplete();
    }

    @Override
    public boolean canFlipToNextPage() {
        if (handler != null)
            return handler.canRecover();

        return false;
    }

    @Override
    public void createControl(Composite parent) {

        ConnectionErrorState state = (ConnectionErrorState) getState();

        handler = new SimpleConnectionErrorHandler(state.getErrors());
        handler.createControl(parent);

        setControl(handler.getControl());
    }

    @Override
    public IWizardPage getNextPage() {
        if (handler.canRecover() && handler.isComplete()) {
            ConnectionPageDecorator page = (ConnectionPageDecorator) getPreviousPage();
            return page.getNextPage();
        }

        return super.getNextPage();
    }

    static class SimpleConnectionErrorHandler extends IConnectionErrorHandler {

        Map<IService, Throwable> errors;

        SimpleConnectionErrorHandler(Map<IService, Throwable> errors) {
            this.errors = errors;

            setName("Simple"); //$NON-NLS-1$
        }

        @Override
        public boolean canHandle(IService service, Throwable t) {
            return true;
        }

        @Override
        public boolean canRecover() {
            return false;
        }

        @Override
        protected Control create(Composite parent) {
            Composite root = new Composite(parent, SWT.NONE);
            root.setLayout(new FormLayout());

            Label label = new Label(root, SWT.LEFT);
            label.setText(Messages.ConnectionErrorPage_message);

            ListViewer listViewer = new ListViewer(root);
            listViewer.setLabelProvider(new LabelProvider() {
                @Override
                public String getText(Object element) {
                    IService service = (IService) element;
                    return service.getIdentifier().toString();
                }
            });
            listViewer.setContentProvider(new ArrayContentProvider());
            listViewer.setInput(errors.keySet().toArray());

            final Text text = new Text(root,
                    SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.SCROLL_LINE);

            FormData layoutData = new FormData();
            layoutData.top = new FormAttachment(0);
            layoutData.left = new FormAttachment(0);
            label.setLayoutData(layoutData);

            layoutData = new FormData();
            layoutData.top = new FormAttachment(label, 2, SWT.BOTTOM);
            layoutData.left = new FormAttachment(0);
            layoutData.right = new FormAttachment(100);
            layoutData.bottom = new FormAttachment(50);
            listViewer.getList().setLayoutData(layoutData);

            layoutData = new FormData();
            layoutData.top = new FormAttachment(listViewer.getList(), 2, SWT.BOTTOM);
            layoutData.left = new FormAttachment(0);
            layoutData.right = new FormAttachment(100);
            layoutData.bottom = new FormAttachment(100);
            text.setLayoutData(layoutData);

            listViewer.addSelectionChangedListener(new ISelectionChangedListener() {

                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                    if (selection == null || selection.isEmpty())
                        return;

                    IService service = (IService) selection.getFirstElement();
                    Throwable t = errors.get(service);

                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    t.printStackTrace(new PrintStream(out));
                    text.setText(new String(out.toByteArray()));
                }
            });

            return root;
        }

    }
}
