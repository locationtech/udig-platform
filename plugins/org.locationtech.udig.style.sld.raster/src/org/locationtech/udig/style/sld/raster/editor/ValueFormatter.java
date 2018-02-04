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

import java.text.DecimalFormat;

import org.opengis.coverage.SampleDimensionType;

/**
 * Class for formatting values in value field of colormaptype panels
 * 
 * @author Emily
 *
 */
public class ValueFormatter {

	/*
	 * default data types
	 */
	public enum DataType{
		INTEGER,
		DOUBLE;
	}
	
	private DataType rawType;
	private DataType dataType;
	private DecimalFormat numberFormat;
	
	/**
	 * Creates a new formatter
	 */
	public ValueFormatter(){
		this.rawType = DataType.DOUBLE;
	}
	
	/**
	 * Sets the data type
	 * @param dataType
	 */
	public void setDataType(DataType dataType){
		this.dataType = dataType;
	}
	
	/**
	 * 
	 * @return the data type
	 */
	public DataType getDataType(){
		return this.dataType;
	}
	
	/**
	 * 
	 * @return the raw data type
	 */
	public DataType getRawDataType(){
		return this.rawType;
	}
	
	/**
	 * 
	 * @return the custom formatter
	 */
	public DecimalFormat getFormat(){
		return this.numberFormat;
	}
	
	/**
	 * Sets the raw data type of the raster
	 * @param type  
	 */
	public void setRawDataType(SampleDimensionType type){
		this.rawType = getDefaultDataType(type);
		
	}
	
	/**
	 * Sets the custom formatter
	 * @param formatter
	 */
	public void setNumberFormatter(DecimalFormat formatter){
		this.numberFormat = formatter;
	}
	
	/**
	 * Formats a number based on the current field values.
	 * @param value
	 * @return
	 */
	public String formatNumber(double value){
		if (numberFormat == null){
			DataType formattype = dataType;
			if (formattype == null){
				formattype = rawType;
			}
			if (formattype == DataType.INTEGER){
				return String.valueOf(((int)value));
			}else if (formattype == DataType.DOUBLE){
				return String.valueOf(value);
			}
		}else{
			return numberFormat.format(value);

		}
		return String.valueOf(value);
		
	}
	
	
	private static DataType getDefaultDataType(SampleDimensionType type){
		
		if (type == SampleDimensionType.REAL_32BITS || 
				type == SampleDimensionType.REAL_64BITS ){
			return DataType.DOUBLE;
		}else if (type == SampleDimensionType.SIGNED_8BITS ||
				type == SampleDimensionType.SIGNED_16BITS ||
				type == SampleDimensionType.SIGNED_32BITS){
			return DataType.INTEGER;
		}else if (type == SampleDimensionType.UNSIGNED_1BIT ||
				type == SampleDimensionType.UNSIGNED_2BITS ||
				type == SampleDimensionType.UNSIGNED_4BITS ||
				type == SampleDimensionType.UNSIGNED_8BITS ||
				type == SampleDimensionType.UNSIGNED_16BITS||
				type == SampleDimensionType.UNSIGNED_32BITS){
			return DataType.INTEGER;
		}
						
		return DataType.DOUBLE;
		
	}
}
