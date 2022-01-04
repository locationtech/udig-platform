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

import java.util.Iterator;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;
import org.locationtech.udig.feature.panel.FeaturePanelPage;
import org.locationtech.udig.feature.panel.FeaturePanelPageContributor;
import org.locationtech.udig.internal.ui.UiPlugin;
import org.locationtech.udig.project.EditManagerEvent;
import org.locationtech.udig.project.IEditManager;
import org.locationtech.udig.project.IEditManagerListener;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
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
public class FeatureView extends PageBookView implements FeaturePanelPageContributor {

    public static final String ID = "org.locationtech.udig.feature.editor.featureView"; //$NON-NLS-1$

    /**
     * The current part for which this property sheets is active
     */
    private IWorkbenchPart currentPart;

    /**
     * The current map of the feature view
     */
    private IMap currentMap;

    /**
     * We are listening to the workbench selection to see if our currentPart has anything to say to
     * us.
     */
    private ISelectionListener workbenchListener = new ISelectionListener() {
        @Override
        public void selectionChanged(IWorkbenchPart part, ISelection sel) {
            if (sel == null || !isImportant(part)) {
                return;
            }
            // we ignore selection if we are hidden OR selection is coming from
            // another source as
            // the last one
            setContributor(part);
        }
    };

    /**
     * We are listing to our map to see if the current layer changes.
     * <p>
     * If the current layer changes we pass it on the the currentPage; with ourselves (ie the
     * FeaturePanelPageContributor) as the provider.
     */
    private IEditManagerListener editListener = new IEditManagerListener() {
        @Override
        public void changed(EditManagerEvent event) {
            if (event.getType() == EditManagerEvent.SELECTED_LAYER) {
                StructuredSelection sel = new StructuredSelection(event.getNewValue());

                // pass the selection to the page
                IPage page = getCurrentPage(); // We are expecting a
                                               // FeaturePanelPage here
                if (page != null && page instanceof ISelectionListener) {
                    ISelectionListener notifyPage = (ISelectionListener) page;
                    notifyPage.selectionChanged(FeatureView.this, sel);
                }
            }
            if (event.getType() == EditManagerEvent.EDIT_FEATURE) {
                StructuredSelection sel = new StructuredSelection(event.getNewValue());

                // pass the selection to the page
                IPage page = getCurrentPage(); // We are expecting a
                                               // FeaturePanelPage here
                if (page != null && page instanceof ISelectionListener) {
                    ISelectionListener notifyPage = (ISelectionListener) page;
                    notifyPage.selectionChanged(FeatureView.this, sel);
                }
            }
        }
    };

    public void setContributor(IWorkbenchPart part) {
        if (currentPart == part) {
            return;
        }
        if (currentPart != null) {
            if (currentMap != null) {
                IEditManager editManager = currentMap.getEditManager();
                editManager.removeListener(editListener);
            }
        }
        this.currentPart = part;

        StructuredSelection editSelection = null;
        if (currentPart != null) {
            if (currentMap != null) {
                IEditManager editManager = currentMap.getEditManager();
                editManager.addListener(editListener);
                editSelection = new StructuredSelection(editManager.getEditFeature());
            }
        }
        // pass the selection to the page
        IPage page = getCurrentPage(); // We are expecting a FeaturePanelPage
                                       // here
        if (page != null && page instanceof ISelectionListener) {
            ISelectionListener notifyPage = (ISelectionListener) page;
            notifyPage.selectionChanged(part, editSelection);
        }
    }

    @Override
    public void init(IViewSite site) throws PartInitException {
        site.getPage().addSelectionListener(workbenchListener);
        super.init(site);
    }

    @Override
    public void dispose() {
        super.dispose(); // run super

        // remove ourselves as a selection listener
        getSite().getPage().removeSelectionListener(workbenchListener);
        if (currentPart != null) {
            IMap map = currentPart.getAdapter(IMap.class);
            if (map != null) {
                map.getEditManager().removeListener(editListener);
            }
            currentPart = null;
        }
        if (currentMap != null) {
            currentMap.getEditManager().removeListener(editListener);
            currentMap = null;
        }
    }

    @Override
    public SimpleFeatureType getSchema() {
        if (getCurrentContributingPart() != null) {
            setContributor(getCurrentContributingPart());
        }
        return getSchema(currentPart);
    }

    private SimpleFeatureType getSchema(IWorkbenchPart part) {
        IMap map = part.getAdapter(IMap.class);
        if (map == null) {
            return null;
        }
        IEditManager editManager = map.getEditManager();
        if (editManager == null) {
            return null;
        }
        ILayer selectedLayer = editManager.getSelectedLayer();
        if (selectedLayer == null) {
            return null;
        }
        return selectedLayer.getSchema();
    }

    @Override
    protected IPage createDefaultPage(PageBook book) {
        MessagePage page = new MessagePage();
        page.setMessage("Default Page");

        initPage(page);
        page.createControl(getPageBook());

        return page;
    }

    @Override
    protected PageRec doCreatePage(IWorkbenchPart part) {
        IMap map = part.getAdapter(IMap.class);
        if (map == null) {
            MessagePage page = new MessagePage();
            page.setMessage("Please select a Map");
            initPage(page);
            page.createControl(getPageBook());

            PageRec rec = new PageRec(part, page);
            return rec;
        }
        setContributor(part);
        IPage page = new FeaturePanelPage(this);
        initPage((IPageBookViewPage) page);
        page.createControl(getPageBook());
        return new PageRec(part, page);
    }

    @Override
    protected void doDestroyPage(IWorkbenchPart part, PageRec pageRecord) {
    }

    @Override
    protected IWorkbenchPart getBootstrapPart() {
        IWorkbenchWindow window = UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
        if (window == null)
            return null;

        IWorkbenchPage page = window.getActivePage();
        if (page == null)
            return null;

        IEditorPart editor = page.getActiveEditor();
        if (editor == null)
            return null;

        IMap map = editor.getAdapter(IMap.class);
        if (map == null)
            return null;

        return editor;
    }

    /**
     * IWorkbenchPart considered important if the part can adapt to an IMap (which allows us to
     * track any changes to the edit feature).
     */
    @Override
    public boolean isImportant(IWorkbenchPart part) {
        return toMap(part) != null;
    }

    public static IMap toMap(IWorkbenchPart part) {
        IMap map = part.getAdapter(IMap.class);
        if (map != null)
            return map;

        ILayer layer = part.getAdapter(ILayer.class);
        if (layer != null)
            return layer.getMap();

        return null; // unable to determine a map
    }

    /**
     * Determine an ILayer from a workbench part.
     * <p>
     * If the part provides a layer it is used; if it only provides a Map the selected layer is
     * used.
     * </p>
     *
     * @param part
     * @param selection
     * @return layer, or null if not available
     */
    public static ILayer toLayer(IWorkbenchPart part) {
        // see if the part can turn into a layer
        //
        ILayer layer = part.getAdapter(ILayer.class);
        if (layer != null) {
            return layer;
        }

        IMap map = part.getAdapter(IMap.class);
        if (map != null) {
            return map.getEditManager().getSelectedLayer();
        }
        return null;
    }

    public static ILayer toEditLayer(IWorkbenchPart part) {
        IMap map = part.getAdapter(IMap.class);
        if (map != null) {
            return map.getEditManager().getEditLayer();
        }
        ILayer layer = part.getAdapter(ILayer.class);
        if (layer != null) {
            if (layer == layer.getMap().getEditManager().getEditLayer()) {
                return layer;
            }
        }
        return null;
    }

    /**
     * ISelection considered important if we can determine a Layer from it; more importantly the
     * layer needs to have a non null getSchema() so we can configure a FeaturePanelPage with it.
     *
     * @param part
     * @param selection
     * @return layer, or null if not available
     */
    public static ILayer toLayer(ISelection selection) {
        // try the selection first
        if (selection != null && !selection.isEmpty()
                && selection instanceof IStructuredSelection) {
            IStructuredSelection sel = (IStructuredSelection) selection;
            for (Iterator<?> iter = sel.iterator(); iter.hasNext();) {
                Object item = iter.next();
                if (item instanceof ILayer) {
                    return (ILayer) item;
                }
                if (item instanceof IAdaptable) {
                    ILayer layer = ((IAdaptable) item).getAdapter(ILayer.class);
                    if (layer != null) {
                        return layer;
                    }
                }
                if (item instanceof IMap) {
                    return ((IMap) item).getEditManager().getSelectedLayer();
                }
            }
        }
        return null;
    }

}
