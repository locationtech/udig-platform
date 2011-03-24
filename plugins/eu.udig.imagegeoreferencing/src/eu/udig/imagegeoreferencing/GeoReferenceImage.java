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
 */
package eu.udig.imagegeoreferencing;

import java.awt.Image;

import net.refractions.udig.project.IMap;
import net.refractions.udig.project.render.displayAdapter.IMapDisplay;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.tool.Tool;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

/**
 * A class representing an image to be geo-referenced.  This image is mapped to
 * a image file on disk.
 * 
 * @author GDavis, Refractions Research
 *
 */
public class GeoReferenceImage {

    private boolean useSWT;
    private Image AWTImage;
    private Image scaledAWTImage;
    private org.eclipse.swt.graphics.Image SWTImage;
    private org.eclipse.swt.graphics.Image scaledSWTImage;
    private String filename;
    private int posX = 100; // default to 100 pixels
    private int posY = 100; // default to 100 pixels
    private boolean isSelected = false;
    private int scaledWidth;
    private int scaledHeight;
    private final static double SCALE_FACTOR = 0.3;

    public GeoReferenceImage( Image image, String filename ) {
        this.AWTImage = image;
        this.filename = filename;
        this.useSWT = false;
    }

    public GeoReferenceImage( org.eclipse.swt.graphics.Image image, String filename ) {
        this.SWTImage = image;
        this.filename = filename;
        this.useSWT = true;
    }

    public boolean isUsingSWT() {
        return this.useSWT;
    }

    public void setUsingSWT( boolean useSWT ) {
        this.useSWT = useSWT;
    }

    public Image getAWTImage() {
        return AWTImage;
    }

    public void setAWTImage( Image image ) {
        this.AWTImage = image;
    }

    public org.eclipse.swt.graphics.Image getSWTImage() {
        return SWTImage;
    }

    public void setSWTImage( org.eclipse.swt.graphics.Image image ) {
        if (this.SWTImage != null)
            this.SWTImage.dispose();
        this.SWTImage = image;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename( String filename ) {
        this.filename = filename;
    }

    public Image getScaledAWTImage() {
        return scaledAWTImage;
    }

    /**
     * Scale the source AWT image to the specific size
     * 
     * @param width
     * @param height
     * @param hints for scaling algorithm
     */
    public void createScaledAWTImage( int width, int height, int hints ) {
        setScaledAWTImage(AWTImage.getScaledInstance(width, height, hints));
    }

    /**
     * Scale the source SWT image to the specific size
     * 
     * @param width
     * @param height
     * @param antialias (SWT.ON/OFF)
     * @param interpoloation (SWT.HIGH/MEDIUM/LOW)
     */
    public void createScaledSWTImage( int width, int height, int antialias, int interpolation ) {
        // use same aspect ratio and scale it fast
        org.eclipse.swt.graphics.Image scaled = new org.eclipse.swt.graphics.Image(Display.getDefault(), width, height);
        GC gc = new GC(scaled);
        gc.setAntialias(antialias);
        gc.setInterpolation(interpolation);
        gc.drawImage(SWTImage, 0, 0, SWTImage.getBounds().width, SWTImage.getBounds().height, 0, 0, width, height);
        gc.dispose();
        setScaledSWTImage(scaled);
    }

    /**
     * Creates a quick scaled image 30% the size of the current map's viewport with a
     * fast algorithm.
     */
    public void createScaledAWTImageToFitMap( IMap map ) {
        // rescale the image to fit the map area so it can be worked with (30% of map)
        IMapDisplay mapDisplay = map.getRenderManager().getMapDisplay();
        int displayW = mapDisplay.getWidth();
        int scaledW = (int) (displayW * SCALE_FACTOR);
        // use same aspect ratio and scale it fast
        createScaledAWTImage(scaledW, -1, Image.SCALE_FAST);
    }

    /**
     * Creates a quick scaled image 30% the size of the current map's viewport with a
     * fast algorithm.
     */
    public void createScaledSWTImageToFitMap( IMap map ) {
        // rescale the image to fit the map area so it can be worked with (30% of map)
        IMapDisplay mapDisplay = map.getRenderManager().getMapDisplay();
        int displayW = mapDisplay.getWidth();
        int scaledW = (int) (displayW * SCALE_FACTOR);
        // use same aspect ratio and scale it fast
        createScaledSWTImage(scaledW, -1, SWT.OFF, SWT.LOW);
    }

    public void setScaledAWTImage( Image scaledAWTImage ) {
        this.scaledAWTImage = scaledAWTImage;
        this.scaledWidth = scaledAWTImage.getWidth(null);
        this.scaledHeight = scaledAWTImage.getHeight(null);
    }

    public org.eclipse.swt.graphics.Image getScaledSWTImage() {
        return scaledSWTImage;
    }

    public void setScaledSWTImage( org.eclipse.swt.graphics.Image scaledSWTImage ) {
        this.scaledSWTImage = scaledSWTImage;
        this.scaledWidth = scaledSWTImage.getBounds().width;
        this.scaledHeight = scaledSWTImage.getBounds().height;
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX( int posX ) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY( int Posy ) {
        this.posY = Posy;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected( boolean isSelected ) {
        this.isSelected = isSelected;
        // anytime we are unselecting an image, delete any mapmarkers
        if (!isSelected) {
            deleteMapGraphicPoints(ApplicationGIS.getActiveMap());
        }
    }

    /*
     * Delete any mapgraphic points if resizing the image
     */
    private void deleteMapGraphicPoints( IMap map ) {
        Tool placeMarkersTool = GeoReferenceUtils.findTool(PlaceMarkersTool.TOOLID);
        if (placeMarkersTool != null && placeMarkersTool instanceof PlaceMarkersTool) {
            ((PlaceMarkersTool) placeMarkersTool).clearPointVars();
        }

        // GeoReferenceMapGraphic mapGraphic = GeoReferenceUtils.getMapGraphic(map);
        // if (mapGraphic != null) {
        // if (mapGraphic.getImageMarkers() != null) {
        // mapGraphic.getImageMarkers().clear();
        // }
        // if (mapGraphic.getBasemapMarkers() != null) {
        // mapGraphic.getBasemapMarkers().clear();
        // }
        // }
    }

    public void dispose() {
        if (this.SWTImage != null)
            this.SWTImage.dispose();
        if (this.scaledSWTImage != null)
            this.scaledSWTImage.dispose();
    }

    public int getScaledWidth() {
        return scaledWidth;
    }

    public int getScaledHeight() {
        return scaledHeight;
    }

}
