/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2007-2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.render.impl;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.measure.converter.UnitConverter;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import org.locationtech.udig.core.Pair;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.ProjectBlackboardConstants;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.internal.render.ViewportModel;
import org.locationtech.udig.project.preferences.PreferenceConstants;
import org.locationtech.udig.project.render.IRenderManager;
import org.locationtech.udig.project.render.displayAdapter.IMapDisplay;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.referencing.GeodeticCalculator;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

/**
 * Methods for calculating the ScaleDenominator
 * 
 * @author jesse
 */
public final class ScaleUtils {

	static final double ACCURACY = 0.00000001;
	private static final int MAX_ITERATIONS = 10;
	private static Envelope WORLD = new Envelope(-180, 180, -90, 90);

    /**
     * Default pixel size in meters, producing a default of 90.7 DPI
     */
    public static final double DEFAULT_PIXEL_SIZE_METER = 0.00028;
    public static final double METERS_PER_DEGREE = 6378137.0 * 2.0 * Math.PI / 360.0;
    public static final double DEGREES_PER_METER = 360.0 / 6378137.0 * 2.0 * Math.PI;
    public static final double FEET_TO_METERS = 0.3048;
    
	private ScaleUtils() {/*no instance for you*/}

	/**
	 * Calculate the resolution of a for a tile given a tile scale in pixels.
	 * <p>
	 * This is used to calculate the resolution for a WMS tile. We need to consider the total size of the
	 * WMS Layer; and the tile size.
	 * 
	 * @param bounds The full bounds of a WMS layer
	 * @param scale The tile scale (viewport_scale * tile.width)
	 * @param tileWidth The tile width in pixels
	 * @return the resolution
	 */
	public static double calculateResolutionFromScale(ReferencedEnvelope bounds, double scale, int tileWidth) {
	    if (isLatLong(bounds.getCoordinateReferenceSystem())){
	        return  (DEFAULT_PIXEL_SIZE_METER * DEGREES_PER_METER) * ( (scale*tileWidth) * DEGREES_PER_METER);
	    } else {
	        return DEFAULT_PIXEL_SIZE_METER * scale;
	    }
	}

	public static Unit<?> getUnit(CoordinateReferenceSystem crs) {
		return crs.getCoordinateSystem().getAxis(0).getUnit();
	}

	public static double fromMeterToCrs(double value,
			CoordinateReferenceSystem crs) {
		Unit<?> unit = getUnit(crs);
		UnitConverter converter = SI.METER.getConverterTo(unit);
		return converter.convert(value);
	}

	public static double fromCrsToMeter(double value,
			CoordinateReferenceSystem crs) {
		Unit<?> unit = getUnit(crs);
		UnitConverter converter = unit.getConverterTo(javax.measure.unit.SI.METER);
		return converter.convert(value);
	}

	/**
	 * Determines if the crs is a lat/long crs (has angular units)
	 * 
	 * @return true if the crs is a latlong crs (has angular units)
	 */
	public static boolean isLatLong(CoordinateReferenceSystem crs) {
		Unit<?> unit = getUnit(crs);
		boolean isLatLong = unit.getStandardUnit().equals(javax.measure.unit.SI.RADIAN);
		return isLatLong;
	}

	/**
	 * Calculates the width of the bounding box that will fit in the display
	 * size at the specified scale.
	 * 
	 * @param newScaleDenominator
	 *            The scale denominator to use to calculate the bounds
	 * @param displaySize
	 *            the size of the display
	 * @param dpi
	 *            the dots per inch of the display
	 * @param currentBounds
	 *            the current bounds, this is required if the current CRS is
	 *            latlong
	 * @return the width of the extent that is at the specified scale
	 */

	public static ReferencedEnvelope calculateBoundsFromScale(
			double newScaleDenominator, Dimension displaySize, int dpi,
			ReferencedEnvelope currentBounds) {
		double MIN_SCALE = 1.0E-100;
		if (newScaleDenominator <= MIN_SCALE || Double.isInfinite(newScaleDenominator) || Double.isNaN(newScaleDenominator)) {
			return currentBounds;
		}

		return calculateBoundsFromScaleInternal(newScaleDenominator,
				displaySize, dpi, currentBounds, 0);
	}

	private static ReferencedEnvelope calculateBoundsFromScaleInternal(
			double newScaleDenominator, Dimension displaySize, int dpi,
			ReferencedEnvelope currentBounds, int iterations) {
		double oldScaleDenom = calculateScaleDenominator(currentBounds,
				displaySize, dpi);
		
		if(oldScaleDenom<=0 || Double.isInfinite(oldScaleDenom) || Double.isNaN(oldScaleDenom)){
			return currentBounds;
		}
		double ratio = newScaleDenominator / oldScaleDenom;
		double newWidth = currentBounds.getWidth() * ratio;
		ReferencedEnvelope newExtent = calculateBounds(newWidth, displaySize,
				currentBounds);
		double calculatedScale = calculateScaleDenominator(newExtent,
				displaySize, dpi);
		if (Math.abs(calculatedScale - newScaleDenominator) > ACCURACY
				&& iterations < MAX_ITERATIONS && calculatedScale>0) {
			return calculateBoundsFromScaleInternal(newScaleDenominator,
					displaySize, dpi, newExtent, iterations + 1);
		} else {
			return newExtent;
		}
	}

	private static ReferencedEnvelope calculateBounds(double width,
			Dimension displaySize, ReferencedEnvelope originalExtent) {
		Coordinate center = originalExtent.centre();
		double height = width * displaySize.height / displaySize.width;
		CoordinateReferenceSystem crs = originalExtent
				.getCoordinateReferenceSystem();
		double minx = center.x - width / 2;
		double maxx = center.x + width / 2;
		double miny = center.y - height / 2;
		double maxy = center.y + height / 2;
		return new ReferencedEnvelope(minx, maxx, miny, maxy, crs);
	}

	private static double distancePerPixel(Dimension displaySize,
			ReferencedEnvelope currentBounds) {
		ReferencedEnvelope referencePixel = toValidPixelBoundsClosestToCenter(
				displaySize, currentBounds);
		try {
			ReferencedEnvelope referencePixelLatLong = referencePixel
					.transform(DefaultGeographicCRS.WGS84, true);
			double minX = referencePixelLatLong.getMinX();
			double maxX = referencePixelLatLong.getMaxX();
			double scale = 1;

			if (referencePixelLatLong.getWidth() > 360) {
				scale = referencePixelLatLong.getWidth() / 18;
				minX = 0;
				maxX = 180;
			}
			GeodeticCalculator calc = new GeodeticCalculator();
			double centerY = centeredYWithinWorld(referencePixelLatLong);
			calc.setStartingGeographicPoint(minX, centerY);
			calc.setDestinationGeographicPoint(maxX, centerY);
			return calc.getOrthodromicDistance() * scale;
		} catch (FactoryException e) {
			ProjectPlugin.log("error transforming: " + referencePixel
					+ " to latlong", e);
			return -1;
		} catch (TransformException e) {
            ProjectPlugin.log("error transforming: " + referencePixel
                    + " to latlong", e);
            return -1;
        } catch (AssertionError e) {
            ProjectPlugin.log("Bad parameters", e);
            return -1;
        }
	}

	/**
	 * Finds the y coord that is centered extent and or the center of the world
	 */
	private static double centeredYWithinWorld(ReferencedEnvelope extent) {
		Coordinate centre = extent.centre();
		if (WORLD.contains(centre)) {
			return centre.y;
		}

		return 0;
	}

	/**
	 * Calculates the world bounds of the center pixel of the screen. If the
	 * pixel is not within the world lat/long bounds it finds the closest pixel
	 * that is within the world and calculates the bounds for that.
	 */
	static ReferencedEnvelope toValidPixelBoundsClosestToCenter(
			Dimension displaySize, ReferencedEnvelope currentBounds) {

		Coordinate centre = currentBounds.centre();
		Point referencePixel = nearestPixel(centre.x, centre.y, currentBounds,
				displaySize);

		ReferencedEnvelope pixelBounds = pixelBounds(referencePixel.x,
				referencePixel.y, currentBounds, displaySize);

		return shiftToWorld(pixelBounds);
	}

	static ReferencedEnvelope shiftToWorld(ReferencedEnvelope pixelBounds) {
		DefaultGeographicCRS wgs84 = DefaultGeographicCRS.WGS84;
		ReferencedEnvelope latLong;
		try {
			latLong = pixelBounds.transform(wgs84, true);

			if (WORLD.contains(latLong)) {
				return pixelBounds;
			}

			double deltax = 0, deltay = 0;
			if (latLong.getWidth() < WORLD.getWidth()) {
				if (latLong.getMinX() < WORLD.getMinX()) {
					deltax = WORLD.getMinX() - latLong.getMinX();
				}
				if (latLong.getMaxX() > WORLD.getMaxX()) {
					deltax = WORLD.getMaxX() - latLong.getMaxX();
				}
			}

			if (latLong.getHeight() < WORLD.getHeight()) {
				if (latLong.getMinY() < WORLD.getMinY()) {
					deltay = WORLD.getMinY() - latLong.getMinY();
				}
				if (latLong.getMaxY() > WORLD.getMaxY()) {
					deltay = WORLD.getMaxX() - latLong.getMaxY();
				}
			}

			latLong.translate(deltax, deltay);

			return latLong.transform(
					pixelBounds.getCoordinateReferenceSystem(), true);
		} catch (TransformException e) {
			ProjectPlugin.log("", e);
		} catch (FactoryException e) {
			ProjectPlugin.log("", e);
		}
		return pixelBounds;
	}

	/**
	 * calculates the pixel closest to x and y that is contained within the
	 * world
	 * 
	 * @param displaySize
	 */
	static Point nearestPixel(double x, double y, ReferencedEnvelope extent,
			Dimension displaySize) {
		if (WORLD.contains(x, y)) {
			return worldToPixel(new Coordinate(x, y), extent, displaySize);
		}

		double newX, newY;

		if (x < WORLD.getMinX()) {
			newX = WORLD.getMinX();
		} else if (x > WORLD.getMaxX()) {
			newX = WORLD.getMaxX();
		} else {
			newX = x;
		}

		if (y < WORLD.getMinY()) {
			newY = WORLD.getMinY();
		} else if (y > WORLD.getMaxY()) {
			newY = WORLD.getMaxY();
		} else {
			newY = y;
		}

		return worldToPixel(new Coordinate(newX, newY), extent, displaySize);
	}

	public static ReferencedEnvelope pixelBounds(int x, int y,
			ReferencedEnvelope currentBounds, Dimension displaySize) {
		double minX = ((double) x);
		double maxX = ((double) x) + 1;
		double minY = ((double) y);
		double maxY = ((double) y) + 1;

		Coordinate ul = pixelToWorld(minX, minY, currentBounds, displaySize);
		Coordinate lr = pixelToWorld(maxX, maxY, currentBounds, displaySize);

		if( ul==null || lr==null ){
			return new ReferencedEnvelope(new Envelope(), currentBounds.getCoordinateReferenceSystem());
		}
		
		return new ReferencedEnvelope(ul.x, lr.x, ul.y, lr.y, currentBounds
				.getCoordinateReferenceSystem());

	}

	public static double calculateScaleDenominator(ReferencedEnvelope bounds,
			Dimension displaySize, int dpi) {
		
		if( bounds.getWidth()==0 || bounds.getHeight()==0){
			return -1;
		}

		CoordinateReferenceSystem crs = bounds.getCoordinateReferenceSystem();

		int width = displaySize.width;
		int height = displaySize.height;
		boolean isLatLong = isLatLong(crs);

		if (isLatLong) {
			double distancePerPixel = distancePerPixel(displaySize, bounds);
			if (distancePerPixel < 0) {
				return -1;
			}
			double pixelSize = 1.0 / dpi * 25.4 / 1000;
			double scaleDenominator = distancePerPixel / pixelSize;
			return scaleDenominator;

		} else {
			double diaWidthUnits = Math.sqrt(bounds.getWidth() * bounds.getWidth() + bounds.getHeight() * bounds.getHeight());
			double diaWidthPx = Math.sqrt(width * width + height * height);
			double d1 = fromCrsToMeter(diaWidthUnits , crs);
			double meter = (d1 * dpi / 2.54 * 100.0) / diaWidthPx;
			return meter;
		}
	}

	public static Envelope centerPixelBounds(IMapDisplay display,
			ReferencedEnvelope bounds) {
		Coordinate ul = pixelToWorld((int) (display.getWidth() / 2 - 0.5),
				Math.floor( ((double)display.getHeight()) / 2.0 - 0.5), bounds, display
						.getDisplaySize());
		Coordinate lr = pixelToWorld((int) (display.getWidth() / 2 + 0.5),
				Math.floor( ((double)display.getHeight()) / 2.0 + 0.5), bounds, display.getDisplaySize());
		return new Envelope(ul.x, lr.x, ul.y, lr.y);
	}

	public static boolean withinValidWorld(ReferencedEnvelope bounds) {
		Envelope world = new Envelope(-181, 181, -91, 91);
		return world.contains(bounds.centre());
	}

	public static Coordinate pixelToWorld(double x, double y,
			ReferencedEnvelope extent, Dimension displaySize) {
		// set up the affine transform and calculate scale values
		AffineTransform at = worldToScreenTransform(extent, displaySize);

		try {
			Point2D result = at.inverseTransform(
					new java.awt.geom.Point2D.Double(x, y),
					new java.awt.geom.Point2D.Double());
			Coordinate c = new Coordinate(result.getX(), result.getY());

			return c;
		} catch (Exception e) {
			ProjectPlugin.log("Error transforming point:" + x + "," + y
					+ " to a coordinate", e);
		}

		return null;
	}

	/**
	 * @see ViewportModel#worldToScreenTransform(Envelope, Dimension)
	 */
	public static AffineTransform worldToScreenTransform(Envelope mapExtent,
			Dimension screenSize) {
		double scaleX = screenSize.getWidth() / mapExtent.getWidth();
		double scaleY = screenSize.getHeight() / mapExtent.getHeight();

		double tx = -mapExtent.getMinX() * scaleX;
		double ty = (mapExtent.getMinY() * scaleY) + screenSize.getHeight();

		AffineTransform at = new AffineTransform(scaleX, 0.0d, 0.0d, -scaleY,
				tx, ty);

		return at;
	}
	
	public static Point worldToPixel(Coordinate coord,
			ReferencedEnvelope bounds, Dimension displaySize) {

		Point2D w = new Point2D.Double(coord.x, coord.y);
		AffineTransform at = worldToScreenTransform(bounds, displaySize);
		Point2D p = at.transform(w, new Point2D.Double());
		return new Point((int)Math.round(p.getX()), (int)Math.round(p.getY()));
	}


    /**
     * This method restricts the bounds so that the resulting bounding box is at a legal scale within the bounds.  The centers of
     * the new and old BBox should be the same.
     *
     * @param bbox the bbox to restrict to a value between the min and max scales.
     * 
     * @return a new bounds that is withing the min and max bounds of the layer
     */
    public static ReferencedEnvelope fitToMinAndMax(ReferencedEnvelope bbox, ILayer layer) {
        ReferencedEnvelope bounds = restrictMinimum(bbox, layer);
        bounds = restrictMaximum(bounds, layer.getMap(), layer);

        return bounds;
    }

    /**
     * Checks the max scale denominator on the layer
     */
    private static ReferencedEnvelope restrictMaximum(ReferencedEnvelope bounds,
            IMap map, ILayer layer) {
        double maxFromLayer = ((Layer) layer).getMaxScaleDenominator();

        ReferencedEnvelope result = bounds;
        ReferencedEnvelope maxBounds = calculateBoundsFromScale(bounds, maxFromLayer, layer);
        if (bounds.contains((Envelope)maxBounds))
            result = maxBounds;
        return result;
    }

    /**
     * Checks the minScale denominator on the layer and the preference that is
     * set globally.
     */
    private static ReferencedEnvelope restrictMinimum(ReferencedEnvelope bounds, ILayer layer) {
        double minFromLayer = ((Layer) layer).getMinScaleDenominator();

        /*
         * Additional analysis of bounds. What if the only one point exists in
         * the layer? The application should not zoom to the scale 1:1
         */
        Integer minimumScale = layer.getMap().getBlackboard().getInteger(
                ProjectBlackboardConstants.LAYER__MINIMUM_ZOOM_SCALE);
        if (minimumScale == null) {
            minimumScale = ProjectPlugin.getPlugin().getPluginPreferences()
                    .getInt(PreferenceConstants.P_MINIMUM_ZOOM_SCALE);
        }

        ReferencedEnvelope result = bounds;
        if (minimumScale != null && minFromLayer < minimumScale) {
            ReferencedEnvelope minimumBounds = calculateBoundsFromScale(bounds,
                    minimumScale, layer);
            if (minimumBounds.contains((Envelope)bounds))
                result = minimumBounds;
        } else {
            ReferencedEnvelope minimumBounds = calculateBoundsFromScale(bounds,
                    minFromLayer, layer);
            if (minimumBounds.contains((Envelope)bounds))
                result = minimumBounds;
        }
        return result;
    }

    private static ReferencedEnvelope calculateBoundsFromScale(
            ReferencedEnvelope requestedBounds, double scaleDenominator, ILayer layer) {

        IRenderManager renderManager = layer.getMap().getRenderManager();
        if( renderManager==null ){
            // no render manager and therefore no scale to calculate.
            return requestedBounds;
        }
        IMapDisplay mapDisplay = renderManager.getMapDisplay();

        return ScaleUtils.calculateBoundsFromScale(scaleDenominator, mapDisplay
                .getDisplaySize(), mapDisplay.getDPI(), requestedBounds);
    }

    /**
     * Creates affine transform for zooming that keeps <i>fixedPoint</i> stationary.
     *
     * @param zoom zoom ration
     * @param fixedPoint point to keep stationary
     * @return an <i>AffineTransform</i> object containing scale transform that keeps
     *         <i>fixedPoint</i> stationary
     */
    public static AffineTransform createScaleTransformWithFixedPoint( double zoom,
            Coordinate fixedPoint ) {
        AffineTransform t = new AffineTransform();
        t.translate(fixedPoint.x, fixedPoint.y);
        t.scale(1 / zoom, 1 / zoom);
        t.translate(-fixedPoint.x, -fixedPoint.y);
        return t;
    }

	public static Envelope transformEnvelope( ReferencedEnvelope srcEnvelope, AffineTransform transformer ) {
	    Point2D lowLeft = new Point2D.Double(srcEnvelope.getMinX(), srcEnvelope.getMinY());
	    Point2D transformedLowLeft = new Point2D.Double();
	    transformedLowLeft = transformer.transform(lowLeft, transformedLowLeft);
	    Point2D upRight = new Point2D.Double(srcEnvelope.getMaxX(), srcEnvelope.getMaxY());
	    Point2D transformedUpRight = new Point2D.Double();
	    transformedUpRight = transformer.transform(upRight, transformedUpRight);
	    return new Envelope(transformedLowLeft.getX(), transformedUpRight.getX(),
	            transformedLowLeft.getY(), transformedUpRight.getY());
	}
	
	public static class CalculateZoomLevelParameter {
		public final ViewportModel model;
		public final IMapDisplay display;
		public final double previousZoom;
		public final double zoomChange;
		public final double requiredCloseness;
		public final Coordinate fixedPoint;
		public final boolean alwayUsePreferredZoomLevels;
		public final boolean alwaysChangeZoom;

		/**
		 *
		 * @param requiredCloseness see calculateClosestScale for a description of this parameter
		 */
		public CalculateZoomLevelParameter(ViewportModel model,
				IMapDisplay display, double previousZoom, double zoomChange, Coordinate fixedPoint,
				boolean alwayUsePreferredZoomLevels, boolean alwaysChangeZoom, double requiredCloseness) {
			this.model = model;
			this.display = display;
			this.previousZoom = previousZoom;
			this.zoomChange = zoomChange;
			this.fixedPoint = fixedPoint;
			this.alwayUsePreferredZoomLevels = alwayUsePreferredZoomLevels;
			this.alwaysChangeZoom = alwaysChangeZoom;
			this.requiredCloseness = requiredCloseness;
		}
	}
	public static double calculateZoomLevel(CalculateZoomLevelParameter params) {
		
		SortedSet<Double> preferredScaleDenominators = params.model.getPreferredScaleDenominators();
		if(!params.alwayUsePreferredZoomLevels && !preferredScaleDenominators.isEmpty() &&
				preferredScaleDenominators != params.model.getDefaultPreferredScaleDenominators()) {
	        Pair<Double, ReferencedEnvelope> previousScale = calculateScaleFromZoom(params.previousZoom, params.model.getBounds(), params);
	        ZoomCalculation targetZoomInfo = new ZoomCalculation(previousScale, params);
	        
	        double chosen;
	        
			double varianceFromPrevious = Math.abs(targetZoomInfo.closestMatch - previousScale.getLeft())/previousScale.getLeft();
			if(params.alwaysChangeZoom && varianceFromPrevious < 0.1) {
	        	if(params.zoomChange < 1) {
					chosen = targetZoomInfo.nextGreater == null? targetZoomInfo.closestMatch : targetZoomInfo.nextGreater;
	        	} else {
	        		chosen = targetZoomInfo.nextSmaller == null ? targetZoomInfo.closestMatch : targetZoomInfo.nextSmaller;
	        	}
	        } else {
	        	chosen = targetZoomInfo.closestMatch;
	        }
	        return params.model.getScaleDenominator()/chosen;
		} else {
				return Math.abs(params.previousZoom*params.zoomChange);
		}
	}

	private static Pair<Double, ReferencedEnvelope> calculateScaleFromZoom(double zoom,
			ReferencedEnvelope baseEnv, CalculateZoomLevelParameter params) {
		AffineTransform transformer = ScaleUtils.createScaleTransformWithFixedPoint(zoom,params.fixedPoint);
		ReferencedEnvelope transformedEnvelope = new ReferencedEnvelope(transformEnvelope(baseEnv, transformer),baseEnv.getCoordinateReferenceSystem());
		Double scale = ScaleUtils.calculateScaleDenominator(transformedEnvelope, params.display.getDisplaySize(), params.display.getDPI());
		// if there is a close match in preferred scale round to that scale
		Double closest = calculateClosestScale(params.model.getPreferredScaleDenominators(), scale, params.requiredCloseness);
		if(Math.abs(closest - scale)/scale < 0.01) {
			scale = closest;
		}
		return Pair.create(scale, transformedEnvelope);
	}

	/**
	 * Find the scale in the set of scaleDenominators that is closest to scale.
	 *
	 * @param scaleDenominators the options that can be chosen
	 * @param scale the desired scale
	 * @param requiredCloseness the nearness required to the next zoom level before the algorithm will allow zooming in to the next leve.
	 * 						    for example when zooming you might want the scale to be 70% of the way to the next closest level before zooming
	 * 							in to that level.  This is useful when zooming to a set of features.  If the zoom is 50% like normal then the zoom will not 
	 * 							show all features.
	 * 							values are 0-1 where 1 means the chosen scale will always be less than scale and 0 means the chosen scale will always be greater
	 * 							Usually this is set in the preferences.  see zoomClosenessPreference.
	 * @return
	 */
	public static Double calculateClosestScale(SortedSet<Double> scaleDenominators, double scale, double requiredCloseness) {
		SortedSet<Double> tail = scaleDenominators.tailSet(scale);
		SortedSet<Double> head = scaleDenominators.headSet(scale);
		Double distantZoom = first(tail,Double.MAX_VALUE);
		Double closeZoom = last(head,Double.MIN_VALUE);
		if(Math.abs(distantZoom - Double.MAX_VALUE) < 0.0001) {
			return closeZoom;
		}
		if(Math.abs(closeZoom - Double.MIN_VALUE) < 0.0001) {
			return distantZoom;
		}
		if((distantZoom - scale) < (distantZoom - closeZoom)*requiredCloseness) {
			return distantZoom;
		} else {
			return closeZoom;
		}
	}

	private static class ZoomCalculation {
        Double nextSmaller;
        double closestMatch;
        Double nextGreater;
        double scale;
        
        public ZoomCalculation(Pair<Double,ReferencedEnvelope> previous,CalculateZoomLevelParameter params) {
    		scale = calculateScaleFromZoom(params.zoomChange, previous.getRight(), params).getLeft();
    		SortedSet<Double> preferredScaleDenominators = new TreeSet<Double>(params.model.getPreferredScaleDenominators());
    		boolean zoomingIn = params.zoomChange < 1;
    		SortedSet<Double> tailSet = preferredScaleDenominators.tailSet(scale);
    		SortedSet<Double> headSet = preferredScaleDenominators.headSet(scale);
    		
    		if(preferredScaleDenominators.contains(scale)) {
    			closestMatch = scale;
    			tailSet.remove(scale);
    			headSet.remove(scale);
    			nextGreater = first(tailSet,null);
    			nextSmaller = last(headSet, null);
    		} else if(zoomingIn) {
    			closestMatch = tailSet.isEmpty() ? scale : tailSet.first();
    			tailSet.remove(closestMatch);
    			nextGreater = first(tailSet,null);
    			nextSmaller = last(headSet,null);
    		} else {
    			closestMatch = headSet.isEmpty() ? scale : headSet.last();
    			headSet.remove(closestMatch);
    			nextSmaller = last(headSet,null);
    			nextGreater = first(tailSet,null);				
    		}
		}
    	
    	@Override
    	public String toString() {
    		return scale+" ["+nextSmaller+", *"+closestMatch+"*, "+nextGreater+"]";
    	}
	}

	private static Double first(SortedSet<Double> set, Double defaultVal) {
		if(set.isEmpty()) return defaultVal;
		else return set.first();
	}
	private static Double last(SortedSet<Double> set, Double defaultVal) {
		if(set.isEmpty()) return defaultVal;
		else return set.last();
	}

	public static double zoomClosenessPreference() {
		return ProjectPlugin.getPlugin().getPreferenceStore().getDouble(PreferenceConstants.P_ZOOM_REQUIRED_CLOSENESS);
	}
}
