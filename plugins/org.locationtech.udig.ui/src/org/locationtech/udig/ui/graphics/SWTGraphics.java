/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.ui.graphics;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Path;
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

    static final AffineTransform AFFINE_TRANSFORM = new AffineTransform();

    private Transform swtTransform;

	private GC gc = null;

	private Color fore = null;

	private Color back = null;

	private Display display;

	private Font font = null;
	
	/**
	 * Construct <code>SWTGraphics</code>.
	 * 
	 * @param Image
	 *            image
     *  @param display the display to use with the 
	 * @param display
	 *            The display object
	 */
	public SWTGraphics(Image image, Display display) {
		this(new GC(image), display);
        
	}

	/**
	 * Construct <code>SWTGraphics</code>.
	 * 
	 * @param gc
	 *            The GC object
	 * @param display
	 *            The display object
	 */
	public SWTGraphics(GC gc, Display display) {
        AWTSWTImageUtils.checkAccess();
        this.display=display;
		setGraphics(gc, display);
	}

    void setGraphics( GC gc, Display display ) {
        AWTSWTImageUtils.checkAccess();
        this.gc = gc;
        // this.display=display;
        if (back != null)
            back.dispose();
        back = new Color(display, 255, 255, 255);
        gc.setBackground(back);
        
        if (swtTransform != null)
        	swtTransform.dispose();
        swtTransform = new Transform(display);

        gc.setAdvanced(true);
    }
	
    public <T> T getGraphics( Class<T> adaptee ) {
        AWTSWTImageUtils.checkAccess();
        if (adaptee.isAssignableFrom(GC.class)) {
            return adaptee.cast(gc);
        }
        return null;
    }

    public void dispose() {
        AWTSWTImageUtils.checkAccess();
        if (fore != null)
            fore.dispose();
        if (back != null)
            back.dispose();
        if( swtTransform!=null )
            swtTransform.dispose();
        gc.dispose();
    }

    public void drawPath( Path path ) {
        AWTSWTImageUtils.checkAccess();
        gc.drawPath(path);
    }

    /**
     * @see org.locationtech.udig.project.render.ViewportGraphics#draw(java.awt.Shape)
     */
    public void draw( Shape s ) {
        AWTSWTImageUtils.checkAccess();
        Path path = AWTSWTImageUtils.convertToPath(s, display);
        if( path!=null ){
            gc.drawPath(path);
            path.dispose();
        }
        
    }

    /**
     * Converts the shape to a path object.  Remember to dispose of the path object when
     * done.
     * 
     * @param shape
     * @return the shape converted to a {@link Path} object.
     * @deprecated Use {@link AWTSWTImageUtils#convertToPath(Shape,Device)} instead
     */
    public static Path convertToPath( Shape shape, Device device  ) {
        return AWTSWTImageUtils.convertToPath(shape, device);
    }

    /**
     * @deprecated Use {@link AWTSWTImageUtils#createPath(PathIterator,Device)} instead
     */
    public static Path createPath( PathIterator p, Device device ) {
        return AWTSWTImageUtils.createPath(p, device);
    }


    /**
     * @see org.locationtech.udig.project.render.ViewportGraphics#draw(java.awt.Shape)
     */
    public void fill( Shape s ) {
        Color tmp = prepareForFill();
        Path path = AWTSWTImageUtils.convertToPath(s, display);
        gc.fillPath(path);
        path.dispose();
        gc.setBackground(tmp);
    }

    private Color prepareForFill() {
        AWTSWTImageUtils.checkAccess();
        Color tmp=gc.getBackground();
        if( fore==null ){
            gc.setBackground(gc.getForeground());
        }else{
            gc.setBackground(fore);
        }
        return tmp;
    }

    public void fillPath( Path path ) {
        Color tmp = prepareForFill();
        gc.fillPath(path);
        gc.setBackground(tmp);
    }

    public void drawRect( int x, int y, int width, int height ){
        Color tmp = prepareForFill();

        gc.drawRectangle(x, y, width, height);
        gc.setBackground(tmp);
    }

    /**
     * @see org.locationtech.udig.project.render.ViewportGraphics#fillRect(int, int, int, int)
     */
    public void fillRect( int x, int y, int width, int height ) {
        Color tmp = prepareForFill();
        gc.fillRectangle(new Rectangle(x, y, width, height));
        
        gc.setBackground(tmp);
    }

	/**
	 * @see org.locationtech.udig.project.render.ViewportGraphics#setColor(java.awt.Color)
	 */
	public void setColor(final java.awt.Color c) { 
        AWTSWTImageUtils.checkAccess();
		Color color = new Color(display, c.getRed(), c.getGreen(), c.getBlue());
		gc.setForeground(color);
		gc.setAlpha(c.getAlpha());
		if (fore != null)
			fore.dispose();
		fore = color;
	}

    /**
     * This is hard because - background doesn't mean what we think it means.
     * 
     * @see org.locationtech.udig.project.render.ViewportGraphics#setBackground(java.awt.Color)
     */
    public void setBackground( java.awt.Color c ) {
        AWTSWTImageUtils.checkAccess();
        Color color = new Color(display, c.getRed(), c.getGreen(), c.getBlue());
        gc.setBackground(color);
        if (back != null)
            back.dispose();
        back = color;
    }

    /**
     * @see org.locationtech.udig.project.render.ViewportGraphics#setStroke(int, int)
     */
    public void setStroke( int style, int width ) {
        AWTSWTImageUtils.checkAccess();
        
        gc.setLineWidth(width);
        switch( style ) {
        case LINE_DASH: {
            gc.setLineStyle(SWT.LINE_DASH);
            break;
        }
        case LINE_DASHDOT: {
            gc.setLineStyle(SWT.LINE_DASHDOT);
            break;
        }
        case LINE_DASHDOTDOT: {
            gc.setLineStyle(SWT.LINE_DASHDOTDOT);
            break;
        }
        case LINE_DOT: {
            gc.setLineStyle(SWT.LINE_DOT);
            break;
        }
        case LINE_SOLID: {
            gc.setLineStyle(SWT.LINE_SOLID);
            break;
        }

        case LINE_SOLID_ROUNDED: {
            gc.setLineCap(SWT.CAP_ROUND);
            gc.setLineJoin(SWT.JOIN_ROUND);
            gc.setLineStyle(SWT.LINE_SOLID);
            break;
        }
        default: {
            gc.setLineStyle(SWT.LINE_SOLID);
            break;
        }
        }
    }

    /**
     * @see org.locationtech.udig.project.render.ViewportGraphics#setClip(java.awt.Rectangle)
     */
    public void setClip( java.awt.Rectangle r ) {
        AWTSWTImageUtils.checkAccess();
        gc.setClipping(r.x, r.y, r.width, r.height);
    }

	/**
	 * @see org.locationtech.udig.project.render.ViewportGraphics#translate(java.awt.Point)
	 */
	public void translate(Point offset) {
        AWTSWTImageUtils.checkAccess();
        swtTransform.translate(offset.x, offset.y);
        gc.setTransform(swtTransform);
	}

    public void clearRect( int x, int y, int width, int height ) {
        AWTSWTImageUtils.checkAccess();
        gc.fillRectangle(x, y, width, height);
    }

    public void drawImage( RenderedImage rimage, int x, int y ) {
        AWTSWTImageUtils.checkAccess();
        drawImage(rimage, x, y, x + rimage.getWidth(),
                y + rimage.getHeight(), 0, 0, rimage.getWidth(), rimage.getHeight());
    }

    /**
     * @deprecated Use {@link AWTSWTImageUtils#createDefaultImage(Display,int,int)} instead
     */
    public static Image createDefaultImage( Display display, int width, int height ) {
        return AWTSWTImageUtils.createDefaultImage(display, width, height);
    }

    /**
     * @deprecated Use {@link AWTSWTImageUtils#createImageDescriptor(RenderedImage,boolean)} instead
     */
    public static ImageDescriptor createImageDescriptor( final RenderedImage image,
            final boolean transparent ) {
                return AWTSWTImageUtils.createImageDescriptor(image, transparent);
            }

    /** Create a buffered image that can be be converted to SWTland later 
     * @deprecated Use {@link AWTSWTImageUtils#createBufferedImage(int,int)} instead*/
    public static BufferedImage createBufferedImage( int w, int h ) {
        return AWTSWTImageUtils.createBufferedImage(w, h);
    }

    /**
     * @deprecated Use {@link AWTSWTImageUtils#createSWTImage(RenderedImage,boolean)} instead
     */
    public static Image createSWTImage( RenderedImage image, boolean transparent ) {
        return AWTSWTImageUtils.createSWTImage(image, transparent);
    }


    /**
     * @deprecated Use {@link AWTSWTImageUtils#createImageData(RenderedImage,boolean)} instead
     */
    public static ImageData createImageData( RenderedImage image, boolean transparent ) {
        return AWTSWTImageUtils.createImageData(image, transparent);
    }

    public void drawString( String string, int x, int y, int alignx, int aligny ) {
        AWTSWTImageUtils.checkAccess();
        org.eclipse.swt.graphics.Point text = gc.stringExtent(string);
        int w = (int)text.x;
        int h = (int)text.y;
        
        int x2 = (alignx == 0) ? x - w/2 : (alignx > 0) ? x - w : x;
        int y2 = (aligny == 0) ? y + h/2 : (aligny > 0) ? y + h : y;

        gc.drawString(string, x2, y2,true);
    }

    public void setTransform( AffineTransform transform ) {
        AWTSWTImageUtils.checkAccess();
        double[] matrix=new double[6];
        transform.getMatrix(matrix);

        //Note that the arguments are not in the same order as the elements returned by 
        //AffineTransform.getMatrix(double[]). Consult the javadocs for details.
        swtTransform.setElements(
                (float)matrix[0], (float)matrix[2],
                (float)matrix[1], (float)matrix[3],
                (float)matrix[4], (float)matrix[5] );
        
        gc.setTransform(swtTransform);
    }

    public int getFontHeight() {
        AWTSWTImageUtils.checkAccess();
        return gc.getFontMetrics().getHeight();
    }

    public int stringWidth( String str ) {
        AWTSWTImageUtils.checkAccess();
        return -1;
    }

    public int getFontAscent() {
        AWTSWTImageUtils.checkAccess();
        return gc.getFontMetrics().getAscent();
    }

    public Rectangle2D getStringBounds( String str ) {
        AWTSWTImageUtils.checkAccess();
        org.eclipse.swt.graphics.Point extent = gc.textExtent(str);
        
        return new java.awt.Rectangle(0,0,extent.x, extent.y);
    }

    public void drawLine( int x1, int y1, int x2, int y2 ) {
        AWTSWTImageUtils.checkAccess();
        gc.drawLine(x1, y1, x2, y2);
    }

    /**
     * @see org.locationtech.udig.ui.graphics.ViewportGraphics#drawImage(java.awt.Image, int, int)
     *      Current version can only draw Image if the image is an RenderedImage
     */
    public void drawImage( java.awt.Image awtImage, int x, int y ) {
        AWTSWTImageUtils.checkAccess();
        RenderedImage rimage = (RenderedImage) awtImage;
        drawImage(rimage, x, y);
    }

    public void drawImage( RenderedImage rimage, int dx1, int dy1, int dx2, int dy2, int sx1,
            int sy1, int sx2, int sy2 ) {
        AWTSWTImageUtils.checkAccess();
        assert rimage != null;
        Image swtImage = null;
        try {
            if( rimage instanceof BufferedImage )
                swtImage = AWTSWTImageUtils.convertToSWTImage((BufferedImage)rimage);
            else{
                swtImage = AWTSWTImageUtils.createSWTImage(rimage);
            }
            if (swtImage != null) {
                gc.drawImage(swtImage, sx1, sy1, Math.abs(sx2-sx1), Math.abs(sy2-sy1), 
                        dx1, dy1, Math.abs(dx2-dx1), Math.abs(dy2-dy1) );
                swtImage.dispose();
            }
        } finally {
            if (swtImage != null)
                swtImage.dispose();
        }
    
    }

    /**
     * @see org.locationtech.udig.ui.graphics.ViewportGraphics#drawImage(java.awt.Image, int, int,
     *      int, int, int, int, int, int)
     */
    public void drawImage( java.awt.Image awtImage, int dx1, int dy1, int dx2, int dy2, int sx1,
            int sy1, int sx2, int sy2 ) {
        AWTSWTImageUtils.checkAccess();
        RenderedImage rimage = (RenderedImage) awtImage;
        drawImage(rimage, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2);
    }

    public void drawImage( Image swtImage, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1,
            int sx2, int sy2 ) {
        AWTSWTImageUtils.checkAccess();

        gc.drawImage(swtImage, sx1, sy1, Math.abs(sx2-sx1), Math.abs(sy2-sy1), 
                dx1, dy1, Math.abs(dx2-dx1), Math.abs(dy2-dy1) );

    }
    
    public void drawImage( Image swtImage, int x, int y ) {
        gc.drawImage(swtImage, x, y);
    }

    public AffineTransform getTransform() {
        AWTSWTImageUtils.checkAccess();
        if( swtTransform==null )
            return AFFINE_TRANSFORM;
        
        float[] matrix=new float[6];
        swtTransform.getElements(matrix);
        return new AffineTransform(matrix);
    }
    
    
    public void drawOval( int x, int y, int width, int height ){
        gc.drawOval(x,y,width, height);
    }

    public void fillOval( int x, int y, int width, int height ) {
        gc.fillOval(x, y, width, height);
    }
    
    /**
     * Creates an image descriptor that from the source image.
     *
     * @param image source image
     * @return  an image descriptor that from the source image.
     * @deprecated Use {@link AWTSWTImageUtils#createImageDescriptor(BufferedImage)} instead
     */
    public static ImageDescriptor createImageDescriptor( final BufferedImage image ) {
        return AWTSWTImageUtils.createImageDescriptor(image);
    }

    /**
     * Converts a BufferedImage to an SWT Image.  You are responsible for disposing the created image.  This
     * method is faster than creating a SWT image from a RenderedImage so use this method if possible.
     *
     * @param image source image.
     * @return a swtimage showing the source image.
     * @deprecated Use {@link AWTSWTImageUtils#convertToSWTImage(BufferedImage)} instead
     */
    public static Image convertToSWTImage( BufferedImage image ) {
        return AWTSWTImageUtils.convertToSWTImage(image);
    }
    
    /**
     * Creates an ImageData from the 0,0,width,height section of the source BufferedImage.
     * <p>
     * This method is faster than creating the ImageData from a RenderedImage so use this method if possible.
     * </p>
     *
     * @param image source image.
     * @return an ImageData from the 0,0,width,height section of the source BufferedImage
     * @deprecated Use {@link AWTSWTImageUtils#createImageData(BufferedImage)} instead
     */
    public static ImageData createImageData( BufferedImage image ) {
        return AWTSWTImageUtils.createImageData(image);
    }
    
    /**
     * Converts a RenderedImage to an SWT Image.  You are responsible for disposing the created image.  This
     * method is slower than calling {@link #createSWTImage(BufferedImage, int, int)}.
     *
     * @param image source image.
     * @param width the width of the final image
     * @param height the height of the final image
     * @return a swtimage showing the 0,0,width,height rectangle of the source image.
     * @deprecated Use {@link AWTSWTImageUtils#createSWTImage(RenderedImage)} instead
     */
    public static Image createSWTImage( RenderedImage image  ) {
        return AWTSWTImageUtils.createSWTImage(image);
    }
    
    /**
     * Creates an ImageData from the source RenderedImage.
     * <p>
     * This method is slower than using {@link AWTSWTImageUtils#createImageData(BufferedImage, int, int)}.
     * </p>
     *
     * @param image source image.
     * @return an ImageData from the source RenderedImage.
     * @deprecated Use {@link AWTSWTImageUtils#createImageData(RenderedImage)} instead
     */
    public static ImageData createImageData( RenderedImage image ) {
        return AWTSWTImageUtils.createImageData(image);
    }
    public Shape getClip() {
        Rectangle clipping = gc.getClipping();
        return new java.awt.Rectangle(clipping.x, clipping.y, clipping.width, clipping.height);
    }

    public void setClipBounds( java.awt.Rectangle newBounds ) {
        gc.setClipping(new Rectangle(newBounds.x, newBounds.y, newBounds.width, newBounds.height));
    }

	public java.awt.Color getBackgroundColor() {
        AWTSWTImageUtils.checkAccess();
		return AWTSWTImageUtils.swtColor2awtColor(gc, gc.getBackground());
	}

	public java.awt.Color getColor() {
        AWTSWTImageUtils.checkAccess();
		return AWTSWTImageUtils.swtColor2awtColor(gc, gc.getForeground());
	}
	
	/**
     * @deprecated Use {@link AWTSWTImageUtils#swtColor2awtColor(GC,Color)} instead
     */
    public static java.awt.Color swtColor2awtColor(GC gc, Color swt) {
        return AWTSWTImageUtils.swtColor2awtColor(gc, swt);
    }

    public void drawRoundRect( int x, int y, int width, int height, int arcWidth, int arcHeight ) {
        AWTSWTImageUtils.checkAccess();
        gc.drawRoundRectangle(x, y, width, height, arcWidth, arcHeight);
    }

    /**
     * @deprecated Use {@link AWTSWTImageUtils#checkAccess()} instead
     */
    static void checkAccess() {
        AWTSWTImageUtils.checkAccess();
    }
    
    public void fillRoundRect(  int x, int y, int width, int height, int arcWidth, int arcHeight ) {
        Color tmp = prepareForFill();
        gc.fillRoundRectangle(x, y, width, height, arcWidth, arcHeight);
        gc.setBackground(tmp);
    }

    public void setLineDash( int[] dash ) {
        gc.setLineDash(dash);
    }

    public void setLineWidth( int width ) {
        gc.setLineWidth(width);
    }
    /**
     * Takes an AWT Font.
     *
     * @param style
     * @return
     * @deprecated Use {@link AWTSWTImageUtils#toFontStyle(java.awt.Font)} instead
     */
    public static int toFontStyle( java.awt.Font f ){
        return AWTSWTImageUtils.toFontStyle(f);
    }
    
    public void setFont(java.awt.Font f){
        Font swtFont;  
        
        int size = (f.getSize()* getDPI() ) / 72;
        int style = AWTSWTImageUtils.toFontStyle( f );
                
        swtFont = new Font( gc.getDevice(),f.getFamily(), size, style );
        if (font != null){
            font.dispose();
        }
        font = swtFont;
        gc.setFont(font);
    }
    
    public int getDPI() {
        return gc.getDevice().getDPI().y;
    }

    public void fillGradientRectangle( int x, int y, int width, int height,
            java.awt.Color startColor, java.awt.Color endColor, boolean isVertical ) {
        Color color1 = new Color(display, startColor.getRed(), startColor.getGreen(), startColor
                .getBlue());
        Color color2 = new Color(display, endColor.getRed(), endColor.getGreen(), endColor
                .getBlue());
        gc.setForeground(color1);
        gc.setBackground(color2);

        gc.fillGradientRectangle(x, y, width, height, isVertical);
        color1.dispose();
        color2.dispose();
    }

    /**
     * Converts SWT FontData to a AWT Font
     * 
     * @param fontData the font data
     * @return the equivalent AWT font
     * @deprecated Use {@link AWTSWTImageUtils#swtFontToAwt(FontData)} instead
     */
    public static java.awt.Font swtFontToAwt( FontData fontData ) {
        return AWTSWTImageUtils.swtFontToAwt(fontData);
    }

    /**
     * Converts an AWTFont to a SWT Font
     * 
     * @param font and AWT Font
     * @param fontRegistry
     * @return the equivalent SWT Font
     * @deprecated Use {@link AWTSWTImageUtils#awtFontToSwt(java.awt.Font,FontRegistry)} instead
     */
    public static org.eclipse.swt.graphics.Font awtFontToSwt( java.awt.Font font, FontRegistry fontRegistry ) {
        return AWTSWTImageUtils.awtFontToSwt(font, fontRegistry);
    }
}
