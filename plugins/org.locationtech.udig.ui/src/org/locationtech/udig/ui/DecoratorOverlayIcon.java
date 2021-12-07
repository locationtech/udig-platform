/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.ui;

import java.util.Arrays;

import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

/**
 * An DecoratorOverlayIcon consists of a main icon and several adornments.
 * <p>
 * Eclipse has many implementations of this class - all of them are internal. This one is based on
 * org.eclipse.ui.internal.decorators.DecoratorOverlayIcon and has the advantage of having an equals
 * and hashcode implementation. This allows the use of the resulting ImageDescriptors to be cached
 * using weak references.
 * </p>
 * <p>
 * Overlays are of course 7x8 icons with a white keyline around them.
 * <p>
 * Example use:
 *
 * <pre>
 * <code>
 * new DecoratorOverlayIcon(base,
 *                 new ImageDescriptor[][]={
 *                     null,     // TOP_LEFT     unsure if we will use this
 *                     modified, // TOP_RIGHT    indicate content modified (w/ *)
 *                     status,   // BOTTOM_LEFT  called the auxiliary overlay warn, error, success
 *                     cached,   // BOTTOM_RIGHT not yet defined, recommended cache status...
 *                     null,     // UNDERLAY     not sure if we will use this
 *                 }
 * );
 * </code>
 * </pre>
 * </p>
 */
public class DecoratorOverlayIcon extends CompositeImageDescriptor {
    static final Point DEFAULT_SIZE = new Point(22, 16);

    // the base image
    private Image base;

    // the overlay images
    private ImageDescriptor[] overlays;

    // the size
    private Point size;

    public static final int TOP_LEFT = 0;

    public static final int TOP_RIGHT = 1;

    public static final int BOTTOM_LEFT = 2;

    public static final int BOTTOM_RIGHT = 3;

    public static final int UNDERLAY = 4;

    /**
     * OverlayIcon constructor defaults to size of base image.
     * <p>
     * Overlays are of course 7x8 icons with a white keyline around them.
     * </p>
     * <p>
     * Example use:
     *
     * <pre>
     * <code>
     * new DecoratorOverlayIcon(base,
     *                 new ImageDescriptor[][]={
     *                     null,     // TOP_LEFT     unsure if we will use this
     *                     modified, // TOP_RIGHT    indicate content modified (w/ *)
     *                     status,   // BOTTOM_LEFT  called the auxiliary overlay warn, error, success
     *                     cached,   // BOTTOM_RIGHT not yet defined, recommended cache status...
     *                     null,     // UNDERLAY     not sure if we will use this
     *                 }
     * );
     * </code>
     * </pre>
     * </p>
     *
     * @param base the base image
     * @param overlays the overlay images
     */
    public DecoratorOverlayIcon(Image baseImage, ImageDescriptor[] overlaysArray) {
        this.base = baseImage;
        this.overlays = new ImageDescriptor[overlaysArray.length];
        int i = overlaysArray.length;
        this.overlays = new ImageDescriptor[i];

        System.arraycopy(overlaysArray, 0, overlays, 0, i);
        Rectangle bounds = baseImage.getBounds();
        this.size = new Point(bounds.width, bounds.height);
    }

    /**
     * OverlayIcon constructor allowing explicit size.
     * <p>
     * Overlays are of course 7x8 icons with a white keyline around them.
     * </p>
     * <p>
     * Example use:
     *
     * <pre>
     * <code>
     * new DecoratorOverlayIcon(base,
     *                 new ImageDescriptor[][]={
     *                     null,     // TOP_LEFT     unsure if we will use this
     *                     modified, // TOP_RIGHT    indicate content modified (w/ *)
     *                     status,   // BOTTOM_LEFT  called the auxiliary overlay warn, error, success
     *                     cached,   // BOTTOM_RIGHT not yet defined, recommended cache status...
     *                     null,     // UNDERLAY     not sure if we will use this
     *                 },
     *                 new Point(22,16)
     * );
     * </code>
     * </pre>
     * </p>
     *
     * @param base the base image
     * @param overlays the overlay images
     * @param size the size, 22x16 if null
     */
    public DecoratorOverlayIcon(Image baseImage, ImageDescriptor[] overlaysArray, Point sizeValue) {
        this.base = baseImage;
        int i = 0;
        if (overlaysArray != null)
            i = overlaysArray.length;
        this.overlays = new ImageDescriptor[i];

        System.arraycopy(overlaysArray, 0, overlays, 0, i);
        this.size = sizeValue;
    }

    /**
     * Draw the overlays for the receiver.
     */
    protected void drawOverlays(ImageDescriptor[] overlaysArray) {

        for (int i = 0; i < overlays.length; i++) {
            ImageDescriptor overlay = overlaysArray[i];
            if (overlay == null)
                continue;

            ImageData overlayData = overlay.getImageData(100);
            // Use the missing descriptor if it is not there.
            if (overlayData == null)
                overlayData = ImageDescriptor.getMissingImageDescriptor().getImageData(100);
            switch (i) {
            case TOP_LEFT:
                drawImage(overlayData, 0, 0);
                break;
            case TOP_RIGHT:
                drawImage(overlayData, size.x - overlayData.width, 0);
                break;
            case BOTTOM_LEFT:
                drawImage(overlayData, 0, size.y - overlayData.height);
                break;
            case BOTTOM_RIGHT:
                drawImage(overlayData, size.x - overlayData.width, size.y - overlayData.height);
                break;
            }
        }
    }

    /** Note this can only be equal to another DecoratorOverlayIcon. */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DecoratorOverlayIcon))
            return false;
        DecoratorOverlayIcon other = (DecoratorOverlayIcon) o;
        return base.equals(other.base) && Arrays.equals(overlays, other.overlays);
    }

    /** Hashcode for a base with no overlays will be the same as for a base. */
    @Override
    public int hashCode() {
        int code = base.hashCode();
        for (int i = 0; i < overlays.length; i++) {
            if (overlays[i] != null)
                code ^= overlays[i].hashCode();
        }
        return code;
    }

    @Override
    protected void drawCompositeImage(int width, int height) {
        ImageDescriptor underlay = overlays[UNDERLAY];
        if (underlay != null)
            drawImage(underlay.getImageData(100), 0, 0);
        drawImage(base.getImageData(100), 0, 0);
        drawOverlays(overlays);
    }

    @Override
    protected Point getSize() {
        return size;
    }
}
