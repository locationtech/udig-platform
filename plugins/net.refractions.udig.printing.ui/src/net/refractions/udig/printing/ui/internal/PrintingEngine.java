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
import java.util.Iterator;

import net.refractions.udig.printing.model.Box;
import net.refractions.udig.printing.model.Page;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * The PrintingEngine takes a page and processes it, taking each Box and realizing its contents and
 * sending the result to the printer.
 * 
 * @author Richard Gould
 */
public class PrintingEngine implements Pageable, Printable {

    Page diagram;
    private IProgressMonitor monitor;
    private PrinterJob printerJob;

    /**
     * Constructs a PrintingEngine using the given Page
     * 
     * @param page the Page to be printed
     */
    public PrintingEngine( Page diagram ) {
        this.diagram = diagram;
    }

    /**
     * Iterates through the Page's Boxes, drawing to the provided Graphics object
     * 
     * @see java.awt.print.Printable#print(java.awt.Graphics, java.awt.print.PageFormat, int)
     */
    public int print( Graphics graphics, PageFormat pageFormat, int pageIndex )
            throws PrinterException {

        if (pageIndex >= 1) {
            return Printable.NO_SUCH_PAGE;
        }

        Graphics2D graphics2d = (Graphics2D) graphics;

        AffineTransform at = graphics2d.getTransform();
        double dpi = at.getScaleX() * 72;

        if (PrintingPlugin.isDebugging(TRACE_PRINTING)) {
            PrintingPlugin.log("Printing page " + pageIndex, null); //$NON-NLS-1$
            System.out.println("PageFormat: " + pageFormat); //$NON-NLS-1$
            System.out.println("PageFormat height: " + pageFormat.getHeight()); //$NON-NLS-1$
            System.out.println("PageFormat width: " + pageFormat.getWidth()); //$NON-NLS-1$
            System.out
                    .println("PageFormat imageableX,Y " + pageFormat.getImageableX() + ", " + pageFormat.getImageableY()); //$NON-NLS-1$ //$NON-NLS-2$
            System.out.println("PageFormat imageable height: " + pageFormat.getImageableHeight()); //$NON-NLS-1$
            System.out.println("PageFormat imageable width: " + pageFormat.getImageableWidth()); //$NON-NLS-1$
            System.out
                    .println("PageFormat orientation (LANDSCAPE=" + PageFormat.LANDSCAPE + ", PORTRAIT=" + PageFormat.PORTRAIT + "): " + pageFormat.getOrientation()); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

            System.out.println("Graphics: clip bounds: " + graphics2d.getClipBounds()); //$NON-NLS-1$
            System.out.println("Transform: scaleX: " + at.getScaleX()); //$NON-NLS-1$
            System.out.println("Transform: scaleY: " + at.getScaleY()); //$NON-NLS-1$
            System.out.println("DPI?? : " + dpi); //$NON-NLS-1$
        }

        graphics2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

        Iterator<Box> iter = diagram.getBoxes().iterator();
        while( iter.hasNext() ) {

            Box box = iter.next();
            graphics2d = (Graphics2D) graphics.create(box.getLocation().x, box.getLocation().y, box
                    .getSize().width, box.getSize().height);

            box.getBoxPrinter().draw(graphics2d, monitor);

        }

        return Printable.PAGE_EXISTS;
    }
    /**
     * @param monitor
     */
    public void setMonitor( IProgressMonitor monitor ) {
        this.monitor = monitor;
    }

    public int getNumberOfPages() {
        return 1;
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
