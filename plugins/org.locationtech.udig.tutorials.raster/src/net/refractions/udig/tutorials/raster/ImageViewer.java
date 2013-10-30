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
package net.refractions.udig.tutorials.raster;



// J2SE dependencies
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;

import javax.media.jai.GraphicsJAI;
import javax.media.jai.PlanarImage;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.geotools.coverage.grid.GridCoverage2D;


/**
 * A very simple viewer for {@link GridCoverage2D}. This viewer provides no zoom
 * capability, no user interaction and ignores the coordinate system. It is just
 * for quick test of grid coverage.
 *
 * @version $Id: Viewer.java 13129 2005-04-15 03:07:06Z desruisseaux $
 * @author Martin Desruisseaux
 */
public class ImageViewer extends JPanel {
    /**
     * The image to display.
     */
    private final RenderedImage image;

    /**
     * The transform from grid to coordinate system.
     * Usually an identity transform for this simple viewer.
     */
    private final AffineTransform gridToCoordinateSystem = new AffineTransform();

    /**
     * Constructs a viewer for the specified image.
     *
     * @param coverage The image to display.
     */
    public ImageViewer(RenderedImage image) {
        image = this.image = PlanarImage.wrapRenderedImage(image);
        gridToCoordinateSystem.translate(-image.getMinX(), -image.getMinY());
        setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
    }

    /**
     * Constructs a viewer for the specified grid coverage.
     *
     * @param coverage The coverage to display.
     */
    public ImageViewer(final GridCoverage2D coverage) {
        this(coverage.getRenderedImage());
    }

    /**
     * Paint this component.
     */
    public void paintComponent(final Graphics graphics) {
        super.paintComponent(graphics);
        final GraphicsJAI g = GraphicsJAI.createGraphicsJAI((Graphics2D) graphics, this);
        g.drawRenderedImage(image, gridToCoordinateSystem);
    }

    /**
     * A convenience method showing a grid coverage. The application
     * will be terminated when the user close the frame.
     *
     * @param  coverage The coverage to display.
     * @param  title The window title.
     * @return The viewer, for information.
     */
    public static ImageViewer show(final GridCoverage2D coverage, final String title) {
        final StringBuffer buffer = new StringBuffer();
        buffer.append(title);
        buffer.append(" - ");
        buffer.append(coverage.getName().toString());
        JFrame frame = new JFrame(buffer.toString());

        ImageViewer viewer = new ImageViewer(coverage);        
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);       
        frame.getContentPane().add(viewer);
        frame.pack();
        frame.setVisible(true);
        
        return viewer;
    }

}
