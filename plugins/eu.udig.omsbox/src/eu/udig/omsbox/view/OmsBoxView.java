/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.udig.omsbox.view;

import i18n.omsbox.Messages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.part.ViewPart;
import org.joda.time.DateTime;

import eu.udig.omsbox.OmsBoxPlugin;
import eu.udig.omsbox.core.ModuleDescription;
import eu.udig.omsbox.core.ModuleDescription.Status;
import eu.udig.omsbox.core.OmsModulesManager;
import eu.udig.omsbox.core.ScriptHandler;
import eu.udig.omsbox.utils.ImageCache;
import eu.udig.omsbox.utils.OmsBoxConstants;
import eu.udig.omsbox.utils.ViewerFolder;
import eu.udig.omsbox.utils.ViewerModule;
import eu.udig.omsbox.view.widgets.ModuleGui;

/**
 * The database view.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class OmsBoxView extends ViewPart {

    public static final String ID = "eu.udig.omsbox.view.DatabaseView"; //$NON-NLS-1$

    private Composite modulesGuiComposite;
    private StackLayout modulesGuiStackLayout;
    private TreeViewer modulesViewer;

    private ModuleGui currentSelectedModuleGui;

    private HashMap<String, Control> module2GuiMap = new HashMap<String, Control>();

    public OmsBoxView() {

    }

    @Override
    public void createPartControl( Composite parent ) {
        module2GuiMap.clear();

        Composite mainComposite = new Composite(parent, SWT.None);
        GridLayout mainLayout = new GridLayout(1, false);
        mainLayout.marginWidth = 5;
        mainLayout.marginHeight = 5;
        mainComposite.setLayout(mainLayout);
        mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));

        Composite modulesComposite = new Composite(mainComposite, SWT.NONE);
        GridData modulesGroupGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        modulesComposite.setLayoutData(modulesGroupGD);
        GridLayout modulesCompositeLayout = new GridLayout(3, true);
        modulesCompositeLayout.marginWidth = 0;
        modulesComposite.setLayout(modulesCompositeLayout);
        
        Label modulesLabel = new Label(modulesComposite, SWT.NONE);
        modulesLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        modulesLabel.setText(Messages.OmsBoxView_Modules);
        Label dummyLabel = new Label(modulesComposite, SWT.NONE);
        GridData dummyLabelGD = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
        dummyLabelGD.horizontalSpan = 2;
        dummyLabel.setLayoutData(dummyLabelGD);
        
        Composite modulesListComposite = new Composite(modulesComposite, SWT.NONE);
        modulesListComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        modulesListComposite.setLayout(new GridLayout(1, false));

        HashMap<String, List<ModuleDescription>> availableModules = OmsModulesManager.getInstance().browseModules(false);
        modulesViewer = createTreeViewer(modulesListComposite);
        List<ViewerFolder> viewerFolders = ViewerFolder.hashmap2ViewerFolders(availableModules);
        modulesViewer.setInput(viewerFolders);
        addFilterButtons(modulesListComposite);
        addQuickSettings(modulesListComposite);

        modulesGuiComposite = new Composite(modulesComposite, SWT.BORDER);
        modulesGuiStackLayout = new StackLayout();
        modulesGuiComposite.setLayout(modulesGuiStackLayout);
        GridData modulesGuiCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        modulesGuiCompositeGD.horizontalSpan = 2;
        modulesGuiComposite.setLayoutData(modulesGuiCompositeGD);
        Label l = new Label(modulesGuiComposite, SWT.SHADOW_ETCHED_IN);
        l.setText(Messages.OmsBoxView_No_module_selected);
        modulesGuiStackLayout.topControl = l;

    }

    private TreeViewer createTreeViewer( Composite modulesComposite ) {

        PatternFilter patternFilter = new PatternFilter();
        final FilteredTree filter = new FilteredTree(modulesComposite, SWT.SINGLE | SWT.BORDER, patternFilter, true);
        final TreeViewer modulesViewer = filter.getViewer();

        Control control = modulesViewer.getControl();
        GridData controlGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        control.setLayoutData(controlGD);
        modulesViewer.setContentProvider(new ITreeContentProvider(){
            public Object[] getElements( Object inputElement ) {
                return getChildren(inputElement);
            }
            public Object[] getChildren( Object parentElement ) {
                if (parentElement instanceof List< ? >) {
                    List< ? > list = (List< ? >) parentElement;
                    Object[] array = list.toArray();
                    return array;
                }
                if (parentElement instanceof ViewerFolder) {
                    ViewerFolder folder = (ViewerFolder) parentElement;
                    List<ViewerFolder> subFolders = folder.getSubFolders();
                    List<ViewerModule> modules = folder.getModules();
                    List<Object> allObjs = new ArrayList<Object>();
                    allObjs.addAll(subFolders);
                    allObjs.addAll(modules);

                    return allObjs.toArray();
                }
                return new Object[0];
            }
            public Object getParent( Object element ) {
                if (element instanceof ViewerFolder) {
                    ViewerFolder folder = (ViewerFolder) element;
                    return folder.getParentFolder();
                }
                if (element instanceof ViewerModule) {
                    ViewerModule module = (ViewerModule) element;
                    return module.getParentFolder();
                }
                return null;
            }

            public boolean hasChildren( Object element ) {
                return getChildren(element).length > 0;
            }

            public void dispose() {
            }
            public void inputChanged( Viewer viewer, Object oldInput, Object newInput ) {
            }

        });

        modulesViewer.setLabelProvider(new LabelProvider(){
            public Image getImage( Object element ) {
                if (element instanceof ViewerFolder) {
                    return ImageCache.getInstance().getImage(ImageCache.CATEGORY);
                }
                if (element instanceof ViewerModule) {
                    ModuleDescription md = ((ViewerModule) element).getModuleDescription();
                    Status status = md.getStatus();
                    if (status == Status.experimental) {
                        return ImageCache.getInstance().getImage(ImageCache.MODULEEXP);
                    } else {
                        return ImageCache.getInstance().getImage(ImageCache.MODULE);
                    }
                }
                return null;
            }

            public String getText( Object element ) {
                if (element instanceof ViewerFolder) {
                    ViewerFolder categoryFolder = (ViewerFolder) element;
                    return categoryFolder.getName();
                }
                if (element instanceof ViewerModule) {
                    ModuleDescription module = ((ViewerModule) element).getModuleDescription();
                    return module.getName().replaceAll("\\_\\_", ".");
                }
                return ""; //$NON-NLS-1$
            }
        });

        modulesViewer.addSelectionChangedListener(new ISelectionChangedListener(){

            public void selectionChanged( SelectionChangedEvent event ) {
                if (!(event.getSelection() instanceof IStructuredSelection)) {
                    return;
                }
                IStructuredSelection sel = (IStructuredSelection) event.getSelection();

                Object selectedItem = sel.getFirstElement();
                if (selectedItem == null) {
                    // unselected, show empty panel
                    putUnselected();
                    return;
                }

                if (selectedItem instanceof ViewerModule) {
                    ModuleDescription currentSelectedModule = ((ViewerModule) selectedItem).getModuleDescription();
                    currentSelectedModuleGui = new ModuleGui(currentSelectedModule);

                    Control control = currentSelectedModuleGui.makeGui(modulesGuiComposite, false);

                    // Label dummyLabel = new Label(modulesGuiComposite, SWT.NONE);
                    // dummyLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
                    // false));
                    // dummyLabel.setText(currentSelectedModule.toString());

                    modulesGuiStackLayout.topControl = control;
                    modulesGuiComposite.layout(true);
                } else {
                    putUnselected();
                }
            }

        });

        return modulesViewer;
    }

    private void addFilterButtons( Composite modulesComposite ) {
        Button filterActive = new Button(modulesComposite, SWT.CHECK);
        filterActive.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        filterActive.setText(Messages.OmsBoxView_Load_Experimental);
        final ExperimentalFilter activeFilter = new ExperimentalFilter();
        modulesViewer.addFilter(activeFilter);
        filterActive.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected( SelectionEvent event ) {
                if (((Button) event.widget).getSelection())
                    modulesViewer.removeFilter(activeFilter);
                else
                    modulesViewer.addFilter(activeFilter);
            }
        });
    }

    private void addQuickSettings( Composite modulesListComposite ) {
        Group quickSettingsGroup = new Group(modulesListComposite, SWT.NONE);
        quickSettingsGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        quickSettingsGroup.setLayout(new GridLayout(2, true));
        quickSettingsGroup.setText("Process settings");

        Label heapLabel = new Label(quickSettingsGroup, SWT.NONE);
        heapLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        heapLabel.setText("Memory [MB]");
        final Combo heapCombo = new Combo(quickSettingsGroup, SWT.DROP_DOWN);
        heapCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        heapCombo.setItems(OmsBoxConstants.HEAPLEVELS);
        int savedHeapLevel = OmsBoxPlugin.getDefault().retrieveSavedHeap();
        for( int i = 0; i < OmsBoxConstants.HEAPLEVELS.length; i++ ) {
            if (OmsBoxConstants.HEAPLEVELS[i].equals(String.valueOf(savedHeapLevel))) {
                heapCombo.select(i);
                break;
            }
        }
        heapCombo.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e ) {
                String item = heapCombo.getText();
                OmsBoxPlugin.getDefault().saveHeap(Integer.parseInt(item));
            }
        });
        heapCombo.addModifyListener(new ModifyListener(){
            public void modifyText( ModifyEvent e ) {
                String item = heapCombo.getText();
                try {
                    Integer.parseInt(item);
                } catch (Exception ex) {
                    return;
                }
                if (item.length() > 0) {
                    OmsBoxPlugin.getDefault().saveHeap(Integer.parseInt(item));
                }
            }
        });
        
        Label logLabel = new Label(quickSettingsGroup, SWT.NONE);
        logLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        logLabel.setText("Debug info");
        final Combo logCombo = new Combo(quickSettingsGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
        logCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        logCombo.setItems(OmsBoxConstants.LOGLEVELS_GUI);
        String savedLogLevel = OmsBoxPlugin.getDefault().retrieveSavedLogLevel();
        for( int i = 0; i < OmsBoxConstants.LOGLEVELS_GUI.length; i++ ) {
            if (OmsBoxConstants.LOGLEVELS_GUI[i].equals(savedLogLevel)) {
                logCombo.select(i);
                break;
            }
        }
        logCombo.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e ) {
                String item = logCombo.getText();
                OmsBoxPlugin.getDefault().saveLogLevel(item);
            }
        });

    }

    @Override
    public void setFocus() {
    }

    private static class ExperimentalFilter extends ViewerFilter {
        public boolean select( Viewer arg0, Object arg1, Object arg2 ) {
            if (arg2 instanceof ViewerFolder) {
                return true;
            }
            if (arg2 instanceof ViewerModule) {
                ModuleDescription md = ((ViewerModule) arg2).getModuleDescription();
                if (md.getStatus() == ModuleDescription.Status.experimental) {
                    return false;
                }
                return true;
            }
            return false;
        }
    }

    /**
     * Resfresh the viewer.
     */
    public void relayout() {
        Display.getDefault().syncExec(new Runnable(){
            public void run() {
                // refresh widgets
                HashMap<String, List<ModuleDescription>> availableModules = OmsModulesManager.getInstance().browseModules(false);
                List<ViewerFolder> viewerFolders = ViewerFolder.hashmap2ViewerFolders(availableModules);
                modulesViewer.setInput(viewerFolders);
            }
        });
    }

    /**
     * Put the properties view to an label that defines no selection.
     */
    public void putUnselected() {
        Label l = new Label(modulesGuiComposite, SWT.SHADOW_ETCHED_IN);
        l.setText(Messages.OmsBoxView_No_module_selected);
        modulesGuiStackLayout.topControl = l;
        modulesGuiComposite.layout(true);
    }

    /**
     * Runs the module currently selected in the gui.
     * 
     * @throws Exception
     */
    public void runSelectedModule() throws Exception {
        ScriptHandler handler = new ScriptHandler();
        String script = handler.genereateScript(currentSelectedModuleGui);
        if (script == null) {
            return;
        }

        String scriptID = currentSelectedModuleGui.getModuleDescription().getName() + " "
                + new DateTime().toString(OmsBoxConstants.dateTimeFormatterYYYYMMDDHHMMSS);
        handler.runModule(scriptID, script);
    }

    /**
     * Generates the script for the module currently selected in the gui.
     * 
     * @return the generated script.
     * @throws Exception 
     */
    public String generateScriptForSelectedModule() throws Exception {
        ScriptHandler handler = new ScriptHandler();
        String script = handler.genereateScript(currentSelectedModuleGui);
        return script;
    }
}
