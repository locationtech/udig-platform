package net.refractions.udig.style.sld;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

public class StyleGlyph {

    static final int DEFAULT_WIDTH = 16;
    static final int DEFAULT_HEIGHT = 16;
    static final int DEFAULT_DEPTH = 24;

    private static Image image( Display display, RGB[] rgb ) {
        PaletteData palette = new PaletteData(0xFF0000, 0xFF00, 0xFF);
        ImageData imageData = new ImageData(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_DEPTH, palette);
        imageData.transparentPixel = palette.getPixel(display.getSystemColor(
                SWT.COLOR_WIDGET_BACKGROUND).getRGB());

        return new Image(display, imageData);
    }

    private static ImageDescriptor descriptor( ImageData imageData, Image image, GC gc ) {
        final ImageData finalImageData = (ImageData) image.getImageData().clone();

        image.dispose();
        gc.dispose();

        return new ImageDescriptor(){
            public ImageData getImageData() {
                return finalImageData;
            }
        };
    }

    public static ImageDescriptor point( int color, int width ) {
        Display display = PlatformUI.getWorkbench().getDisplay();
        Color fill = display.getSystemColor(color);
        Color back = display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);

        Image image = image(display, new RGB[]{fill.getRGB(), back.getRGB()});
        GC gc = new GC(image);

        ImageData imageData = image.getImageData();
        int w = imageData.width;
        int h = imageData.height;

        gc.setBackground(back);
        gc.fillRectangle(0, 0, w, h);

        gc.setBackground(fill);
        gc.fillOval(w / 2 - width / 2, h / 2 - width / 2, width, width);

        return descriptor(imageData, image, gc);
    }

    public static ImageDescriptor text( int color, int width ) {
        Display display = PlatformUI.getWorkbench().getDisplay();
        Color line = display.getSystemColor(color);
        Color back = display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);

        Image image = image(display, new RGB[]{line.getRGB(), back.getRGB()});
        GC gc = new GC(image);

        ImageData imageData = image.getImageData();
        int w = imageData.width;
        int h = imageData.height;

        gc.setBackground(back);
        gc.fillRectangle(0, 0, w, h);

        gc.setForeground(line);
        gc.setLineWidth(width);

        gc.drawLine(2, 2, 2, 4);
        gc.drawLine(2, 2, w - 2, 2);
        gc.drawLine(w - 2, 2, h - 2, 4);
        gc.drawLine(8, 2, 8, h - 2);
        gc.drawLine(4, h - 2, w - 4, h - 2);

        return descriptor(imageData, image, gc);
    }

    public static void disable( ImageDescriptor descriptor ) {
        ImageData imageData = descriptor.getImageData();
        //PaletteData paletteData = imageData.palette;
        
        for( int i = 2; i < DEFAULT_WIDTH - 2; i++ ) {
            imageData.setPixel(i, i, 0);
        }
    }

    public static ImageDescriptor line( int color, int width ) {

        Display display = PlatformUI.getWorkbench().getDisplay();
        Color line = display.getSystemColor(color);
        Color back = display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);

        Image image = image(display, new RGB[]{line.getRGB(), back.getRGB()});
        GC gc = new GC(image);

        ImageData imageData = image.getImageData();
        int w = imageData.width;
        int h = imageData.height;

        gc.setBackground(back);
        gc.fillRectangle(0, 0, w, h);

        gc.setBackground(line);
        gc.setForeground(line);
        gc.setLineWidth(width);
        gc.drawLine(1, h / 2, w - 1, h / 2);

        return descriptor(imageData, image, gc);
    }

    public static ImageDescriptor polygon( int lineColor, int fillColor, int width ) {
        Display display = PlatformUI.getWorkbench().getDisplay();
        Color line = display.getSystemColor(lineColor);
        Color fill = display.getSystemColor(fillColor);
        Color back = display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);

        Image image = image(display, new RGB[]{line.getRGB(), fill.getRGB(), back.getRGB()});
        GC gc = new GC(image);

        ImageData imageData = image.getImageData();
        int w = imageData.width;
        int h = imageData.height;

        gc.setBackground(back);
        gc.fillRectangle(0, 0, w, h);

        gc.setBackground(fill);
        gc.fillRectangle(2, 2, w - 4, h - 4);

        gc.setForeground(line);
        gc.setLineWidth(width);
        gc.drawRectangle(2, 2, w - 4, w - 4);

        return descriptor(imageData, image, gc);
    }
    
    public static ImageDescriptor raster( int lineColor, int fillColor, int width ) {
        Display display = PlatformUI.getWorkbench().getDisplay();
        Color line = display.getSystemColor(lineColor);
        Color fill = display.getSystemColor(fillColor);
        Color back = display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);

        Image image = image(display, new RGB[]{line.getRGB(), fill.getRGB(), back.getRGB()});
        GC gc = new GC(image);

        ImageData imageData = image.getImageData();
        int w = imageData.width;
        int h = imageData.height;

        gc.setBackground(back);
        gc.fillRectangle(0, 0, w, h);

        gc.setBackground(fill);
        gc.fillRectangle(2, 2, w - 4, h - 4);

        gc.setForeground(line);
        gc.setLineWidth(width);
        gc.drawRectangle(2, 2, w - 4, w - 4);

        return descriptor(imageData, image, gc);
    }
}