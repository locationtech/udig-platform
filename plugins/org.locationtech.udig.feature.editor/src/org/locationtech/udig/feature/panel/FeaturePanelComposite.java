/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
 * (C) 2001, 2008 IBM Corporation and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 * Contributors:
 *     Refractions Research - adapt to Feature Model
 *     IBM Corporation - initial API and implementation
 */
/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.locationtech.udig.feature.panel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * Composite responsible for drawing the tabbed property sheet page.
 *
 * @author Jody Garnett
 * @author Anthony Hunter
 */
public class FeaturePanelComposite extends Composite {

    private FeaturePanelWidgetFactory factory;

    private Composite mainComposite;

    private Composite leftComposite;

    private ScrolledComposite scrolledComposite;

    private Composite tabComposite;

    private FeaturePanelTitle title;

    private FeaturePanelList listComposite;

    private boolean displayTitle;

    /**
     * Constructor for a TabbedPropertyComposite
     *
     * @param parent the parent widget.
     * @param factory the widget factory.
     * @param displayTitle if <code>true</code>, then the title bar will be displayed.
     */
    public FeaturePanelComposite(Composite parent, FeaturePanelWidgetFactory factory,
            boolean displayTitle) {
        super(parent, SWT.NO_FOCUS);
        this.factory = factory;
        this.displayTitle = displayTitle;

        createMainComposite();
    }

    /**
     * Create the main composite.
     */
    protected void createMainComposite() {
        mainComposite = factory.createComposite(this, SWT.NO_FOCUS | SWT.NO_SCROLL);
        mainComposite.setLayout(new FormLayout());
        FormData formData = new FormData();
        formData.left = new FormAttachment(0, 0);
        formData.right = new FormAttachment(100, 0);
        formData.top = new FormAttachment(0, 0);
        formData.bottom = new FormAttachment(100, 0);
        mainComposite.setLayoutData(formData);

        createMainContents();
    }

    /**
     * Create the contents in the main composite.
     */
    protected void createMainContents() {
        if (displayTitle) {
            title = new FeaturePanelTitle(mainComposite, factory);

            FormData data = new FormData();
            data.left = new FormAttachment(0, 0);
            data.right = new FormAttachment(100, 0);
            data.top = new FormAttachment(0, 0);
            title.setLayoutData(data);
        }

        leftComposite = factory.createComposite(mainComposite, SWT.NO_FOCUS);
        leftComposite.setLayout(new FormLayout());

        scrolledComposite = factory.createScrolledComposite(mainComposite,
                SWT.H_SCROLL | SWT.V_SCROLL | SWT.NO_FOCUS);

        FormData formData = new FormData();
        formData.left = new FormAttachment(leftComposite, 0);
        formData.right = new FormAttachment(100, 0);
        if (displayTitle) {
            formData.top = new FormAttachment(title, 0);
        } else {
            formData.top = new FormAttachment(0, 0);
        }
        formData.bottom = new FormAttachment(100, 0);
        scrolledComposite.setLayoutData(formData);

        formData = new FormData();
        formData.left = new FormAttachment(0, 0);
        formData.right = new FormAttachment(scrolledComposite, 0);
        if (displayTitle) {
            formData.top = new FormAttachment(title, 0);
        } else {
            formData.top = new FormAttachment(0, 0);
        }
        formData.bottom = new FormAttachment(100, 0);
        leftComposite.setLayoutData(formData);

        tabComposite = factory.createComposite(scrolledComposite, SWT.NO_FOCUS);
        tabComposite.setLayout(new FormLayout());

        scrolledComposite.setContent(tabComposite);
        scrolledComposite.setAlwaysShowScrollBars(false);
        scrolledComposite.setExpandVertical(true);
        scrolledComposite.setExpandHorizontal(true);

        listComposite = new FeaturePanelList(leftComposite, factory);
        formData = new FormData();
        formData.left = new FormAttachment(0, 0);
        formData.right = new FormAttachment(100, 0);
        formData.top = new FormAttachment(0, 0);
        formData.bottom = new FormAttachment(100, 0);
        listComposite.setLayoutData(formData);
    }

    /**
     * Get the tabbed property list, which is the list of tabs on the left hand side of this
     * composite.
     *
     * @return the tabbed property list.
     */
    public FeaturePanelList getList() {
        return listComposite;
    }

    /**
     * Get the tabbed property title bar.
     *
     * @return the tabbed property title bar or <code>null</code> if not used.
     */
    public FeaturePanelTitle getTitle() {
        return title;
    }

    /**
     * Get the tab composite where sections display their property contents.
     *
     * @return the tab composite.
     */
    public Composite getTabComposite() {
        return tabComposite;
    }

    /**
     * Get the scrolled composite which surrounds the title bar and tab composite.
     *
     * @return the scrolled composite.
     */
    public ScrolledComposite getScrolledComposite() {
        return scrolledComposite;
    }

    /**
     * Get the widget factory.
     *
     * @return the widget factory.
     */
    protected FeaturePanelWidgetFactory getFactory() {
        return factory;
    }

    @Override
    public void dispose() {
        listComposite.dispose();
        if (displayTitle) {
            title.dispose();
        }
        super.dispose();
    }
}
