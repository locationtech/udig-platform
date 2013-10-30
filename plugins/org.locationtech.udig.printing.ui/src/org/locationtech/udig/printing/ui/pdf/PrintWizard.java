/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.printing.ui.pdf;

import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterJob;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.core.internal.Icons;
import net.refractions.udig.printing.model.Box;
import net.refractions.udig.printing.model.ModelFactory;
import net.refractions.udig.printing.model.Page;
import net.refractions.udig.printing.ui.Template;
import net.refractions.udig.printing.ui.TemplateFactory;
import net.refractions.udig.printing.ui.internal.Messages;
import net.refractions.udig.printing.ui.internal.PrintingPlugin;
import net.refractions.udig.printing.ui.internal.TemplatePrintingEngine;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.internal.MapEditorInput;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.opengis.coverage.grid.GridCoverageReader;

import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;

public class PrintWizard extends Wizard implements IExportWizard {
    
    private Map map;
    private PrintWizardPage1 page1;
    
    private static float MARGIN = 36; //inches * points per inch
    
    public PrintWizard(){
        PrintingPlugin plugin = PrintingPlugin.getDefault();
        
        java.util.Map<String, TemplateFactory> templateFactories = plugin.getTemplateFactories();
        setWindowTitle(Messages.PrintWizard_Title);
        
        String key = Icons.WIZBAN +"exportpdf_wiz.gif"; //$NON-NLS-1$
        ImageRegistry imageRegistry = plugin.getImageRegistry();        
        ImageDescriptor image = imageRegistry.getDescriptor( key );
        if( image == null ){
            URL banURL = plugin.getBundle().getResource( "icons/" + key ); //$NON-NLS-1$
            image = ImageDescriptor.createFromURL( banURL );
            imageRegistry.put( key, image );
        }
        
        //get copy of map
        IEditorInput input = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
        .getActiveEditor().getEditorInput();
        map = (Map) ((MapEditorInput) input).getProjectElement();
        
        setDefaultPageImageDescriptor( image );        
        page1 = new PrintWizardPage1(templateFactories);
        addPage(page1);
    }
    
    @Override    
    public boolean canFinish() {
        return page1.isPageComplete();
    }
    
    @Override
    public boolean performFinish() {
    
        Template template = page1.getTemplateFactory().createTemplate();
        boolean showRasters = page1.getRasterEnabled();
        
        //adjust scale        
        double currentViewportScaleDenom = map.getViewportModel().getScaleDenominator();
        if (currentViewportScaleDenom == -1) 
            throw new IllegalStateException("no scale denominator is available from the viewport model"); //$NON-NLS-1$
        
        if (page1.getScaleOption() == PrintWizardPage1.CUSTOM_MAP_SCALE) {
            float customScale = page1.getCustomScale();
            template.setMapScaleHint(customScale);
        } 
        else if (page1.getScaleOption() == PrintWizardPage1.CURRENT_MAP_SCALE) {
           template.setMapScaleHint(currentViewportScaleDenom);            
        }
        else if (page1.getScaleOption() == PrintWizardPage1.ZOOM_TO_SELECTION) {
            template.setZoomToSelectionHint(true);
            template.setMapScaleHint(currentViewportScaleDenom);
        }  
        
        
        final PrinterJob printerJob = PrinterJob.getPrinterJob();
        final PageFormat pageFormat = printerJob.defaultPage();
        
        
        //setup the paper
        
        Paper paper = new Paper();
        Rectangle pageSize = getITextPageSize(page1.getPageSize());
        paper.setSize(pageSize.getWidth(), pageSize.getHeight());
        //paper.setSize(11.7*72, 16.5*72);
        //double imageableWidth = paper.getWidth() - MARGIN*2;
        //double imageableHeight = paper.getHeight() - MARGIN*2;
        paper.setImageableArea(0, 0, paper.getWidth(), paper.getHeight() );
        pageFormat.setPaper(paper);

        
        if (page1.getTemplateFactory().createTemplate().getPreferredOrientation() == Template.ORIENTATION_LANDSCAPE) {
            pageFormat.setOrientation(PageFormat.LANDSCAPE);
        }
        
        final String jobName = map.getName();
        final TemplatePrintingEngine engine = new TemplatePrintingEngine(map, template, showRasters);
        
        printerJob.setPrintable(engine, pageFormat);
       
        
        Job job = new Job(Messages.PrintAction_jobTitle){ 
            protected IStatus run( IProgressMonitor monitor ) {
               
                
                if (printerJob.printDialog()) {
                    try {
                        
                        printerJob.setJobName(jobName);
                        printerJob.print();
                        
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                
                return Status.OK_STATUS;
            }
        };
        
        if( job.isSystem() )
            job.setSystem(false);
        
        job.schedule();
        
        
        
        return true;
    }
    
    /**
     * converts a page size "name" (such as "A3" or "A4" into a 
     * rectangle object that iText will understand.
     */
    private Rectangle getITextPageSize(String pageSizeName) {
        if (pageSizeName.equals("A3")) //$NON-NLS-1$
            return PageSize.A3;
        if (pageSizeName.equals("A4")) //$NON-NLS-1$
            return PageSize.A4;
        if (pageSizeName.equals("Letter")) 
            return PageSize.LETTER;
        throw new IllegalArgumentException(pageSizeName + " is not a supported page size"); //$NON-NLS-1$
    }
    
    protected Page makePage(int width, int height) {
        
        Template template = getTemplate();  
        
        Map mapCopy = null;
        
        //make one copy of the map with no raster layers
        mapCopy = (Map) ApplicationGIS.copyMap(map);
        List<Layer> layersNoRasters = mapCopy.getLayersInternal();
        
        if (!page1.getRasterEnabled()){
            List<Layer> toRemove = new ArrayList<Layer>();
            for (Layer layer : layersNoRasters ) {
                for (IGeoResource resource : layer.getGeoResources()) {
                    if (resource.canResolve(GridCoverageReader.class)) {
                        toRemove.add(layer);
                    }
                }
            }
            layersNoRasters.removeAll(toRemove);
        }
        
        
        //adjust scale        
        if (page1.getScaleOption() == PrintWizardPage1.CUSTOM_MAP_SCALE) {
            float customScale = page1.getCustomScale();
            template.setMapScaleHint(customScale);
        } 
        else if (page1.getScaleOption() == PrintWizardPage1.CURRENT_MAP_SCALE) {
            double currentViewportScaleDenom = map.getViewportModel().getScaleDenominator();
            if (currentViewportScaleDenom == -1) 
                throw new IllegalStateException("no scale denominator is available from the viewport model"); //$NON-NLS-1$
           template.setMapScaleHint(currentViewportScaleDenom);            
        }
        else if (page1.getScaleOption() == PrintWizardPage1.ZOOM_TO_SELECTION) {
            template.setZoomToSelectionHint(true);
        }     
        
        //3. make the page itself 
        Page page = ModelFactory.eINSTANCE.createPage();
        page.setSize(new Dimension(width, height));

        //page name stuff not required, because this page will just get discarded
        MessageFormat formatter = new MessageFormat(Messages.CreatePageAction_newPageName, Locale.getDefault()); 
        if (page.getName() == null || page.getName().length() == 0) {
            page.setName(formatter.format(new Object[] { mapCopy.getName() }));
        }

        template.init(page, mapCopy);
        
        //copy the boxes from the template into the page
        Iterator<Box> iter = template.iterator();        
        while (iter.hasNext()) {
            page.getBoxes().add(iter.next());
        }
        return page;
    }

    
    /**
     * Creates a page based on the template selected in the wizard
     * **Note: this function may swap the width and height if the
     * template prefers different page orientation.
     *
     * @return a page
     */
    protected Page makePage2(float width, float height) {
        
        Template template = getTemplate();  
        
        //2. get *copy* of map
        IEditorInput input = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
        .getActiveEditor().getEditorInput();
        
        //get a copy of the map and the project
        Map mapCopy = null;
 
        Map map = (Map) ((MapEditorInput) input).getProjectElement();
        mapCopy = (Map) ApplicationGIS.copyMap(map);
        
        //adjust scale        
        if (page1.getScaleOption() == ExportPDFWizardPage1.CUSTOM_MAP_SCALE) {
            float customScale = page1.getCustomScale();
            template.setMapScaleHint(customScale);
        } 
        else if (page1.getScaleOption() == ExportPDFWizardPage1.CURRENT_MAP_SCALE) {
            double currentViewportScaleDenom = map.getViewportModel().getScaleDenominator();
            if (currentViewportScaleDenom == -1) 
                throw new IllegalStateException("no scale denominator is available from the viewport model"); //$NON-NLS-1$
           template.setMapScaleHint(currentViewportScaleDenom);            
        }
        else if (page1.getScaleOption() == ExportPDFWizardPage1.ZOOM_TO_SELECTION) {
            template.setZoomToSelectionHint(true);
        }        
        
        //3. make the page itself 
        Page page = ModelFactory.eINSTANCE.createPage();
        page.setSize(new Dimension((int)width, (int)height));

        //page name stuff not required, because this page will just get discarded
        MessageFormat formatter = new MessageFormat(Messages.CreatePageAction_newPageName, Locale.getDefault()); 
        if (page.getName() == null || page.getName().length() == 0) {
            page.setName(formatter.format(new Object[] { mapCopy.getName() }));
        }

        //page.setProjectInternal(project);
        template.init(page, mapCopy);
        
        //copy the boxes from the template into the page
        Iterator<Box> iter = template.iterator();        
        while (iter.hasNext()) {
            page.getBoxes().add(iter.next());
        }
        return page;
        
        
        //TODO Throw some sort of exception if the page can't be created

    }

    /**
     * Gets the template selected in the wizard.
     *
     * @return a template
     */
    private Template getTemplate() {
           
        TemplateFactory templateFactory = page1.getTemplateFactory();
        Template template = templateFactory.createTemplate();
                
        return template;
    }
    
    public void init( IWorkbench workbench, IStructuredSelection selection ) {
    }

    
}
