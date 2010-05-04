package net.refractions.udig.feature.editor;

import java.util.Iterator;
import java.util.List;

import net.refractions.udig.feature.editor.AbstractPageBookView.PageRec;
import net.refractions.udig.feature.panel.FeaturePanelPage;
import net.refractions.udig.feature.panel.FeaturePanelPageContributor;
import net.refractions.udig.internal.ui.UiPlugin;
import net.refractions.udig.project.EditManagerEvent;
import net.refractions.udig.project.IEditManager;
import net.refractions.udig.project.IEditManagerListener;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.IMapListener;
import net.refractions.udig.project.MapEvent;
import net.refractions.udig.project.MapEvent.MapEventType;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.IFeatureSite;
import net.refractions.udig.project.ui.IUDIGView;
import net.refractions.udig.project.ui.feature.FeaturePanelProcessor;
import net.refractions.udig.project.ui.feature.FeatureSiteImpl;
import net.refractions.udig.project.ui.internal.ProjectUIPlugin;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;

/**
 * View allowing direct editing of the currently selected feature.
 * <p>
 * The currently selected feature is handled by the EditManager; and is
 * communicated with the a page via a FeatureSite. We also have a special
 * EditFeature implementation where each setAttribute call is backed by a
 * command.
 * <p>
 * This is the "most normal" implementation directly extending PageBookView
 * resulting in one "feature panel page" per workbench part. This should provide
 * exellent isolation between maps allowing the user to quickly switch between
 * them.
 * 
 * @author Jody
 * @since 1.2.0
 */
public class FeatureView extends PageBookView implements FeaturePanelPageContributor {

    public static final String ID = "net.refractions.udig.feature.editor.featureView";

    /**
     * The current part for which this property sheets is active
     */
    private IWorkbenchPart currentPart;

    /**
     * The current map of the feature view
     */
    private IMap currentMap;

    /**
     * We are listening to the workbench selection to see if our currentPart has
     * anything to say to us.
     */
    private ISelectionListener workbenchListener = new ISelectionListener() {
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
     * If the current layer changes we pass it on the the currentPage; with
     * ourselves (ie the FeaturePanelPageContributor) as the provider.
     */
    private IEditManagerListener editListener = new IEditManagerListener() {
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

    public void dispose() {
        super.dispose(); // run super

        // remove ourselves as a selection listener
        getSite().getPage().removeSelectionListener(workbenchListener);
        if (currentPart != null) {
            IMap map = (IMap) currentPart.getAdapter(IMap.class);
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

    public SimpleFeatureType getSchema() {
        if (getCurrentContributingPart() != null) {
            setContributor(getCurrentContributingPart());
        }
        return getSchema(currentPart);
    }

    private SimpleFeatureType getSchema(IWorkbenchPart part) {
        IMap map = (IMap) part.getAdapter(IMap.class);
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

        initPage((IPageBookViewPage) page);
        page.createControl(getPageBook());

        return page;
    }

    @Override
    protected PageRec doCreatePage(IWorkbenchPart part) {
        IMap map = (IMap) part.getAdapter(IMap.class);
        if (map == null) {
            MessagePage page = new MessagePage();
            page.setMessage("Please select a Map");
            initPage(page);
            page.createControl(getPageBook());

            PageRec rec = new PageRec(part, page);
            return rec;
        }
        setContributor(part);
        IPage page = (IPage) new FeaturePanelPage(this);
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

        IMap map = (IMap) editor.getAdapter(IMap.class);
        if (map == null)
            return null;

        return editor;
    }

    /**
     * IWorkbenchPart considered important if the part can adapt to an IMap
     * (which allows us to track any changes to the edit feature).
     */
    @Override
    public boolean isImportant(IWorkbenchPart part) {
        return toMap(part) != null;
    }

    public static IMap toMap(IWorkbenchPart part) {
        IMap map = (IMap) part.getAdapter(IMap.class);
        if (map != null)
            return map;

        ILayer layer = (ILayer) part.getAdapter(ILayer.class);
        if (layer != null)
            return layer.getMap();

        return null; // unable to determine a map
    }

    /**
     * Determine an ILayer from a workbench part.
     * <p>
     * If the part provides a layer it is used; if it only provides a Map the
     * selected layer is used.
     * 
     * @param part
     * @param selection
     * @return layer, or null if not available
     */
    public static ILayer toLayer(IWorkbenchPart part) {
        // see if the part can turn into a layer
        //
        ILayer layer = (ILayer) part.getAdapter(ILayer.class);
        if (layer != null) {
            return layer;
        }

        IMap map = (IMap) part.getAdapter(IMap.class);
        if (map != null) {
            return map.getEditManager().getSelectedLayer();
        }
        return null;
    }

    public static ILayer toEditLayer(IWorkbenchPart part) {
        IMap map = (IMap) part.getAdapter(IMap.class);
        if (map != null) {
            return map.getEditManager().getEditLayer();
        }
        ILayer layer = (ILayer) part.getAdapter(ILayer.class);
        if (layer != null) {
            if (layer == layer.getMap().getEditManager().getEditLayer()) {
                return layer;
            }
        }
        return null;
    }

    /**
     * ISelection considered important if we can determine a Layer from it; more
     * importantly the layer needs to have a non null getSchema() so we can
     * configure a FeaturePanelPage with it.
     * 
     * @param part
     * @param selection
     * @return layer, or null if not available
     */
    public static ILayer toLayer(ISelection selection) {
        // try the selection first
        if (selection != null && !selection.isEmpty() && selection instanceof IStructuredSelection) {
            IStructuredSelection sel = (IStructuredSelection) selection;
            for (Iterator<?> iter = sel.iterator(); iter.hasNext();) {
                Object item = iter.next();
                if (item instanceof ILayer) {
                    return (ILayer) item;
                }
                if (item instanceof IAdaptable) {
                    ILayer layer = (ILayer) ((IAdaptable) item).getAdapter(ILayer.class);
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
