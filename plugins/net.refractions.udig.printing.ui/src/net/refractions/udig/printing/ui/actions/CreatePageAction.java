/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.printing.ui.actions;

import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import net.refractions.udig.printing.model.Box;
import net.refractions.udig.printing.model.ModelFactory;
import net.refractions.udig.printing.model.Page;
import net.refractions.udig.printing.ui.Template;
import net.refractions.udig.printing.ui.TemplateFactory;
import net.refractions.udig.printing.ui.internal.BasicTemplateFactory;
import net.refractions.udig.printing.ui.internal.Messages;
import net.refractions.udig.printing.ui.internal.PrintingPlugin;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.Project;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.internal.MapEditor;
import net.refractions.udig.project.ui.internal.MapEditorInput;
import net.refractions.udig.project.ui.internal.MapEditorWithPalette;
import net.refractions.udig.project.ui.internal.MapPart;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ListDialog;

/**
 * Creates a Page using the current map
 * 
 * @author Richard Gould
 * 
 * @version 1.3.0
 */
public class CreatePageAction implements IEditorActionDelegate {

    public void run( IAction action ) {
        IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                .getActivePage().getActiveEditor();
        IEditorInput input = activeEditor.getEditorInput();
        if (!(activeEditor instanceof MapEditor) && !(input instanceof MapEditorInput)) {
            MessageDialog.openError(Display.getDefault().getActiveShell(),
                    Messages.CreatePageAction_printError_title,
                    Messages.CreatePageAction_printError_text);
        }

        MapPart mapEditor = (MapPart) activeEditor;
        
        Template template = getPageTemplate();

        if (template == null) {
            return;
        }

        Map map = null;
        Project project = null;

        Map oldMap = (Map) ((MapEditorInput) input).getProjectElement();
        project = oldMap.getProjectInternal();
        try {
            map = (Map) EcoreUtil.copy(oldMap);   
        }
        catch( Throwable t ){
            // unable to copy map?
            t.printStackTrace();
            return;
        }

        project.getElementsInternal().add(map);

        //Point size = //mapEditor.getComposite().getSize();
        Point partSize;
        if( mapEditor instanceof MapEditor ){
        	MapEditor part = (MapEditor) mapEditor;
        	partSize = part.getComposite().getSize();
        }
        else if( mapEditor instanceof MapEditorWithPalette){
        	MapEditorWithPalette part = (MapEditorWithPalette) mapEditor;
        	partSize = part.getComposite().getSize();
        }
        else {
        	//java.awt.Dimension size = map.getRenderManager().getMapDisplay().getDisplaySize();
        	//Point partSize = new Point(size.width,size.height);
        	partSize = new Point(500,500);
        }
        Page page = createPage(template, map, project, partSize );

        ApplicationGIS.openProjectElement(page, false);
    }

    private Page createPage( Template template, Map map, Project project, Point partSize ) {
        int width = 800;
        int height = 600;
        if (partSize != null) {
            width = partSize.x;
            height = partSize.y;
        }else{
            PageFormat pageFormat = PrinterJob.getPrinterJob().defaultPage();
            width = new Double(pageFormat.getImageableWidth()).intValue();
            height = new Double(pageFormat.getImageableHeight()).intValue();
        }

        Page page = ModelFactory.eINSTANCE.createPage();

        page.setSize(new Dimension(width, height));

        MessageFormat formatter = new MessageFormat("{0} - " + template.getAbbreviation(), Locale
                .getDefault());
        if (page.getName() == null || page.getName().length() == 0) {
            page.setName(formatter.format(new Object[]{map.getName()}));
        }

        page.setProjectInternal(project);
        template.init(page, map);
        Iterator<Box> iter = template.iterator();
        while( iter.hasNext() ) {
            page.getBoxes().add(iter.next());
        }
        return page;
    }

    private Template getPageTemplate() {
        final java.util.Map<String, TemplateFactory> templateFactories = PrintingPlugin
                .getDefault().getTemplateFactories();

        // TODO move to a preference initializer
        PrintingPlugin.getDefault().getPluginPreferences().setValue(
                PrintingPlugin.PREF_DEFAULT_TEMPLATE,
                "net.refractions.udig.printing.ui.internal.BasicTemplate"); //$NON-NLS-1$

        String defaultTemplate = PrintingPlugin.getDefault().getPluginPreferences().getString(
                PrintingPlugin.PREF_DEFAULT_TEMPLATE);

        ListDialog dialog = createTemplateChooserDialog(templateFactories);

        TemplateFactory templateFactory = (TemplateFactory) PrintingPlugin.getDefault()
                .getTemplateFactories().get(defaultTemplate);

        dialog.setInitialSelections(new Object[]{templateFactory});
        int result = dialog.open();
        if (result == Window.CANCEL || dialog.getResult().length == 0) {
            return null;
        }

        Template template = null;

        templateFactory = ((TemplateFactory) dialog.getResult()[0]);

        if (templateFactory == null) {
            PrintingPlugin.log(Messages.CreatePageAction_error_cannotFindDefaultTemplate, null);

            TemplateFactory firstAvailable = (TemplateFactory) templateFactories.values()
                    .iterator().next();
            if (firstAvailable == null) {
                PrintingPlugin.log(
                        "Unable to locate any templates, resorting to hard coded default.", null); //$NON-NLS-1$
                template = new BasicTemplateFactory().createTemplate();
            } else {
                template = firstAvailable.createTemplate();
            }
        } else {
            template = templateFactory.createTemplate();
        }
        return template;
    }

    /**
     *
     * @param templateFactories
     * @return
     */
    private ListDialog createTemplateChooserDialog(
            final java.util.Map<String, TemplateFactory> templateFactories ) {
        ListDialog dialog = new ListDialog(Display.getDefault().getActiveShell());
        dialog.setTitle(Messages.CreatePageAction_dialog_title);
        dialog.setMessage(Messages.CreatePageAction_dialog_message);
        
        Set<String> keySet = templateFactories.keySet();
        List<String> keyList = new ArrayList<String>();
        keyList.addAll(keySet);
        Collections.sort(keyList);
        List<TemplateFactory> valuesList = new ArrayList<TemplateFactory>();
        for( String key : keyList ) {
            valuesList.add(templateFactories.get(key));
        }
        
        dialog.setInput(valuesList);
        ArrayContentProvider provider = new ArrayContentProvider();
        dialog.setContentProvider(provider);

        ILabelProvider labelProvider = new LabelProvider(){
            public String getText( Object element ) {
                return ((TemplateFactory) element).getName();
            }
        };
        dialog.setLabelProvider(labelProvider);
        return dialog;
    }

    public void setActiveEditor( IAction action, IEditorPart targetEditor ) {
    }

    public void selectionChanged( IAction action, ISelection selection ) {
    }
}
