/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.ui.graphics;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.util.ArrayList;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Display;

/**
 * A Graphics object that wraps SWT's GC object
 * 
 * @author jeichar
 * @since 0.3
 */
public class SWTGraphics implements ViewportGraphics {
	/** The <code>TRANSPARENT</code> color */
	public final static int TRANSPARENT = 0x220000 | 0x2200 | 0x22;

	private static final AffineTransform IDENTITY = new AffineTransform();

	private GC gc = null;

	private double[] current = new double[2];

	private double[] last = new double[2];

	private double[] move_to = new double[2];

	private Color fore = null;

	private Color back = null;

	private Display display;

	private Dimension displaySize;

	/**
	 * Construct <code>SWTGraphics</code>.
	 * 
	 * @param Image
	 *            image
	 * @param display
	 *            The display object
	 */
	public SWTGraphics(Image image, Display display) {
		this(new GC(image), display, new Dimension(image.getImageData().width,
				image.getImageData().height));
	}

	/**
	 * Construct <code>SWTGraphics</code>.
	 * 
	 * @param gc
	 *            The GC object
	 * @param display
	 *            The display object
	 */
	public SWTGraphics(GC gc, Display display, Dimension displaySize) {
		setGraphics(gc, display);
		this.displaySize = displaySize;
	}

	void setGraphics(GC gc, Display display) {
		this.gc = gc;
		// this.display=display;
		if (back != null)
			back.dispose();
		back = new Color(display, 255, 255, 255);
		gc.setBackground(back);
	}

	public void dispose() {
		if (fore != null)
			fore.dispose();
		if (back != null)
			back.dispose();
	}

	/**
	 * @see net.refractions.udig.project.render.ViewportGraphics#draw(java.awt.Shape)
	 */
	public void draw(Shape s) {
		PathIterator p = s.getPathIterator(IDENTITY);
		boolean singlePoint = true;
		p.currentSegment(current);
		move_to = current.clone();
		p.next();
		while (!p.isDone()) {
			singlePoint = false;
			last = current.clone();
			switch (p.currentSegment(current)) {
			case PathIterator.SEG_CLOSE:
				gc.drawLine((int) current[0], (int) current[1],
						(int) move_to[0], (int) move_to[1]);
				break;
			case PathIterator.SEG_LINETO:
				gc.drawLine((int) last[0], (int) last[1], (int) current[0],
						(int) current[1]);
				break;
			case PathIterator.SEG_MOVETO:
				move_to = current;
				break;
			case PathIterator.SEG_QUADTO:
			case PathIterator.SEG_CUBICTO:
			default:
			}
			p.next();
		}
		if (singlePoint == true) {
			gc.drawPoint((int) current[0], (int) current[1]);
		}
	}

	// public void drawPoint( Shape shape, SimpleFeature feature, PointSymbolizer
	// pointSymbolizer ) {
	// /** Factory that will resolve symbolizers into rendered styles */
	// SLDStyleFactory styleFactory = new SLDStyleFactory();
	// /** The painter class we use to depict shapes onto the screen */
	// StyledShapePainter painter = new StyledShapePainter(null);
	//        
	// Style2D style = styleFactory.createStyle(feature, pointSymbolizer, null);
	//        
	// //Graphics2D graphics = getContext().getImage().createGraphics();
	// painter.paint(this, shape, style, 1.0);
	// }

	/**
	 * @see net.refractions.udig.project.render.ViewportGraphics#draw(java.awt.Shape)
	 */
	public void fill(Shape s) {
		gc.setBackground(fore);
		PathIterator p = s.getPathIterator(IDENTITY);
		ArrayList<Integer> pts = new ArrayList<Integer>();

		p.currentSegment(current);
		for (double ord : current)
			pts.add((int) ord); // "move_to"

		p.next();
		while (!p.isDone()) {
			switch (p.currentSegment(current)) {

			case PathIterator.SEG_LINETO:
				p.currentSegment(current);
				for (double ord : current)
					pts.add((int) ord); // "line_to"
				break;

			case PathIterator.SEG_MOVETO:
			case PathIterator.SEG_CLOSE: {
				for (double ord : current)
					pts.add((int) ord); // "close"
				final int SIZE = pts.size();
				int polygon[] = new int[SIZE];
				for (int i = 0; i < SIZE; i++)
					polygon[i] = pts.get(i);
				gc.fillPolygon(polygon);

				pts.clear(); // closed we can start again now
			}
				break;

			case PathIterator.SEG_QUADTO:
			case PathIterator.SEG_CUBICTO:
			default:

			}
			p.next();
		}
		gc.setBackground(fore);
	}

	/**
	 * @see net.refractions.udig.project.render.ViewportGraphics#fillRect(int,
	 *      int, int, int)
	 */
	public void fillRect(int x, int y, int width, int height) {

		gc.fillRectangle(new Rectangle(x, y, width, height));
	}

	/**
	 * @see net.refractions.udig.project.render.ViewportGraphics#setColor(java.awt.Color)
	 */
	public void setColor(java.awt.Color c) {
		Color color = new Color(display, c.getRed(), c.getGreen(), c.getBlue());
		gc.setForeground(color);
		if (fore != null)
			fore.dispose();
		fore = color;
	}

	/**
	 * This is hard because - background doesn't mean what we think it means.
	 * 
	 * @see net.refractions.udig.project.render.ViewportGraphics#setBackground(java.awt.Color)
	 */
	public void setBackground(java.awt.Color c) {
		Color color = new Color(display, c.getRed(), c.getGreen(), c.getBlue());
		gc.setBackground(color);
		if (back != null)
			back.dispose();
		back = color;
	}

	/**
	 * @see net.refractions.udig.project.render.ViewportGraphics#setStroke(int,
	 *      int)
	 */
	public void setStroke(int style, int width) {
		switch (style) {
		case LINE_DASH: {
			gc.setLineStyle(SWT.LINE_DASH);
		}
		case LINE_DASHDOT: {
			gc.setLineStyle(SWT.LINE_DASHDOT);
		}
		case LINE_DASHDOTDOT: {
			gc.setLineStyle(SWT.LINE_DASHDOTDOT);
		}
		case LINE_DOT: {
			gc.setLineStyle(SWT.LINE_DOT);
		}
		case LINE_SOLID: {
			gc.setLineStyle(SWT.LINE_SOLID);
		}
		}
		gc.setLineWidth(width);
	}

	/**
	 * @see net.refractions.udig.project.render.ViewportGraphics#setClip(java.awt.Rectangle)
	 */
	public void setClip(java.awt.Rectangle r) {
		gc.setClipping(r.x, r.y, r.width, r.height);
	}

	/**
	 * @see net.refractions.udig.project.render.ViewportGraphics#translate(java.awt.Point)
	 */
	public void translate(Point offset) {
		Transform transform=new Transform(display);
		gc.getTransform(transform);
		transform.translate(offset.x, offset.y);
		gc.setTransform(transform);
	}

	/**
	 * @see net.refractions.udig.project.render.ViewportGraphics#clearRect(int,
	 *      int, int, int)
	 */
	public void clearRect(int x, int y, int width, int height) {
		Color c = gc.getForeground();
		gc.setForeground(gc.getBackground());
		gc.fillRectangle(x, y, width, height);
		gc.setForeground(c);
	}

	/**
	 * @see net.refractions.udig.project.render.ViewportGraphics#drawImage(javax.media.jai.PlanarImage,
	 *      int, int)
	 */
	public void drawImage(RenderedImage rimage, int x, int y) {
		drawImage(rimage, 0, 0, rimage.getWidth(), rimage.getWidth(), x, y, x
				+ rimage.getWidth(), y + rimage.getWidth());
	}

	public static Image createDefaultImage(Display display, int width,
			int height) {
		ImageData swtdata = null;
		PaletteData palette;
		int depth;

		depth = 24;
		palette = new PaletteData(0xFF0000, 0xFF00, 0xFF);
		swtdata = new ImageData(width, height, depth, palette);
		swtdata.transparentPixel = -1;
		swtdata.alpha = -1;
		swtdata.alphaData = new byte[swtdata.data.length];
		for (int i = 0; i < swtdata.alphaData.length; i++) {
			swtdata.alphaData[i] = (byte) i;
		}
		return new Image(display, swtdata);

	}

	private ImageData awtImageToSWT(Raster raster, Rectangle size) {
		ImageData swtdata = null;
		int width = size.width;
		int height = size.height;
		PaletteData palette;
		int depth;

		depth = 24;
		palette = new PaletteData(0xFF0000, 0xFF00, 0xFF);
		swtdata = new ImageData(width, height, depth, palette);
		swtdata.transparentPixel = TRANSPARENT;

		byte blueT = (byte) ((TRANSPARENT) & 0xFF);
		byte greenT = (byte) ((TRANSPARENT >> 8) & 0xFF);
		byte redT = (byte) ((TRANSPARENT >> 16) & 0xFF);
		// System.out.println("red="+redT+"blue"+blueT+"green"+greenT);
		// System.out.println("Transparent"+TRANSPARENT);

		// awtImage2.getRGB();
		int[] awtdata = raster.getPixels(0, 0, width, height, new int[width
				* height * raster.getNumBands()]);
		int step = swtdata.depth / 8;

		byte[] data = swtdata.data;
		int baseindex = 0;
		for (int y = 0; y < height; y++) {
			int idx = ((0 + y) * swtdata.bytesPerLine) + (0 * step);

			for (int x = 0; x < width; x++) {
				baseindex = (x + (y * width)) * 4;

				if (awtdata[baseindex + 3] == 0) {
					data[idx++] = blueT;
					data[idx++] = greenT;
					data[idx++] = redT;
				} else {
					data[idx++] = (byte) awtdata[baseindex];
					data[idx++] = (byte) awtdata[baseindex + 1];
					data[idx++] = (byte) awtdata[baseindex + 2];
				}
			}
		}

		return swtdata;
	}

	public static ImageDescriptor createImageDescriptor(
			final RenderedImage image, final boolean transparent) {
		return new ImageDescriptor() {
			public ImageData getImageData() {
				return createImageData(image, transparent);
			}
		};
	}

	/** Create a buffered image that can be be coverted to SWTland later */
	public static BufferedImage createBufferedImage(int w, int h) {
		return new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR_PRE);
	}

	public static Image createSWTImage(RenderedImage image, boolean transparent) {
		ImageData data = createImageData(image, transparent);

		return new org.eclipse.swt.graphics.Image(Display.getDefault(), data);
	}
	
	

	// optimized version that works if the image is rgb with a byte data buffer
    public static ImageData createImageDataFromBytes( RenderedImage image ) {
        ImageData swtdata = null;
        int width = image.getWidth();
        int height = image.getHeight();
        PaletteData palette;
        int depth;
        depth = 24;
        palette = new PaletteData(0xFF0000, 0xFF00, 0xFF);
        swtdata = new ImageData(width, height, depth, palette);
        
        Raster raster = image.getData();
        raster.getDataElements(0,0,width, height, swtdata.data);

        
        return swtdata;
    }


	public static ImageData createImageData( RenderedImage image, boolean transparent ) {
		
//		if( image.getData().getDataBuffer().getDataType()==DataBuffer.TYPE_BYTE
//				&& !image.getColorModel().hasAlpha() )
//			return createImageDataFromBytes(image);
	    ImageData swtdata = null;
	    int width = image.getWidth();
	    int height = image.getHeight();
	    PaletteData palette;
	    int depth;
	
	    depth = 24;
	    palette = new PaletteData(0xFF0000, 0xFF00, 0xFF);
	    swtdata = new ImageData(width, height, depth, palette);
	    byte blueT=(byte) 255;
		byte greenT=(byte) 255;
		byte redT=(byte) 255;
		if ( transparent ){
	    	swtdata.transparentPixel = TRANSPARENT;
	    
	        blueT = (byte) ((TRANSPARENT) & 0xFF);
	        greenT = (byte) ((TRANSPARENT >> 8) & 0xFF);
	        redT = (byte) ((TRANSPARENT >> 16) & 0xFF);
	    }
	    Raster raster = image.getData();
	    int numbands=raster.getNumBands();
	    int[] awtdata = raster.getPixels(0, 0, width, height, new int[width * height
	            * numbands]);
	    int step = swtdata.depth / 8;
	
	    byte[] data = swtdata.data;
	    int baseindex = 0;
	    for( int y = 0; y < height; y++ ) {
	        int idx = ((0 + y) * swtdata.bytesPerLine) + (0 * step);
	
	        for( int x = 0; x < width; x++ ) {
	            baseindex = (x + (y * width)) * numbands;
	
	            if (numbands==4 && awtdata[baseindex + 3] == 0) {
	                data[idx++] = blueT;
	                data[idx++] = greenT;
	                data[idx++] = redT;
	            } else {
	                data[idx++] = (byte) awtdata[baseindex];
	                data[idx++] = (byte) awtdata[baseindex + 1];
	                data[idx++] = (byte) awtdata[baseindex + 2];
	            }
	        }
	    }
	    return swtdata;
	}

	/**
	 * @see net.refractions.udig.ui.graphics.ViewportGraphics#drawString(String,
	 *      int, int)
	 */
	public void drawString(String string, int x, int y, int alignx, int aligny) {
		// FIXME do the alignment.
		gc.drawString(string, x, y);
	}

	/**
	 * @see net.refractions.udig.project.render.ViewportGraphics#setTransform(java.awt.geom.AffineTransform)
	 */
	public void setTransform(AffineTransform transform) {
		double [] matrix=new double[6];
		transform.getMatrix(matrix);
		gc.setTransform( new Transform(display, (float)matrix[0], (float)matrix[1], (float)matrix[2], 
				(float)matrix[3], (float)matrix[4], (float)matrix[5]));
	}

	/**
	 * @see net.refractions.udig.ui.graphics.ViewportGraphics#drawImage(java.awt.Image,
	 *      int, int)
	 * 
	 * Current version can only draw Image if the image is an RenderedImage
	 */
	public void drawImage(java.awt.Image image, int x, int y) {
		RenderedImage rimage = (RenderedImage) image;
		drawImage(rimage, x, y);
	}

	/**
	 * @see net.refractions.udig.ui.graphics.ViewportGraphics#drawImage(java.awt.Image,
	 *      int, int, int, int, int, int, int, int)
	 */
	public void drawImage(java.awt.Image image, int dx1, int dy1, int dx2,
			int dy2, int sx1, int sy1, int sx2, int sy2) {
		RenderedImage rimage = (RenderedImage) image;
		drawImage(rimage, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2);
	}

	public void drawImage(RenderedImage rimage, int dx1, int dy1, int dx2,
			int dy2, int sx1, int sy1, int sx2, int sy2) {
		assert rimage != null;
		Image swtImage = null;
		try {
			swtImage = createSWTImage(rimage, true);
			if (swtImage != null) {
				gc.drawImage(swtImage, sx1, sy1, sx2 - sx1, sy2 - sy1,
						dx1, dy1, dx2-dx1, dy2-dy1);
				swtImage.dispose();
			}
		} finally {
			if (swtImage != null)
				swtImage.dispose();
		}

	}

	public int getFontHeight() {
		return gc.getFontMetrics().getHeight();
	}

	public int stringWidth(String str) {
		return -1;
	}

	public int getFontAscent() {
		return gc.getFontMetrics().getAscent();
	}

	public Rectangle2D getStringBounds(String str) {
		return null;
	}

	public void drawLine(int x1, int y1, int x2, int y2) {
		gc.drawLine(x1, y1, x2, y2);
	}

	public void drawImage(Image image, int dx1, int dy1, int dx2, int dy2,
			int sx1, int sy1, int sx2, int sy2) {
		gc.drawImage(image, sx1, sy1, sx2 - sx1, sy2 - sy1, dx1, dy1,
				dx2 - dx1, dy2 - dy1);
	}
}