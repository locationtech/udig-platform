/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2005, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.legend.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.geotools.styling.ColorMap;
import org.geotools.styling.ColorMapEntry;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Symbolizer;
import org.opengis.filter.expression.Expression;

/**
 * Creates legend entries for a ColorMap
 * style.
 * 
 * @author Emily Gouge
 *
 */
public class ColorMapLegendCreator {

	/**
	 * Searches the feature type styles for a color map;
	 * @param style
	 * @return
	 */
	private static ColorMap findColorMap(FeatureTypeStyle[] style){
		if (style == null){
			return null;
		}
		for (int i = 0; i < style.length; i ++){
			FeatureTypeStyle fstyle = style[i];	
			List<Rule> rules = fstyle.rules();
			if (rules == null){
				continue;
			}
			
			for (Rule rule : rules){
				Symbolizer[] syms = rule.getSymbolizers();
				if (syms == null){
					continue;
				}
				for (int j = 0; j < syms.length; j ++){
					Symbolizer sym = syms[j];	
					if (sym instanceof RasterSymbolizer && 
							((RasterSymbolizer) sym).getColorMap() != null){
						return ((RasterSymbolizer) sym).getColorMap();
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Creates legend entries from color map.  
	 * <p>This function searches all the featuretypestyles provided
	 * for a color map.  The first color map found is used in the
	 * legend.  If no color maps are found then null is returned.
	 * </p>
	 * @param style array of styles to search for color map
	 * @param imageSize size of icon to create for legend entry
	 * @return list of legend entries in the first color map found or
	 * null if no color map found
	 */
	public static List<LegendEntry> findEntries(FeatureTypeStyle[] style, Dimension imageSize, Dimension textSize){
		ColorMap cm = findColorMap(style);
		if (cm == null || cm.getColorMapEntries().length == 0){
			return null;
		}
		return findEntries(cm, imageSize, textSize);
		
	}
	
	/**
	 * Finds legend entries for the given color map.
	 * 
	 * @param colorMap
	 * @param imageSize
	 * @return list of legend entries or null if entries could not be created 
	 */
	public static List<LegendEntry> findEntries(ColorMap colorMap, Dimension imageSize, Dimension textSize){
		
		if (colorMap.getType() == ColorMap.TYPE_INTERVALS){
			return createIntervalEntries(colorMap, imageSize);
		}else if (colorMap.getType() == ColorMap.TYPE_RAMP){
			if (imageSize.height < textSize.height){
				imageSize = new Dimension(imageSize.width, textSize.height);
			}
			return createRampEntries(colorMap, imageSize);
		}else if (colorMap.getType() == ColorMap.TYPE_VALUES){
			return createValuesEntries(colorMap,imageSize);
			
		}
		
		return null;
		
	}
	
	/**
	 * Determines if the provided color map entry
	 * represents a no data entry.
	 * 
	 * @param entry
	 * @return <code>true</code> if no data entry, <code>false</code> otherwise
	 */
	private static boolean isNoData(ColorMapEntry entry){
		if (entry.getLabel() != null && entry.getLabel().equals("-no data-")){
			return true;
		}
		return false;
	}
	/*
	 * Create legend entries for color ramp color map
	 */
	private static List<LegendEntry> createRampEntries(ColorMap colorMap, final Dimension imageSize){
		List<LegendEntry> lentries = new ArrayList<LegendEntry>();
		final ColorMapEntry[] entries = colorMap.getColorMapEntries();
		boolean first = true;
		for (int i = 1; i < entries.length; i += 1){
			final ColorMapEntry entry = entries[i];
			final ColorMapEntry prevEntry = entries[i-1];
			
			if (isNoData(prevEntry)){
				//skip no data entries in legend
				continue;
			}
			
			ImageDescriptor dd = null;
			int idx = -1;
			if (first){
				idx = 0;
			}else if (i > entries.length - 2){
				idx = 2;
			}
			final int index = idx;
			
			first = false;
			dd = new ImageDescriptor() {
				@Override
				public ImageData getImageData() {

					Display display = PlatformUI.getWorkbench().getDisplay();

					Image swtImage = new Image(display, imageSize.width,
							imageSize.height);
					GC gc = new GC(swtImage);
					gc.setAntialias(SWT.ON);

					org.eclipse.swt.graphics.Color c1 = getColor(display,
							entry.getColor());
					org.eclipse.swt.graphics.Color c2 = getColor(display,
							prevEntry.getColor());

					gc.setAlpha(getAlpha(entry.getOpacity()));
					gc.setBackground(c1);
					gc.setForeground(c2);
					gc.fillGradientRectangle(1, 0, imageSize.width-2,
							imageSize.height, true);

					gc.setAlpha(150);
					gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
					gc.drawLine(0, 0, 0, imageSize.height);
					gc.drawLine(imageSize.width-1, 0, imageSize.width-1, imageSize.height);
	                if (index == 0){
	                	gc.drawLine(0, 0, imageSize.width-1, 0);	
	                }else if (index == 2){
	                	gc.drawLine(0, imageSize.height - 1, imageSize.width-1, imageSize.height - 1);
	                }
	                
					c1.dispose();
					c2.dispose();

					ImageData clone = (ImageData) swtImage.getImageData()
							.clone();
					swtImage.dispose();

					return clone;
				}
			};
			
		        
			String[] text = null;
			String q1 = entry.getQuantity().evaluate(null, String.class);
			String q2 = prevEntry.getQuantity().evaluate(null, String.class);

			String l1 = entry.getLabel();
			String l2 = prevEntry.getLabel();
			if (i < entries.length - 1){
				if (l2 == null){
					text = new String[]{q2};
				}else{
					text = new String[]{l2 + " (" + q2 + ")"}; //$NON-NLS-1$ //$NON-NLS-2$
				}
			}else{
				text = new String[2];
				if (l2 == null){
					text[0] = q2;
				}else{
					text[0] = l2 + " (" + q2 + ")";  //$NON-NLS-1$//$NON-NLS-2$
				}
				if (l1 == null){
					text[1] = q1;
				}else{
					text[1] = l1 + " (" + q1 + ")";  //$NON-NLS-1$//$NON-NLS-2$
				}
			}
			
			LegendEntry le = new LegendEntry(text, dd);
			le.setSpacingAfter(0);
	        le.setTextPosition(SWT.TOP);
	        lentries.add(le);
			
		}
		lentries.get(lentries.size()-1).setSpacingAfter(null);	//default spacing
		return lentries;
	}
	
	
	/*
	 * Create legend entries for interval color ramp
	 */
	private static List<LegendEntry> createIntervalEntries(ColorMap colorMap, final Dimension imageSize){
		List<LegendEntry> lentries = new ArrayList<LegendEntry>();
		final ColorMapEntry[] entries = colorMap.getColorMapEntries();
		for (int i = 0; i < entries.length; i ++){
			ColorMapEntry prevEntry = null; 
			if (i > 0){
				prevEntry = entries[i-1];
			}
			final ColorMapEntry entry = entries[i];
			
			if (isNoData(entry)){
				//skip no data entries in legend
				continue;
			}
			
			ImageDescriptor dd = null;
				 dd = new ImageDescriptor() {
					@Override
		            public ImageData getImageData() {
		                              
		                Display display =PlatformUI.getWorkbench().getDisplay();
		                
		                Image swtImage = new Image(display, imageSize.width, imageSize.height);
		                GC gc = new GC(swtImage);
		                gc.setAntialias(SWT.ON);
		                
		                org.eclipse.swt.graphics.Color c = getColor(display, entry.getColor());
		                
		                gc.setAlpha(getAlpha(entry.getOpacity()));
		                gc.setBackground( c );
		                gc.fillRectangle( 1, 1, imageSize.width-2, imageSize.height-2);
		                c.dispose();
		                
		                gc.setAlpha(150);
		                gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
		                gc.drawRectangle(1, 1, imageSize.width - 2, imageSize.height-2);
		                
		                ImageData clone = (ImageData) swtImage.getImageData().clone();                
		                swtImage.dispose();
		                
		                return clone;
		            }
		        };
		        String text = null;
		        
		        String q1 = entry.getQuantity().evaluate(null, String.class);
		        String l1 = entry.getLabel();
		        
		        if (prevEntry == null){
		        	text = "< " + q1; //$NON-NLS-1$
		        	if (l1 != null){
		        		text = l1 + " (" + text + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		        	}
		        }else{
		        	String q2 = prevEntry.getQuantity().evaluate(null, String.class);
	        
		        	text = q2 + " - " + q1; //$NON-NLS-1$ 
		        	if (l1 != null){
		        		text = l1 + " (" + text + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		        	}
		        }
		        LegendEntry le = new LegendEntry(text, dd);
		        lentries.add(le);

			
		}
		return lentries;
	}
	
	
	/*
	 * Create legend entries for value color map
	 */
	private static List<LegendEntry> createValuesEntries(ColorMap colorMap, final Dimension imageSize){
		List<LegendEntry> lentries = new ArrayList<LegendEntry>();
		
		final ColorMapEntry[] entries = colorMap.getColorMapEntries();
		for (int i = 0; i < entries.length; i ++){
			final ColorMapEntry entry = entries[i];	
			
			if (isNoData(entry)){
				//skip no data entries in legend
				continue;
			}
			
			ImageDescriptor dd = null;
				 dd = new ImageDescriptor() {
					@Override
		            public ImageData getImageData() {
		                Display display =PlatformUI.getWorkbench().getDisplay();
		                
		                Image swtImage = new Image(display, imageSize.width, imageSize.height);
		                GC gc = new GC(swtImage);
		                gc.setAntialias(SWT.ON);
		                org.eclipse.swt.graphics.Color c = getColor(display, entry.getColor());

		                gc.setAlpha(getAlpha(entry.getOpacity()));
		                gc.setBackground( c );
		                gc.fillRectangle( 1, 1, imageSize.width-2, imageSize.height-2);
		                
		                gc.setAlpha(150);
		                gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
		                gc.drawRectangle(1, 1, imageSize.width - 2, imageSize.height-2);
		                
		                c.dispose();
		                
		                ImageData clone = (ImageData) swtImage.getImageData().clone();                
		                swtImage.dispose();
		                
		                return clone;
		            }
		        };
		        
		        String text = null;
		        String q1 = entry.getQuantity().evaluate(null, String.class);
		        String l1 = entry.getLabel();
		     	text = q1;
		        if (l1 != null){
		        	text = l1 + " (" + q1 + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		        }
		        LegendEntry le = new LegendEntry(text, dd);
		        lentries.add(le);
			
		}
		return lentries;
	}
	
	/*
	 * NOTE: Caller must dispose of created color
	 */
	private static org.eclipse.swt.graphics.Color getColor(Display display,
			Expression colorExpression) {
		String colorString = colorExpression.evaluate(null, String.class);
		if (colorString.startsWith("0x")) { //$NON-NLS-1$
			colorString = colorString.substring(2);
		}
		if (!colorString.startsWith("#")) { //$NON-NLS-1$
			colorString = "#" + colorString; //$NON-NLS-1$
		}
		Color c1 = Color.decode(colorString);

		org.eclipse.swt.graphics.Color c = new org.eclipse.swt.graphics.Color(
				display, c1.getRed(), c1.getGreen(), c1.getBlue());
		return c;

	}
	
	/*
	 * return the alpha value from the expression.
	 * 
	 * the returned value is scaled to 0-255 alpha scale
	 * the expression is assumed to provide the alpha between 0 and 1
	 */
	private static int getAlpha(Expression alphaExpression){
		  Double alpha = alphaExpression.evaluate(null, Double.class);
          if (alpha != null){
          	return((int)(alpha * 255));
          }
          return 255;
	}
}
