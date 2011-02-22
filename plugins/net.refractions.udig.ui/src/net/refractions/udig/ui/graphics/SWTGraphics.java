/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.ui.graphics;

import java.awt.Point;
import java.awt.Shape;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
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
import org.eclipse.swt.graphics.PaletteData;
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

    private static final AffineTransform AFFINE_TRANSFORM = new AffineTransform();

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
        checkAccess();
        this.display=display;
		setGraphics(gc, display);
	}

    void setGraphics( GC gc, Display display ) {
        checkAccess();
        this.gc = gc;
        // this.display=display;
        if (back != null)
            back.dispose();
        back = new Color(display, 255, 255, 255);
        gc.setBackground(back);


        gc.setAdvanced(true);
    }

	public GC getGC(){
        checkAccess();
		return gc;
	}


    public void dispose() {
        checkAccess();
        if (fore != null)
            fore.dispose();
        if (back != null)
            back.dispose();
        if( swtTransform!=null )
            swtTransform.dispose();
        gc.dispose();
    }

    public void drawPath( Path path ) {
        checkAccess();
        gc.drawPath(path);
    }

    /**
     * @see net.refractions.udig.project.render.ViewportGraphics#draw(java.awt.Shape)
     */
    public void draw( Shape s ) {
        checkAccess();
        Path path = convertToPath(s, display);
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
     */
    public static Path convertToPath( Shape shape, Device device  ) {
        checkAccess();
        PathIterator p = shape.getPathIterator(AFFINE_TRANSFORM);

        return createPath(p, device);
    }

    public static Path createPath( PathIterator p, Device device ) {
        if (p.isDone())
            return null;

        float[] current = new float[6];
        Path path = new Path(device);
        while( !p.isDone() ) {
            int result = p.currentSegment(current);
            switch( result ) {
            case PathIterator.SEG_CLOSE:
                path.close();
                break;
            case PathIterator.SEG_LINETO:
                path.lineTo(current[0], current[1]);
                break;
            case PathIterator.SEG_MOVETO:
                path.moveTo(current[0], current[1]);
                break;
            case PathIterator.SEG_QUADTO:
                path.quadTo(current[0],   current[1],  current[2],
                         current[3]);
                break;
            case PathIterator.SEG_CUBICTO:
                path.cubicTo( current[0],  current[1],  current[2],
                         current[3],  current[4],  current[5]);
                break;
            default:
            }
            p.next();
        }
        return path;
    }


    /**
     * @see net.refractions.udig.project.render.ViewportGraphics#draw(java.awt.Shape)
     */
    public void fill( Shape s ) {
        Color tmp = prepareForFill();
        Path path = convertToPath(s, display);
        gc.fillPath(path);
        path.dispose();
        gc.setBackground(tmp);
    }

    private Color prepareForFill() {
        checkAccess();
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
     * @see net.refractions.udig.project.render.ViewportGraphics#fillRect(int, int, int, int)
     */
    public void fillRect( int x, int y, int width, int height ) {
        Color tmp = prepareForFill();
        gc.fillRectangle(new Rectangle(x, y, width, height));

        gc.setBackground(tmp);
    }

	/**
	 * @see net.refractions.udig.project.render.ViewportGraphics#setColor(java.awt.Color)
	 */
	public void setColor(final java.awt.Color c) {
        checkAccess();
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
     * @see net.refractions.udig.project.render.ViewportGraphics#setBackground(java.awt.Color)
     */
    public void setBackground( java.awt.Color c ) {
        checkAccess();
        Color color = new Color(display, c.getRed(), c.getGreen(), c.getBlue());
        gc.setBackground(color);
        if (back != null)
            back.dispose();
        back = color;
    }

    /**
     * @see net.refractions.udig.project.render.ViewportGraphics#setStroke(int, int)
     */
    public void setStroke( int style, int width ) {
        checkAccess();

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
     * @see net.refractions.udig.project.render.ViewportGraphics#setClip(java.awt.Rectangle)
     */
    public void setClip( java.awt.Rectangle r ) {
        checkAccess();
        gc.setClipping(r.x, r.y, r.width, r.height);
    }

	/**
	 * @see net.refractions.udig.project.render.ViewportGraphics#translate(java.awt.Point)
	 */
	public void translate(Point offset) {
        checkAccess();
        if( swtTransform==null ){
            swtTransform=new Transform(display);
        }
        swtTransform.translate(offset.x, offset.y);
        gc.setTransform(swtTransform);
	}

    public void clearRect( int x, int y, int width, int height ) {
        checkAccess();
        gc.fillRectangle(x, y, width, height);
    }

    public void drawImage( RenderedImage rimage, int x, int y ) {
        checkAccess();
        drawImage(rimage, x, y, x + rimage.getWidth(),
                y + rimage.getHeight(), 0, 0, rimage.getWidth(), rimage.getHeight());
    }

    public static Image createDefaultImage( Display display, int width, int height ) {
        checkAccess();
        ImageData swtdata = null;
        PaletteData palette;
        int depth;

        depth = 24;
        palette = new PaletteData(0xFF0000, 0xFF00, 0xFF);
        swtdata = new ImageData(width, height, depth, palette);
        swtdata.transparentPixel = TRANSPARENT;
        swtdata.alpha = -1;
        swtdata.alphaData = new byte[swtdata.data.length];
        int j=2;
        for( int i = 0; i < swtdata.alphaData.length; i++ ) {
            swtdata.alphaData[i] = (byte) 255;
            swtdata.data[i]=(byte)((TRANSPARENT>>(j*8) & 0xFF));
            j--;
            if( j<0 )
                j=2;
        }

        return new Image(display, swtdata);

    }

//    private ImageData awtImageToSWT( Raster raster, Rectangle size ) {
//        if( Display.getCurrent() == null )
//            SWT.error (SWT.ERROR_THREAD_INVALID_ACCESS);
//        ImageData swtdata = null;
//        int width = size.width;
//        int height = size.height;
//        PaletteData palette;
//        int depth;
//
//        depth = 24;
//        palette = new PaletteData(0xFF0000, 0xFF00, 0xFF);
//        swtdata = new ImageData(width, height, depth, palette);
//        swtdata.transparentPixel = TRANSPARENT;
//
//        byte blueT = (byte) ((TRANSPARENT) & 0xFF);
//        byte greenT = (byte) ((TRANSPARENT >> 8) & 0xFF);
//        byte redT = (byte) ((TRANSPARENT >> 16) & 0xFF);
//        // System.out.println("red="+redT+"blue"+blueT+"green"+greenT);
//        // System.out.println("Transparent"+TRANSPARENT);
//
//        // awtImage2.getRGB();
//        int[] awtdata = raster.getPixels(0, 0, width, height, new int[width * height
//                * raster.getNumBands()]);
//        int step = swtdata.depth / 8;
//
//        byte[] data = swtdata.data;
//        int baseindex = 0;
//        for( int y = 0; y < height; y++ ) {
//            int idx = ((0 + y) * swtdata.bytesPerLine) + (0 * step);
//
//            for( int x = 0; x < width; x++ ) {
//                baseindex = (x + (y * width)) * 4;
//
//                if (awtdata[baseindex + 3] == 0) {
//                    data[idx++] = blueT;
//                    data[idx++] = greenT;
//                    data[idx++] = redT;
//                } else {
//                    data[idx++] = (byte) awtdata[baseindex];
//                    data[idx++] = (byte) awtdata[baseindex + 1];
//                    data[idx++] = (byte) awtdata[baseindex + 2];
//                }
//            }
//        }
//
//		return swtdata;
//	}

    public static ImageDescriptor createImageDescriptor( final RenderedImage image,
            final boolean transparent ) {
        checkAccess();
        return new ImageDescriptor(){
            public ImageData getImageData() {
                return createImageData(image, transparent);
            }
        };
    }

    /** Create a buffered image that can be be coverted to SWTland later */
    public static BufferedImage createBufferedImage( int w, int h ) {
        checkAccess();
        return new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR_PRE);
    }

    public static Image createSWTImage( RenderedImage image, boolean transparent ) {
        checkAccess();

        ImageData data;
        if( image instanceof BufferedImage ){
            data=createImageData((BufferedImage)image);
        }else
            data = createImageData(image, transparent);

        return new org.eclipse.swt.graphics.Image(Display.getDefault(), data);
    }


    public static ImageData createImageData( RenderedImage image, boolean transparent ) {
        checkAccess();

        ImageData swtdata = null;
        int width = image.getWidth();
        int height = image.getHeight();
        PaletteData palette;
        int depth;

        depth = 24;
        palette = new PaletteData(0xFF, 0xFF00, 0xFF0000);
        swtdata = new ImageData(width, height, depth, palette);
        Raster raster = image.getData();
        int numbands = raster.getNumBands();
        int[] awtdata = raster.getPixels(0, 0, width, height, new int[width * height * numbands]);
        int step = swtdata.depth / 8;

        byte[] data = swtdata.data;
        swtdata.transparentPixel = -1;
        int baseindex = 0;
        for( int y = 0; y < height; y++ ) {
            int idx = ((0 + y) * swtdata.bytesPerLine) + (0 * step);

            for( int x = 0; x < width; x++ ) {
                int pixel = (x + (y * width));
                baseindex = pixel * numbands;

                data[idx++] = (byte) awtdata[baseindex + 2];
                data[idx++] = (byte) awtdata[baseindex + 1];
                data[idx++] = (byte) awtdata[baseindex];
                if (numbands == 4 && transparent) {
                    swtdata.setAlpha(x, y, awtdata[baseindex + 3]);
                }
            }
        }
        return swtdata;
    }

    public void drawString( String string, int x, int y, int alignx, int aligny ) {
        checkAccess();
        org.eclipse.swt.graphics.Point text = gc.stringExtent(string);
        int w = (int)text.x;
        int h = (int)text.y;

        int x2 = (alignx == 0) ? x - w/2 : (alignx > 0) ? x - w : x;
        int y2 = (aligny == 0) ? y + h/2 : (aligny > 0) ? y + h : y;

        gc.drawString(string, x2, y2,true);
    }

    public void setTransform( AffineTransform transform ) {
        checkAccess();
        double[] matrix=new double[6];
        transform.getMatrix(matrix);
        if( swtTransform==null ){
            swtTransform=new Transform(display,
                    (float)matrix[0], (float)matrix[1], (float)matrix[2],
                    (float)matrix[3], (float)matrix[4], (float)matrix[5] );
        }else{
            swtTransform.setElements(
                    (float)matrix[0], (float)matrix[1], (float)matrix[2],
                    (float)matrix[3], (float)matrix[4], (float)matrix[5] );
        }

        gc.setTransform(swtTransform);
    }

    public int getFontHeight() {
        checkAccess();
        return gc.getFontMetrics().getHeight();
    }

    public int stringWidth( String str ) {
        checkAccess();
        return -1;
    }

    public int getFontAscent() {
        checkAccess();
        return gc.getFontMetrics().getAscent();
    }

    public Rectangle2D getStringBounds( String str ) {
        checkAccess();
        org.eclipse.swt.graphics.Point extent = gc.textExtent(str);

        return new java.awt.Rectangle(0,0,extent.x, extent.y);
    }

    public void drawLine( int x1, int y1, int x2, int y2 ) {
        checkAccess();
        gc.drawLine(x1, y1, x2, y2);
    }

    /**
     * @see net.refractions.udig.ui.graphics.ViewportGraphics#drawImage(java.awt.Image, int, int)
     *      Current version can only draw Image if the image is an RenderedImage
     */
    public void drawImage( java.awt.Image awtImage, int x, int y ) {
        checkAccess();
        RenderedImage rimage = (RenderedImage) awtImage;
        drawImage(rimage, x, y);
    }

    public void drawImage( RenderedImage rimage, int dx1, int dy1, int dx2, int dy2, int sx1,
            int sy1, int sx2, int sy2 ) {
        checkAccess();
        assert rimage != null;
        Image swtImage = null;
        try {
            if( rimage instanceof BufferedImage )
                swtImage = createSWTImage((BufferedImage)rimage);
            else{
                swtImage = createSWTImage(rimage);
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
     * @see net.refractions.udig.ui.graphics.ViewportGraphics#drawImage(java.awt.Image, int, int,
     *      int, int, int, int, int, int)
     */
    public void drawImage( java.awt.Image awtImage, int dx1, int dy1, int dx2, int dy2, int sx1,
            int sy1, int sx2, int sy2 ) {
        checkAccess();
        RenderedImage rimage = (RenderedImage) awtImage;
        drawImage(rimage, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2);
    }

    public void drawImage( Image swtImage, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1,
            int sx2, int sy2 ) {
        checkAccess();

        gc.drawImage(swtImage, sx1, sy1, Math.abs(sx2-sx1), Math.abs(sy2-sy1),
                dx1, dy1, Math.abs(dx2-dx1), Math.abs(dy2-dy1) );

    }

    public void drawImage( Image swtImage, int x, int y ) {
        gc.drawImage(swtImage, x, y);
    }

    public AffineTransform getTransform() {
        checkAccess();
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
     * Creates an image with a depth of 24 and has a transparency channel.
     *
     * @param device device to use for creating the image
     * @param width the width of the final image
     * @param height the height of the final image
     * @return an image with a depth of 24 and has a transparency channel.
     */
    public static Image createDefaultImage( Device device, int width, int height ) {
        checkAccess();
        ImageData swtdata = null;
        PaletteData palette;
        int depth;

        depth = 24;
        palette = new PaletteData(0xFF0000, 0xFF00, 0xFF);
        swtdata = new ImageData(width, height, depth, palette);
        swtdata.transparentPixel = -1;
        swtdata.alpha = -1;
        swtdata.alphaData = new byte[swtdata.data.length];
        for( int i = 0; i < swtdata.alphaData.length; i++ ) {
            swtdata.alphaData[i] = 0;
        }
        return new Image(device, swtdata);

    }

    /**
     * Creates an image descriptor that from the source image.
     *
     * @param image source image
     * @return  an image descriptor that from the source image.
     */
    public static ImageDescriptor createImageDescriptor( final BufferedImage image ) {
        checkAccess();
        return new ImageDescriptor(){
            public ImageData getImageData() {
                return createImageData(image);
            }
        };
    }

    /**
     * Converts a BufferedImage to an SWT Image.  You are responsible for disposing the created image.  This
     * method is faster than creating a SWT image from a RenderedImage so use this method if possible.
     *
     * @param image source image.
     * @return a swtimage showing the source image.
     */
    public static Image createSWTImage( BufferedImage image ) {
        checkAccess();
        ImageData data;
        data = createImageData(image);

        return new org.eclipse.swt.graphics.Image(Display.getDefault(), data);
    }

    /**
     * Creates an ImageData from the 0,0,width,height section of the source BufferedImage.
     * <p>
     * This method is faster than creating the ImageData from a RenderedImage so use this method if possible.
     * </p>
     *
     * @param image source image.
     * @return an ImageData from the 0,0,width,height section of the source BufferedImage
     */
    public static ImageData createImageData( BufferedImage image ) {
        checkAccess();

        if( image.getType()!=BufferedImage.TYPE_3BYTE_BGR ){
            return createImageData((RenderedImage)image, image.getTransparency()!=Transparency.OPAQUE);
        }

        int width=image.getWidth();
        int height=image.getHeight();
        int bands=image.getColorModel().getColorSpace().getNumComponents();
        int depth=24;
        byte[] pixels = ((DataBufferByte) image.getRaster()
                .getDataBuffer()).getData();
        ImageData data = new ImageData(width, height, depth, new PaletteData(
                0x0000ff, 0x00ff00, 0xff0000), width * bands, pixels);
        return data;
    }

    /**
     * Converts a RenderedImage to an SWT Image.  You are responsible for disposing the created image.  This
     * method is slower than calling {@link #createSWTImage(BufferedImage, int, int)}.
     *
     * @param image source image.
     * @param width the width of the final image
     * @param height the height of the final image
     * @return a swtimage showing the 0,0,width,height rectangle of the source image.
     */
    public static Image createSWTImage( RenderedImage image  ) {
        checkAccess();
        ImageData data = createImageData(image);

        return new org.eclipse.swt.graphics.Image(Display.getDefault(), data);
    }

    /**
     * Creates an ImageData from the source RenderedImage.
     * <p>
     * This method is slower than using {@link #createImageData(BufferedImage, int, int)}.
     * </p>
     *
     * @param image source image.
     * @return an ImageData from the source RenderedImage.
     */
    public static ImageData createImageData( RenderedImage image ) {
        checkAccess();

        if( image instanceof BufferedImage )
            return createImageData((BufferedImage)image);
        int depth=24;
        int width=image.getWidth();
        int height=image.getHeight();
        byte[] pixels = ((DataBufferByte) image.getTile(0, 0)
                .getDataBuffer()).getData();
        ImageData data = new ImageData(width, height, depth, new PaletteData(
                0xff0000, 0x00ff00, 0x0000ff), width, pixels);
        return data;
    }
    public Shape getClip() {
        Rectangle clipping = gc.getClipping();
        return new java.awt.Rectangle(clipping.x, clipping.y, clipping.width, clipping.height);
    }

    public void setClipBounds( java.awt.Rectangle newBounds ) {
        gc.setClipping(new Rectangle(newBounds.x, newBounds.y, newBounds.width, newBounds.height));
    }

	public java.awt.Color getBackgroundColor() {
        checkAccess();
		return swt2awt(gc, gc.getBackground());
	}

	public java.awt.Color getColor() {
        checkAccess();
		return swt2awt(gc, gc.getForeground());
	}

	public static java.awt.Color swt2awt(GC gc, Color swt) {
        java.awt.Color awt = new java.awt.Color(swt.getRed(), swt.getGreen(), swt.getBlue(), gc.getAlpha());
		return awt;
	}

    public void drawRoundRect( int x, int y, int width, int height, int arcWidth, int arcHeight ) {
        checkAccess();
        gc.drawRoundRectangle(x, y, width, height, arcWidth, arcHeight);
    }

    private static void checkAccess() {
        if( Display.getCurrent() == null )
            SWT.error (SWT.ERROR_THREAD_INVALID_ACCESS);
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
     */
    public static int toFontStyle( java.awt.Font f ){
        int s = SWT.NORMAL;

        if( f.isItalic()){
            s = s | SWT.ITALIC;
        }
        if( f.isBold() ){
            s = s | SWT.BOLD;
        }
        return s;
    }

    public void setFont(java.awt.Font f){
        Font swtFont;

        int size = (f.getSize()* getDPI() ) / 72;
        int style = toFontStyle( f );

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
     */
    public static java.awt.Font swtFontToAwt( FontData fontData ) {
        int style = java.awt.Font.PLAIN;
        if ((fontData.getStyle() & SWT.BOLD) == SWT.BOLD) {
            style = java.awt.Font.BOLD;
        }
        if ((fontData.getStyle() & SWT.ITALIC) == SWT.ITALIC) {
            style |= java.awt.Font.ITALIC;
        }

        java.awt.Font font = new java.awt.Font(fontData.getName(), style, fontData.getHeight());
        return font;
    }

    /**
     * Converts an AWTFont to a SWT Font
     *
     * @param font and AWT Font
     * @param fontRegistry
     * @return the equivalent SWT Font
     */
    public static org.eclipse.swt.graphics.Font awtFontToSwt( java.awt.Font font, FontRegistry fontRegistry ) {
        String fontName = font.getFontName();
        if (fontRegistry.hasValueFor(fontName)) {
            return fontRegistry.get(fontName);
        }

        int style = 0;
        if ((font.getStyle() & java.awt.Font.BOLD) == java.awt.Font.BOLD) {
            style = SWT.BOLD;
        }
        if ((font.getStyle() & java.awt.Font.ITALIC) == java.awt.Font.ITALIC) {
            style |= SWT.ITALIC;
        }
        FontData data = new FontData(fontName, font.getSize(), style);
        fontRegistry.put(fontName, new FontData[]{data});
        return fontRegistry.get(fontName);
    }
}
