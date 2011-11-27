/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.project.ui.tool.options;

import java.awt.SystemColor;

import net.refractions.udig.project.ui.controls.ScaleRatioLabel;
import net.refractions.udig.project.ui.internal.tool.display.ToolProxy;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.StatusLineLayoutData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;

/**
 * Adds a button to the status bar that allows quick access to the active modal tool preference
 * page, if no preference page is defined it will open the parent tools preference page.
 * 
 * @see tool.exsd
 * @author leviputna
 * @since 1.2.0
 */
public class PreferencesShortcutToolOptionsContributionItem extends ContributionItem {
    final IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getService(
            IHandlerService.class);
    final ICommandService commandService = (ICommandService) PlatformUI.getWorkbench().getService(
            ICommandService.class);

    final String defultPreferencePage = "net.refractions.udig.tools.edit.preferences";

    //private ToolBar toolBar;
    private Label icon;
    private ToolProxy activeTool;
    private Link link;

    private Listener listener = new Listener(){
        @Override
        public void handleEvent( Event event ) {
            try {
                if (activeTool == null) {
                    return; // we must be disposed or something
                }
                String page;
                if (activeTool.getPreferencePageId() == null) {
                    page = defultPreferencePage;
                } else {
                    page = activeTool.getPreferencePageId();
                }
                final ParameterizedCommand command = commandService
                        .deserialize("org.eclipse.ui.window.preferences(preferencePageId=" + page
                                + ")");

                handlerService.executeCommand(command, null);
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
    };
    private Composite group;

    @Override
    public void fill( Composite parent ) {
        Label separator = new Label(parent, SWT.SEPARATOR);
        
        group = new Composite(parent, SWT.NO_SCROLL|SWT.NO_TRIM );
        // group.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_BLUE));
        StatusLineLayoutData statusLineLayoutData = new StatusLineLayoutData();
        statusLineLayoutData.widthHint = SWT.DEFAULT;
        statusLineLayoutData.heightHint = ScaleRatioLabel.STATUS_LINE_HEIGHT;
        group.setLayoutData(statusLineLayoutData);

        GridLayout gridLayout = new GridLayout(2,false);
        gridLayout.horizontalSpacing=0;
        gridLayout.marginTop=0;
        gridLayout.marginBottom=0;
        gridLayout.marginHeight=0;
        gridLayout.marginLeft=0;
        gridLayout.marginRight=0;
        gridLayout.marginWidth=0;
        group.setLayout(gridLayout);
        
        icon = new Label( group, SWT.HORIZONTAL );
        icon.addListener(SWT.Selection, listener);
        GridData layoutData  = new GridData(SWT.RIGHT,SWT.CENTER,false,true);
        layoutData.widthHint=16;
        layoutData.heightHint=16;
//        GridData layoutData = data;
//        layoutData.heightHint=ScaleRatioLabel.STATUS_LINE_HEIGHT;
        layoutData.horizontalIndent=0;
//        layoutData.verticalIndent=0;        
        icon.setLayoutData( layoutData);
        
        link = new Link(group, SWT.CENTER );
        link.addListener(SWT.Selection,listener);
        layoutData  = new GridData(SWT.RIGHT,SWT.CENTER,false,true);
//        layoutData.heightHint=ScaleRatioLabel.STATUS_LINE_HEIGHT;
        layoutData.horizontalIndent=0;
//        layoutData.verticalIndent=0;        
        link.setLayoutData(layoutData);
        refreshButton();
    }
    private void refreshButton() {
        if( activeTool != null ){
            if( icon != null && !icon.isDisposed() ){
                icon.setImage(activeTool.getImage());
                icon.setToolTipText(activeTool.getToolTipText());
            }
            if( link != null && !link.isDisposed() ){
                String preferencePageId = activeTool.getPreferencePageId();
                if( preferencePageId == null ){
                    preferencePageId = defultPreferencePage;
                }
                String text = "<a href=\""+preferencePageId+"\">"+activeTool.getName()+"</a>";
                link.setText(text);
                link.setToolTipText(activeTool.getToolTipText());
            }
        }
    }
    /**
     * Update the ItemContribution to reflect the current ModalTool.
     * 
     * @param modalToolProxy
     */
    public void update( ToolProxy modalToolProxy ) {
        // remember the active tool for the preference page lookup
        activeTool = modalToolProxy;
        refreshButton();
    }

    public boolean isDisposed() {
        return group == null || group.isDisposed();
    }

    @Override
    public void dispose() {
        super.dispose();
        if( icon != null ){
            icon.dispose();
            icon = null;
        }
        if( link != null ){
            link.dispose();
            link = null;
        }
        if( group != null ){
            group.dispose();
            group = null;
        }
        activeTool = null;
    }

}
