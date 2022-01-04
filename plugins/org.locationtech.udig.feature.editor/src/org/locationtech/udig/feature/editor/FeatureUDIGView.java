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
package org.locationtech.udig.feature.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageSite;
import org.eclipse.ui.part.ViewPart;
import org.locationtech.udig.feature.panel.FeaturePanelPage;
import org.locationtech.udig.feature.panel.FeaturePanelPageContributor;
import org.locationtech.udig.internal.ui.UiPlugin;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.IFeatureSite;
import org.locationtech.udig.project.ui.IUDIGView;
import org.locationtech.udig.project.ui.feature.FeatureSiteImpl;
import org.locationtech.udig.project.ui.tool.IToolContext;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * View allowing direct editing of the currently selected feature.
 * <p>
 * The currently selected feature is handled by the EditManager; and is communicated with the a page
 * via a FeatureSite. We also have a special EditFeature implementation where each setAttribute call
 * is backed by a command.
 * <p>
 * This is the "most normal" implementation directly extending PageBookView resulting in one
 * "feature panel page" per workbench part. This should provide excellent isolation between maps
 * allowing the user to quickly switch between them.
 *
 * @author Jody
 * @since 1.2.0
 */
public class FeatureUDIGView extends ViewPart implements FeaturePanelPageContributor, IUDIGView {
    public static final String ID = "org.locationtech.udig.feature.editor.featureView"; //$NON-NLS-1$

    private IToolContext context;

    private FeaturePanelPage featurePage;

    private MessagePage messagePage;

    private PageBook book;

    private SimpleFeature current;

    private PageSite pageSite;

    @Override
    public void init(IViewSite site) throws PartInitException {
        super.init(site);
        this.pageSite = new PageSite(site);
    }

    @Override
    public void createPartControl(Composite parent) {
        book = new PageBook(parent, SWT.NONE);
        messagePage = new MessagePage();
        messagePage.setMessage("Please select a feature with the edit geometry tool");
        messagePage.init(pageSite);
        messagePage.createControl(book);

        featurePage = new FeaturePanelPage(this);

        featurePage.init(pageSite);
        featurePage.setFeatureSite(new FeatureSiteImpl());
        featurePage.createControl(book);
        final IMap map = ApplicationGIS.getActiveMap();
        if (map != ApplicationGIS.NO_MAP) {
            try {
                editFeatureChanged(map.getEditManager().getEditFeature());
            } catch (Throwable e) {
                UiPlugin.log("Default SimpleFeature Editor threw an exception", e); //$NON-NLS-1$
            }
        }

        book.showPage(messagePage.getControl());
    }

    @Override
    public SimpleFeatureType getSchema() {
        if (current == null)
            return null;
        return current.getFeatureType();
    }

    @Override
    public void setFocus() {
        if (current == null) {
            book.showPage(messagePage.getControl());
            messagePage.setFocus();

            featurePage.editFeatureChanged(null);
        } else {
            book.showPage(featurePage.getControl());
            featurePage.setFocus();
            if (current == null) {
                featurePage.editFeatureChanged(null);
            }
        }
    }

    @Override
    public void editFeatureChanged(SimpleFeature feature) {
        this.current = feature;
        if (feature == null) {
            book.showPage(messagePage.getControl());
            return;
        }
        // pass the selection to the page

        IMap activeMap = ApplicationGIS.getActiveMap();
        IFeatureSite site = featurePage.getFeatureSite();
        if (site == null) {
            site = new FeatureSiteImpl(activeMap);
            featurePage.setFeatureSite(site);
        } else {
            ((FeatureSiteImpl) site).setMapInternal((Map) activeMap);
            ((FeatureSiteImpl) site).setFeature(feature);
        }
        featurePage.editFeatureChanged(feature);
        featurePage.refresh();
        book.showPage(featurePage.getControl());
    }

    @Override
    public IToolContext getContext() {
        return context;
    }

    @Override
    public void setContext(IToolContext newContext) {
        this.context = newContext;
    }

}
