/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.style.sld.raster.editor;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.locationtech.udig.style.sld.raster.SLDRasterPlugin;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.geotools.filter.expression.ExpressionBuilder;
import org.geotools.styling.ColorMap;
import org.geotools.styling.ColorMapEntryImpl;
import org.geotools.styling.ColorMapImpl;
import org.opengis.coverage.grid.GridCoverageReader;
import org.opengis.filter.expression.Expression;
import org.osgi.framework.Bundle;

/**
 * The class reading default colortables from disk.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 * 
 */
public class PredefinedColorRules {

	/**
	 * The rainbow colormap is the only one that has to exist.
	 */
	public final static String[][] rainbow = new String[][] { //
	{ "255", "255", "0" }, // //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			{ "0", "255", "0" }, // //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			{ "0", "255", "255" }, // //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			{ "0", "0", "255" }, // //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			{ "255", "0", "255" }, // //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			{ "255", "0", "0" } }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	/**
	 * The {@link HashMap map} holding all predefined color rules.
	 */
	private List<PredefinedColorRule> colorRules = new ArrayList<PredefinedColorRule>();

	private Double dataMin = null;
	private Double dataMax = null;
	private GridCoverageReader reader = null;
	private double[] noDataValues = null;

	Job minMaxJob = new Job("ComputeMinMax") { //$NON-NLS-1$
		@Override
		protected IStatus run(IProgressMonitor monitor) {
			try {
				double[] noData = noDataValues;
				if (noData == null) {
					noData = new double[] { -9999.0 };
				}
				double[] minmax = (new ClassificationEngine()).computeMinMax(
						noData, reader, ClassificationEngine.WARN_VALUE / 2);
				if (minmax != null) {
					setDataMinMax(minmax[0], minmax[1]);
				}
			} catch (Exception e) {
				SLDRasterPlugin.log(e.getMessage(), e);
			}

			return Status.OK_STATUS;
		}
	};

	public PredefinedColorRules(GridCoverageReader reader, double[] noData) {
		this.reader = reader;
		this.noDataValues = noData;

	}

	public void dispose(){
		colorRules.clear();
		try {
			reader.dispose();
		} catch (IOException e) {
			SLDRasterPlugin.log(e.getMessage(), e);
		}
	}
	private void setDataMinMax(Double min, Double max) {
		this.dataMax = max;
		this.dataMin = min;
		colorRules.clear();
	}

	public List<PredefinedColorRule> getPredefinedSets() {
		if (colorRules == null || colorRules.size() == 0) {
			readColorRules();
		}
		return colorRules;
	}

	public void clear() {
		colorRules.clear();
	}
	
	public PredefinedColorRule getPredefinedRule(String name){
		for (PredefinedColorRule r: getPredefinedSets()){
			if (r.getName().equals(name)){
				return r;
			}
		}
		return null;
	}

	/**
	 * Reads and returns the {@link HashMap map} holding all predefined color
	 * rules.
	 * 
	 * <p>
	 * The map has the name of the colortable as key and and array of Strings as
	 * value, representing the colors and values of the colortable.<br>
	 * The array can be of two types:<br>
	 * <ul>
	 * <li>
	 * 3 values per row (r, g, b): in that case the colortable will be
	 * interpolated over a supplied range and be kept continuos between the
	 * values. On example is the elevation map.</li>
	 * <li>
	 * 8 values per row (v1, r1, g1, b1, v2, r2, g2, b2): in that case the
	 * values of every step is defined and the color rules are used as they
	 * come. One example is the corine landcover map, that has to be consistent
	 * with the given values and colors.</li>
	 * </ul>
	 * </p>
	 * 
	 * @param doReset
	 *            if true the folder of colortables is reread.
	 * @return the map of colortables.
	 */
	private void readColorRules() {
		colorRules.clear();
		/*
		 * read the default colortables from the plugin folder
		 */
		try {
			// create the rainbow colortable, which has to exist
			colorRules.add(new PredefinedColorRule("rainbow", rainbow)); //$NON-NLS-1$

			File colorTablesFolder = null;
			// TODO : move predefined color-tables to a central plugin
			Bundle bundle = Platform.getBundle("org.locationtech.udig.style.advanced.raster"); //$NON-NLS-1$
			if (bundle != null) {
				URL queriesUrl = bundle.getResource("colortables"); //$NON-NLS-1$
				String colorTablesFolderPath = FileLocator
						.toFileURL(queriesUrl).getPath();
				colorTablesFolder = new File(colorTablesFolderPath);
			}

			if (colorTablesFolder != null && colorTablesFolder.exists()) {
				File[] files = colorTablesFolder.listFiles();
				for (File file : files) {
					String name = file.getName();
					if (name.toLowerCase().endsWith(".clr")) { //$NON-NLS-1$
						BufferedReader bR = new BufferedReader(new FileReader(
								file));
						List<String[]> lines = new ArrayList<String[]>();
						String line = null;
						int cols = 0;
						while ((line = bR.readLine()) != null) {
							if (line.startsWith("#")) { //$NON-NLS-1$
								continue;
							}
							String[] lineSplit = line.trim().split("\\s+"); //$NON-NLS-1$
							cols = lineSplit.length;
							lines.add(lineSplit);
						}
						bR.close();
						String[][] linesArray = (String[][]) lines
								.toArray(new String[lines.size()][cols]);
						String ruleName = FilenameUtils.getBaseName(file
								.getName());
						ruleName = ruleName.replaceAll("\\_", " "); //$NON-NLS-1$ //$NON-NLS-2$
						colorRules.add(new PredefinedColorRule(ruleName,
								linesArray));
					}
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private ColorMap parseRulesValuesList(final String[][] colorRules)
			throws IOException {

		ColorMap cm = new ColorMapImpl();
		ExpressionBuilder builder = new ExpressionBuilder();
		if (colorRules[0].length == 3) {
			if (dataMin == null && dataMax == null) {
				minMaxJob.schedule();
				try {
					minMaxJob.join();
				} catch (InterruptedException e) {
					SLDRasterPlugin.log(e.getMessage(), e);
					return null;
				}
			}
			/*
			 * the colorrules are without values, so we ramp through them over
			 * the range.
			 */
			if (dataMin == null) {
				dataMin = -100.0;
			}
			if (dataMax == null) {
				dataMax = 5000.0;
			}

			// calculate the color increment
			float rinc = (float) (dataMax - dataMin)
					/ (float) (colorRules.length - 1);

			for (int i = 0; i < colorRules.length - 1; i++) {
				try {
					double to = dataMin + ((i + 1) * rinc);
					Color toColor = new Color(
							Integer.parseInt(colorRules[i + 1][0]),
							Integer.parseInt(colorRules[i + 1][1]),
							Integer.parseInt(colorRules[i + 1][2]));
					if (i == 0) {
						double from = dataMin + (i * rinc);
						Color fromColor = new Color(
								Integer.parseInt(colorRules[i][0]),
								Integer.parseInt(colorRules[i][1]),
								Integer.parseInt(colorRules[i][2]));

						ColorMapEntryImpl cme = new ColorMapEntryImpl();
						cme.setColor((Expression) builder.literal(fromColor)
								.build());
						cme.setQuantity((Expression) builder.literal(from)
								.build());
						cm.addColorMapEntry(cme);
					}
					ColorMapEntryImpl cme = new ColorMapEntryImpl();
					cme.setColor((Expression) builder.literal(toColor).build());
					cme.setQuantity((Expression) builder.literal(to).build());
					cm.addColorMapEntry(cme);

				} catch (NumberFormatException e) {
					SLDRasterPlugin.log(e.getMessage(), e);
					continue;
				}
			}

		} else {
			/*
			 * in this case we have also the values for the range defined and
			 * the color rule has to be "v1 r1 g1 b1 v2 r2 g2 b2".
			 */
			if (colorRules[0].length != 8) {
				throw new IOException(
						"The colortable can have records of 3 or 8 columns. Check your colortables."); //$NON-NLS-1$
			}

			for (int i = 0; i < colorRules.length; i++) {
				try {
					double to = Double.parseDouble(colorRules[i][4]);
					Color toColor = new Color(
							Integer.parseInt(colorRules[i][5]),
							Integer.parseInt(colorRules[i][6]),
							Integer.parseInt(colorRules[i][7]));
					if (i == 0) {
						double from = Double.parseDouble(colorRules[i][0]);
						Color fromColor = new Color(
								Integer.parseInt(colorRules[i][1]),
								Integer.parseInt(colorRules[i][2]),
								Integer.parseInt(colorRules[i][3]));

						ColorMapEntryImpl cme = new ColorMapEntryImpl();
						cme.setColor((Expression) builder.literal(fromColor)
								.build());
						cme.setQuantity((Expression) builder.literal(from)
								.build());
						cm.addColorMapEntry(cme);
					}
					ColorMapEntryImpl cme = new ColorMapEntryImpl();
					cme.setColor((Expression) builder.literal(toColor).build());
					cme.setQuantity((Expression) builder.literal(to).build());
					cm.addColorMapEntry(cme);
				} catch (NumberFormatException e) {
					SLDRasterPlugin.log(e.getMessage(), e);
					continue;
				}
			}

		}
		return cm;

	}

	class PredefinedColorRule {
		String name;
		ColorMap colorMap;
		String[][] rules;

		public PredefinedColorRule(String name, String[][] rules) {
			this.name = name;
			this.rules = rules;
		}

		public String getName() {
			return this.name;
		}

		public ColorMap getColorMap() {
			if (colorMap == null) {
				try{
					this.colorMap = PredefinedColorRules.this
							.parseRulesValuesList(rules);
				}catch (Exception ex){
					SLDRasterPlugin.log(ex.getMessage(), ex);
				}
			}
			return this.colorMap;
		}
	}
}
