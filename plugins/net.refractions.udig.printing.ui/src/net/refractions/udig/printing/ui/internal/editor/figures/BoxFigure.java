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
package net.refractions.udig.printing.ui.internal.editor.figures;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

import net.refractions.udig.printing.model.Box;
import net.refractions.udig.printing.model.PropertyListener;
import net.refractions.udig.ui.PlatformGIS;
import net.refractions.udig.ui.graphics.AWTSWTImageUtils;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * Draws LabelBoxes
 * 
 * @author Richard Gould
 * @since 0.3
 */
public class BoxFigure extends Figure {
    private Box box;

    private RectangleFigure rectangleFigure;
    private Label label;

    public BoxFigure() {
        this.rectangleFigure = new RectangleFigure();
        this.label = new Label();
        this.add(rectangleFigure);
        this.add(label);
        setOpaque(false);
    }

    public String getText() {
        return this.label.getText();
    }

    public Rectangle getTextBounds() {
        return this.label.getTextBounds();
    }

    public void setName( String name ) {
        this.label.setText(name);
        this.repaint();
    }

    public void setBox( Box newBox ) {
        this.box = newBox;

        this.repaint();
    }
    
    public void setBounds( Rectangle rect ) {
        super.setBounds(rect);
        this.rectangleFigure.setBounds(rect);
        this.label.setBounds(rect);
    }

    protected void paintFigure( Graphics graphics ) {
        super.paintFigure(graphics);
    }

    PreviewJob drawJob = new PreviewJob();

    boolean rendering=false;
    private IJobChangeListener listener = new JobChangeAdapter(){

        @Override
        public void scheduled( IJobChangeEvent event ) {
            rendering=true;
        }
        
        public void done( IJobChangeEvent event ) {
            rendering=false;
            if( PlatformUI.getWorkbench().isClosing())
                return;
            
            Display.getDefault().asyncExec(new Runnable(){
                public void run() {
                    repaint();
                }
            });
        }
    };
    

    protected void paintClientArea( Graphics graphics ) {
        if( box.getSize().width<1 || box.getSize().height<1)
            return;
        
        drawJob.addJobChangeListener(listener);
        graphics.translate(this.getLocation().x, this.getLocation().y);
        
        if(box.getBoxPrinter() == null)
        	return;

        if ((!box.getBoxPrinter().isNewPreviewNeeded() && drawJob.getCacheImage()!=null)
                || (drawJob.getDraws()>5 && drawJob.getCacheImage()!=null) ) {
            drawJob.setDraws(0);
            // on the Draw2D graphics context
            graphics.drawImage(drawJob.getCacheImage(),0,0);
        } else {
            // gets the Graphics2D context
            if( !rendering ){
                drawJob.clearCache();
                drawJob.setDisplay(Display.getCurrent());
                drawJob.setBox(box);
                drawJob.schedule();
            }
            String message = "Rendering Preview";
            Color color = graphics.getBackgroundColor();
            graphics.setBackgroundColor(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
            graphics.fillRectangle(0,0,box.getSize().width-1, box.getSize().height-1);
            graphics.setBackgroundColor(color);
            graphics.setForegroundColor(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
            graphics.drawRectangle(0,0,box.getSize().width-1, box.getSize().height-1);
            int fontHeight = graphics.getFontMetrics().getHeight();
            int fontWidth = graphics.getFontMetrics().getAverageCharWidth()*message.length();
            graphics.drawText(message, box.getSize().width/2-fontWidth, box.getSize().height/2-fontHeight);
        }

    }

    private static class PreviewJob extends Job {

        private Box box;
        private Display display;
        private Image cacheImage;
        private int draws=0;

        public synchronized void setBox( Box box ) {
            this.box = box;
        }

        public void clearCache() {
            if( cacheImage!=null )
                cacheImage.dispose();
            cacheImage=null;
        }

        PreviewJob() {
            super("Preview Job");
        }

        public synchronized void setDisplay( Display display2 ) {
            this.display = display2;
        }

        public synchronized Image getCacheImage() {
            return cacheImage;
        }

        
        
        protected IStatus run( IProgressMonitor monitor ) {
            int width = box.getSize().width;
            int height = box.getSize().height;
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            
            // does the Java 2D painting
            Graphics2D createGraphics = image.createGraphics();
            box.getBoxPrinter().createPreview(createGraphics, monitor);

            ImageRunnable runnable = new ImageRunnable();
            runnable.image = image;
            
            PlatformGIS.syncInDisplayThread (runnable);
            
            synchronized (this) {
                this.cacheImage = runnable.swtImage;
                draws++;
            }
            return Status.OK_STATUS;
        }
        
        private class ImageRunnable implements Runnable {

        	Image swtImage;
			private RenderedImage image;
			
			public void run() {
				swtImage = AWTSWTImageUtils.createSWTImage(image, true);
			}
        	
        }

        /**
         * @return Returns the draws.
         */
        public synchronized int getDraws() {
            return draws;
        }

        /**
         * @param draws The draws to set.
         */
        public synchronized void setDraws( int draws ) {
            this.draws = draws;
        }

    }
    
}
