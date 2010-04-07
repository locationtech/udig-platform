/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.ui.graphics;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Transparency;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;

import javax.swing.Icon;

import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * Provides a bunch of Utility methods for converting between AWT and SWT
 * 
 * @author jesse
 * @since 1.1.0
 */
public final class AWTSWTImageUtils {

    /**
     * Convert an SWT Image to a BufferedImage - this one rips the ImageData
     * out of the live Image; and then copies it into a BufferedImage.
     * 
     */
    public static BufferedImage convertToAWT( Image image ){
        ImageData data = image.getImageData();
        return convertToAWT( data );
    }
    /**
     * Converts an SWT ImageData to a BufferedImage - It isn't incredibly optimized so be careful :)
     * <p>
     * We should be able to use use JAI to produce a RenderedImage around
     * the provided ImageData. It wound be a buffered image but it will be something
     * that can efficiently be drawn when printing.
     * </p>
     * @return a Buffered Image
     */
    public static BufferedImage convertToAWT(ImageData data) {
      ColorModel colorModel = null;
      PaletteData palette = data.palette;
      if (palette.isDirect) {
          // no alpha data?
          if(data.alphaData==null) {
              colorModel = new DirectColorModel(32, 0x00ff0000, 0x0000ff00, 0x000000ff, 0xff000000);               
              BufferedImage bufferedImage = new BufferedImage(colorModel, 
                      colorModel.createCompatibleWritableRaster(data.width, data.height), 
                      false, null);               
              WritableRaster raster = bufferedImage.getRaster();              
              int[] pixelArray = new int[4];
              for (int y = 0; y < data.height; y++) {
                  for (int x = 0; x < data.width; x++) {
                      int pixel = data.getPixel(x, y);                        
                      RGB rgb = palette.getRGB(pixel);
                      pixelArray[0] = rgb.red;
                      pixelArray[1] = rgb.green;
                      pixelArray[2] = rgb.blue;
                      if (pixel == data.transparentPixel) {
                          pixelArray[3]=0;  // transparent
                      }
                      else {
                          pixelArray[3]=255; // opaque
                      }
                      raster.setPixels(x, y, 1, 1, pixelArray);                       
                  }
              }
                int w = bufferedImage.getWidth();
                int h = bufferedImage.getHeight();
                Raster ras = bufferedImage.getData();
                for( int i = 0; i < w; i++ ) {
                    for( int j = 0; j < h; j++ ) {
                        double[] pixel = ras.getPixel(i, j, new double[4]);
                    }
                }

                return bufferedImage;
          }
          
          // image has alpha data, preserve it
          else {
              colorModel = new DirectColorModel(32, 0x00ff0000, 0x0000ff00, 0x000000ff, 0xff000000);               
              BufferedImage bufferedImage = new BufferedImage(colorModel, 
                      colorModel.createCompatibleWritableRaster(data.width, data.height), 
                      false, null);               
              WritableRaster raster = bufferedImage.getRaster();              
              int[] pixelArray = new int[4];
              for (int y = 0; y < data.height; y++) {
                  for (int x = 0; x < data.width; x++) {
                      int pixel = data.getPixel(x, y);                        
                      RGB rgb = palette.getRGB(pixel);
                      pixelArray[0] = rgb.red;
                      pixelArray[1] = rgb.green;
                      pixelArray[2] = rgb.blue;
                      pixelArray[3]=data.getAlpha(x,y);
                      raster.setPixels(x, y, 1, 1, pixelArray);                       
                  }
              }
              return bufferedImage;
          }
          
          //la paleta swt no es directa ¿?¿?¿?
          
//        ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_sRGB);
//        colorModel = new DirectColorModel(colorSpace, data.depth, palette.redMask,
//                palette.greenMask, palette.blueMask, 0, false, DataBuffer.TYPE_INT);
////        colorModel = new DirectColorModel(data.depth, palette.redMask,
////            palette.greenMask, palette.blueMask);
//        BufferedImage bufferedImage = new BufferedImage(colorModel,
//            colorModel.createCompatibleWritableRaster(data.width,
//                data.height), false, null);
//        WritableRaster raster = bufferedImage.getRaster();
//        int[] pixelArray = new int[3];
//        for (int y = 0; y < data.height; y++) {
//          for (int x = 0; x < data.width; x++) {
//            int pixel = data.getPixel(x, y);
//            RGB rgb = palette.getRGB(pixel);
//            pixelArray[0] = rgb.red;
//            pixelArray[1] = rgb.green;
//            pixelArray[2] = rgb.blue;
//            raster.setPixels(x, y, 1, 1, pixelArray);
//          }
//        }
//        return bufferedImage;
      } else {
        RGB[] rgbs = palette.getRGBs();
        byte[] red = new byte[rgbs.length];
        byte[] green = new byte[rgbs.length];
        byte[] blue = new byte[rgbs.length];
        for (int i = 0; i < rgbs.length; i++) {
          RGB rgb = rgbs[i];
          red[i] = (byte) rgb.red;
          green[i] = (byte) rgb.green;
          blue[i] = (byte) rgb.blue;
        }
        if (data.transparentPixel != -1) {
          colorModel = new IndexColorModel(data.depth, rgbs.length, red,
              green, blue, data.transparentPixel);
        } else {
          colorModel = new IndexColorModel(data.depth, rgbs.length, red,
              green, blue);
        }
        BufferedImage bufferedImage = new BufferedImage(colorModel,
            colorModel.createCompatibleWritableRaster(data.width,
                data.height), false, null);
        WritableRaster raster = bufferedImage.getRaster();
        int[] pixelArray = new int[1];
        for (int y = 0; y < data.height; y++) {
          for (int x = 0; x < data.width; x++) {
            int pixel = data.getPixel(x, y);
            pixelArray[0] = pixel;
            raster.setPixel(x, y, pixelArray);
          }
        }
        return bufferedImage;
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
        AWTSWTImageUtils.checkAccess();
        PathIterator p = shape.getPathIterator(SWTGraphics.AFFINE_TRANSFORM);
    
        return AWTSWTImageUtils.createPath(p, device);
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
     * Creates an image with a depth of 24 and has a transparency channel.
     *
     * @param device device to use for creating the image
     * @param width the width of the final image
     * @param height the height of the final image 
     * @return an image with a depth of 24 and has a transparency channel.
     */
    public static Image createDefaultImage( Device device, int width, int height ) {
        AWTSWTImageUtils.checkAccess();
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
    public static Image createDefaultImage( Display display, int width, int height ) {
        AWTSWTImageUtils.checkAccess();
        ImageData swtdata = null;
        PaletteData palette;
        int depth;
    
        depth = 24;
        palette = new PaletteData(0xFF0000, 0xFF00, 0xFF);
        swtdata = new ImageData(width, height, depth, palette);
        swtdata.transparentPixel = 0;
        //swtdata.transparentPixel = -1;
        swtdata.alpha = -1;
        swtdata.alphaData = new byte[swtdata.data.length];
        int j=2;
        for( int i = 0; i < swtdata.alphaData.length; i++ ) {
            swtdata.alphaData[i] = (byte) 255;
        }
        
        return new Image(display, swtdata);  
    }

    /** Create a buffered image that can be be converted to SWTland later */
    public static BufferedImage createBufferedImage( int w, int h ) {
        //AWTSWTImageUtils.checkAccess();
        return new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR_PRE);
    }

    public static Image createSWTImage( RenderedImage image, boolean transparent ) {
        AWTSWTImageUtils.checkAccess();
        
        ImageData data; 
        if( image instanceof BufferedImage ){
            data=AWTSWTImageUtils.createImageData((BufferedImage)image);
        }else
            data = AWTSWTImageUtils.createImageData(image, transparent);
    
        return new org.eclipse.swt.graphics.Image(Display.getDefault(), data);
    }

    public static ImageData createImageData( RenderedImage image, boolean transparent ) {
        AWTSWTImageUtils.checkAccess();
    
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

    public static ImageDescriptor createImageDescriptor( final RenderedImage image,
            final boolean transparent ) {
        AWTSWTImageUtils.checkAccess();
        return new ImageDescriptor(){
            public ImageData getImageData() {
                return createImageData(image, transparent);
            }
        };
    }

    /**
     * Creates an image descriptor that from the source image.
     *
     * @param image source image
     * @return  an image descriptor that from the source image.
     */
    public static ImageDescriptor createImageDescriptor( final BufferedImage image ) {
        AWTSWTImageUtils.checkAccess();
        return new ImageDescriptor(){
            public ImageData getImageData() {
                return AWTSWTImageUtils.createImageData(image);
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
    public static Image convertToSWTImage( BufferedImage image ) {
        AWTSWTImageUtils.checkAccess();
        ImageData data;
        data = AWTSWTImageUtils.createImageData(image);
        
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
        AWTSWTImageUtils.checkAccess();
    
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
     * method is slower than calling {@link SWTGraphics#createSWTImage(BufferedImage, int, int)}.
     *
     * @param image source image.
     * @param width the width of the final image
     * @param height the height of the final image
     * @return a swtimage showing the 0,0,width,height rectangle of the source image.
     */
    public static Image createSWTImage( RenderedImage image  ) {
        AWTSWTImageUtils.checkAccess();
        ImageData data = AWTSWTImageUtils.createImageData(image);
    
        return new org.eclipse.swt.graphics.Image(Display.getDefault(), data);
    }

    /**
     * Creates an ImageData from the source RenderedImage.
     * <p>
     * This method is slower than using {@link createImageData}.
     * </p>
     *
     * @param image source image.
     * @return an ImageData from the source RenderedImage.
     */
    public static ImageData createImageData( RenderedImage image ) {
        AWTSWTImageUtils.checkAccess();
    
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

    public static java.awt.Color swtColor2awtColor(GC gc, Color swt) {
        java.awt.Color awt = new java.awt.Color(swt.getRed(), swt.getGreen(), swt.getBlue(), gc.getAlpha());
        return awt;
    }

    static void checkAccess() {
        if( Display.getCurrent() == null )
            SWT.error (SWT.ERROR_THREAD_INVALID_ACCESS);
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

    public static Icon imageDescriptor2awtIcon(final ImageDescriptor imageDescriptor) {
        Icon awtIcon = new Icon(){
            ImageData imageData = imageDescriptor.getImageData();
    
            public int getIconHeight() {
                return imageData.width;
            }
    
            public int getIconWidth() {
                return imageData.height;
            }
    
            public void paintIcon( Component comp, Graphics g, int x, int y ) {
                BufferedImage image = convertToAWT(imageData);
                g.drawImage(image, x, y, null);
            }
            
        };
        return awtIcon;
    }

    /**
     * Converts a Swing {@link Icon} to an {@link ImageDescriptor}
     *
     * @param icon icon to convert
     * @return an ImageDescriptor
     */
    public static ImageDescriptor awtIcon2ImageDescriptor( final Icon icon ) {
        ImageDescriptor descriptor = new ImageDescriptor(){
    
            @Override
            public ImageData getImageData() {
                BufferedImage image = createBufferedImage(icon.getIconWidth(), icon.getIconHeight());
                Graphics2D g = image.createGraphics();
                try{
                    icon.paintIcon(null, g, 0, 0);
                }finally{
                    g.dispose();
                }
                ImageData data = createImageData(image);
                return data;
            }
            
        };
        return descriptor;
    }
    
}
