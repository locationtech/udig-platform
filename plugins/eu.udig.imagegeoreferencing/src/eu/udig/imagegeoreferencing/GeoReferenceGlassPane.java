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
import java.awt.Point;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;

import com.vividsolutions.jts.geom.Envelope;

import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.render.displayAdapter.ViewportPane;
import net.refractions.udig.project.ui.render.glass.GlassPane;

/**
 * A GlassPane that is used for displaying and manipulating images for geo-referencing
 * them with the base map.
 * 
 * @author GDavis, Refractions Research
 *
 */
public class GeoReferenceGlassPane extends GlassPane {

    private boolean draggingImage = false;
    private boolean resizingImage = false;
    private boolean draggingMarker = false;
    private GeoReferenceMapGraphic draggingImageGraphic = null;
    private GeoReferenceMapGraphic resizingImageGraphic = null;
    private PlaceMarker draggingMarkerPlacemarker = null;
    private Point dragOffset;
    private Point resizeMovement;
    private Point resizePlace;

    // anchor corner for resizing (tl = 1, tr = 2, bl = 3, br = 4)
    private int anchorCorner;

    public GeoReferenceGlassPane( ViewportPane p ) {
        super(p);
    }

    /**
     * If dragging or resizing is set to on, then draw the selected image's frame. 
     */
    @Override
    public void draw( GC graphics ) {
        // if there is no action going on to cause an object to be drawn
        // on the glasspane, exit
        if (!draggingImage && !resizingImage && !draggingMarker) {
            return;
        }

        // draw the draggingGraphic's frame at the current position
        if (draggingImage) {
            if (draggingImageGraphic == null) {
                return;
            }

            // get the selected geo image
            GeoReferenceImage selectedGeoImage = GeoReferenceUtils.getSelectedGeoImage(draggingImageGraphic.getImages(),
                    getSite().getMap());

            if (selectedGeoImage != null) {
                graphics.setForeground(new Color(graphics.getDevice(), GeoReferenceMapGraphic.BORDERRGB));
                graphics.setLineWidth(GeoReferenceMapGraphic.BORDERWIDTH);
                graphics.drawRectangle(dragOffset.x, dragOffset.y, selectedGeoImage.getScaledWidth(),
                        selectedGeoImage.getScaledHeight());
            }
        } else if (resizingImage) {
            if (resizingImageGraphic == null) {
                return;
            }

            // get the selected geo image
            GeoReferenceImage selectedGeoImage = GeoReferenceUtils.getSelectedGeoImage(resizingImageGraphic.getImages(),
                    getSite().getMap());

            if (selectedGeoImage != null) {
                graphics.setForeground(new Color(graphics.getDevice(), GeoReferenceMapGraphic.BORDERRGB));
                graphics.setLineWidth(GeoReferenceMapGraphic.BORDERWIDTH);

                // depending on what the anchor corner is, resize the border accordingly and
                // always keep the same aspect ratio
                Envelope env = getResizeImageEnvelope(selectedGeoImage);
                graphics.drawRectangle((int) env.getMinX(), (int) env.getMinY(), (int) env.getWidth(), (int) env.getHeight());
            }
        } else if (draggingMarker) {
            if (draggingMarkerPlacemarker == null) {
                return;
            }
            java.awt.Color awtColor = draggingMarkerPlacemarker.getColor();
            Color swtColor = new Color(graphics.getDevice(), awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue());
            graphics.setForeground(swtColor);
            graphics.setBackground(swtColor);
            int halfsize = PlaceMarker.DRAWING_SIZE / 2;
            graphics.fillOval((int) (dragOffset.x - halfsize), (int) (dragOffset.y - halfsize), PlaceMarker.DRAWING_SIZE,
                    PlaceMarker.DRAWING_SIZE);
            // Rectangle2D stringBounds = graphics.getStringBounds(String.valueOf(count));
            // graphics.drawString(String.valueOf(count), (int)point.getX(),
            // (int)(point.getY()+stringBounds.getHeight()), ViewportGraphics.ALIGN_MIDDLE,
            // ViewportGraphics.ALIGN_BOTTOM);
        }

    }

    /**
     * Start a drag sequence for the given mapGraphic
     * 
     * @param graphic
     * @param starting x
     * @param starting y
     */
    public void startDraggingImage( GeoReferenceMapGraphic graphic, int x, int y ) {
        draggingImage = true;
        draggingImageGraphic = graphic;
        dragOffset = new Point(x, y);
    }

    public void endDraggingImage( int x, int y ) {
        dragOffset.x = x;
        dragOffset.y = y;
        draggingImage = false;

        // update the selected geo image
        if (draggingImageGraphic != null) {
            GeoReferenceImage selectedGeoImage = GeoReferenceUtils.getSelectedGeoImage(draggingImageGraphic.getImages(),
                    getSite().getMap());
            if (selectedGeoImage != null) {
                selectedGeoImage.setPosX(x);
                selectedGeoImage.setPosY(y);
            }
        }
    }

    /**
     * Start a drag sequence for the given placemarker
     * 
     * @param marker
     * @param starting x
     * @param starting y
     */
    public void startDraggingMarker( PlaceMarker marker, int x, int y ) {
        draggingMarker = true;
        draggingMarkerPlacemarker = marker;
        dragOffset = new Point(x, y);
    }

    public void endDraggingMarker( int x, int y ) {
        dragOffset.x = x;
        dragOffset.y = y;
        draggingMarker = false;

        // update the dragged marker
        if (draggingMarkerPlacemarker != null) {
            draggingMarkerPlacemarker.setPoint(new Point(x, y));
            // also set the map coord of this point since it is a basemap marker
            if (draggingMarkerPlacemarker.isBasemapMarker() && getSite().getMap() != null) {
                IMap map = getSite().getMap();
                draggingMarkerPlacemarker.setCoord(map.getViewportModel().pixelToWorld(x, y));
            }
        }
    }

    /**
     * Set the incremental drag translation for when this is glasspane is repainted.
     * 
     * @param x
     * @param y
     */
    public void setDragTranslation( int x, int y ) {
        dragOffset.x = x;
        dragOffset.y = y;
    }

    /**
     * Start a resizing sequence for the given mapGraphic
     * 
     * @param graphic
     * @param starting x
     * @param starting y
     */
    public void startResizingImage( GeoReferenceMapGraphic graphic, int x, int y ) {
        resizingImage = true;
        resizingImageGraphic = graphic;
        Point clicked = new Point(x, y);

        // determine what corner is closest to resize
        GeoReferenceImage selectedGeoImage = GeoReferenceUtils.getSelectedGeoImage(resizingImageGraphic.getImages(), getSite()
                .getMap());
        resizePlace = new Point(x, y);
        resizeMovement = new Point(0, 0);
        double tl = clicked.distance(selectedGeoImage.getPosX(), selectedGeoImage.getPosY());
        double tr = clicked.distance(selectedGeoImage.getPosX() + selectedGeoImage.getScaledWidth(), selectedGeoImage.getPosY());
        double bl = clicked.distance(selectedGeoImage.getPosX(), selectedGeoImage.getPosY() + selectedGeoImage.getScaledHeight());
        double br = clicked.distance(selectedGeoImage.getPosX() + selectedGeoImage.getScaledWidth(), selectedGeoImage.getPosY()
                + selectedGeoImage.getScaledHeight());

        // whatever value is smallest is the reszie corner, and its opposite corner is
        // the anchor corner (tl = 1, tr = 2, bl = 3, br = 4)
        anchorCorner = 4;
        double smallest = tl;
        if (tr < smallest) {
            anchorCorner = 3;
            smallest = tr;
        }
        if (bl < smallest) {
            anchorCorner = 2;
            smallest = bl;
        }
        if (br < smallest) {
            anchorCorner = 1;
            smallest = br;
        }
    }

    public void endResizingImage( int x, int y ) {
        resizeMovement.x = x - resizePlace.x;
        resizeMovement.y = y - resizePlace.y;
        resizingImage = false;

        // update the selected geo image
        if (resizingImageGraphic != null) {
            GeoReferenceImage selectedGeoImage = GeoReferenceUtils.getSelectedGeoImage(resizingImageGraphic.getImages(),
                    getSite().getMap());
            if (selectedGeoImage != null) {
                Envelope env = getResizeImageEnvelope(selectedGeoImage);
                selectedGeoImage.setPosX((int) env.getMinX());
                selectedGeoImage.setPosY((int) env.getMinY());
                if (selectedGeoImage.isUsingSWT()) {
                    selectedGeoImage.createScaledSWTImage((int) env.getWidth(), -1, SWT.OFF, SWT.LOW);
                } else {
                    selectedGeoImage.createScaledAWTImage((int) env.getWidth(), -1, Image.SCALE_FAST);
                }
            }
        }
    }

    /**
     * Determine the resize envelope based on the anchor corner set and the current image
     * and the resizeOffset
     * 
     * @param selectedGeoImage
     * @param x
     * @param y
     * @return
     */
    private Envelope getResizeImageEnvelope( GeoReferenceImage selectedGeoImage ) {
        // first ensure we keep the same aspect ratio by using whatever plane
        // was moved the most
        double ratio = (double) selectedGeoImage.getScaledHeight() / selectedGeoImage.getScaledWidth();
        if (anchorCorner == 1 || anchorCorner == 4) {
            if (resizeMovement.y > resizeMovement.x) {
                // y moved most
                resizeMovement.x = (int) (ratio * resizeMovement.y);
            } else {
                // x moved most
                resizeMovement.y = (int) (ratio * resizeMovement.x);
            }
        } else {
            if (resizeMovement.y > resizeMovement.x) {
                // y moved most
                resizeMovement.x = (int) (ratio * resizeMovement.y) * -1;
            } else {
                // x moved most
                resizeMovement.y = (int) (ratio * resizeMovement.x) * -1;
            }
        }

        // depending on what the anchor corner is, resize the env accordingly
        int x = 0;
        int y = 0;
        int w = 10;
        int h = 10;
        if (anchorCorner == 1) {
            // tl
            x = selectedGeoImage.getPosX();
            y = selectedGeoImage.getPosY();
            w = selectedGeoImage.getScaledWidth() + resizeMovement.x;
            h = selectedGeoImage.getScaledHeight() + resizeMovement.y;
            // ensure we aren't smaller than 10 pixels
            if (w < 10) {
                w = 10;
            }
            if (h < 10) {
                h = 10;
            }
        } else if (anchorCorner == 2) {
            // tr
            double anchorX = selectedGeoImage.getPosX() + selectedGeoImage.getScaledWidth();
            double anchorY = selectedGeoImage.getPosY();
            x = selectedGeoImage.getPosX() + resizeMovement.x;
            y = (int) anchorY;
            w = (int) (anchorX - (selectedGeoImage.getPosX() + resizeMovement.x));
            h = selectedGeoImage.getScaledHeight() + resizeMovement.y;
            if (x > anchorX)
                x = (int) anchorX;
            if (y > anchorY)
                y = (int) anchorY;
            // ensure we aren't smaller than 10 pixels
            if (w < 10) {
                x = (int) anchorX - 10;
                w = 10;
            }
            if (h < 10) {
                h = 10;
            }
        } else if (anchorCorner == 3) {
            // bl
            double anchorX = selectedGeoImage.getPosX();
            double anchorY = selectedGeoImage.getPosY() + selectedGeoImage.getScaledHeight();
            x = selectedGeoImage.getPosX();
            y = selectedGeoImage.getPosY() + resizeMovement.y;
            w = selectedGeoImage.getScaledWidth() + resizeMovement.x;
            h = (int) anchorY - y;
            if (x > anchorX)
                x = (int) anchorX;
            if (y > anchorY)
                y = (int) anchorY;
            // ensure we aren't smaller than 10 pixels
            if (w < 10) {
                w = 10;
            }
            if (h < 10) {
                h = 10;
                y = (int) anchorY - 10;
            }
        } else if (anchorCorner == 4) {
            // br
            double anchorX = selectedGeoImage.getPosX() + selectedGeoImage.getScaledWidth();
            double anchorY = selectedGeoImage.getPosY() + selectedGeoImage.getScaledHeight();
            x = selectedGeoImage.getPosX() + resizeMovement.x;
            y = selectedGeoImage.getPosY() + resizeMovement.y;
            w = (int) (anchorX - (selectedGeoImage.getPosX() + resizeMovement.x));
            h = (int) anchorY - y;
            if (x > anchorX)
                x = (int) anchorX;
            if (y > anchorY)
                y = (int) anchorY;
            // ensure we aren't smaller than 10 pixels
            if (w < 10) {
                x = (int) anchorX - 10;
                w = 10;
            }
            if (h < 10) {
                h = 10;
                y = (int) anchorY - 10;
            }
        }

        // System.out.println(x+", "+y+", "+w+", "+h);
        return new Envelope(x, x + w, y, y + h);
    }

    /**
     * Set the incremental resize translation for when this is glasspane is repainted.
     * 
     * @param x
     * @param y
     */
    public void setResizeTranslation( int x, int y ) {
        resizeMovement.x = x - resizePlace.x;
        resizeMovement.y = y - resizePlace.y;
    }

}
