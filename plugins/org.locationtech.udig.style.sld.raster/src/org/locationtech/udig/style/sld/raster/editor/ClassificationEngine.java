/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.style.sld.raster.editor;

import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridEnvelope2D;
import org.geotools.coverage.grid.GridGeometry2D;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.processing.OperationJAI;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.parameter.DefaultParameterDescriptor;
import org.geotools.parameter.DefaultParameterDescriptorGroup;
import org.geotools.parameter.ParameterGroup;
import org.jaitools.media.jai.zonalstats.ZonalStats;
import org.jaitools.media.jai.zonalstats.ZonalStatsDescriptor;
import org.jaitools.numeric.Range;
import org.jaitools.numeric.Statistic;
import org.locationtech.udig.style.sld.raster.internal.Messages;
import org.opengis.coverage.grid.GridCoordinates;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.coverage.grid.GridCoverageReader;
import org.opengis.coverage.grid.GridGeometry;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Engine for computing classes for raster styling
 * @author Emily
 *
 */
public class ClassificationEngine {
	
	private String errorMessage = null;
	
	public ClassificationEngine(){
		this.errorMessage = null;
	}
	/*
	 * Maximum value for warning users to limit sample size
	 */
	public static final Long WARN_VALUE = 1000000l;
	
	
	/**
	 * Breaks the given raster into equal interval bins.
	 * 
	 * @param interval size of interval
	 * @param valuesToIgnore values to ignore
	 * @param layer raster layer
	 * @param sampleSize maximum sample size
	 * @return list of double values representing the bins or null if cancelled
	 * @throws Exception
	 */
	public List<Double> computeDefinedInterval(double interval,double[] valuesToIgnore,
			GridCoverageReader layer, Long sampleSize) throws Exception{
		this.errorMessage = null;
		double[] minmax = computeMinMax(valuesToIgnore, layer, sampleSize);
		if (minmax == null){
			return null;
		}
		double min = minmax[0];
		double max = minmax[1];
		ArrayList<Double> breaks = new ArrayList<Double>();
		for (double x = min; x <= max; x += interval){
			breaks.add(x);
		}
		return breaks;
	}
	
	/**
	 * Breaks the given raster into equal interval bins.
	 * 
	 * @param numIntervals number of bins
	 * @param valuesToIgnore values to ignore
	 * @param layer raster layer
	 * @param sampleSize maximum sample size
	 * @return list of double values representing the bins or null if cancelled
	 * @throws Exception
	 */
	public List<Double> computeEqualInterval(int numIntervals,double[] valuesToIgnore, 
			GridCoverageReader layer, Long sampleSize) throws Exception{
		this.errorMessage = null;
		double[] minmax = computeMinMax(valuesToIgnore, layer, sampleSize);
		if (minmax == null){
			return null;
		}
		double min = minmax[0];
		double max = minmax[1]; 
		double interval = (max-min) / numIntervals;
		
		ArrayList<Double> breaks = new ArrayList<Double>();
		if (interval == 0){
			breaks.add(min);
		}else{
			double value = min;
			for (int i = 0; i < numIntervals; i ++){
				breaks.add(value);
				value = value + interval;
			}
			breaks.add(max);
		}
		return breaks;
		
	}
	
	/**
	 * Prompts the user to ensure they want to continue when
	 * using a large sample size;
	 * @return true if processing to continue, false otherwise
	 */
	private boolean warnLargeSize(){
		final boolean[] ret = {true};
		Display.getDefault().syncExec(new Runnable(){
			@Override
			public void run() {
					if (!MessageDialog.openConfirm(Display.getDefault().getActiveShell(),
							Messages.ClassifyDialog_ConfirmDialogText, 
							MessageFormat.format(Messages.ClassifyDialog_RasterCellWaring, new Object[]{WARN_VALUE}))){
						ret[0] = false;
					}
			}});
		return ret[0];
		
	}
	
	/**
	 * Computes the minimum and maximum for
	 * the only band in a single band raster
	 * 
	 * @param valuesToIgnore values to ignore
	 * @param layer raster layer
	 * @param sampleSize maximum sample size
	 * @return double {minimum, maximum} or null
	 * @throws Exception
	 */
	public double[] computeMinMax(double[] valuesToIgnore, GridCoverageReader layer, Long sampleSize) throws Exception{
		final Statistic[] stats = new Statistic[] { 
				Statistic.MIN,
				Statistic.MAX };
		this.errorMessage = null;
		List<Range<Double>> ignore = new ArrayList<Range<Double>>();
		if (valuesToIgnore != null){
			for (Double no : valuesToIgnore){
				ignore.add(new Range<Double>(no));
			}
		}
		
		GridCoverage gcRaw = layer.read(null);
		
		
		if (sampleSize != null){
			int rSize = (int) Math.ceil(Math.sqrt(sampleSize.doubleValue()));
			GridEnvelope2D gridRange = new GridEnvelope2D(new Rectangle(0,0, rSize, rSize));
			GridGeometry2D world = new GridGeometry2D(gridRange,  new ReferencedEnvelope(gcRaw.getEnvelope()));
			DefaultParameterDescriptor<GridGeometry> gridGeometryDescriptor = new DefaultParameterDescriptor<GridGeometry>(
					AbstractGridFormat.READ_GRIDGEOMETRY2D.getName()
							.toString(), GridGeometry.class, null, world);

			ParameterGroup readParams = new ParameterGroup(
					new DefaultParameterDescriptorGroup(
							"Test", //$NON-NLS-1$
							new GeneralParameterDescriptor[] { gridGeometryDescriptor }));

			List<GeneralParameterValue> list = readParams.values();
			GeneralParameterValue[] values = list
					.toArray(new GeneralParameterValue[0]);
			gcRaw = layer.read(values);
		}
			
		GridCoordinates high = gcRaw.getGridGeometry().getGridRange().getHigh();
		GridCoordinates low = gcRaw.getGridGeometry().getGridRange().getLow();
		int width = high.getCoordinateValue(0) - low.getCoordinateValue(0);
		int height = high.getCoordinateValue(1) - low.getCoordinateValue(1);
		if (width * height > WARN_VALUE){
			if (!warnLargeSize()){
				return null;
			}
			
		}
			
		final OperationJAI op = new OperationJAI("ZonalStats"); //$NON-NLS-1$
		ParameterValueGroup params = op.getParameters();
		params.parameter("dataImage").setValue(gcRaw); //$NON-NLS-1$
		params.parameter("stats").setValue(stats); //$NON-NLS-1$
		params.parameter("bands").setValue(new Integer[] { 0 }); //$NON-NLS-1$
		if (ignore.size() > 0){
			params.parameter("ranges").setValue(ignore); //$NON-NLS-1$
			params.parameter("rangesType").setValue(Range.Type.EXCLUDE); //$NON-NLS-1$
			params.parameter("rangeLocalStats").setValue(false); //$NON-NLS-1$
		}
		

		final GridCoverage2D coverage = (GridCoverage2D) op.doOperation(params,null);
		final ZonalStats zstats = (ZonalStats) coverage
				.getProperty(ZonalStatsDescriptor.ZONAL_STATS_PROPERTY);
		double min = zstats.statistic(Statistic.MIN).results().get(0).getValue();
		double max = zstats.statistic(Statistic.MAX).results().get(0).getValue();
		return new double[]{min,max};
	}
	
	/**
	 * Computes quantile breaks for the given layer.
	 * 
	 * @param numBins number of bins
	 * @param valuesToIgnore values to ignore
	 * @param layer raster layer to sample
	 * @param sampleSize maximum sample size
	 * @param monitor progress monitor
	 * @return list of doubles representing the quantile breaks or null if cancelled
	 * @throws Exception
	 */
	public List<Double> computeQuantile(int numBins,
			double[] valuesToIgnore,
			GridCoverageReader layer, 
			Long sampleSize,
			IProgressMonitor monitor) throws Exception{
		this.errorMessage = null;
		GridCoverage gcRaw = layer.read(null);
		if (sampleSize != null){
			int rSize = (int) Math.ceil(Math.sqrt(sampleSize.doubleValue()));
			GridEnvelope2D gridRange = new GridEnvelope2D(new Rectangle(0,0, rSize, rSize));
			GridGeometry2D world = new GridGeometry2D(gridRange,  new ReferencedEnvelope(gcRaw.getEnvelope()));
			DefaultParameterDescriptor<GridGeometry> gridGeometryDescriptor = new DefaultParameterDescriptor<GridGeometry>(
					AbstractGridFormat.READ_GRIDGEOMETRY2D.getName()
							.toString(), GridGeometry.class, null, world);

			ParameterGroup readParams = new ParameterGroup(
					new DefaultParameterDescriptorGroup(
							"Test", //$NON-NLS-1$
							new GeneralParameterDescriptor[] { gridGeometryDescriptor }));

			List<GeneralParameterValue> list = readParams.values();
			GeneralParameterValue[] values = list
					.toArray(new GeneralParameterValue[0]);
			gcRaw = layer.read(values);
		}
						
		if (monitor.isCanceled()){
			return null;
		}
		GridCoordinates high = gcRaw.getGridGeometry().getGridRange().getHigh();
		GridCoordinates low = gcRaw.getGridGeometry().getGridRange().getLow();
		int width = high.getCoordinateValue(0) - low.getCoordinateValue(0);
		int height = high.getCoordinateValue(1) - low.getCoordinateValue(1);
		
		if (width * height > WARN_VALUE){
			if (!warnLargeSize()){
				return null;
			}
		}
		
		int recSize = 1000;
		HashSet<Double> ignoreset = new HashSet<Double>();
		if (valuesToIgnore != null){
			for (Double d : valuesToIgnore){
				ignoreset.add(d);
			}
		}

		List<Double> data = new ArrayList<Double>(width * height);
		for (int x = 0; x < width; x+=recSize){
			for (int y = 0; y < height; y += recSize){
				int w = recSize;
				int h = recSize;
				if (x + recSize > width){
					w = width - x;
				}
				if (y + recSize > height){
					h = height - y;
				}
				Rectangle r = new Rectangle(x, y, w, h);
				Raster rs = gcRaw.getRenderedImage().getData(r);
				DataBuffer df  = rs.getDataBuffer();
				for (int i = 0; i < df.getSize(); i ++){
					Double v = df.getElemDouble(i);
					if (!ignoreset.contains(v)){
						data.add(v);
					}
				}
			}
			if (monitor.isCanceled()){
				return null;
			}
		}
		
		
		 // sort the list
		Collections.sort(data);

	
		if (numBins > data.size()) { //resize
			numBins = data.size();
		}
		double min[] = new double[numBins];
		double max[] = new double[numBins];
		for( int i = 0; i < numBins; i ++){
			min[i] = Double.POSITIVE_INFINITY;
			max[i] = Double.NEGATIVE_INFINITY;
		}
		
		// calculate number of items to put into each of the larger bins
		int binPop = new Double(Math.ceil((double) data.size() / numBins)).intValue();
		// determine index of bin where the next bin has one less item
		int lastBigBin = data.size() % numBins;
		if (lastBigBin == 0) lastBigBin = numBins;
		else lastBigBin--;

		// put the items into their respective bins
		int item = 0;
		for (int binIndex = 0; binIndex < numBins; binIndex++) {
			for (int binMember = 0; binMember < binPop; binMember++) {
				double value = data.get(item++);
				if (min[binIndex] > value){
					min[binIndex] = value;
				}
				if (max[binIndex] < value){
					max[binIndex] = value;
				}
			}
			if (lastBigBin == binIndex)
				binPop--; // decrease the number of items in a bin for the next item
		}
		
		ArrayList<Double> results = new ArrayList<Double>(numBins + 1);
		for (int i = 0 ; i< numBins; i ++){
			results.add(min[i]);
		}
		results.add(max[numBins-1]);
		return results;
		
	}
	
	/**
	 * 
	 * @param layer the raster to read
	 * @param sampleSize the maximum number of values to sample
	 * @param monitor 
	 * @return list of unique values found in the raster or null
	 * if cancelled
	 * @throws IOException 
	 * @throws IllegalArgumentException 
	 */
	public Set<Double> computeUniqueValues(GridCoverageReader layer, Long sampleSize, IProgressMonitor monitor) throws IllegalArgumentException, IOException{
		this.errorMessage = null;
		GridCoverage gcRaw = layer.read(null);
		if (sampleSize != null){
			int rSize = (int) Math.ceil(Math.sqrt(sampleSize.doubleValue()));
			GridEnvelope2D gridRange = new GridEnvelope2D(new Rectangle(0,0, rSize, rSize));
			GridGeometry2D world = new GridGeometry2D(gridRange,  new ReferencedEnvelope(gcRaw.getEnvelope()));
			DefaultParameterDescriptor<GridGeometry> gridGeometryDescriptor = new DefaultParameterDescriptor<GridGeometry>(
					AbstractGridFormat.READ_GRIDGEOMETRY2D.getName()
							.toString(), GridGeometry.class, null, world);

			ParameterGroup readParams = new ParameterGroup(
					new DefaultParameterDescriptorGroup(
							"Test", //$NON-NLS-1$
							new GeneralParameterDescriptor[] { gridGeometryDescriptor }));

			List<GeneralParameterValue> list = readParams.values();
			GeneralParameterValue[] values = list
					.toArray(new GeneralParameterValue[0]);
			gcRaw = layer.read(values);
		}
						
		if (monitor.isCanceled()){
			return null;
		}
		GridCoordinates high = gcRaw.getGridGeometry().getGridRange().getHigh();
		GridCoordinates low = gcRaw.getGridGeometry().getGridRange().getLow();
		int width = high.getCoordinateValue(0) - low.getCoordinateValue(0);
		int height = high.getCoordinateValue(1) - low.getCoordinateValue(1);
		
		if (width * height > WARN_VALUE){
			if (!warnLargeSize()){
				return null;
			}
		}
		
		int recSize = 1000;
		boolean maxReached = false;
		HashSet<Double> results = new HashSet<Double>();
		for (int x = 0; x < width; x+=recSize){
			for (int y = 0; y < height; y += recSize){
				int w = recSize;
				int h = recSize;
				if (x + recSize > width){
					w = width - x;
				}
				if (y + recSize > height){
					h = height - y;
				}
				Rectangle r = new Rectangle(x, y, w, h);
				
				Raster rs = gcRaw.getRenderedImage().getData(r);
				DataBuffer df  = rs.getDataBuffer();
				for (int i = 0; i < df.getSize(); i ++){
					results.add(df.getElemDouble(i));
					if (results.size() >= SingleBandEditorPage.MAX_ENTRIES){
						errorMessage = MessageFormat.format(Messages.UniqueValuesDialog_MaxValueError, SingleBandEditorPage.MAX_ENTRIES);
						maxReached = true;
						break;
					}
				}
			}
			if (maxReached){
				break;
			}
			if (monitor.isCanceled()){
				return null;
			}
		}
		return results;
	}
	
	/**
	 * Returns the last error message generated by the engine;
	 * @return
	 */
	public String getLastErrorMessage(){
		return this.errorMessage;
	}
}
