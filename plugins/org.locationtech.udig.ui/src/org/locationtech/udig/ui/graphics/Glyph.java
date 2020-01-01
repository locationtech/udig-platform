/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.ui.graphics;

import java.awt.Color;

import org.locationtech.udig.ui.Drawing;
import org.locationtech.udig.ui.PlatformGIS;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.geotools.styling.Rule;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

/**
 * Utility methods to create common ImageDescriptors.
 * 
 * @author jgarnett
 * @since 0.7.0
 */
public class Glyph {

    private final static int DEFAULT_WIDTH = 16;
    private final static int DEFAULT_HEIGHT = 16;
    static final int DEFAULT_DEPTH = 24;
        
	public static ImageDescriptor push( final ImageDescriptor icon ){	
		return new ImageDescriptor(){
			@Override
			public ImageData getImageData() {
				ImageData push = icon.getImageData();
				if( !push.palette.isDirect){
					RGB[] rgb=new RGB[push.palette.colors.length];
					System.arraycopy(push.palette.colors, 0, rgb, 0, push.palette.colors.length);
					rgb[push.transparentPixel]=Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW).getRGB();
					push.palette=new PaletteData(rgb);
					push.transparentPixel=-1;
                    
                    createBorder(push);
                    
					return push;
				}
				int pushColour=push.palette.getPixel(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW).getRGB());
				
				for( int x = 0; x<push.width; x++ )
					for( int y = 0; y<push.height; y++ ){
						if( push.getAlpha(x, y) == 0 ){
							push.setAlpha(x, y, 255 );
							push.setPixel( x, y, pushColour );
						}
						if( push.getPixel(x,y)==push.transparentPixel ){
							push.setPixel( x, y, pushColour);							
						}
					}
				return push;
			}

            private void createBorder( ImageData push ) {
                for( int y = 0; y<push.height; y++ ){
                    for( int x = 0; x<push.width; x++ ){
                        if( y==0 || x==0 )
                            push.setPixel(x,y,0);
                    }
                }
            }
			
		};		
	}
    /**
     * Create a transparent image, this is a *real* resource against the
     * provided display.
     * 
     * @param display
     * @param rgb
     * @return
     */
    public static Image image( Display display ) {
        PaletteData palette = new PaletteData(0xFF0000, 0xFF00, 0xFF);
        ImageData imageData = new ImageData(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_DEPTH, palette);
        imageData.transparentPixel = palette.getPixel(display.getSystemColor(
                SWT.COLOR_WIDGET_BACKGROUND).getRGB());

        return new Image(display, imageData);
    }
    
    public final static int WHITE = 0xFFFFFF;
    private static final Color DEFAULT_BORDER = new Color(0,0,0);
    private static final Color DEFAULT_FILL = new Color(27,158,119, 255);
    // public final static int CLEAR = 0x220000|0x2200|0x22;
    
    /** Utility class for working with Images, Features and Styles */
    static Drawing d = Drawing.create();    
    
    /**
     * Convert Color to to SWT 
     * @param color 
     * 
     * @return SWT Color
     */     
    static org.eclipse.swt.graphics.Color color( Color color ){
        Display display = PlatformUI.getWorkbench().getDisplay();        
        return new org.eclipse.swt.graphics.Color(display, color.getRed(), color.getGreen(), color.getBlue() );        
    }
    static ImageData extractImageDataAndDispose( Image image ) {
        ImageData data = (ImageData) image.getImageData();        
        image.dispose();        
        return data;
    }
    
    /**
     * Render a icon based on the current style.
     * <p>
     * Simple render of point in the center of the screen.
     * </p>
     * @param style
     * @return Icon representing style applyed to an image
     */
    public static ImageDescriptor point( final Rule rule ) {
        return new ImageDescriptor(){
            public ImageData getImageData() {
                Image image = null; 
                try {
                Display display =PlatformUI.getWorkbench().getDisplay();
                
                image = new Image(display, DEFAULT_WIDTH, DEFAULT_HEIGHT);
                d.drawDirect( image, display, d.feature(d.point(7,7)), rule );                     
                return extractImageDataAndDispose( image );
                } catch(RuntimeException ex) {
                    if(image != null && !image.isDisposed()) {
                        image.dispose();
                    }
                    throw ex;
                }
            }
        };
    }
    /**
     * Icon for point data in the provided color
     * <p>
     * XXX: Suggest point( SLD style ) at a later time.
     * </p>
     * @return ImageDescriptor
     */    
    public static ImageDescriptor point() {
    	return point(DEFAULT_BORDER, DEFAULT_FILL);
    }

    /**
     * Icon for point data in the provided color
     * <p>
     * XXX: Suggest point( SLD style ) at a later time.
     * </p>
     * @param color
     * @param fill
     * @return ImageDescriptor
     */    
    public static ImageDescriptor point( final Color color, final Color fill ) {
        return new ImageDescriptor(){
            public ImageData getImageData() {
                Image swtImage = null;
                try {
                Display display =PlatformUI.getWorkbench().getDisplay();
                
                swtImage = new Image(display, DEFAULT_WIDTH, DEFAULT_HEIGHT);
                GC gc = new GC(swtImage);                
                gc.setAntialias(SWT.ON);
                gc.setLineCap( SWT.CAP_SQUARE );
                gc.setLineStyle( SWT.LINE_SOLID );
                gc.setLineWidth( 1 );
                
                Color c = color;
                Color f = fill;
                
                if( c == null && f == null ){ // only need default if both are empty                    
                    c = Color.BLACK;
                    f = Color.LIGHT_GRAY;
                }
                if( f != null ){
                    gc.setBackground( color( f ) );
                    gc.setAlpha(f.getAlpha());
                    gc.fillRectangle( 8,7, 5, 5 );
                }
                if( c != null ){
                    gc.setForeground( color( c ) );
                    gc.setAlpha(c.getAlpha());
                    gc.drawRectangle( 8,7, 5, 5 );
                }
                ImageData clone = (ImageData) swtImage.getImageData().clone();                
                swtImage.dispose();
                gc.dispose();                
                return clone;
                } catch(RuntimeException ex) {
                    if(swtImage != null && !swtImage.isDisposed()) {
                        swtImage.dispose();
                    }
                    throw ex;
                }
            }
        };
    } 
    /**
     * Complex render of Geometry allowing presentation of point, line and polygon styles.
     * <p>
     * Layout:<pre><code>
     *    1 2 3 4 5 6 7 8 9101112131415
     *   0
     *  1          LL                 L  
     *  2          L L                L
     *  3         L  L               L                   
     *  4        L    L             L  
     *  5        L     L            L  
     *  6       L      L           L   
     *  7      L        L         L    
     *  8      L         L        L    
     *  9     L          L       L     
     * 10    L            L     L      
     * 11    L             L    L      
     * 12   L              L   L       
     * 13  L                L L        
     * 14  L                 LL            
     * 15
     * </code><pre>
     * </p>
     */
    public static ImageDescriptor line() {
    	return line(DEFAULT_BORDER,1);
    }

    /**
     * Complex render of Geometry allowing presentation of point, line and polygon styles.
     * <p>
     * Layout:<pre><code>
     *    1 2 3 4 5 6 7 8 9101112131415
     *   0
     *  1          LL                 L  
     *  2          L L                L
     *  3         L  L               L                   
     *  4        L    L             L  
     *  5        L     L            L  
     *  6       L      L           L   
     *  7      L        L         L    
     *  8      L         L        L    
     *  9     L          L       L     
     * 10    L            L     L      
     * 11    L             L    L      
     * 12   L              L   L       
     * 13  L                L L        
     * 14  L                 LL            
     * 15
     * </code><pre>
     * </p>
     * @param style 
     * @return Icon representing geometry style
     */
    public static ImageDescriptor line( final Rule rule ) {
        final SimpleFeature feature=d.feature(d.line(new int[]{1,14, 6,0, 11,14, 15,1}));
        return new ImageDescriptor(){
            public ImageData getImageData() {
                Image image = null;
                try {
                Display display =PlatformUI.getWorkbench().getDisplay();
                
                image = new Image(display, DEFAULT_WIDTH, DEFAULT_HEIGHT);
                d.drawDirect( image, display,
                        feature,
                        rule );                
                return extractImageDataAndDispose( image );
                } catch(RuntimeException ex) {
                    if(image != null && !image.isDisposed()) {
                        image.dispose();
                    }
                    throw ex;
                }
            }
        };       
    }
    /**
     * Icon for linestring in the provided color and width.
     * <p>
     * XXX: Suggest line( SLD style ) at a later time.
     * </p>
     * @param black
     * @return Icon
     */
    public static ImageDescriptor line( Color color, int width ) {
        Color color2 = color;
        int width2 = width;
        if (color2 == null) {
            color2 = Color.BLACK;
        }
        
        if (width2 <= 0) {
            width2 = 1;
        }
        
        final int finalWidth = width2;
        final Color finalColor = color2;
                
        return new ImageDescriptor(){
            public ImageData getImageData() {
                Image swtImage = null;
                try {
                Display display =PlatformUI.getWorkbench().getDisplay();
                
                swtImage = new Image(display, DEFAULT_WIDTH, DEFAULT_HEIGHT);
                GC gc = new GC(swtImage);
                gc.setAntialias(SWT.ON);

                gc.setLineCap( SWT.CAP_SQUARE );
                gc.setLineStyle( SWT.LINE_SOLID );
                
                gc.setForeground( color( finalColor ) );
                gc.setAlpha(finalColor.getAlpha());
                gc.setLineWidth( finalWidth );
                gc.drawLine(1, 13, 6, 2);
                gc.drawLine(6, 2, 9, 13);
                gc.drawLine(9, 13, 14, 2);                
                
                ImageData clone = (ImageData) swtImage.getImageData().clone();
                
                swtImage.dispose();
                
                return clone;
                } catch(RuntimeException ex) {
                    if(swtImage != null && !swtImage.isDisposed()) {
                        swtImage.dispose();
                    }
                    throw ex;
                }
            }
        };
    }

    /**
     * Complex render of Geometry allowing presentation of point, line and polygon styles.
     * <p>
     * Layout:<pre><code>
     *    1 2 3 4 5 6 7 8 9101112131415
     *   0
     *  1 
     *  2
     *  3           L                 L                  
     *  4       p  L L           PPPPPP
     *  5         L   L     PPPPP   L p
     *  6        L     LPPPP       L  p
     *  7       L    PPPL         L   p
     *  8      L   PP    L       L    p
     *  9     L   P       L     L     P
     * 10    L   P         L   L      P
     * 11   L   P           L L       P
     * 12  L   P             L        P
     * 13      p                      P
     * 14      PPPPPPPPPPPPPPPPPPPPPPPP    
     * 15
     * </code><pre>
     * </p>
     * @param style 
     * @return Icon representing geometry style
     */
    public static ImageDescriptor geometry( final Rule rule ) {
        return new ImageDescriptor(){
            public ImageData getImageData() {
                Image image = null;
                try {
                    Display display = PlatformUI.getWorkbench().getDisplay();

                    image = new Image(display, DEFAULT_WIDTH, DEFAULT_HEIGHT);
                    d.drawDirect(image, display, d.feature(d.line(new int[]{0, 12, 6, 3, 11, 12, 15, 3})), rule);
                    d.drawDirect(image, display, d.feature(d.point(4, 4)), rule);

                    return extractImageDataAndDispose(image);
                } catch (RuntimeException ex) {
                    if (image != null && !image.isDisposed()) {
                        image.dispose();
                    }
                    throw ex;
                }
            }
        };       
    }
    /**
     * Icon for generic Geometry or Geometry Collection.
     * @param color 
     * @param fill 
     * 
     * @return Icon
     */
    public static ImageDescriptor geometry( final Color color, final Color fill ) {
        return new ImageDescriptor(){
            public ImageData getImageData() {
                
                Image swtImage = null;
                try {
                    Display display =PlatformUI.getWorkbench().getDisplay();
                    
                    swtImage = new Image(display, DEFAULT_WIDTH, DEFAULT_HEIGHT);
                    GC gc = new GC(swtImage);
                    gc.setAntialias(SWT.ON);
                    gc.setLineCap( SWT.CAP_SQUARE );
                    gc.setLineStyle( SWT.LINE_SOLID );
                    gc.setLineWidth( 1 );
                    
                    Color c = color;
                    Color f = fill;
                    
                    if( c == null && f == null ){ // only need default if both are empty                    
                        c = Color.BLACK;
                        f = Color.LIGHT_GRAY;
                    }
                    if( f != null ){
                        gc.setBackground( color( f ) );
                        gc.setAlpha(f.getAlpha());
                        gc.fillRoundRectangle( 2,1, 13, 13, 2, 2 );
                    }
                    if( c != null ){
                        gc.setForeground( color( c ) );
                        gc.setAlpha(c.getAlpha());
                        gc.drawRoundRectangle( 2,1, 13, 13, 2, 2 );
                    }
                    ImageData clone = (ImageData) swtImage.getImageData().clone();                
                    swtImage.dispose();
                    
                    return clone;
                } catch(RuntimeException ex) {
                    if(swtImage != null && !swtImage.isDisposed()) {
                        swtImage.dispose();
                    }
                    throw ex;
                }
            }
        };
    }     

    /**
     * Render of a polygon allowing style.
     * <p>
     * Layout:<pre><code>
     *    1 2 3 4 5 6 7 8 9101112131415
     *   0
     *  1             
     *  2                      PPPPPPPP
     *  3                PPPPPP       P                  
     *  4           PPPPPP            P
     *  5        PPP                  p
     *  6      PP                     p
     *  7     P                       p
     *  8    P                        p
     *  9   P                         P
     * 10   P                         P
     * 11  P                          P
     * 12  P                          P
     * 13  P                          P
     * 14  PPPPPPPPPPPPPPPPPPPPPPPPPPPP    
     * 15
     * </code><pre>
     * </p>
     * @param style 
     * @return Icon representing geometry style
     */
    public static ImageDescriptor polygon( final Rule rule ) {
        return new ImageDescriptor(){
            public ImageData getImageData() {
                final Image[] image = new Image[1];
                try {
                	final Display display =PlatformUI.getWorkbench().getDisplay();
                PlatformGIS.syncInDisplayThread(display, new Runnable(){
                    public void run() {
                        image[0] = new Image(display, DEFAULT_WIDTH, DEFAULT_HEIGHT);
                        try {
                            d.drawDirect( image[0], display,
                                d.feature(d.polygon(new int[]{1,14, 3,9, 4,6,  6,4,  9,3, 14,1, 14,14})),
                                rule );
                        }
                        catch (Throwable npe ){
                            // unavailable
                        }
                    }
                });
                
                    return extractImageDataAndDispose( image[0] );
                } catch(RuntimeException ex){
                    if(image[0] != null && !image[0].isDisposed()) {                    
                        image[0].dispose();
                    }
                    throw ex;
                }
            }
        };       
    }
  
    /**
     * Icon for polygon in default border, fill and width
     */
    public static ImageDescriptor polygon() {
    	return polygon(DEFAULT_BORDER, DEFAULT_FILL,1);
    }

    	/**
     * Icon for polygon in provided border, fill and width
     * 
     * @param black
     * @param gray
     * @param i
     * @return
     */
    public static ImageDescriptor polygon( final Color color, final Color fill, final int width ) {        
        return new ImageDescriptor(){
            public ImageData getImageData() {
                Image swtImage = null;
                try {
                Display display =PlatformUI.getWorkbench().getDisplay();
                
                swtImage = new Image(display, DEFAULT_WIDTH, DEFAULT_HEIGHT);
                GC gc = new GC(swtImage);
                gc.setAntialias(SWT.ON);
                gc.setLineCap( SWT.CAP_SQUARE );
                gc.setLineStyle( SWT.LINE_SOLID );
                
                org.eclipse.swt.graphics.Color t = null;
                
                Color c = color;
                Color f = fill;
                int w = width > 0 ? width : 1;
                
                if( c == null && f == null ){ // only need default if both are empty                    
                    c = Color.BLACK;
                    f = Color.LIGHT_GRAY;
                }
                if( f != null ){
                    gc.setBackground( t = color( f ) );
                    t.dispose();
                }
                if( c != null ){
                    gc.setForeground( t = color( c ) );
                    t.dispose();
                }                
                gc.setLineWidth( w );
                
                int[] points = { 1,14, 3,9, 4,6,  6,4,  9,3, 14,1, 14,14 };

                gc.setAlpha(f.getAlpha());
                gc.fillPolygon(points);
                gc.setAlpha(c.getAlpha());
                gc.drawPolygon(points);
                
                ImageData clone = (ImageData) swtImage.getImageData().clone();
                swtImage.dispose();
                return clone;
                } finally {
                    if(swtImage != null && !swtImage.isDisposed()) 
                        swtImage.dispose();
                }
            }
        };
    }

    /**
     * Icon for grid data, small grid made up of provided colors.
     * <p>
     * Layout:<pre><code>
     *    0 1 2 3 4 5 6 7 8 9 101112131415
     *  0  
     *  1   AAAAAAAAAAAAABBBBBBBBBBBBBB           
     *  2   AAAAAAAAAAAAABBBBBBBBBBBBBB
     *  3   AAAAAAAAAAAAABBBBBBBBBBBBBB                  
     *  4   AAAAAAAAAAAAABBBBBBBBBBBBBB
     *  5   AAAAAAAAAAAAABBBBBBBBBBBBBB
     *  6   AAAAAAAAAAAAABBBBBBBBBBBBBB
     *  7   AAAAAAAAAAAAABBBBBBBBBBBBBB
     *  8   CCCCCCCCCCCCCDDDDDDDDDDDDDD
     *  9   CCCCCCCCCCCCCDDDDDDDDDDDDDD
     * 10   CCCCCCCCCCCCCDDDDDDDDDDDDDD
     * 11   CCCCCCCCCCCCCDDDDDDDDDDDDDD
     * 12   CCCCCCCCCCCCCDDDDDDDDDDDDDD
     * 13   CCCCCCCCCCCCCDDDDDDDDDDDDDD
     * 14   CCCCCCCCCCCCCDDDDDDDDDDDDDD
     * 15
     * </code><pre>
     * </p>
     * @param a
     * @param b
     * @param c
     * @param d1
     * @return Icon representing a grid
     * 
     */
    public static ImageDescriptor grid( Color a, Color b, Color c, Color d1) {
        if (a == null) {
            a = Color.BLACK;
        }        
        if (b == null) {
            b = Color.DARK_GRAY;
        }
        
        if (c == null) {
            c = Color.LIGHT_GRAY;
        }
        
        if (d1 == null) {
            d1 = Color.WHITE;
        }        
        final Color finalA = a;
        final Color finalB = b;
        final Color finalC = c;
        final Color finalD = d1;
        
        return new ImageDescriptor(){
            public ImageData getImageData() {
                              
                Display display =PlatformUI.getWorkbench().getDisplay();
                
                Image swtImage = new Image(display, DEFAULT_WIDTH, DEFAULT_HEIGHT);
                GC gc = new GC(swtImage);
                gc.setAntialias(SWT.ON);
                org.eclipse.swt.graphics.Color c = null;
                
                gc.setBackground( c = color( finalA ) );
                gc.fillRectangle( 0, 0, 7, 7);
                c.dispose();
                
                gc.setBackground( c = color( finalB ));
                gc.fillRectangle( 7, 0, 15, 7 ); 
                c.dispose();
                
                gc.setBackground( c = color( finalC ));
                gc.fillRectangle( 0, 7, 7, 15 );
                c.dispose();
                
                gc.setBackground( c = color( finalD ));
                gc.fillRectangle( 7, 7, 15, 15 );                
                c.dispose();
                
                gc.setForeground( c = color( Color.BLACK ) );
                gc.drawRectangle( 0, 0, 7, 7 );
                gc.drawRectangle( 0, 0, 15, 7 );
                gc.drawRectangle( 0, 0, 7, 15 );
                gc.drawRectangle( 0, 0, 15, 15 );
                c.dispose();
                
                ImageData clone = (ImageData) swtImage.getImageData().clone();                
                swtImage.dispose();
                
                return clone;
            }
        };
    }

    /**
     * Render of a color swatch allowing style.
     * <p>
     * Layout:<pre><code>
     *    0 1 2 3 4 5 6 7 8 9 101112131415
     *  0  
     *  1  dddddddddddddddddddddddddddd           
     *  2 dCCCCCCCCCCCCCCCCCCCCCcCCCCCCd
     *  3 dCCCCCCCCCCCCCCCCCCCCCCcCCCCCd                  
     *  4 dCCCCCCCCCCCCCCCCCCCCCCCcCCCCd
     *  5 dCCCCCCCCCCCCCCCCCCCCCCCCcCCCd
     *  6 dCCCCCCCCCCCCCCCCCCCCCCCCCcCCd
     *  7 dCCCCCCCCCCCCCCCCCCCCCCCCCCcCd
     *  8 dCcCCCCCCCCCCCCCCCCCCCCCCCCCCd
     *  9 dCCcCCCCCCCCCCCCCCCCCCCCCCCCCd
     * 10 dCCCcCCCCCCCCCCCCCCCCCCCCCCCCd
     * 11 dCCCCcCCCCCCCCCCCCCCCCCCCCCCCd
     * 12 dCCCCCcCCCCCCCCCCCCCCCCCCCCCCd
     * 13 ddCCCCCcCCCCCCCCCCCCCCCCCCCCdd
     * 14  ddddddddddddddddddddddddddd
     * 15    
     * </code><pre>
     * </p>
     * @param style 
     * @return Icon representing geometry style
     */
    public static ImageDescriptor swatch( Color c ) {
        Color c2=c;
        if( c==null ){
            c2=Color.GRAY;
        }else{
            c2=c;
        }
        
        final Color color=c2;
        
        int saturation = color.getRed() + color.getGreen() + color.getBlue();               
        final Color contrast = saturation < 384 ? c.brighter() : c.darker();        
        return new ImageDescriptor(){
            public ImageData getImageData() {
                              
                Display display =PlatformUI.getWorkbench().getDisplay();
                
                Image swtImage = new Image(display, DEFAULT_WIDTH, DEFAULT_HEIGHT);
                GC gc = new GC(swtImage);
                gc.setAntialias(SWT.ON);
                org.eclipse.swt.graphics.Color swtColor = color( color );
                try{
	                gc.setBackground( swtColor );
	                gc.fillRoundRectangle( 0, 0, 14, 14, 2, 2);
                }
                finally {
                	swtColor.dispose();
                }
                try {
                	swtColor = color( contrast );
                	gc.setForeground( swtColor );
                	gc.drawRoundRectangle( 0, 0, 14, 14, 2, 2 );                 
                } finally {
                	swtColor.dispose();
                }
                ImageData clone = (ImageData) swtImage.getImageData().clone();                
                swtImage.dispose();
                
                return clone;
            }
        };     
    }  
    /**
     * Icon for grid data, small grid made up of provided colors.
     * Layout:<pre><code>
     *    0 1 2 3 4 5 6 7 8 9 101112131415
     *  0  
     *  1 AABBCDEEFfGgHhIiJjKkllmmnnoopp           
     *  2 AABBCDEEFfGgHhIiJjKkllmmnnoopp
     *  3 AABBCDEEFfGgHhIiJjKkllmmnnoopp                 
     *  4 AABBCDEEFfGgHhIiJjKkllmmnnoopp
     *  5 AABBCDEEFfGgHhIiJjKkllmmnnoopp
     *  6 AABBCDEEFfGgHhIiJjKkllmmnnoopp
     *  7 AABBCDEEFfGgHhIiJjKkllmmnnoopp
     *  8 AABBCDEEFfGgHhIiJjKkllmmnnoopp
     *  9 AABBCDEEFfGgHhIiJjKkllmmnnoopp
     * 10 AABBCDEEFfGgHhIiJjKkllmmnnoopp
     * 11 AABBCDEEFfGgHhIiJjKkllmmnnoopp
     * 12 AABBCDEEFfGgHhIiJjKkllmmnnoopp
     * 14 AABBCDEEFfGgHhIiJjKkllmmnnoopp
     * 15
     * </code><pre>
     * </p>
     * @param c palette of colors
     * @return Icon representing a palette
     * 
     */
    public static ImageDescriptor palette( Color c[]) {
    	final Color[] colors = new Color[16];
    	Color color = Color.GRAY;
    	if( c == null ){
    		for( int i=0; i<16; i++) color = Color.GRAY;
    	}
    	else {
    		for( int i=0; i<16; i++) {
    			int lookup = (i*c.length)/16;
    			if( c[ lookup ] != null ) color = c[ lookup ];
    			colors[i] = color;    			
    		}
    	}
        return new ImageDescriptor(){
            public ImageData getImageData() {
                              
                Display display =PlatformUI.getWorkbench().getDisplay();
                
                Image swtImage = new Image(display, DEFAULT_WIDTH, DEFAULT_HEIGHT);
                GC gc = new GC(swtImage);
                gc.setAntialias(SWT.ON);
                org.eclipse.swt.graphics.Color swtColor = null;
                
                for( int i=0; i<16;i++){
                	try {
                		swtColor = color( colors[i] );
                		gc.setForeground( swtColor );
                		gc.drawLine(i,0,i,15);
                	}
                	finally {
                		swtColor.dispose();
                	}
                }
                try {
                	swtColor = color( Color.GRAY );
                	gc.setForeground( swtColor );                	
                	gc.drawRoundRectangle( 0, 0, 14, 14, 2, 2 );
                }
                finally {
                	swtColor.dispose();
                }
                                
                ImageData clone = (ImageData) swtImage.getImageData().clone();                
                swtImage.dispose();
                
                return clone;
            }
        };
    }
    public static ImageDescriptor icon( SimpleFeatureType ft ) {
        if( ft==null || ft.getGeometryDescriptor()==null )
            return null;
        
        Class<?> geomType = ft.getGeometryDescriptor().getType().getBinding();
        return icon(geomType);
    }
    public static ImageDescriptor icon(Class<?> geomType) {
		if( Point.class.isAssignableFrom(geomType) 
                || MultiPoint.class.isAssignableFrom(geomType) ){
            return point(DEFAULT_BORDER, DEFAULT_FILL);
        }
        
        if( LineString.class.isAssignableFrom(geomType) 
                || MultiLineString.class.isAssignableFrom(geomType) 
                || LinearRing.class.isAssignableFrom(geomType)){
            return line(DEFAULT_BORDER, 1);
        }
        
        if( Polygon.class.isAssignableFrom(geomType) 
                || MultiPolygon.class.isAssignableFrom(geomType) ){
            return polygon(DEFAULT_BORDER, DEFAULT_FILL, 1);
        }
        
        return geometry(DEFAULT_BORDER, DEFAULT_FILL);
    }    
}
