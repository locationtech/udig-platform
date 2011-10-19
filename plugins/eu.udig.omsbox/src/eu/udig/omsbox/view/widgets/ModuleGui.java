/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Library General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any
 * later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Library General Public License
 * along with this library; if not, write to the Free Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package eu.udig.omsbox.view.widgets;

import java.util.ArrayList;
import java.util.List;

import net.miginfocom.swt.MigLayout;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import eu.udig.omsbox.OmsBoxPlugin;
import eu.udig.omsbox.core.FieldData;
import eu.udig.omsbox.core.ModuleDescription;
import eu.udig.omsbox.utils.OmsBoxUtils;

/**
 * A class representing the main tabbed component gui.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class ModuleGui {
    private final ModuleDescription mainModuleDescription;

    private ModuleGuiFactory formGuiFactory = new ModuleGuiFactory();

    private List<ModuleGuiElement> modulesOuputGuiList;

    private List<ModuleGuiElement> modulesInputGuiList;

    private boolean hideComplex;

    public ModuleGui( ModuleDescription mainModuleDescription ) {
        this.mainModuleDescription = mainModuleDescription;
    }

    @SuppressWarnings("nls")
    public Control makeGui( Composite parent, boolean hideComplex ) {
        this.hideComplex = hideComplex;

        modulesInputGuiList = new ArrayList<ModuleGuiElement>();
        modulesOuputGuiList = new ArrayList<ModuleGuiElement>();

        // parent has FillLayout
        // create the tab folder
        final CTabFolder folder = new CTabFolder(parent, SWT.BOTTOM);
        folder.setUnselectedCloseVisible(false);
        folder.setLayout(new FillLayout());
        folder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        // for every Tab object create a tab
        String layoutConstraint = "insets 20 20 20 20, fillx";

        makeInputTab(folder, layoutConstraint);
        makeOutputTab(folder, layoutConstraint);
        makeDescription(folder, layoutConstraint);

        return folder;
    }

    private void makeDescription( final CTabFolder folder, String layoutConstraint ) {
        // the tabitem
        CTabItem tab = new CTabItem(folder, SWT.NONE);
        tab.setText("description");

        try {
            Browser browser = new Browser(folder, SWT.NONE);
            GridData layoutData = new GridData(GridData.FILL_BOTH);
            browser.setLayoutData(layoutData);

            String className = mainModuleDescription.getClassName();
            String moduleDocumentationPath = OmsBoxUtils.getModuleDocumentationPath(className);
            browser.setUrl("file:" + moduleDocumentationPath);
            tab.setControl(browser);
        } catch (SWTError e) {
            e.printStackTrace();
            
            Label problemLabel = new Label(folder, SWT.NONE);
            problemLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            problemLabel.setText("An error occurred while loading the documentation.");
            tab.setControl(problemLabel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void makeInputTab( final CTabFolder folder, String layoutConstraint ) {
        List<FieldData> inputsList = mainModuleDescription.getInputsList();
        if (inputsList.size() == 0) {
            return;
        }
        if (hideComplex) {
            // if all are complex we do not want te tab
            boolean oneNotComplex = false;
            for( FieldData fieldData : inputsList ) {
                if (fieldData.isSimpleType()) {
                    oneNotComplex = true;
                }
            }
            if (!oneNotComplex) {
                return;
            }
        }

        // the tabitem
        CTabItem tab = new CTabItem(folder, SWT.NONE);
        tab.setText("inputs");
        folder.setSelection(tab);

        // we want the content to scroll
        final ScrolledComposite scroller = new ScrolledComposite(folder, SWT.V_SCROLL);
        scroller.setLayout(new FillLayout());

        // the actual content of the tab
        Composite tabComposite = new Composite(scroller, SWT.NONE);
        layoutConstraint = layoutConstraint + ", gapy 15";
        tabComposite.setLayout(new MigLayout(layoutConstraint, ""));

        // which goes as content to the scrolled composite
        scroller.setContent(tabComposite);
        scroller.setExpandVertical(true);
        scroller.setExpandHorizontal(true);

        // the scroller gets the control of the tab item
        tab.setControl(scroller);

        int[] row = new int[]{0};
        for( int j = 0; j < inputsList.size(); j++ ) {
            FieldData inputData = inputsList.get(j);
            if (hideComplex && !inputData.isSimpleType() && !OmsBoxUtils.isFieldExceptional(inputData)) {
                continue;
            }

            // remove region related widgets, if the user chose to not have them.
            if (inputData.isProcessingRegionRelated() && OmsBoxPlugin.getDefault().doIgnoreProcessingRegion()) {
                continue;
            }

            List<ModuleGuiElement> inputList = formGuiFactory.createInputGui(inputData, row);
            for( ModuleGuiElement moduleGuiElement : inputList ) {
                moduleGuiElement.makeGui(tabComposite);
            }
            modulesInputGuiList.addAll(inputList);

            row[0] = row[0] + 1;
        }

        Point size = folder.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        scroller.setMinHeight(size.y);
        scroller.setMinWidth(size.x);
    }

    private void makeOutputTab( final CTabFolder folder, String layoutConstraint ) {
        List<FieldData> outputsList = mainModuleDescription.getOutputsList();
        if (outputsList.size() == 0) {
            return;
        }

        // if all are complex we do not want te tab
        boolean atLeastOneIsSimple = false;
        boolean atLeastOneIsSimpleArray = false;
        boolean atLeastOneIsComplex = false;
        for( FieldData fieldData : outputsList ) {
            if (fieldData.isSimpleType()) {
                atLeastOneIsSimple = true;
            } else if (fieldData.isSimpleArrayType()) {
                atLeastOneIsSimpleArray = true;
            } else {
                atLeastOneIsComplex = true;
            }
        }

        /*
         * if we hide the complex and there i sno simple, 
         * do not show the tab.
         */
        if (hideComplex && !atLeastOneIsSimple && atLeastOneIsComplex) {
            return;
        }
        /*
         * if we have only simple ones or simple arrays 
         * do not show the tab.
         */
        if ((atLeastOneIsSimple || atLeastOneIsSimpleArray) && !atLeastOneIsComplex) {
            return;
        }

        // the tabitem
        CTabItem tab = new CTabItem(folder, SWT.NONE);
        tab.setText("outputs");

        // we want the content to scroll
        final ScrolledComposite scroller = new ScrolledComposite(folder, SWT.V_SCROLL);
        scroller.setLayout(new FillLayout());

        // the actual content of the tab
        Composite tabComposite = new Composite(scroller, SWT.NONE);
        tabComposite.setLayout(new MigLayout(layoutConstraint, ""));

        // which goes as content to the scrolled composite
        scroller.setContent(tabComposite);
        scroller.setExpandVertical(true);
        scroller.setExpandHorizontal(true);

        // the scroller gets the control of the tab item
        tab.setControl(scroller);

        int[] row = new int[]{0};
        for( int j = 0; j < outputsList.size(); j++ ) {
            FieldData outputData = outputsList.get(j);
            if (hideComplex && !outputData.isSimpleType()) {
                continue;
            }

            // remove region related widgets, if the user chose to not have them.
            if (outputData.isProcessingRegionRelated() && OmsBoxPlugin.getDefault().doIgnoreProcessingRegion()) {
                continue;
            }

            List<ModuleGuiElement> ouputList = formGuiFactory.createOutputGui(outputData, row);
            for( ModuleGuiElement moduleGuiElement : ouputList ) {
                moduleGuiElement.makeGui(tabComposite);
            }
            modulesOuputGuiList.addAll(ouputList);
            row[0] = row[0] + 1;
        }

        Point size = folder.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        scroller.setMinHeight(size.y);
        scroller.setMinWidth(size.x);
    }
    public ModuleDescription getModuleDescription() {
        return mainModuleDescription;
    }

    public List<ModuleGuiElement> getModulesInputGuiList() {
        return modulesInputGuiList;
    }

    public List<ModuleGuiElement> getModulesOuputGuiList() {
        return modulesOuputGuiList;
    }
}
