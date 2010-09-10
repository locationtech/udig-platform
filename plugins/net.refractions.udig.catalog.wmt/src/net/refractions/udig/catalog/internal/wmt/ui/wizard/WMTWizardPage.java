package net.refractions.udig.catalog.internal.wmt.ui.wizard;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.internal.wmt.Trace;
import net.refractions.udig.catalog.internal.wmt.WMTPlugin;
import net.refractions.udig.catalog.internal.wmt.WMTService;
import net.refractions.udig.catalog.internal.wmt.WMTServiceExtension;
import net.refractions.udig.catalog.internal.wmt.ui.wizard.controls.CSControl;
import net.refractions.udig.catalog.internal.wmt.ui.wizard.controls.MQControl;
import net.refractions.udig.catalog.internal.wmt.ui.wizard.controls.OSMCloudMadeControl;
import net.refractions.udig.catalog.internal.wmt.ui.wizard.controls.OSMControl;
import net.refractions.udig.catalog.internal.wmt.ui.wizard.controls.WMTWizardControl;
import net.refractions.udig.catalog.internal.wmt.ui.wizard.controls.WWControl;
import net.refractions.udig.catalog.internal.wmt.wmtsource.MQSource;
import net.refractions.udig.catalog.internal.wmt.wmtsource.NASASource;
import net.refractions.udig.catalog.internal.wmt.wmtsource.NASASourceManager;
import net.refractions.udig.catalog.internal.wmt.wmtsource.OSMCloudMadeSource;
import net.refractions.udig.catalog.internal.wmt.wmtsource.OSMCycleMapSource;
import net.refractions.udig.catalog.internal.wmt.wmtsource.OSMMapnikSource;
import net.refractions.udig.catalog.internal.wmt.wmtsource.OSMOsmarenderSource;
import net.refractions.udig.catalog.internal.wmt.wmtsource.OSMSource;
import net.refractions.udig.catalog.internal.wmt.wmtsource.WMTSource;
import net.refractions.udig.catalog.ui.AbstractUDIGImportPage;
import net.refractions.udig.catalog.ui.UDIGConnectionPage;
import net.refractions.udig.catalog.wmt.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * 
 * Based on WmsWizardPage
 * <p>
 *
 * </p>
 * @author to.srwn
 * @since 1.1.0
 */
public class WMTWizardPage extends AbstractUDIGImportPage implements UDIGConnectionPage {

      
    private WMTServiceExtension serviceExtension;
    private Tree tree;
    private StackLayout stackLayoutInfoBox;
    private Composite infoBox;
    private Composite childControl;
    

    public WMTWizardPage() {
        super(Messages.Wizard_Title); 
        
        serviceExtension = new WMTServiceExtension();
    }

    public String getId() {
        return "net.refractions.udig.catalog.ui.WMT"; //$NON-NLS-1$
    }

    @Override
    public boolean leavingPage() {
        // Skip the resource selection wizard page
        IRunnableWithProgress runnable = new IRunnableWithProgress(){
            public void run( IProgressMonitor monitor ) throws InvocationTargetException,
                    InterruptedException {
                getWizard().getWorkflow().next();
            }
        };

        try {
            getContainer().run(true, false, runnable);
        } catch (InvocationTargetException e2) {
            throw (RuntimeException) new RuntimeException().initCause(e2);
        } catch (InterruptedException e2) {
            throw (RuntimeException) new RuntimeException().initCause(e2);
        }

        return super.leavingPage();
    }

    //region Get selected GeoResources/Services
    //region GeoResources
    /**
     * Builds a list of all selected GeoResources
     */
    @Override
    public Collection<URL> getResourceIDs() {        
        Collection<URL> resourceIDs = new LinkedList<URL>();        
        getSelectedResources(resourceIDs, tree);
        
        return resourceIDs;
    }
    
    /**
     * Loops the Tree-View for selected GeoResources
     *
     * @param resourceIDs
     * @param tree
     */
    private void getSelectedResources(Collection<URL> resourceIDs, Tree tree) {
        for (int i = 0; i < tree.getItemCount(); i++) {
            TreeItem treeItem = tree.getItem(i);
            
            if (treeItem.getChecked() || treeItem.getGrayed()) {
                getSelectedResources(resourceIDs, treeItem);
            }
        }
    }
    
    /**
     * Loops a TreeItem for selected GeoResources
     *
     * @param resourceIDs
     * @param treeItem Selected TreeItem
     */
    private void getSelectedResources(Collection<URL> resourceIDs, TreeItem treeItem) {
        if(treeItem.getData() instanceof WMTWizardTreeItemData) {
            WMTWizardTreeItemData itemData = (WMTWizardTreeItemData) treeItem.getData();
            
            IGeoResource geoResource = itemData.getGeoResource();
            
            if (geoResource != null) {
                WMTPlugin.debug("[Wizard.getSelectedResources] adding " +  //$NON-NLS-1$
                        geoResource.getIdentifier(), Trace.WIZARD);
                
                resourceIDs.add(geoResource.getIdentifier());
            }
        }
        

        for (int i = 0; i < treeItem.getItemCount(); i++) {
            TreeItem childItem = treeItem.getItem(i);
            
            if (childItem.getChecked() || childItem.getGrayed()) {
                getSelectedResources(resourceIDs, childItem);
            }
        }
    }
    //endregion
    
    //region Services
    /**
     * Loops the tree and returns selected services.
     */
    @Override
    public Collection<IService> getServices() {        
        Collection<IService> services = new ArrayList<IService>();
        
        for (int i = 0; i < tree.getItemCount(); i++) {
            TreeItem parentItem = tree.getItem(i);
            
            /**
             * only check children if parent is checked
             * or grayed (which means that not all children are checked)
             */
            if (parentItem.getChecked() || parentItem.getGrayed()) {
                addItemData(services, parentItem.getData());
                
                for (int j = 0; j < parentItem.getItemCount(); j++) {
                    TreeItem childItem = parentItem.getItem(j);
                    
                    if (childItem.getChecked()) {
                        addItemData(services, childItem.getData());
                    }                    
                }
                
            }
        }        
        
        return services;
    }
    
    private void addItemData(Collection<IService> services, Object data) {
        if (data instanceof WMTWizardTreeItemData) {
            WMTWizardTreeItemData itemData = (WMTWizardTreeItemData) data;
            
            IService service = itemData.getService();
            
            if (service != null) {
                services.add(service);
            }
        }
    }
    //endregion
    
    //region Adding Services to the Tree-View
    /**
     * Creates a service from a given WMTSource class,
     * adds this service to a new TreeItem as data object
     * and sets the name for the TreeItem 
     *
     * @param treeItem The parent TreeItem.
     * @param sourceClass The class for which the service should be created.
     */
    private void addWMTSourceToTree(TreeItem treeItem, Class<? extends WMTSource> sourceClass,
            WMTWizardControl controlFactory) {
        TreeItem newTreeItem = new TreeItem(treeItem, SWT.NONE);
        
        WMTService service = serviceExtension.createService(sourceClass);
        
        newTreeItem.setText(service.getName());
        
        WMTWizardTreeItemData data = new WMTWizardTreeItemData(service, controlFactory);
        newTreeItem.setData(data);
        //newTreeItem.setData(service);
    }
    //endregion
    
    public void createControl(Composite parent) {
        // only when this is called for the first time
        if (tree != null && !tree.isDisposed()) return;
            
        childControl = new Composite(parent, SWT.NONE);
        final Composite composite = childControl;
        composite.setLayout(new RowLayout());
        setControl(composite); 
        
        //region Create tree component
        tree = new Tree(composite, SWT.BORDER | SWT.CHECK);
        tree.setLayoutData(new RowData(200, 442));
        tree.addListener(SWT.Selection, new org.eclipse.swt.widgets.Listener(){
            public void handleEvent(Event event) {
                TreeItem item = (TreeItem) event.item;
                
                displayInfoControl(item);
                
                if (event.detail == SWT.CHECK) {
                    // Check child items
                    boolean checked = item.getChecked();
                    checkItems(item, checked);
                    checkPath(item.getParentItem(), checked, false);
                    
                    // now update the buttons
                    if (noItemChecked()) {
                        setPageComplete(false); 
                    } else {
                        setPageComplete(true);
                    }
                    getWizard().getContainer().updateButtons();
                }
            }
        });
        
        infoBox = new Composite(composite, SWT.NONE);
        stackLayoutInfoBox = new StackLayout();
        infoBox.setLayout(stackLayoutInfoBox);
        infoBox.setLayoutData(new RowData(430, 470));
        //endregion
        
        //region Add OpenStreeMap services
        TreeItem osm = new TreeItem(tree, SWT.NONE);
        osm.setText(OSMSource.NAME);
        
        OSMControl osmControlFactory = new OSMControl();
        addWMTSourceToTree(osm, OSMMapnikSource.class, osmControlFactory);
        addWMTSourceToTree(osm, OSMOsmarenderSource.class, osmControlFactory);
        addWMTSourceToTree(osm, OSMCycleMapSource.class, osmControlFactory);
        
        OSMCloudMadeControl osmCloudMadeControlFactory = new OSMCloudMadeControl();
        TreeItem cloudMadeTreeItem = new TreeItem(osm, SWT.NONE);        
        cloudMadeTreeItem.setText(OSMCloudMadeSource.NAME);
        WMTWizardTreeItemData dataCloudMade = new WMTWizardTreeItemData(null, osmCloudMadeControlFactory);
        cloudMadeTreeItem.setData(dataCloudMade);
        
        CSControl osmCSControlFactory = new CSControl();
        TreeItem csTreeItem = new TreeItem(osm, SWT.NONE);        
        csTreeItem.setText(Messages.Wizard_CS_Title);
        WMTWizardTreeItemData dataCS = new WMTWizardTreeItemData(null, osmCSControlFactory);
        csTreeItem.setData(dataCS);

        osm.setExpanded(true);
        //endregion
        
        //region Add MapQuest services
        MQControl mqControlFactory = new MQControl();
        TreeItem mq = new TreeItem(tree, SWT.NONE);
        mq.setText(MQSource.NAME);

        addWMTSourceToTree(mq, MQSource.class, mqControlFactory);

        mq.setExpanded(true);
        //endregion
        
        //region Add NASA WorldWind example
        TreeItem ww = new TreeItem(tree, SWT.NONE);
        ww.setText(Messages.Wizard_Ww_Example_Title);

        WWControl wwControlFactory = new WWControl();
        TreeItem wwTreeItem = new TreeItem(ww, SWT.NONE);        
        wwTreeItem.setText(Messages.Wizard_Ww_Example_Demis_Title);
        WMTWizardTreeItemData dataWW = new WMTWizardTreeItemData(null, wwControlFactory);
        wwTreeItem.setData(dataWW);
        
        ww.setExpanded(true);
        //endregion
        
        //region Add NASA services
        TreeItem nasa = new TreeItem(tree, SWT.NONE);
        nasa.setText(NASASource.NAME);
        
        NASASourceManager nasaManager = NASASourceManager.getInstance();
        
        nasaManager.buildWizardTree(nasa);

        nasa.setExpanded(true);
        //endregion
        
        // Enable first service for usability reasons 
        osm.getItems()[0].setChecked(true);
        tree.setSelection(osm.getItems()[0]);
        displayInfoControl(osm.getItems()[0]);
        osm.setChecked(true);
        osm.setGrayed(true);
        //endregion


        composite.pack();

      }
    
    //region GUI helper methods    
    private void displayInfoControl(TreeItem item) {
        if (item.getData() != null && (item.getData() instanceof WMTWizardTreeItemData)) {
            WMTWizardTreeItemData itemData = (WMTWizardTreeItemData) item.getData();
            
            stackLayoutInfoBox.topControl = itemData.getControlFactory().getControl(infoBox);
            infoBox.layout();
            childControl.pack();
         }
    }
    
    /**
     * Loops the tree and counts checked items
     * 
     * @return (selectedItemCount <= 0)
     */
    private boolean noItemChecked() {
        int selectedItemCount = 0;
        
        for (int i = 0; i < tree.getItemCount(); i++) {
            TreeItem parentItem = tree.getItem(i);
            
            if (parentItem.getChecked() || parentItem.getGrayed()) {
                for (int j = 0; j < parentItem.getItemCount(); j++) {
                    TreeItem childItem = parentItem.getItem(j);
                    
                    if (childItem.getChecked()) {
                        selectedItemCount++;
                    }                    
                }                
            }
        }           
            
        return selectedItemCount <= 0;
    }
    
    //region Check children when parent is checked
    /**
     * GUI: helper method for Tree
     * 
     * (Un-)checks all children of an item recursive.
     * 
     * http://dev.eclipse.org/viewcvs/index.cgi/org.eclipse.swt.snippets/src/org/eclipse/swt/snippets/Snippet274.java?view=co
     *
     * @param item
     * @param checked
     * @param grayed
     */
    private void checkPath(TreeItem item, boolean checked, boolean grayed) {
        if (item == null) return;
        if (grayed) {
            checked = true;
        } else {
            int index = 0;
            TreeItem[] items = item.getItems();
            while (index < items.length) {
                TreeItem child = items[index];
                if (child.getGrayed() || checked != child.getChecked()) {
                    checked = grayed = true;
                    break;
                }
                index++;
            }
        }
        item.setChecked(checked);
        item.setGrayed(grayed);
        checkPath(item.getParentItem(), checked, grayed);
    }

    /**
     * GUI: helper method for Tree
     * 
     * (Un-)checks all children of an item recursive.
     * 
     * http://dev.eclipse.org/viewcvs/index.cgi/org.eclipse.swt.snippets/src/org/eclipse/swt/snippets/Snippet274.java?view=co
     *
     * @param item
     * @param checked
     */
    private void checkItems(TreeItem item, boolean checked) {
        item.setGrayed(false);
        item.setChecked(checked);
        TreeItem[] items = item.getItems();
        for (int i = 0; i < items.length; i++) {
            checkItems(items[i], checked);
        }
    }
    //endregion
    //endregion
    
    void close() {
        if( getContainer()!=null && getContainer().getShell()!=null && !getContainer().getShell().isDisposed() ){
            getContainer().getShell().close();
        }
    }
}

