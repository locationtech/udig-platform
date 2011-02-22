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
 *
 */
package net.refractions.udig.mapgraphic.scalebar;

import static net.refractions.udig.mapgraphic.scalebar.Unit.*;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.text.MessageFormat;
import java.util.Locale;

import net.refractions.udig.core.IProviderWithParam;
import net.refractions.udig.core.Pair;
import net.refractions.udig.mapgraphic.MapGraphic;
import net.refractions.udig.mapgraphic.MapGraphicContext;
import net.refractions.udig.mapgraphic.internal.Messages;
import net.refractions.udig.mapgraphic.style.FontStyle;
import net.refractions.udig.mapgraphic.style.FontStyleContent;
import net.refractions.udig.mapgraphic.style.LocationStyleContent;
import net.refractions.udig.project.IStyleBlackboard;
import net.refractions.udig.project.render.displayAdapter.IMapDisplay;
import net.refractions.udig.ui.graphics.ViewportGraphics;

/**
 * Provides a decorator object that represents a typical Scalebar
 *
 * @author Richard Gould
 * @since 0.6.0
 */
public class ScalebarMapGraphic implements MapGraphic {

    private static final String NUMBER_PATTERN = "{0,number,integer} "; //$NON-NLS-1$

    public ScalebarMapGraphic() {
    }

    /**
     * Finds a "nice" number to display for the given measurement
     *
     * @param idealDistance the optimal distance in any unit.
     * @param range a strategy for obtaining "Good"/"attractive values. The first time the method is
     *        called a value of -1 will be passed to the parameter. Following calls will pass the
     *        result of the previous call. If the object returns null then it will signal that there
     *        are no more good values. This will be to obtain the next value. The provider should
     *        not contain state.
     * @return one of the values provided by the strategy.  Always is smaller than idealDistance
     */
    static int closestInt( int idealDistance, final IProviderWithParam<Integer, Integer> range ) {

        if (range == null) {
            throw new NullPointerException("Range cannot be null"); //$NON-NLS-1$
        }

        int result = -1;
        Integer next = range.get(-1);
        Integer previous = -1;
        while(  next !=null ) {
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
     * @see net.refractions.udig.project.render.decorator.Decorator#draw(java.awt.Graphics2D,
     *      net.refractions.udig.project.render.RenderContext)
     */
    public void draw( MapGraphicContext context ) {
        // draw1(context);
        drawScaleDenom(context);
    }

    private void drawScaleDenom( MapGraphicContext context ) {
        double scaleDenom = context.getViewportModel().getScaleDenominator();
        IMapDisplay display = context.getMapDisplay();
        int dpi = display.getDPI();
        int displayHeight = display.getHeight();

        double i = 1 / (double) dpi;
        double d = i * 25.4;
        double pixelInMeters = d / 1000;

        double inMeters = scaleDenom * pixelInMeters;

        Rectangle location = getGraphicLocation(context);

        if (inMeters == 0.0) {
            drawWarning(context.getGraphics(), location, displayHeight);
        } else {
            Pair<Integer, Pair<Integer, Unit>> result = calculateUnitAndLength(inMeters, location.width, KILOMETER, METER, CENTIMETER);
            int trueBarLength = result.getLeft();
            Pair<Integer, Unit> unitMeasure = result.getRight();
            int nice = unitMeasure.getLeft();
            Unit measurement = unitMeasure.getRight();
            doDraw(measurement, context, trueBarLength, nice);
        }

    }

    private Rectangle getGraphicLocation( MapGraphicContext context ) {
        Rectangle location = (Rectangle) context.getLayer().getStyleBlackboard().get(
                LocationStyleContent.ID);
        if (location == null) {
            location = LocationStyleContent.createDefaultStyle();
            context.getLayer().getStyleBlackboard().put(LocationStyleContent.ID, location);
        }
        return location;
    }

    private FontStyle getFontStyle( MapGraphicContext context ) {
        IStyleBlackboard styleBlackboard = context.getSelectedLayer().getStyleBlackboard();
        FontStyle style = (FontStyle) styleBlackboard.get(FontStyleContent.ID);
        if( style==null ){
            style = new FontStyle();
            styleBlackboard.put(FontStyleContent.ID, style);
        }
        return style;
    }

    /**
     * Calculate the size of the scalebar to be drawn and the unit and actual distance given that
     * size. The calculation is based on the size of the MapGraphic location (style) and rounding to
     * a "good" size.
     * @param meterPerPixel distance per pixel in meters
     * @param idealBarLength the preferred length of the bar in pixels
     * @param units =
     *
     * @return Pair< NumberOfPixeslToDraw, Pair<GroundDistance, Unit>>
     */
     static Pair<Integer, Pair<Integer, Unit>> calculateUnitAndLength( double meterPerPixel,
            int idealBarLength, Unit... units  ) {

         if( units==null || units.length==0){
             throw new IllegalArgumentException("units must have at least one unit"); //$NON-NLS-1$
         }


        final double mIdealBarDistance = (meterPerPixel * idealBarLength);
        final NiceIntegers niceIntegers = new NiceIntegers();

        Unit unit = units[0];
        double displayDistance = -1;
        int barLengthPixels = idealBarLength;

        for( int i=0; displayDistance < 1 && i<units.length; i++ ) {
            unit = units[i];
            displayDistance = closestInt((int) unit.meterToUnit(mIdealBarDistance), niceIntegers);

            barLengthPixels = (int) (unit.unitToMeter(displayDistance) / meterPerPixel);
        }

        Pair<Integer, Unit> unitMeasure = new Pair<Integer, Unit>((int) displayDistance, unit);

        return new Pair<Integer, Pair<Integer, Unit>>(barLengthPixels, unitMeasure);
    }

    private void doDraw( Unit measurement, MapGraphicContext context,
            int trueBarLength, int nice ) {

        Rectangle location = getGraphicLocation(context);

        /*
         * Draw the scale bar
         */
        int x = location.x;
        int y = location.y;

        int width = trueBarLength;
        int height = location.height;

        int barWidth = drawBar(context.getGraphics(), location, x, y, width, height);

        drawString(context, measurement, nice, x, y, height, barWidth);
    }

    private void drawString( MapGraphicContext context, Unit measurement, int nice, int x, int y,
            int height, int barWidth ) {
        ViewportGraphics graphics = context.getGraphics();

        FontStyle font = getFontStyle(context);

        if( font!=null && font.getFont()!=null ){
            graphics.setFont(font.getFont());
        }

        MessageFormat formatter = new MessageFormat("", Locale.getDefault()); //$NON-NLS-1$
        formatter.applyPattern(NUMBER_PATTERN+measurement.display);

        Object[] arguments = {nice};
        String msg = formatter.format(arguments);

        int yText = y + (height - barWidth) - 4;
        int xText = x + barWidth + 4;

        // Draw a little alpha box underneath the text, so it can be viewed in dark areas.
        graphics.setColor(new Color(255, 255, 255, 103)); // 103/255 = ~40% transparency

        Rectangle2D msgBounds = graphics.getStringBounds(msg);
        RoundRectangle2D roundBounds = new RoundRectangle2D.Double();
        roundBounds.setRoundRect(xText - 1, yText - (int) msgBounds.getHeight() + 3, msgBounds
                .getWidth() + 4, msgBounds.getHeight() - 1, 10, 10);
        graphics.fill(roundBounds);

        // Draw the string denoting kilometres (or metres)
        graphics.setColor(Color.BLACK);
        graphics.drawString(msg, xText, yText, ViewportGraphics.ALIGN_LEFT,
                ViewportGraphics.ALIGN_BOTTOM);
    }

    private int drawBar( ViewportGraphics graphics, Rectangle location, int x, int y, int width,
            int height ) {
        int barWidth = 4;
        int barHeight = 10;

        int barStartX = x+1 + barWidth / 2;
        int barStartY = y-2 + location.height-barHeight;

        GeneralPath path = new GeneralPath();
        path.moveTo(barStartX, barStartY);
        path.lineTo(barStartX, barStartY + barHeight);
        path.lineTo(barStartX + width, barStartY + barHeight);
        path.lineTo(barStartX + width, barStartY);

        graphics.setColor(Color.WHITE);
        graphics.setStroke(ViewportGraphics.LINE_SOLID, barWidth + 2);

        graphics.draw(path);

        graphics.setColor(Color.BLACK);
        graphics.setStroke(ViewportGraphics.LINE_SOLID, barWidth);

        graphics.draw(path);
        return barWidth;
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
