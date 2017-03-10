/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2008, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.mapgraphic.scalebar;

import static org.locationtech.udig.mapgraphic.scalebar.Unit.CENTIMETER;
import static org.locationtech.udig.mapgraphic.scalebar.Unit.KILOMETER;
import static org.locationtech.udig.mapgraphic.scalebar.Unit.METER;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.text.MessageFormat;
import java.util.Locale;

import org.locationtech.udig.catalog.util.CRSUtil;
import org.locationtech.udig.core.IProviderWithParam;
import org.locationtech.udig.core.Pair;
import org.locationtech.udig.mapgraphic.MapGraphic;
import org.locationtech.udig.mapgraphic.MapGraphicContext;
import org.locationtech.udig.mapgraphic.internal.Messages;
import org.locationtech.udig.mapgraphic.scalebar.BarStyle.BarType;
import org.locationtech.udig.mapgraphic.style.FontStyle;
import org.locationtech.udig.mapgraphic.style.FontStyleContent;
import org.locationtech.udig.mapgraphic.style.LocationStyleContent;
import org.locationtech.udig.project.IStyleBlackboard;
import org.locationtech.udig.project.ProjectBlackboardConstants;
import org.locationtech.udig.project.render.displayAdapter.IMapDisplay;
import org.locationtech.udig.ui.graphics.ViewportGraphics;

/**
 * Provides a decorator object that represents a typical Scalebar
 * 
 * @author Richard Gould
 * @since 0.6.0
 */
public class ScalebarMapGraphic implements MapGraphic {

    private static final String NUMBER_PATTERN = "{0,number,integer} "; //$NON-NLS-1$

    /** The width of the lines draw in the scalebar */
    private static final int BAR_WIDTH = 1;
    /** The height of the scale bar */
    private static final int BAR_HEIGHT = 8;

    /**
     * Finds a "nice" number to display for the given measurement
     * 
     * @param idealDistance the optimal distance in any unit.
     * @param range a strategy for obtaining "Good"/"attractive values. The first time the method is
     *        called a value of -1 will be passed to the parameter. Following calls will pass the
     *        result of the previous call. If the object returns null then it will signal that there
     *        are no more good values. This will be to obtain the next value. The provider should
     *        not contain state.
     * @return one of the values provided by the strategy. Always is smaller than idealDistance
     */
    static int closestInt( int idealDistance, final IProviderWithParam<Integer, Integer> range ) {

        if (range == null) {
            throw new NullPointerException("Range cannot be null"); //$NON-NLS-1$
        }

        int result = -1;
        Integer next = range.get(-1);
        Integer previous = -1;
        while( next != null ) {
            // perfect match lets take this
            if (next == idealDistance) {
                result = next;
                break;
            }

            // the current value is too high so lets take the previous.
            // this is so it will always display.
            // if this sucks then choose a better provider strategy
            if (idealDistance < next) {
                result = previous;
                break;
            }
            previous = next;
            next = range.get(next);
        }
        return result;
    }
    /*
     * @see org.locationtech.udig.project.render.decorator.Decorator#draw(java.awt.Graphics2D,
     *      org.locationtech.udig.project.render.RenderContext)
     */
    public void draw( MapGraphicContext context ) {
        // draw1(context);
        drawScaleDenom(context);
    }

    private void drawScaleDenom( MapGraphicContext context ) {
        double scaleDenom = context.getViewportModel().getScaleDenominator();
        IMapDisplay display = context.getMapDisplay();
        int dpi = display.getDPI(); // pixels per inch
        int displayHeight = display.getHeight();

        double i = 1 / (double) dpi; // invert to get in per pixel
        double pixelInMeters = i * .0254; // to meters per pixel 

        double inMeters = scaleDenom * pixelInMeters;

        Rectangle location = getGraphicLocation(context);
        
        // reserve this area of the screen so labels are not drawn here
        context.getLabelPainter().put( location );

        if (inMeters == 0.0) {
            drawWarning(context.getGraphics(), location, displayHeight);
        } else {
            BarStyle type = getBarStyle(context);
            UnitPolicy scalebarUnits = type.getUnits();
            if ((scalebarUnits == UnitPolicy.AUTO) && (CRSUtil.isCoordinateReferenceSystemImperial(context.getCRS()))){
                scalebarUnits = UnitPolicy.IMPERIAL;
            }
            Pair<Integer, Pair<Integer, Unit>> result2 = null;
            if (scalebarUnits == UnitPolicy.IMPERIAL){
                result2 = calculateUnitAndLength(inMeters,
                        location.width / type.getNumintervals(), Unit.MILE, Unit.FOOT, Unit.YARD, Unit.INCHES);
            }else{ /* METRIC and AUTO both treated as metric, since auto is converted above in CRS search */
                result2 = calculateUnitAndLength(inMeters,
                        location.width / type.getNumintervals(), KILOMETER, METER, CENTIMETER);                
            }
            
            int trueBarLength2 = result2.getLeft() * type.getNumintervals();
            Pair<Integer, Unit> unitMeasure2 = result2.getRight();
            int nice2 = unitMeasure2.getLeft();
            Unit measurement2 = unitMeasure2.getRight();
            doDraw(measurement2, context, trueBarLength2, nice2);

        }

    }

    private Rectangle getGraphicLocation( MapGraphicContext context ) {
        IStyleBlackboard styleBlackboard = context.getLayer().getStyleBlackboard();
        Rectangle location = (Rectangle) styleBlackboard.get(
                LocationStyleContent.ID);
        if (location == null) {
            location = LocationStyleContent.createDefaultStyle();
            styleBlackboard.put(LocationStyleContent.ID, location);
        }
        return location;
    }

    private FontStyle getFontStyle( MapGraphicContext context ) {
        IStyleBlackboard styleBlackboard = context.getLayer().getStyleBlackboard();
        FontStyle style = (FontStyle) styleBlackboard.get(FontStyleContent.ID);
        if (style == null) {
            style = new FontStyle();
            styleBlackboard.put(FontStyleContent.ID, style);
        }
        return style;
    }

    private BarStyle getBarStyle( MapGraphicContext context ) {
        IStyleBlackboard styleBlackboard = context.getLayer().getStyleBlackboard();
        BarStyle style = (BarStyle) styleBlackboard.get(BarStyleContent.ID);
        if (style == null) {
            style = new BarStyle();
            styleBlackboard.put(BarStyleContent.ID, style);
        }
        return style;

    }

    /**
     * Calculate the size of the scalebar to be drawn and the unit and actual distance given that
     * size. The calculation is based on the size of the MapGraphic location (style) and rounding to
     * a "good" size.
     * 
     * @param meterPerPixel distance per pixel in meters
     * @param idealBarLength the preferred length of the bar in pixels
     * @param units =
     * @return Pair< NumberOfPixeslToDraw, Pair<GroundDistance, Unit>>
     */
    static Pair<Integer, Pair<Integer, Unit>> calculateUnitAndLength( double meterPerPixel,
            int idealBarLength, Unit... units ) {

        if (units == null || units.length == 0) {
            throw new IllegalArgumentException("units must have at least one unit"); //$NON-NLS-1$
        }

        final double mIdealBarDistance = (meterPerPixel * idealBarLength);
        final NiceIntegers niceIntegers = new NiceIntegers();

        Unit unit = units[0];
        double displayDistance = -1;
        int barLengthPixels = idealBarLength;

        for( int i = 0; displayDistance < 1 && i < units.length; i++ ) {
            unit = units[i];
            displayDistance = closestInt((int) unit.meterToUnit(mIdealBarDistance), niceIntegers);

            barLengthPixels = (int) (unit.unitToMeter(displayDistance) / meterPerPixel);
        }

        Pair<Integer, Unit> unitMeasure = new Pair<Integer, Unit>((int) displayDistance, unit);

        return new Pair<Integer, Pair<Integer, Unit>>(barLengthPixels, unitMeasure);
    }

    private void doDraw( Unit measurement, MapGraphicContext context, int trueBarLength, int nice ) {

        Rectangle location = getGraphicLocation(context);
        
        context.getLabelPainter().put( location );
        
        BarStyle type = getBarStyle(context);
        /*
         * Draw the scale bar
         */
        int x = location.x;
        int y = location.y;

        int width = trueBarLength;
        int height = location.height;

        if (x < 0) {
            x = context.getMapDisplay().getWidth() + x - trueBarLength;
        }
        if (y < 0) {
            y = context.getMapDisplay().getHeight() + y - height;
        }

        int barWidth = -1;

        //get background color
        Color bcolor = (Color) context.getMap().getBlackboard().get(
                ProjectBlackboardConstants.MAP__BACKGROUND_COLOR);
        BarStyle barStyle = getBarStyle(context);
        if (barStyle.getBgColor() != null){
            bcolor = barStyle.getBgColor();
        }
        if (bcolor == null) {
            bcolor = Color.WHITE;
        }
        
        // setup font
        FontStyle font = getFontStyle(context);
        if (font != null && font.getFont() != null) {
            context.getGraphics().setFont(font.getFont());
        }
        
        //draw halo
        Color maskColor = new Color(bcolor.getRed(), bcolor.getGreen(), bcolor.getBlue(), 80);
        computeAndDrawHalo(nice, context, measurement, type, x, y, width, height, maskColor);
        
        //draw bar with labels
        if (type.getType() == BarType.SIMPLE) {
            drawSimpleBar(context.getGraphics(), location, x, y, width, height, type
                    .getColor());
            drawSingleLabel(context, measurement, nice * type.getNumintervals(), x, y, height,
                    barWidth, width, type.getColor());
        } else if (type.getType() == BarType.SIMPLE_LINE) {
            drawLineBar(context.getGraphics(), location, x, y, width, height, type
                    .getNumintervals(), type.getColor(), bcolor);
            drawIntervalLabels(context, measurement, nice, x, y, height, barWidth, width, type
                    .getNumintervals(), type.getColor());
        } else if (type.getType() == BarType.FILLED) {
             drawFilled(context.getGraphics(), location, x, y, width, height, type
                    .getNumintervals(), type.getColor(), bcolor);
            drawIntervalLabels(context, measurement, nice, x, y, height, barWidth, width, type
                    .getNumintervals(), type.getColor());
        } else if (type.getType() == BarType.FILLED_LINE) {
             drawFilledLineBar(context.getGraphics(), location, x, y, width, height, type
                    .getNumintervals(), type.getColor(), bcolor);
            drawIntervalLabels(context, measurement, nice, x, y, height, barWidth, width, type
                    .getNumintervals(), type.getColor());
        }
    }

    private void computeAndDrawHalo( int nice, MapGraphicContext context, Unit measurement,
            BarStyle type, int x, int y, int width, int height, Color bgColor ) {

        // determine maximum label size
        MessageFormat formatter = new MessageFormat("", Locale.getDefault()); //$NON-NLS-1$
        formatter.applyPattern(NUMBER_PATTERN + measurement.display);
        Object[] arguments = {nice * type.getNumintervals()};
        String msg = formatter.format(arguments);
        Rectangle2D msgBounds = context.getGraphics().getStringBounds(msg);

        double textwidth = msgBounds.getWidth();
        double textheight = msgBounds.getHeight();

        if (type.getType() == BarType.SIMPLE){
            textwidth = 10;
        }
        double halox = (int) (x + BAR_WIDTH) - (textwidth / 2.0);
        double halowidth = ((int) (width / (type.getNumintervals() * 2))) * type.getNumintervals()
                * 2 + (textwidth) + 5;

        double haloy = y + height - BAR_HEIGHT - textheight - 2;
        double haloheight = BAR_HEIGHT + textheight + 4;

        context.getGraphics().setColor(bgColor);
        RoundRectangle2D roundBounds = new RoundRectangle2D.Double();
        roundBounds.setRoundRect(halox, haloy, halowidth, haloheight, 6, 6);
        context.getGraphics().fill(roundBounds);
    }
    /**
     * draws a single label for the simple scalebar
     */
    private void drawSingleLabel( MapGraphicContext context, Unit measurement, int nice, int x,
            int y, int height, int barWidth, int width, Color c ) {
        ViewportGraphics graphics = context.getGraphics();

        FontStyle font = getFontStyle(context);

        if (font != null && font.getFont() != null) {
            graphics.setFont(font.getFont());
        }

        MessageFormat formatter = new MessageFormat("", Locale.getDefault()); //$NON-NLS-1$
        formatter.applyPattern(NUMBER_PATTERN + measurement.display);

        Object[] arguments = {nice};
        String msg = formatter.format(arguments);
        Rectangle2D msgBounds = graphics.getStringBounds(msg);

        int yText = y + (height - barWidth) - 4;
        int xText = x + (int) (width * 0.5) - (int) (msgBounds.getWidth() * 0.5);

        // Draw the string denoting kilometres (or metres)
        graphics.setColor(c);
        graphics.drawString(msg, xText, yText, ViewportGraphics.ALIGN_LEFT,
                ViewportGraphics.ALIGN_BOTTOM);
    }

    /**
     * draws the interval labels
     */
    private void drawIntervalLabels( MapGraphicContext context, Unit measurement, int nice, int x,
            int y, int height, int barWidth, int width, int numIntervals, Color c ) {

        ViewportGraphics graphics = context.getGraphics();

        FontStyle font = getFontStyle(context);

        if (font != null && font.getFont() != null) {
            graphics.setFont(font.getFont());
        }

        MessageFormat formatter = new MessageFormat("", Locale.getDefault()); //$NON-NLS-1$
        formatter.applyPattern(NUMBER_PATTERN);

        for( int i = 0; i < numIntervals; i++ ) {

            drawIntervalLabel(nice, x, y, height, barWidth, width, numIntervals, c,
                    graphics, formatter, i);
        }

        formatter = new MessageFormat("", Locale.getDefault()); //$NON-NLS-1$
        formatter.applyPattern(NUMBER_PATTERN + measurement.display);
        drawIntervalLabel(nice, x, y, height, barWidth, width, numIntervals, c,
                graphics, formatter, numIntervals);
    }

    /**
     * draws an individual interval label with a background halo
     */
    private void drawIntervalLabel( int nice, int x, int y, int height, int barWidth, int width,
            int numIntervals, Color c, ViewportGraphics graphics,
            MessageFormat formatter, int i ) {
        Object[] arguments = {nice * i};
        String msg = formatter.format(arguments);
        msg = msg.trim();
        Rectangle2D msgBounds = graphics.getStringBounds(msg);

        int yText = y + (height - barWidth) - 12;
        int xText = x + barWidth + i * (width / numIntervals)
                - ((int) msgBounds.getWidth() / 2);

        // Draw the string denoting kilometres (or metres)
        graphics.setColor(c);
        graphics.drawString(msg, xText, yText, ViewportGraphics.ALIGN_LEFT,
                ViewportGraphics.ALIGN_BOTTOM);
    }

    /**
     * Draws a bar from the start to the end with both ends included:
     * 
     * <pre><code>
     * |                                              |
     * +----------------------------------------------+
     * </code></pre>
     */
    private void drawSimpleBar( ViewportGraphics graphics, Rectangle location, int x, int y,
            int width, int height, Color c ) {

        int barStartX = x + 1 + BAR_WIDTH / 2;
        int barStartY = y - 2 + location.height - BAR_HEIGHT;

        GeneralPath path = new GeneralPath();
        path.moveTo(barStartX, barStartY);
        path.lineTo(barStartX, barStartY + BAR_HEIGHT);
        path.lineTo(barStartX + width, barStartY + BAR_HEIGHT);
        path.lineTo(barStartX + width, barStartY);

        graphics.setColor(c);
        graphics.setStroke(ViewportGraphics.LINE_SOLID, BAR_WIDTH);
        graphics.draw(path);
    }

    /**
     * Draws a scale bar with every second scale filled in.
     * 
     * <pre><code>
     * +--+--+----+----+----+----+
     * |xx|  |xxxx|    |xxxx|    |
     * +--+--+----+----+----+----+
     * </code></pre>
     */
    private void drawFilled( ViewportGraphics graphics, Rectangle location, int x, int y,
            int width, int height, int numIntervals, Color c, Color background ) {

        int barStartX = x + 1 + BAR_WIDTH / 2;
        int barStartY = y - 2 + location.height - BAR_HEIGHT;

        int interval = width / (numIntervals * 2);

        Color color = c;
        Color intervalColor = background;

        graphics.setColor(color);
        graphics.fillRect(barStartX, barStartY, (int) interval, BAR_HEIGHT);
        for( int i = 2; i < numIntervals * 2; i += 4 ) {
            graphics.fillRect(barStartX + i * interval, barStartY, interval * 2, BAR_HEIGHT);
        }

        graphics.setColor(intervalColor);
        graphics.fillRect(barStartX + interval, barStartY, interval, BAR_HEIGHT);
        for( int i = 4; i < numIntervals * 2; i += 4 ) {
            graphics.fillRect(barStartX + i * interval, barStartY, interval * 2, BAR_HEIGHT);
        }

        // draw border around the whole thing
        GeneralPath path = new GeneralPath();
        path.moveTo(barStartX, barStartY);

        path.lineTo(barStartX, barStartY + BAR_HEIGHT);
        path.lineTo(barStartX + numIntervals * 2 * interval, barStartY + BAR_HEIGHT);
        path.lineTo(barStartX + numIntervals * 2 * interval, barStartY);
        path.lineTo(barStartX, barStartY);

        graphics.setColor(color);
        graphics.setStroke(ViewportGraphics.LINE_SOLID, BAR_WIDTH);
        graphics.draw(path);
    }

    /**
     * Draws a bar with lines representing breaks:
     * 
     * <pre><code>
     * +--+--+----+----+----+----+
     * |  |  |    |    |    |    |
     * +--+  +----+    +----+    +
     * |  |  |    |    |    |    |
     * +--+--+----+----+----+----+
     * </code></pre>
     */
    private void drawLineBar( ViewportGraphics graphics, Rectangle location, int x, int y,
            int width, int height, int numIntervals, Color c, Color bcolor ) {

        int barStartX = x + 1 + BAR_WIDTH / 2;
        int barStartY = y - 2 + location.height - BAR_HEIGHT;

        int interval = width / (numIntervals * 2);

        //draw solid background
        graphics.setColor(bcolor);
        graphics.fillRect(barStartX, barStartY, (int) interval * numIntervals * 2, BAR_HEIGHT);
        
        
        GeneralPath path = new GeneralPath();
        // draw outline box
        path.moveTo(barStartX, barStartY);
        path.lineTo(barStartX, barStartY + BAR_HEIGHT);
        path.lineTo(barStartX + numIntervals * 2 * interval, barStartY + BAR_HEIGHT);
        path.lineTo(barStartX + numIntervals * 2 * interval, barStartY);
        path.lineTo(barStartX, barStartY);

        // draw vertical dividers
        path.moveTo(barStartX + interval, barStartY);
        path.lineTo(barStartX + interval, barStartY + BAR_HEIGHT);
        for( int i = 2; i < numIntervals * 2; i += 2 ) {
            path.moveTo(barStartX + i * interval, barStartY);
            path.lineTo(barStartX + i * interval, barStartY + BAR_HEIGHT);
        }

        // draw horizontal dividers
        path.moveTo(barStartX, barStartY + BAR_HEIGHT / 2);
        path.lineTo(barStartX + interval, barStartY + BAR_HEIGHT / 2);
        for( int i = 2; i < numIntervals * 2; i += 4 ) {
            path.moveTo(barStartX + i * interval, barStartY + BAR_HEIGHT / 2);
            path.lineTo(barStartX + (i + 2) * interval, barStartY + BAR_HEIGHT / 2);
        }

        graphics.setColor(c);
        graphics.setStroke(ViewportGraphics.LINE_SOLID, BAR_WIDTH);
        graphics.draw(path);
    }

    /**
     * Draws a bar with lines representing breaks & alternate filled breaks:
     * 
     * <pre><code>
     * +--+--+----+----+----+----+
     * |xx|  |xxxx|    |xxxx|    |
     * +--+--+----+----+----+----+
     * |  |xx|    |xxxx|    |xxxx|
     * +--+--+----+----+----+----+
     * </pre></code>
     */
    private void drawFilledLineBar( ViewportGraphics graphics, Rectangle location, int x, int y,
            int width, int height, int numIntervals, Color c, Color background ) {

        int barStartX = x + 1 + BAR_WIDTH / 2;
        int barStartY = y - 2 + location.height - BAR_HEIGHT;

        int interval = width / (numIntervals * 2);

        Color color = c;
        Color intervalColor = background;


        graphics.setColor(color);
        graphics.fillRect(barStartX, barStartY, interval, BAR_HEIGHT / 2);
        graphics.fillRect((barStartX + interval), barStartY + BAR_HEIGHT / 2, interval,
                BAR_HEIGHT / 2);
        for( int i = 2; i < numIntervals * 2; i += 2 ) {
            if (i % 4 != 0) {
                graphics.fillRect(barStartX + i * interval, barStartY, interval * 2, BAR_HEIGHT / 2);
            } else {
                graphics.fillRect(barStartX + i * interval, barStartY + BAR_HEIGHT / 2,
                        (int) (interval * 2), BAR_HEIGHT / 2);
            }
        }

        graphics.setColor(intervalColor);
        graphics.fillRect(barStartX, barStartY + BAR_HEIGHT / 2, interval, BAR_HEIGHT / 2);
        graphics.fillRect(barStartX + interval, barStartY, interval, BAR_HEIGHT / 2);
        for( int i = 2; i < numIntervals * 2; i += 2 ) {
            if (i % 4 != 0) {
                graphics.fillRect(barStartX + i * interval, barStartY + BAR_HEIGHT / 2,
                        interval * 2, BAR_HEIGHT / 2);
            } else {
                graphics.fillRect(barStartX + i * interval, barStartY, interval * 2, BAR_HEIGHT / 2);
            }
        }

        GeneralPath path = new GeneralPath();
        path.moveTo(barStartX, barStartY);
        path.lineTo(barStartX, barStartY + BAR_HEIGHT);
        path.lineTo(barStartX + numIntervals * 2 * interval, barStartY + BAR_HEIGHT);
        path.lineTo(barStartX + numIntervals * 2 * interval, barStartY);
        path.lineTo(barStartX, barStartY);

        path.moveTo(barStartX + interval, barStartY);
        path.lineTo(barStartX + interval, barStartY + BAR_HEIGHT);

        for( int i = 2; i < numIntervals * 2; i += 2 ) {
            path.moveTo(barStartX + i * interval, barStartY);
            path.lineTo(barStartX + i * interval, barStartY + BAR_HEIGHT);

        }

        graphics.setColor(color);
        graphics.setStroke(ViewportGraphics.LINE_SOLID, BAR_WIDTH);
        graphics.draw(path);
    }

 
    private void drawWarning( ViewportGraphics graphics, Rectangle location, int displayHeight ) {
        graphics.setColor(Color.GRAY);
        graphics.fillRect(location.x, location.y, location.width, location.height);
        graphics.setColor(Color.BLACK);
        // graphics.drawRect( location.x, location.y, location.width, location.height );
        graphics.draw(new Rectangle(location.x, location.y, location.width, location.height));

        int yText = (location.y < (displayHeight / 2))
                ? location.y + location.height + 15
                : location.y - 15;
        graphics.drawString(Messages.ScalebarMapGraphic_zoomInRequiredMessage, location.x, yText,
                -1, -1);
    }

}
