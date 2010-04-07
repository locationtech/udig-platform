/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.printing.ui.internal;

import static net.refractions.udig.printing.ui.internal.PrintingPlugin.TRACE_PRINTING;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.printing.model.Box;
import net.refractions.udig.printing.model.ModelFactory;
import net.refractions.udig.printing.model.Page;
import net.refractions.udig.printing.ui.Template;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.ui.ApplicationGIS;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.geometry.Dimension;
import org.opengis.coverage.grid.GridCoverageReader;

/**
 * This printing engine builds a page dynamically once the print funciton is called
 * 
 * @author banders
 */
public class TemplatePrintingEngine implements Pageable, Printable {

    private static final double MARGIN = 0.5 * 72;
    private Template template;
    private boolean showRasters;
    private double scaleHint;
    private Map map;
    private int numPages;
    
    private IProgressMonitor monitor;
    private PrinterJob printerJob;

    /**
     * Constructs a PrintingEngine using the given Page
     * 
     * @param page the Page to be printed
     */
    public TemplatePrintingEngine( Map map, Template template, boolean showRasters ) {
        this.template = template;
        this.showRasters = showRasters;
        this.map = map;
        this.numPages = -1;
    }

    /**
     * Iterates through the Page's Boxes, drawing to the provided Graphics object
     * 
     * @see java.awt.print.Printable#print(java.awt.Graphics, java.awt.print.PageFormat, int)
     */
    public int print( Graphics graphics, PageFormat pageFormat, int pageIndex )
            throws PrinterException {

        if (pageIndex >= getNumberOfPages()) {
            return Printable.NO_SUCH_PAGE;
        }
        
        template.setActivePage(pageIndex);
        
        //Paper paper = pageFormat.getPaper();
        //paper.setImageableArea(MARGIN, MARGIN, paper.getWidth() - MARGIN*2, paper.getHeight() - MARGIN*2);
        //pageFormat.setPaper(paper);
        Page page = makePage(map, pageFormat, template, showRasters, scaleHint);
        Graphics2D graphics2d = (Graphics2D) graphics;

        AffineTransform at = graphics2d.getTransform();
        double dpi = at.getScaleX() * 72;

        if (PrintingPlugin.isDebugging(TRACE_PRINTING)) {
            PrintingPlugin.log("-Printing page " + pageIndex, null); //$NON-NLS-1$
            System.out.println("-PageFormat: " + pageFormat); //$NON-NLS-1$
            System.out.println("-PageFormat height: " + pageFormat.getHeight()); //$NON-NLS-1$
            System.out.println("-PageFormat width: " + pageFormat.getWidth()); //$NON-NLS-1$
            System.out.println("-PageFormat imageableX,Y " + pageFormat.getImageableX() + ", " + pageFormat.getImageableY()); //$NON-NLS-1$ //$NON-NLS-2$
            System.out.println("-PageFormat imageable height: " + pageFormat.getImageableHeight()); //$NON-NLS-1$
            System.out.println("-PageFormat imageable width: " + pageFormat.getImageableWidth()); //$NON-NLS-1$
            System.out.println("-PageFormat orientation (LANDSCAPE=" + PageFormat.LANDSCAPE + ", PORTRAIT=" + PageFormat.PORTRAIT + "): " + pageFormat.getOrientation()); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

            System.out.println("-Graphics: clip bounds: " + graphics2d.getClipBounds()); //$NON-NLS-1$
            System.out.println("-Transform: scaleX: " + at.getScaleX()); //$NON-NLS-1$
            System.out.println("-Transform: scaleY: " + at.getScaleY()); //$NON-NLS-1$
            System.out.println("-DPI?? : " + dpi); //$NON-NLS-1$
        }

        graphics2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

        Iterator<Box> iter = page.getBoxes().iterator();
        while( iter.hasNext() ) {

            Box box = iter.next();
            graphics2d = (Graphics2D) graphics.create(box.getLocation().x, box.getLocation().y, box
                    .getSize().width, box.getSize().height);

            box.getBoxPrinter().draw(graphics2d, monitor);

        }
        
        return Printable.PAGE_EXISTS;
    }
    
    
    protected Page makePage(Map map, PageFormat pf, Template template, boolean showRasters, double scaleHint) {
        
        Map mapCopy = null;
        mapCopy = (Map) ApplicationGIS.copyMap(map);
        List<Layer> layersNoRasters = mapCopy.getLayersInternal();
        
        if (!showRasters){
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
        //if (scaleHint != -1) {
        //    template.setMapScaleHint(scaleHint);
        //}        
        
        //make the page itself 
        Page page = ModelFactory.eINSTANCE.createPage();
        page.setSize(new Dimension((int)pf.getImageableWidth(), (int)pf.getImageableHeight()));

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
        
        //TODO Throw some sort of exception if the page can't be created

    }

    
    /**
     * @param monitor
     */
    public void setMonitor( IProgressMonitor monitor ) {
        this.monitor = monitor;
    }

    public int getNumberOfPages() {
        makePage(map, new PageFormat(), template, false, -1);
        if (numPages == -1) {
            numPages = template.getNumPages();
        }
        return numPages;
    }

    public PageFormat getPageFormat( int pageIndex ) throws IndexOutOfBoundsException {
        return this.printerJob.defaultPage();
    }

    public Printable getPrintable( int pageIndex ) throws IndexOutOfBoundsException {
        return this;
    }

    public void setPrinterJob( PrinterJob printerJob ) {
        this.printerJob = printerJob;
    }
}
