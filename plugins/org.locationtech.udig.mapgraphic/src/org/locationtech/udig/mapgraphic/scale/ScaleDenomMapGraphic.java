/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.mapgraphic.scale;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;

import org.apache.commons.lang.StringUtils;

import org.locationtech.udig.mapgraphic.MapGraphic;
import org.locationtech.udig.mapgraphic.MapGraphicContext;
import org.locationtech.udig.mapgraphic.style.FontStyle;
import org.locationtech.udig.mapgraphic.style.FontStyleContent;
import org.locationtech.udig.mapgraphic.style.LocationStyleContent;
import org.locationtech.udig.project.IBlackboard;
import org.locationtech.udig.project.IStyleBlackboard;
import org.locationtech.udig.ui.graphics.ViewportGraphics;

/**
 * A mapgraphic that display the map scale in the format
 * "1:X"
 * 
 * Available styles include: Font, Location, ScaleDenomStyle
 * 
 * @author Emily
 *
 */
public class ScaleDenomMapGraphic implements MapGraphic {

	private static NumberFormat NUMBER_FORMAT = NumberFormat.getInstance();
	static {
		NUMBER_FORMAT.setMaximumFractionDigits(0);
	}

	public ScaleDenomMapGraphic() {

	}

	public void draw(MapGraphicContext context) {

		ViewportGraphics g = context.getGraphics();
		IBlackboard mapblackboard = context.getMap().getBlackboard();

		double scaleDenom = context.getViewportModel().getScaleDenominator();

		// scale may be set by printing engine
		Object value = mapblackboard.get("scale");  //$NON-NLS-1$

		if (value != null && value instanceof Double) {
			scaleDenom = ((Double) value).doubleValue();
		}
		FontStyle fs = getFontStyle(context);
		g.setFont(fs.getFont());

		Point loc = getGraphicLocation(context);

		ScaleDenomStyle background = getStyle(context);
		
		String denomStr = NUMBER_FORMAT.format(scaleDenom);
		String str =  "1:" + denomStr; //$NON-NLS-1$
		//check if a prefix is provided
		if (StringUtils.trimToNull(background.getLabel()) != null){
                    str = background.getLabel() + " " + str;
                } 
		Rectangle2D bnds = g.getStringBounds(str);
		
		int x = loc.x;
		int y = loc.y;
		
		if (x < 0){
			x = context.getMapDisplay().getWidth() + x - (int)bnds.getWidth();
		}
		if (y < 0){
			 y = context.getMapDisplay().getHeight() + y - (int)bnds.getHeight();
		}
		
		//draw rectangle		
		if (background.getColor() != null){
			context.getGraphics().setColor(background.getColor());
			g.fillRect(x -2 , y-2, (int)bnds.getWidth() + 4, (int)bnds.getHeight() + 4);
		}

		g.setColor(fs.getColor());
		g.drawString(str, x + (int)bnds.getWidth() / 2, y + (g.getFontHeight() - g.getFontAscent()),
				ViewportGraphics.ALIGN_MIDDLE, ViewportGraphics.ALIGN_MIDDLE);
		
		
	}

	private ScaleDenomStyle getStyle(MapGraphicContext context){
		IStyleBlackboard styleBlackboard = context.getLayer().getStyleBlackboard();
		ScaleDenomStyle style = (ScaleDenomStyle) styleBlackboard.get(ScaleDenomStyleContent.ID);
		
		if (style == null) {
			style = new ScaleDenomStyle();
			styleBlackboard.put(ScaleDenomStyleContent.ID, style);
		}
		return style;	
	}
	
	private Point getGraphicLocation(MapGraphicContext context) {
		IStyleBlackboard styleBlackboard = context.getLayer()
				.getStyleBlackboard();
		
		Rectangle r = (Rectangle)styleBlackboard.get(LocationStyleContent.ID);
		if (r == null){
			r = LocationStyleContent.createDefaultStyle();
		}
		Point point = new Point(r.x,r.y);
		return point;
	}

	private FontStyle getFontStyle(MapGraphicContext context) {
		IStyleBlackboard styleBlackboard = context.getLayer()
				.getStyleBlackboard();
		FontStyle style = (FontStyle) styleBlackboard.get(FontStyleContent.ID);
		if (style == null) {
			style = new FontStyle();
			styleBlackboard.put(FontStyleContent.ID, style);
		}
		return style;
	}

}
